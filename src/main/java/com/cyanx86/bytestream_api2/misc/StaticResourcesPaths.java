package com.cyanx86.bytestream_api2.misc;

public enum StaticResourcesPaths {

    // -- [[ VALUES ]] --
    GAME_RATING_ENTITY_LOGOS("/public/media/rating_entity/");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String staticPath;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    StaticResourcesPaths(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getStaticPath() { return this.staticPath; }

}
