package com.bytestream_api2.games.misc;

import org.springframework.http.MediaType;

public class ImageResourcePackage {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String filname;
    private final MediaType mediaType;
    private final byte[] imageObject;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public ImageResourcePackage(String filename, String mediaType, byte[] imageObject) {
        this.filname = filename;
        this.mediaType = MediaType.parseMediaType(mediaType);
        this.imageObject = imageObject;
    }

    public String getFilname() {
        return filname;
    }

    public MediaType getMediaType() {
        return mediaType != null ? mediaType :
                MediaType.APPLICATION_OCTET_STREAM;
    }

    public byte[] getImageByteArray() {
        return imageObject;
    }

}
