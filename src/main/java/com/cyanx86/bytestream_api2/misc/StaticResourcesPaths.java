package com.cyanx86.bytestream_api2.misc;

public enum StaticResourcesPaths {
    GAME_RATING_ENTITY_LOGOS("/public/media/rating_entity/");

    private final String staticPath;

    StaticResourcesPaths(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getStaticPath() { return this.staticPath; }

}
