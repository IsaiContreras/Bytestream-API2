package com.cyanx86.bytestream_api2.misc;

public enum ImageResolution {

    // -- [[ VALUES ]] --
    ALL_RESOLUTIONS(1, 0, ""),
    ORIGINAL_WIDTH(2, 0, "full"),
    WIDTH_32(4, 32, "32w"),
    WIDTH_64(8, 64, "64w"),
    WIDTH_128(16, 128, "128w"),
    WIDTH_256(32, 256, "256w"),
    WIDTH_512(64, 512, "512w");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final int value;
    private final int width;
    private final String suffix;

    // -- PUBLIC --
    public static final int ALL_RESOLUTIONS_VALUE = 1;
    public static final int ORIGINAL_WIDTH_VALUE = 2;
    public static final int WIDTH_32_VALUE = 4;
    public static final int WIDTH_64_VALUE = 8;
    public static final int WIDTH_128_VALUE = 16;
    public static final int WIDTH_256_VALUE = 32;
    public static final int WIDTH_512_VALUE = 64;

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    ImageResolution(int value, int width, String suffix) {
        this.value = value;
        this.width = width;
        this.suffix = suffix;
    }

    public int getWidth() { return this.width; }
    public String getSuffix() { return this.suffix; }

    public static boolean hasFlag(int combinedFlags, ImageResolution resolution) {
        return (combinedFlags & resolution.value) != 0;
    }

}
