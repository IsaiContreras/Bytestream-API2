package com.bytestream_api2.games.utilities.enums;

import org.jetbrains.annotations.NotNull;

public enum GameArtType {

    // -- [[ VALUES ]] --
    COVER_ART("cover"),
    LANDSCAPE_ART("landscape");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String name;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE -

    // -- PUBLIC --
    GameArtType(@NotNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
