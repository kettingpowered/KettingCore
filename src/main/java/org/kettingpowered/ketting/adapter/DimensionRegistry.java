package org.kettingpowered.ketting.adapter;

public interface DimensionRegistry<R, K, V> extends SharedConstants {

    void createDefaults();
    void register(R registry);
    K getKey(V value);
}
