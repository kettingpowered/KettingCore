package org.kettingpowered.ketting.adapter;

interface SharedConstants {

    String getMcVersion();

    void reload();

    default boolean isValidAdapter() {
        return getMcVersion() != null;
    }
}
