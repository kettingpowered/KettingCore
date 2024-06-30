package org.kettingpowered.ketting.common.deobf;

import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MappingLoader {

    public final Map<String, ClassMapping> mappings;
    public final Map<String, List<Mapping>> bukkitMappings = new HashMap<>();

    MappingLoader(File mojmap, Map<String, String> bukkitMethods) throws IOException {
        mappings = loadMappingsIfPresent(mojmap);
        bukkitMethods.forEach(this::parseBukkitMappings);
    }

    private void parseBukkitMappings(String obf, String mojang) {
        String raw = obf.substring(0, obf.indexOf('(') - 1);
        int lastSlash = raw.lastIndexOf('/');
        String classPath = raw.substring(0, lastSlash);
        String methodName = raw.substring(lastSlash + 1);
        String desc = obf.substring(obf.indexOf(methodName) + methodName.length() + 1).trim();
        bukkitMappings.computeIfAbsent(classPath.replace('/', '.'), k -> new ArrayList<>()).add(Mapping.from(methodName, desc, mojang));
    }

    private @Nullable Map<String, ClassMapping> loadMappingsIfPresent(File mojmap) throws IOException {
        try (final @Nullable InputStream mappingsInputStream = new FileInputStream(mojmap)) {
            final MemoryMappingTree tree = new MemoryMappingTree();
            MappingReader.read(new InputStreamReader(mappingsInputStream, StandardCharsets.UTF_8), MappingFormat.PROGUARD_FILE, tree);

            String LEFT = "named";
            String RIGHT = "official";

            // Since the mapping works like 'named -> official', the source is technically the right side
            tree.setSrcNamespace(RIGHT);
            tree.setDstNamespaces(Collections.singletonList(LEFT));

            final Set<ClassMapping> classes = new HashSet<>();

            final StringPool pool = new StringPool();
            for (final MappingTree.ClassMapping cls : tree.getClasses()) {
                final Map<String, String> methods = new HashMap<>();

                for (final MappingTree.MethodMapping methodMapping : cls.getMethods()) {
                    methods.put(
                            pool.string(methodKey(
                                    methodMapping.getName(LEFT),
                                    methodMapping.getDesc(LEFT)
                            )),
                            pool.string(methodMapping.getName(RIGHT))
                    );
                }

                final ClassMapping map = new ClassMapping(
                        cls.getName(LEFT).replace('/', '.'),
                        cls.getName(RIGHT).replace('/', '.'),
                        Map.copyOf(methods)
                );
                classes.add(map);
            }

            return Set.copyOf(classes).stream().collect(Collectors.toUnmodifiableMap(ClassMapping::deobfName, map -> map));
        }
    }

    public String methodKey(final String obfName, final String obfDescriptor) {
        return obfName + obfDescriptor;
    }

    public String getFromMojang(String className, String mojangName, String desc) {
        if (mappings == null || mappings.isEmpty() || mojangName == null || mojangName.isBlank())
            return mojangName;

        ClassMapping mapping = mappings.get(className);
        if (mapping == null)
            return mojangName;

        return mapping.methods().getOrDefault(methodKey(mojangName, desc), mojangName);
    }

    private static final class StringPool {
        private final Map<String, String> pool = new HashMap<>();

        public String string(final String string) {
            return this.pool.computeIfAbsent(string, Function.identity());
        }
    }
}
