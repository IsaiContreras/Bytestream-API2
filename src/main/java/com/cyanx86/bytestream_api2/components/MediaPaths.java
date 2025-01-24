package com.cyanx86.bytestream_api2.components;

import com.cyanx86.bytestream_api2.misc.MediaEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;

@Component("media_paths_component")
public class MediaPaths {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private Environment environment;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public Path getPathOfEntity(@NotNull MediaEntity mediaEntity, String folder) {
        Path result = Path.of(Objects.requireNonNull(environment.getProperty(MediaEntity.ROOT.getPath())))
                .resolve(mediaEntity.getPath());

        if (folder != null) result = result.resolve(folder);
        return result;
    }

}
