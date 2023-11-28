package org.kettingpowered.ketting.adapter;

interface SharedConstants {

    String getMcVersion();

    default boolean isValidAdapter() {
        return getMcVersion() != null;
    }
}
