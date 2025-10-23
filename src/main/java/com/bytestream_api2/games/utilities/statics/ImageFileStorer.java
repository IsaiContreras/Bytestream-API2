package com.bytestream_api2.games.utilities.statics;

import org.jetbrains.annotations.NotNull;

import org.springframework.web.multipart.MultipartFile;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ImageFileStorer {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public static boolean storeFile(
            Path destination, String filename, ByteArrayInputStream filestream
    ) {
        try {
            if (!Files.exists(destination))
                Files.createDirectories(destination);

            destination = destination.resolve(filename);
            Files.copy(filestream, destination, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (Exception e) { return false; }
    }

    public static boolean storeFile(
            @NotNull BufferedImage image, @NotNull Path destination, @NotNull String filename, @NotNull String extension
    ) {
        try {
            if (!Files.exists(destination))
                Files.createDirectories(destination);

            destination = destination.resolve(filename);
            Files.copy(
                    Objects.requireNonNull(DataConverter.imageToByteArrayInputStream(image, extension)),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return true;
        } catch (Exception e) { return false; }
    }

    public static BufferedImage resizeImage(
            @NotNull BufferedImage originalImage, int width, int height
    ) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }

    public static boolean uploadImageFile(
            MultipartFile multipartFile,
            Path destinyPath,
            @NotNull String filename
    ) {
        BufferedImage image;
        try {
            image = DataConverter.byteArrayToImage(multipartFile.getBytes());
        } catch (Exception e) { return false; }

        String extension = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];

        return ImageFileStorer.storeFile(image, destinyPath, filename, extension);
    }

}
