package com.cyanx86.bytestream_api2.utilities;

import com.cyanx86.bytestream_api2.misc.FilenameFormat;

import java.util.Arrays;

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

}
