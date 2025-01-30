package com.cyanx86.bytestream_api2.utilities;

import com.cyanx86.bytestream_api2.misc.ResourcePaths;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public class ResourcePathProvider {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public static Path getPathOfEntity(@NotNull ResourcePaths mediaEntity, String folder) {
        Path result;
        try {
            result = Path.of(Objects.requireNonNull(ResourcePaths.ROOT.getValue()))
                    .resolve(mediaEntity.getPath());
        } catch (Exception e) { return null; }

        if (folder != null) result = result.resolve(folder);
        return result;
    }

}
