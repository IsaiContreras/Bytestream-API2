package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingConverter;
import com.bytestream_api2.games.mapper.GameRatingMapper;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.utilities.DataConverter;
import com.bytestream_api2.games.utilities.FilenameFormatter;
import com.bytestream_api2.games.utilities.ImageResourceUploader;
import com.bytestream_api2.games.utilities.ResourcePathProvider;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.misc.ResourcePath;
import com.bytestream_api2.games.misc.StaticResourcesPaths;
import com.bytestream_api2.games.model.MGameRating;

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

import javax.imageio.ImageIO;
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

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private boolean uploadLogoImage(MultipartFile imageMultipartFile, GameRating gameRatingData) {
        String filename;
        try {
            filename = FilenameFormatter.formatFilename(
                    FilenameFormat.ENTITY_FILE_FORMAT,
                    new String[]{
                            gameRatingData.getId().toString().concat(gameRatingData.getName()),
                            Objects.requireNonNull(imageMultipartFile.getContentType()).split("/")[1]
                    }
            );
        } catch (Exception e) { return false; }
        if (filename == null)
            return false;

        return ImageResourceUploader.uploadImageFile(
                imageMultipartFile,
                ResourcePathProvider.getPathOfEntity(
                        ResourcePath.GAME_RATING, gameRatingData.getId().toString()
                ),
                filename
        );
    }

    private void getLogoURI(List<MGameRating> ratingList) {
        for (MGameRating ratingItem : ratingList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                ResourcePath.GAME_RATING,
                                String.valueOf(ratingItem.getId())
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            if (files.length > 0)
                ratingItem.setLogoURI(
                        StaticResourcesPaths.GAME_RATING_LOGOS.constructFullURI(
                                environment,
                                new String[] {
                                        ratingItem.getName(),
                                        files[0].getName()
                                }
                        )
                );
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

            if (!this.uploadLogoImage(logo, newItem)) {
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
                    (ratingToUpdate == null || !this.uploadLogoImage(logo, ratingToUpdate))
            )
                return false;

            if (rating.getGameRatingEntity() != null)
                rating.setGameRatingEntity(
                        ratingEntityRepository.findByName(rating.getGameRatingEntity().getName())
                );

            ratingMapper.partialUpdateRating(ratingToUpdate, rating);

            ratingRepository.save(ratingToUpdate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(short id) {
        try {
            GameRating rating = ratingRepository.findById(id);
            rating.setDeletedAt(new Date());

            ratingRepository.save(rating);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hardDelete(short id) {
        try {
            ratingRepository.delete(ratingRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public ResponseEntity<byte[]> getLogoImage(String name, String filename, Integer width, Integer height) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                            ResourcePathProvider
                                    .getPathOfEntity(ResourcePath.GAME_RATING, String.valueOf(rating.getId()))
            ).resolve(filename);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable())
                return ResponseEntity.notFound().build();

            BufferedImage image = ImageIO.read(filePath.toFile());
            String extension = FilenameFormatter.getFileExension(filePath.toString());

            image = ImageResourceUploader.resizeImage(
                    image,
                    width != null ? width : image.getWidth(),
                    height != null ? height : image.getHeight()
            );

            String contentType = context.getMimeType(filePath.toString());
            if (contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() + "\""
                    ).body(DataConverter.imageToByteArray(image, extension));
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public MGameRating getByName(String name) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null || rating.getDeletedAt() != null)
            return null;

        MGameRating mRating = new MGameRating(rating, true);
        this.getLogoURI(List.of(mRating));
        return mRating;
    }

    public List<MGameRating> getByGameRatingEntity(String name, Pageable pageable) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            return null;

        List<MGameRating> results = ratingConverter.parseToList(
                ratingRepository.findByGameRatingEntity(ratingEntity, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        this.getLogoURI(results);
        return results;
    }

    public List<MGameRating> getAll(Pageable pageable) {
        List<MGameRating> results = ratingConverter.parseToList(
                ratingRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        this.getLogoURI(results);
        return results;
    }

}
