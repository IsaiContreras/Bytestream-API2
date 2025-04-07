package com.cyanx86.bytestream_api2.service;

import com.cyanx86.bytestream_api2.component.GameRatingMapper;
import com.cyanx86.bytestream_api2.converter.GameRatingConverter;
import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import com.cyanx86.bytestream_api2.misc.FilenameFormat;
import com.cyanx86.bytestream_api2.misc.ImageResolution;
import com.cyanx86.bytestream_api2.misc.ResourcePath;
import com.cyanx86.bytestream_api2.misc.StaticResourcesPaths;
import com.cyanx86.bytestream_api2.model.MGameRating;
import com.cyanx86.bytestream_api2.repository.GameRatingEntityRepository;
import com.cyanx86.bytestream_api2.repository.GameRatingRepository;
import com.cyanx86.bytestream_api2.utilities.DataConverter;
import com.cyanx86.bytestream_api2.utilities.FilenameFormatter;
import com.cyanx86.bytestream_api2.utilities.ImageResourceManager;
import com.cyanx86.bytestream_api2.utilities.ResourcePathProvider;

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

@Service("game_rating_service")
public class GameRatingService {

    // -- [[ ATTRIBUTE ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private ServletContext context;

    @Autowired
    private Environment environment;

    // Entity Components
    @Autowired
    @Qualifier("game_rating_repository")
    private GameRatingRepository ratingRepository;

    @Autowired
    @Qualifier("game_rating_entity_repository")
    private GameRatingEntityRepository ratingEntityRepository;

    @Autowired
    @Qualifier("game_rating_converter")
    private GameRatingConverter ratingConverter;

    private final GameRatingMapper ratingMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingService.class);

    private final int logoResolutionConfiguration =
            ImageResolution.WIDTH_32_VALUE | ImageResolution.WIDTH_256_VALUE | ImageResolution.ORIGINAL_WIDTH_VALUE;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    private void getLogoURIs(List<MGameRating> ratingList) {
        for (MGameRating ratingItem : ratingList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                ResourcePath.GAME_RATING,
                                ratingItem.getId().toString()
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            for(File fileItem : files) {
                ratingItem.getLogoURIList().add(
                        StaticResourcesPaths.GAME_RATING_LOGOS.constructFullURI(
                                environment,
                                new String[] {
                                        ratingItem.getName(),
                                        fileItem.getName()
                                }
                        )
                );
            }
        }
    }

    // -- PUBLIC --
    public GameRatingService(GameRatingMapper ratingMapper) {
        this.ratingMapper = ratingMapper;
    }

    // CRUD
    public boolean create(@NotNull GameRating rating, @NotNull MultipartFile logo) {
        GameRating newItem = null;
        try {
            rating.setGameRatingEntity(ratingEntityRepository.findByName(
                    rating.getGameRatingEntity() != null ? rating.getGameRatingEntity().getName() : null
            ));

            newItem = ratingRepository.save(rating);

            if (!ImageResourceManager.uploadImageFile(
                    logo, newItem.getId(), ResourcePath.GAME_RATING, this.logoResolutionConfiguration
            )) {
                ratingRepository.delete(newItem);
                return false;
            }
            return true;
        } catch (Exception e) {
            if (newItem != null) ratingRepository.delete(newItem);
            return false;
        }
    }

    public boolean update(@NotNull GameRating rating, MultipartFile logo) {
        try {
            GameRating ratingToUpdate = ratingRepository.findById(rating.getId());
            if (
                    !logo.isEmpty() &&
                    (ratingToUpdate == null || !ImageResourceManager.uploadImageFile(
                            logo,
                            ratingToUpdate.getId(),
                            ResourcePath.GAME_RATING,
                            this.logoResolutionConfiguration
                    ))
            )
                return false;

            rating.setGameRatingEntity(ratingEntityRepository.findByName(
                    rating.getGameRatingEntity() != null ? rating.getGameRatingEntity().getName() : null
            ));

            ratingMapper.partialUpdateRating(ratingToUpdate, rating);

            ratingRepository.save(ratingToUpdate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(UUID id) {
        try {
            GameRating rating = ratingRepository.findById(id);
            rating.setDeletedAt(new Date());

            ratingRepository.save(rating);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hardDelete(UUID id) {
        try {
            ratingRepository.delete(ratingRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public ResponseEntity<Resource> getLogoImage(String name, String filename) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                            ResourcePathProvider
                                    .getPathOfEntity(ResourcePath.GAME_RATING, rating.getId().toString())
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
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public MGameRating getByName(String name) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null || rating.getDeletedAt() != null)
            return null;

        MGameRating mRating = new MGameRating(rating, true);
        this.getLogoURIs(List.of(mRating));
        return mRating;
    }

    public List<MGameRating> getByGameRatingEntity(String name, Pageable pageable) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            return null;

        List<MGameRating> results = ratingConverter.parseToList(
                ratingRepository.findByGameRatingEntity(ratingEntity, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        this.getLogoURIs(results);
        return results;
    }

    public List<MGameRating> getAll(Pageable pageable) {
        List<MGameRating> results = ratingConverter.parseToList(
                ratingRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        this.getLogoURIs(results);
        return results;
    }

}
