package com.cyanx86.bytestream_api2.misc;

public enum FilenameFormat {
    ENTITY_FILE_FORMAT("entity-media.filename.format");

    private final String path;

    FilenameFormat(String path) {
        this.path = path;
    }

    public String getPath() { return this.path; }

}
