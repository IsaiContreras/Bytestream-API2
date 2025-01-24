package com.cyanx86.bytestream_api2.misc;

public enum FilenameFormat {

    // -- [[ VALUES ]] --
    ENTITY_FILE_FORMAT("entity-media.filename.format");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String path;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    FilenameFormat(String path) {
        this.path = path;
    }

    public String getPath() { return this.path; }

}
