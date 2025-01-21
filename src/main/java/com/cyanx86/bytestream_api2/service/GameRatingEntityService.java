package com.cyanx86.bytestream_api2.service;

import com.cyanx86.bytestream_api2.components.FilenameFormatter;
import com.cyanx86.bytestream_api2.components.ImageResourceManager;
import com.cyanx86.bytestream_api2.components.MediaPaths;
import com.cyanx86.bytestream_api2.converter.GameRatingEntityConverter;
import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import com.cyanx86.bytestream_api2.misc.DataConverter;
import com.cyanx86.bytestream_api2.misc.FilenameFormat;
import com.cyanx86.bytestream_api2.misc.ImageResolution;
import com.cyanx86.bytestream_api2.misc.MediaEntity;
import com.cyanx86.bytestream_api2.model.MGameRatingEntity;
import com.cyanx86.bytestream_api2.repository.GameRatingEntityRepository;
import jakarta.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@Service("game_rating_entity_service")
public class GameRatingEntityService {

    @Autowired
    @Qualifier("game_rating_entity_repository")
    private GameRatingEntityRepository ratingEntityRepository;

    @Autowired
    @Qualifier("game_rating_entity_converter")
    private GameRatingEntityConverter ratingEntityConverter;

    @Autowired
    private ServletContext context;

    private Environment environment;

    @Autowired
    @Qualifier("media_paths_component")
    private MediaPaths mediaPaths;

    @Autowired
    @Qualifier("image_resource_manager_component")
    private ImageResourceManager imageResourceManager;

    @Autowired
    @Qualifier("filename_formatter_component")
    private FilenameFormatter filenameFormatter;

    @Autowired
    public GameRatingEntityService(Environment environment) {
        this.environment = environment;
    }

    private int logoResolutionConfiguration = ImageResolution.ALL_RESOLUTIONS_VALUE;

    private static final Log logger = LogFactory.getLog(GameRatingEntityService.class);

    public boolean create(GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        GameRatingEntity newItem = null;
        try {
            ratingEntityRepository.save(gameRatingEntity);
            newItem = ratingEntityRepository.findByName(gameRatingEntity.getName());

            if (!this.uploadEntityLogo(logoImage, newItem.getId(), gameRatingEntity.getName())) {
                ratingEntityRepository.delete(newItem);
                return false;
            }

            return true;
        } catch(Exception e) {
            if (newItem != null) ratingEntityRepository.delete(newItem);
            return false;
        }
    }

    public boolean update(GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        try {
            if (!this.uploadEntityLogo(logoImage, gameRatingEntity.getId(), gameRatingEntity.getName()))
                return false;
            ratingEntityRepository.save(gameRatingEntity);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean delete(UUID id) {
        try {
            GameRatingEntity gameRatingEntity = ratingEntityRepository.findById(id);
            gameRatingEntity.setDeletedAt(new Date());
            ratingEntityRepository.save(gameRatingEntity);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public List<MGameRatingEntity> getAll(Pageable pageable) {
        List<MGameRatingEntity> results = ratingEntityConverter
                .parseToList(ratingEntityRepository.findAll(pageable).getContent());
        this.getEntityLogo(results);
        return results.stream().filter(item -> item.getDeletedAt() == null).toList();
    }

    public MGameRatingEntity getByName(String name) {
        GameRatingEntity queriedRatingEntity = ratingEntityRepository.findByName(name);
        if (queriedRatingEntity.getDeletedAt() != null)
            return null;
        MGameRatingEntity mRatingEntity = new MGameRatingEntity(queriedRatingEntity);
        this.getEntityLogo(List.of(mRatingEntity));
        return mRatingEntity;
    }

    public ResponseEntity<Resource> getImage(String name, String extension, String resolution) {
        ImageResolution imageResolution = Arrays.stream(ImageResolution.values())
                .filter(res -> res.getSuffix().equals(resolution))
                .findFirst()
                .orElse(null);
        if (imageResolution == null)
            return ResponseEntity.notFound().build();

        GameRatingEntity gameRatingEntity = ratingEntityRepository.findByName(name);
        if (gameRatingEntity == null)
            return ResponseEntity.notFound().build();

        try {
            Path filepath = mediaPaths
                    .getPathOfEntity(MediaEntity.GAME_RATING_ENTITIES, gameRatingEntity.getId().toString())
                    .resolve(filenameFormatter.formatFilename(
                            FilenameFormat.ENTITY_FILE_FORMAT,
                            new String[]{gameRatingEntity.getName(), imageResolution.getSuffix(), extension}
                    ));
            Resource resource = new UrlResource(filepath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = context.getMimeType(filepath.toString());
                if (contentType == null)
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\""
                        )
                        .body(resource);
            }
            else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private boolean uploadEntityLogo(MultipartFile image, UUID id, String name) {
        BufferedImage originalImage;
        try {
            originalImage = DataConverter.byteArrayToImage(image.getBytes());
        } catch (Exception e) { return false; }

        BufferedImage[] rescaledImages;
        try {
            rescaledImages = ImageResourceManager.getResizedImageInstances(
                    originalImage,
                    this.logoResolutionConfiguration
            );
        } catch (Exception e) { return false; }

        String extension = Objects.requireNonNull(image.getContentType()).split("/")[1];
        Path storageDestination = mediaPaths.getPathOfEntity(MediaEntity.GAME_RATING_ENTITIES, id.toString());

        for (BufferedImage rescaledItem : rescaledImages) {
            String filename = filenameFormatter.formatFilename(
                    FilenameFormat.ENTITY_FILE_FORMAT,
                    new String[]{
                            name,
                            (rescaledItem.getWidth() == originalImage.getWidth()) ?
                                    "full" : rescaledItem.getWidth() + "w",
                            extension
                    }
            );
            if (!ImageResourceManager.storeFile(storageDestination, filename, rescaledItem, extension))
                return false;
        }
        return true;
    }

    private void getEntityLogo(List<MGameRatingEntity> ratingEntityList) {
        for (MGameRatingEntity ratingEntityItem : ratingEntityList) {
            ImageResolution[] resolutionsValues = ImageResolution.values();
            for (int i = 1; i < resolutionsValues.length; i++) {
                String finalURI = InetAddress.getLoopbackAddress().getHostAddress() + ":" +
                        environment.getProperty("server.port") + "/rating_entity/media/" +
                        ratingEntityItem.getName() + "/" + resolutionsValues[i].getSuffix() + "/" +
                        "png";
                ratingEntityItem.getLogoURIList().add(finalURI);
            }
        }
    }

}
