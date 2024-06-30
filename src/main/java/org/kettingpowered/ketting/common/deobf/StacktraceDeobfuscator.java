package org.kettingpowered.ketting.common.deobf;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class StacktraceDeobfuscator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StacktraceDeobfuscator.class);
    private static MappingLoader mappingLoader;
    private static ClassLoader serverClassLoader;

    private static boolean enabled;

    private static final Map<Class<?>, Map<String, IntList>> lineMapCache = Collections.synchronizedMap(new LinkedHashMap<>(128, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Class<?>, Map<String, IntList>> eldest) {
            return this.size() > 127;
        }
    });

    public static void init(File mojmap, Map<String, String> bukkitMethods, ClassLoader serverClassLoader) {
        if (mappingLoader != null) return;

        try {
            mappingLoader = new MappingLoader(mojmap, bukkitMethods);
        } catch (IOException e) {
            LOGGER.error("Failed to load mappings", e);
            return;
        }

        StacktraceDeobfuscator.serverClassLoader = serverClassLoader;

        int mojmapSize = mappingLoader.mappings.size();
        int bukkitSize = mappingLoader.bukkitMappings.values().stream().mapToInt(List::size).sum();
        LOGGER.info("Loaded {} mappings [{} mojmap, {} bukkit]", mojmapSize + bukkitSize, mojmapSize, bukkitSize);
        enabled = true;
    }

    public static void setEnabled(boolean enabled) {
        StacktraceDeobfuscator.enabled = enabled;
    }

    public static void deobf(Throwable throwable) {
        if (!enabled || mappingLoader == null) return;

        throwable.setStackTrace(deobf(throwable.getStackTrace()));

        final Throwable cause = throwable.getCause();
        if (cause != null) deobf(cause);

        Arrays.stream(throwable.getSuppressed()).forEach(StacktraceDeobfuscator::deobf);
    }

    public static StackTraceElement[] deobf(StackTraceElement[] stacktrace) {
        if (!enabled || mappingLoader == null || stacktrace.length == 0) return stacktrace;

        final StackTraceElement[] deobf = new StackTraceElement[stacktrace.length];
        for (int i = 0; i < stacktrace.length; i++) {
            final StackTraceElement element = stacktrace[i];
            final String className = element.getClassName();

            final List<Mapping> classMappings = mappingLoader.bukkitMappings.get(className);
            if (classMappings == null) {
                deobf[i] = element;
                continue;
            }

            final Class<?> clazz;
            try {
                clazz = Class.forName(className, true, serverClassLoader);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to find class for {}", className, e);
                deobf[i] = element;
                continue;
            }

            final String methodKey = determineMethodForLine(clazz, element.getLineNumber());

            if (methodKey == null) {
                deobf[i] = element;
                continue;
            }

            final Mapping map = classMappings.stream().filter(m -> m.obf().equals(methodKey)).findFirst().orElse(null);

            if (map == null) {
                deobf[i] = element;
                continue;
            }

            final String methodName = mappingLoader.getFromMojang(className, map.mojang(), map.obfDesc());

            deobf[i] = new StackTraceElement(
                    element.getClassLoaderName(),
                    element.getModuleName(),
                    element.getModuleVersion(),
                    className,
                    Objects.equals(methodName, map.mojang()) ? element.getMethodName() : methodName,
                    sourceFileName(className),
                    element.getLineNumber()
            );
        }
        return deobf;
    }

    private static @Nullable String determineMethodForLine(final Class<?> clazz, final int lineNumber) {
        final Map<String, IntList> lineMap = lineMapCache.computeIfAbsent(clazz, StacktraceDeobfuscator::buildLineMap);
        for (final var entry : lineMap.entrySet()) {
            final String methodKey = entry.getKey();
            final IntList lines = entry.getValue();
            for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
                final int num = lines.getInt(i);
                if (num == lineNumber) {
                    return methodKey;
                }
            }
        }
        return null;
    }

    private static String sourceFileName(final String fullClassName) {
        final int dot = fullClassName.lastIndexOf('.');
        final String className = dot == -1
                ? fullClassName
                : fullClassName.substring(dot + 1);
        final String rootClassName = className.split("\\$")[0];
        return rootClassName + ".java";
    }

    private static Map<String, IntList> buildLineMap(final Class<?> key) {
        final Map<String, IntList> lineMap = new HashMap<>();
        final class LineCollectingMethodVisitor extends MethodVisitor {
            private final IntList lines = new IntArrayList();
            private final String name;
            private final String descriptor;

            LineCollectingMethodVisitor(String name, String descriptor) {
                super(Opcodes.ASM9);
                this.name = name;
                this.descriptor = descriptor;
            }

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                this.lines.add(line);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                lineMap.put(mappingLoader.methodKey(this.name, this.descriptor), this.lines);
            }
        }
        final ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new LineCollectingMethodVisitor(name, descriptor);
            }
        };
        try {
            final @Nullable InputStream inputStream = StacktraceDeobfuscator.class.getClassLoader()
                    .getResourceAsStream(key.getName().replace('.', '/') + ".class");
            if (inputStream == null) {
                throw new IllegalStateException("Could not find class file: " + key.getName());
            }
            final byte[] classData;
            try (inputStream) {
                classData = inputStream.readAllBytes();
            }
            final ClassReader reader = new ClassReader(classData);
            reader.accept(classVisitor, 0);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return lineMap;
    }
}
