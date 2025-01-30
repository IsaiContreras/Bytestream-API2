package com.cyanx86.bytestream_api2.misc;

import com.cyanx86.bytestream_api2.configuration.FilenameFormatConfigurer;
import org.jetbrains.annotations.NotNull;

public enum FilenameFormat {

    // -- [[ VALUES ]] --
    ENTITY_FILE_FORMAT("common-entity-media.filename.format"),
    RESOLUTION_NAME_FORMAT("resolution-name.filename.format");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String path;
    private String value = null;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    FilenameFormat(String path) {
        this.path = path;
    }

    public String getPath() { return this.path; }

    public void setValue(@NotNull FilenameFormatConfigurer configurer, @NotNull String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
