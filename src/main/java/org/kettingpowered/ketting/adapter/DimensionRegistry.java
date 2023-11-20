package org.kettingpowered.ketting.adapter;

public interface DimensionRegistry<R> {

    String getMcVersion();
    void createDefaults();
    void register(R registry);
}
