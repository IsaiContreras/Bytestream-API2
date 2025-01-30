package com.cyanx86.bytestream_api2.misc;

import com.cyanx86.bytestream_api2.configuration.ResourcePathsConfigurer;
import org.jetbrains.annotations.NotNull;

public enum ResourcePaths {

    // -- [[ VALUES ]] --
    ROOT("upload.path"),
    GAME_RATING("game-rating-upload.path"),
    GAME_RATING_ENTITIES("game-rating-entity-upload.path"),
    GAME_COVER_ART("game-cover-art-upload.path"),
    GAME_LOGO_ART("game-logo-art-upload.path"),
    GAME_LANDSCAPE_ART("game-landscape-art-upload.path");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String path;
    private String value = null;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    ResourcePaths(@NotNull String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setValue(@NotNull ResourcePathsConfigurer configurer, @NotNull String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
