package com.cyanx86.bytestream_api2.service;

import com.cyanx86.bytestream_api2.component.GameRatingEntityMapper;
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

import org.jetbrains.annotations.NotNull;
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
import java.io.File;
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

    private final GameRatingEntityMapper ratingEntityMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingEntityService.class);

    private final int logoResolutionConfiguration = ImageResolution.ALL_RESOLUTIONS_VALUE;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private void getLogoURIs(List<MGameRatingEntity> ratingEntityList) {
        for (MGameRatingEntity ratingEntityItem : ratingEntityList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                ResourcePath.GAME_RATING_ENTITIES,
                                ratingEntityItem.getId().toString()
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            for (File fileItem : files) {
                ratingEntityItem.getLogoURIList().add(
                        StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.constructFullURI(
                                environment,
                                new String[] {
                                        ratingEntityItem.getName(),
                                        fileItem.getName()
                                }
                        )
                );
            }
        }
    }

    // -- PUBLIC --
    @Autowired
    public GameRatingEntityService(GameRatingEntityMapper ratingEntityMapper) {
        this.ratingEntityMapper = ratingEntityMapper;
    }

    // CUD
    public boolean create(@NotNull GameRatingEntity gameRatingEntity, @NotNull MultipartFile logoImage) {
        GameRatingEntity newItem = null;
        try {
            newItem = ratingEntityRepository.save(gameRatingEntity);

            if (!ImageResourceManager.uploadImageFile(
                    logoImage, newItem.getId(), ResourcePath.GAME_RATING_ENTITIES, this.logoResolutionConfiguration
            )) {
                ratingEntityRepository.delete(newItem);
                return false;
            }
            return true;
        } catch(Exception e) {
            if (newItem != null)
                ratingEntityRepository.delete(newItem);
            return false;
        }
    }

    public boolean update(@NotNull GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        try {
            GameRatingEntity ratingEntityToUpdate = ratingEntityRepository.findById(gameRatingEntity.getId());

            if (
                    !logoImage.isEmpty() &&
                    (
                            ratingEntityToUpdate == null ||
                            !ImageResourceManager.uploadImageFile(
                                    logoImage,
                                    ratingEntityToUpdate.getId(),
                                    ResourcePath.GAME_RATING_ENTITIES,
                                    this.logoResolutionConfiguration
                            )
                    )
            )
                return false;

            ratingEntityMapper.partialUpdateRatingEntity(ratingEntityToUpdate, gameRatingEntity);

            ratingEntityRepository.save(ratingEntityToUpdate);
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

    public boolean hardDelete(UUID id) {
        try {
            ratingEntityRepository.delete(ratingEntityRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public ResponseEntity<Resource> getLogoImage(String name, String filename) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                    ResourcePath.GAME_RATING_ENTITIES,
                    ratingEntity.getId().toString())
            ).resolve(filename);
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
                    ).body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public MGameRatingEntity getByName(String name) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            return null;

        MGameRatingEntity mRatingEntity = new MGameRatingEntity(ratingEntity);
        this.getLogoURIs(List.of(mRatingEntity));
        return mRatingEntity;
    }

    public List<MGameRatingEntity> getAll(Pageable pageable) {
        List<MGameRatingEntity> results = ratingEntityConverter
                .parseToList(ratingEntityRepository.findAll(pageable).getContent());
        this.getLogoURIs(results);
        return results.stream().filter(item -> item.getDeletedAt() == null).toList();
    }

}
