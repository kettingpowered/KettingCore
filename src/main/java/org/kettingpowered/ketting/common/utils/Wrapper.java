package org.kettingpowered.ketting.common.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper class for a generic type.
 * @param <T> The type to wrap.
 */
public class Wrapper<T> {

    private final T value;

    /**
     * Creates a new wrapper with the given value.
     * @param value The value to wrap.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Wrapper<T> wrap(T value) {
        return new Wrapper<>(value);
    }

    private Wrapper(T value) {
        this.value = value;
    }

    /**
     * Gets the wrapped value.
     * @return The wrapped value.
     */
    public T unwrap() {
        return value;
    }
}
