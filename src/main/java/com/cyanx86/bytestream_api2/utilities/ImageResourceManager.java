package com.cyanx86.bytestream_api2.utilities;

import com.cyanx86.bytestream_api2.misc.FilenameFormat;
import com.cyanx86.bytestream_api2.misc.ImageResolution;
import com.cyanx86.bytestream_api2.misc.ResourcePath;

import org.jetbrains.annotations.NotNull;

import org.springframework.web.multipart.MultipartFile;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ImageResourceManager {

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
        @NotNull Path destination, @NotNull String filename, @NotNull BufferedImage image, @NotNull String extension
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

    public static BufferedImage[] getResizedImageInstances(
            @NotNull BufferedImage originalImage, int resolutionConfiguration
    ) {
        List<BufferedImage> resizedImageBuffer = new ArrayList<>();

        ImageResolution[] resolutionValues = ImageResolution.values();
        for (int i = 1; i < resolutionValues.length; i++)
            if (
                ImageResolution.hasFlag(resolutionConfiguration, ImageResolution.ALL_RESOLUTIONS) ||
                ImageResolution.hasFlag(resolutionConfiguration, resolutionValues[i])
            )
                resizedImageBuffer.add(ImageResourceManager.resizeImage(originalImage, resolutionValues[i]));

        return resizedImageBuffer.toArray(BufferedImage[]::new);
    }

    public static BufferedImage resizeImage(
            @NotNull BufferedImage originalImage, @NotNull ImageResolution resolution
    ) {
        if (resolution.equals(ImageResolution.ORIGINAL_WIDTH) || resolution.equals(ImageResolution.ALL_RESOLUTIONS))
            return originalImage;

        int targetWidth = resolution.getWidth();

        float scalePercentage = ((float)targetWidth / (float)originalImage.getWidth());
        int targetHeight = (int)(originalImage.getHeight() * scalePercentage);

        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }

    public static boolean uploadImageFile(
            MultipartFile multipartFile,
            UUID entityID,
            ResourcePath destityPath,
            int resolutionConfiguration
    ) {
        BufferedImage originalImage;
        try {
            originalImage = DataConverter.byteArrayToImage(multipartFile.getBytes());
        } catch (Exception e) { return false; }

        BufferedImage[] rescaledImages;
        try {
            rescaledImages = ImageResourceManager.getResizedImageInstances(
                    originalImage,
                    resolutionConfiguration
            );
        } catch (Exception e) { return false; }

        Path storageDestination = ResourcePathProvider.getPathOfEntity(
                destityPath, entityID.toString()
        );
        if (storageDestination == null)
            return false;

        for (BufferedImage rescaledItem : rescaledImages) {
            String suffix;
            if (rescaledItem.getWidth() == originalImage.getWidth())
                suffix = "full";
            else {
                try {
                    suffix = Objects.requireNonNull(
                            Arrays.stream(ImageResolution.values())
                                    .filter(resolution -> resolution.getWidth() == rescaledItem.getWidth())
                                    .findFirst()
                                    .orElse(null)
                    ).getSuffix();
                } catch (Exception e) {
                    return false;
                }
            }
            String extension = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];

            String filename = FilenameFormatter.formatFilename(
                    FilenameFormat.RESOLUTION_NAME_FORMAT,
                    new String[]{suffix, extension}
            );
            if (filename == null)
                return false;

            if (!ImageResourceManager.storeFile(storageDestination, filename, rescaledItem, extension))
                return false;
        }
        return true;
    }

}
