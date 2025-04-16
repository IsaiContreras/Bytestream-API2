package com.bytestream_api2.games.misc;

import com.bytestream_api2.games.configuration.ResourcePathsConfigurer;
import org.jetbrains.annotations.NotNull;

public enum ResourcePath {

    // -- [[ VALUES ]] --
    ROOT("upload.path"),
    GAME_RATING("game-rating-upload.path"),
    GAME_RATING_ENTITIES("game-rating-entity-upload.path"),
    GAME_COVER_ART("game-cover-art-upload.path"),
    GAME_LOGO_ART("game-logo-art-upload.path"),
    GAME_LANDSCAPE_ART("game-landscape-art-upload.path");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String key;
    private String path = null;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    ResourcePath(@NotNull String path) {
        this.key = path;
    }

    public String getKey() {
        return this.key;
    }

    public void setPath(@NotNull ResourcePathsConfigurer configurer, @NotNull String value) {
        this.path = value;
    }

    public String getPath() {
        return this.path;
    }

}
