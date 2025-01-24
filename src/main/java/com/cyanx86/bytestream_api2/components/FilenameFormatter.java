package com.cyanx86.bytestream_api2.components;

import com.cyanx86.bytestream_api2.misc.FilenameFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("filename_formatter_component")
public class FilenameFormatter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private Environment environment;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private String[] getFormatParts(String format) {
        String[] parts = format.split("%");
        return Arrays.stream(parts)
                .filter(part -> part.length() > 1)
                .map(part -> "%" + part + "%")
                .toArray(String[]::new);
    }

    // -- PUBLIC --
    public String formatFilename(FilenameFormat format, String[] values) {
        String filename = environment.getProperty(format.getPath());
        if (filename == null)
            return null;

        String[] parts = this.getFormatParts(filename);
        for (int i = 0; i < Math.min(parts.length, values.length); i++)
            filename = filename.replace(parts[i], values[i]);

        return filename;
    }

}
