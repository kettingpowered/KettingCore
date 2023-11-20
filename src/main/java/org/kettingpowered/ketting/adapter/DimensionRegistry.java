package org.kettingpowered.ketting.adapter;

public interface DimensionRegistry<R, K, V> {

    String getMcVersion();
    void createDefaults();
    void register(R registry);
    K getKey(V value);
}
