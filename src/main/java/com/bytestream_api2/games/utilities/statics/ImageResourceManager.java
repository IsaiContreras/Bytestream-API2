package com.bytestream_api2.games.utilities.statics;

import com.bytestream_api2.games.exception.imageresource.ImagePathUnresolvedException;
import com.bytestream_api2.games.exception.imageresource.UncaughtImageExtensionException;
import com.bytestream_api2.games.exception.imageresource.UnreadableResourceException;
import com.bytestream_api2.games.utilities.interfaces.ImageContentEntity;
import com.bytestream_api2.games.utilities.classes.ImageResourcePackage;
import com.bytestream_api2.games.utilities.enums.ResourcePath;
import jakarta.servlet.ServletContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Component("image_resource_manager")
public class ImageResourceManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public static boolean uploadEntityImage(
            MultipartFile imageMultiPartFile,
            ImageContentEntity entity,
            String[] addit
    ) {
        String filename;
        try {
            filename = entity.constructFilename(
                    Objects.requireNonNull(imageMultiPartFile.getContentType()).split("/")[1], addit
            );
        } catch (Exception e) { return false; }
        if (filename == null)
            return false;

        return ImageFileStorer.uploadImageFile(
                imageMultiPartFile,
                ResourcePath.getPathOfEntity(entity.getResourcePath(), entity.getSubDirectory()),
                filename
        );
    }

    public static void getURIsOfEntityImage(ImageContentEntity entity) {
        File[] files;
        try {
            files = new File(
                    Objects.requireNonNull(ResourcePath.getPathOfEntity(
                            entity.getResourcePath(),
                            entity.getSubDirectory()
                    )).toString()
            ).listFiles();
        } catch (Exception e) { return; }
        if (files == null)
            return;

        List<String> filenames = new ArrayList<>();
        for (File file : files)
            filenames.add(file.getName());

        entity.setImageURIsFromFilenames(filenames.toArray(new String[0]));
    }

    public static ImageResourcePackage getResourceImage(
            ServletContext context,
            ImageContentEntity entity,
            String filename,
            Integer width,
            Integer height
    ) throws IOException {
        // Obtiene la ruta de la imagen
        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                    ResourcePath.getPathOfEntity(entity.getResourcePath(), entity.getSubDirectory())
            ).resolve(filename);
        } catch (NullPointerException e) {
            throw new ImagePathUnresolvedException("Couldn't resolve image file path.");
        }

        // Construir Resource y ver si el archivo existe y puede leerse.
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable())
            throw new UnreadableResourceException("Resource couldn't load or is unreadable.");

        // Cargar imagen.
        BufferedImage image = ImageIO.read(filePath.toFile());

        // Obtener extensión del archivo.
        String extension = FilenameFormatter.getFileExension(filePath.toString());
        if (extension == null)
            throw new UncaughtImageExtensionException("Couldn't caught image file extension.");

        // Reescalar imagen
        image = ImageFileStorer.resizeImage(
                image,
                width != null ? width : image.getWidth(),
                height != null ? height : image.getHeight()
        );

        return new ImageResourcePackage(
                resource.getFilename(),
                context.getMimeType(filePath.toString()),
                DataConverter.imageToByteArray(image, extension)
        );
    }

}
