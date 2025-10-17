package com.bytestream_api2.games.interfaces;

import com.bytestream_api2.games.enums.ResourcePath;

public interface ImageContentEntity {

    void setImageURIsFromFilenames(String[] filenames);

    String getSubDirectory();

    String constructFilename(String extension, String[] addit);

    ResourcePath getResourcePath();

}
