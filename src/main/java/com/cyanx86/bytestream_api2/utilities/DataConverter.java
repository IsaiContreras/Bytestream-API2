package com.cyanx86.bytestream_api2.utilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public static BufferedImage byteArrayToImage(byte[] byteArray) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(byteArray));
    }

    public static byte[] imageToByteArray(BufferedImage image, String extension) throws IOException {
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        ImageIO.write(image, extension, byteArrayOutput);
        return byteArrayOutput.toByteArray();
    }

    public static BufferedImage byteArrayInputStreamToImage(ByteArrayInputStream byteArrayInputStream) {
        try { return ImageIO.read(byteArrayInputStream);
        } catch (Exception e) { return null; }
    }

    public static ByteArrayInputStream imageToByteArrayInputStream(BufferedImage image, String extension) {
        try {
            ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
            ImageIO.write(image, extension, byteArrayOutput);
            return new ByteArrayInputStream(byteArrayOutput.toByteArray());
        } catch (Exception e) { return null; }
    }

}
