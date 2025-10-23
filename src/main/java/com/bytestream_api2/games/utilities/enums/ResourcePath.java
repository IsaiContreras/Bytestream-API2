package com.bytestream_api2.games.utilities.enums;

import com.bytestream_api2.games.configuration.enums.ResourcePathsConfigurer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public enum ResourcePath {

    // -- [[ VALUES ]] --
    ROOT("upload.path"),
    GAME_RATING("game-rating-upload.path"),
    GAME_RATING_ENTITIES("game-rating-entity-upload.path"),
    GAME_ART("game-art-upload.path");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String key;
    private String path = null;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    ResourcePath(@NotNull String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setPath(@NotNull ResourcePathsConfigurer configurer, @NotNull String value) {
        this.path = value;
    }

    public String getPath() {
        return path;
    }

    public Path getPathOfEntity(String folder) {
        Path result;
        try {
            result = Path.of(Objects.requireNonNull(ResourcePath.ROOT.getPath()))
                    .resolve(this.getPath());
        } catch (Exception e) { return null; }

        if (folder != null) result = result.resolve(folder);
        return result;
    }

    public static Path getPathOfEntity(@NotNull ResourcePath mediaEntity, String folder) {
        Path result;
        try {
            result = Path.of(Objects.requireNonNull(ResourcePath.ROOT.getPath()))
                    .resolve(mediaEntity.getPath());
        } catch (Exception e) { return null; }

        if (folder != null) result = result.resolve(folder);
        return result;
    }

}
