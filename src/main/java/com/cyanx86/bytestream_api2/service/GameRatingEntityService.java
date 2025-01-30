package com.cyanx86.bytestream_api2.service;

import com.cyanx86.bytestream_api2.utilities.FilenameFormatter;
import com.cyanx86.bytestream_api2.utilities.ImageResourceManager;
import com.cyanx86.bytestream_api2.utilities.ResourcePathProvider;
import com.cyanx86.bytestream_api2.converter.GameRatingEntityConverter;
import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import com.cyanx86.bytestream_api2.misc.*;
import com.cyanx86.bytestream_api2.model.MGameRatingEntity;
import com.cyanx86.bytestream_api2.repository.GameRatingEntityRepository;
import com.cyanx86.bytestream_api2.utilities.DataConverter;
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

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@Service("game_rating_entity_service")
public class GameRatingEntityService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private ServletContext context;

    @Autowired
    private Environment environment;

    // Entity Components
    @Autowired
    @Qualifier("game_rating_entity_repository")
    private GameRatingEntityRepository ratingEntityRepository;

    @Autowired
    @Qualifier("game_rating_entity_converter")
    private GameRatingEntityConverter ratingEntityConverter;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingEntityService.class);

    private final int logoResolutionConfiguration = ImageResolution.ALL_RESOLUTIONS_VALUE;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private boolean uploadEntityLogo(MultipartFile image, UUID id) {
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
        Path storageDestination = ResourcePathProvider.getPathOfEntity(
                ResourcePaths.GAME_RATING_ENTITIES, id.toString()
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

    private void getLogoURIs(List<MGameRatingEntity> ratingEntityList) {
        for (MGameRatingEntity ratingEntityItem : ratingEntityList) {
            ImageResolution[] resolutionValues = ImageResolution.values();
            for (int i = 1; i < resolutionValues.length; i++) {
                if (
                        ImageResolution.hasFlag(logoResolutionConfiguration, ImageResolution.ALL_RESOLUTIONS) ||
                        ImageResolution.hasFlag(logoResolutionConfiguration, resolutionValues[i])
                ) {
                    String filename = FilenameFormatter.formatFilename(
                            FilenameFormat.RESOLUTION_NAME_FORMAT,
                            new String[]{
                                    resolutionValues[i].getSuffix(),
                                    "png"
                            }
                    );
                    if (filename == null)
                        continue;

                    String finalURI = StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.constructFullURI(
                            environment,
                            new String[] {
                                    ratingEntityItem.getName(),
                                    filename
                            }
                    );

                    if (finalURI != null)
                        ratingEntityItem.getLogoURIList().add(finalURI);
                }
            }
        }
    }

    // -- PUBLIC --
    public boolean create(GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        GameRatingEntity newItem = null;
        try {
            ratingEntityRepository.save(gameRatingEntity);
            newItem = ratingEntityRepository.findByName(gameRatingEntity.getName());

            if (!this.uploadEntityLogo(logoImage, newItem.getId())) {
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
            if (!this.uploadEntityLogo(logoImage, gameRatingEntity.getId()))
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
        this.getLogoURIs(results);
        return results.stream().filter(item -> item.getDeletedAt() == null).toList();
    }

    public MGameRatingEntity getByName(String name) {
        GameRatingEntity queriedRatingEntity = ratingEntityRepository.findByName(name);
        if (queriedRatingEntity.getDeletedAt() != null)
            return null;
        MGameRatingEntity mRatingEntity = new MGameRatingEntity(queriedRatingEntity);
        this.getLogoURIs(List.of(mRatingEntity));
        return mRatingEntity;
    }

    public ResponseEntity<Resource> getLogoImage(String name, String filename) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                        ResourcePathProvider
                            .getPathOfEntity(ResourcePaths.GAME_RATING_ENTITIES, ratingEntity.getId().toString())
                    )
                    .resolve(filename);
        } catch(Exception e) {
                return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable())
                return ResponseEntity.notFound().build();

            String contentType = context.getMimeType(filePath.toString());
            if (contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                            resource.getFilename() + "\""
                    )
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
