package com.bytestream_api2.games.utilities;

import com.bytestream_api2.games.misc.ResourcePath;
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
    public static Path getPathOfEntity(@NotNull ResourcePath mediaEntity, String folder) {
        Path result;
        try {
            result = Path.of(Objects.requireNonNull(ResourcePath.ROOT.getPath()))
                    .resolve(mediaEntity.getPath());
        } catch (Exception e) { return null; }

        if (folder != null) result = result.resolve(folder);
        return result;
    }

}
