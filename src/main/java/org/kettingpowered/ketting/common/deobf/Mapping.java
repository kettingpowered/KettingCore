package org.kettingpowered.ketting.common.deobf;

public record Mapping(String obfMethod, String obfDesc, String mojang) {

    static Mapping from(String obfMethod, String obfDesc, String mojang) {
        return new Mapping(obfMethod, obfDesc, mojang);
    }

    public String obf() {
        return obfMethod + obfDesc;
    }
}
