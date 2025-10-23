package com.bytestream_api2.games.utilities.statics;

import javax.imageio.ImageIO;
import java.awt.*;
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
        if (extension.equalsIgnoreCase("jpeg"))
            extension = "jpg";

        if (extension.equalsIgnoreCase("jpg") && image.getColorModel().hasAlpha()) {
            BufferedImage fixedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = fixedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, Color.WHITE, null);
            image = fixedImage;
        }

        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, extension, byteArrayOutput);
            if (!success)
                throw new IOException("No ImageIO writer found for format: " + extension);
            return byteArrayOutput.toByteArray();
        }
    }

    public static BufferedImage byteArrayInputStreamToImage(ByteArrayInputStream byteArrayInputStream) {
        try { return ImageIO.read(byteArrayInputStream);
        } catch (Exception e) { return null; }
    }

    public static ByteArrayInputStream imageToByteArrayInputStream(BufferedImage image, String extension) {
        if (extension.equalsIgnoreCase("jpeg"))
            extension = "jpg";

        if (extension.equalsIgnoreCase("jpg") && image.getColorModel().hasAlpha()) {
            BufferedImage fixedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = fixedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, Color.WHITE, null);
            image = fixedImage;
        }

        try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, extension, byteArrayOutput);
            if (!success)
                throw new IOException("No ImageIO writer found for format: " + extension);
            return new ByteArrayInputStream(byteArrayOutput.toByteArray());
        } catch (Exception e) { return null; }
    }

}
