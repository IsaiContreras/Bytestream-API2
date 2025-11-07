package com.bytestream_api2.games.utilities.statics;

import com.bytestream_api2.games.utilities.enums.FilenameFormat;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class FilenameFormatter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --}

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private static String[] getFormatParts(String format) {
        String[] parts = format.split("%");
        return Arrays.stream(parts)
                .filter(part -> part.length() > 1)
                .map(part -> "%" + part + "%")
                .toArray(String[]::new);
    }

    // -- PUBLIC --
    public static String formatFilename(FilenameFormat format, String[] values) {
        String filename = format.getValue();
        if (filename == null)
            return null;

        String[] parts = FilenameFormatter.getFormatParts(filename);
        for (int i = 0; i < Math.min(parts.length, values.length); i++)
            filename = filename.replace(parts[i], values[i]);

        return filename;
    }

    public static String getFileExension(String imageURL) {
        String extension = null;
        try(ImageInputStream imageInputStream = ImageIO.createImageInputStream(new File(imageURL))) {
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);

            if (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                extension = reader.getFormatName();
            }
        } catch (Exception e) { return null; }

        return extension;
    }

}
