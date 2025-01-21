package com.cyanx86.bytestream_api2.components;

import com.cyanx86.bytestream_api2.misc.DataConverter;
import com.cyanx86.bytestream_api2.misc.ImageResolution;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Component("image_resource_manager_component")
public class ImageResourceManager {

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
        Path destination, String filename, BufferedImage image, String extension
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

}
