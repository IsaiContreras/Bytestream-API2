package com.cyanx86.bytestream_api2.misc;

public enum MediaEntity {
    ROOT("upload.path"),
    GAME_RATING("game-rating-upload.path"),
    GAME_RATING_ENTITIES("game-rating-entity-upload.path"),
    GAME_COVER_ART("game-cover-art-upload.path"),
    GAME_LOGO_ART("game-logo-art-upload.path"),
    GAME_LANDSCAPE_ART("game-landscape-art-upload.path");

    private final String path;

    MediaEntity(String path) {
        this.path = path;
    }

    public String getPath() { return this.path; }

}
