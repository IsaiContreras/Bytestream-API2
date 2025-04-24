package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingConverter;
import com.bytestream_api2.games.mapper.GameRatingMapper;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.utilities.*;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private List<MGameRating> getLogoURIOfList(List<MGameRating> ratingList) {
        for (MGameRating ratingItem : ratingList)
            this.getLogoURI(ratingItem);
        return ratingList;
    }

    private MGameRating getLogoURI(MGameRating rating) {
        File[] files;
        try {
            files = new File(
                    Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                            ResourcePath.GAME_RATING,
                            String.valueOf(rating.getId())
                    )).toString()
            ).listFiles();
        } catch (Exception e) { return rating; }
        if (files == null)
            return rating;

        if (files.length > 0)
            rating.setLogoURI(
                    StaticResourcesPaths.GAME_RATING_LOGOS.constructFullURI(
                            environment,
                            new String[] {
                                    rating.getName(),
                                    files[0].getName()
                            }
                    )
            );
        return rating;
    }

    // -- PUBLIC --
    public GameRatingService(GameRatingMapper ratingMapper) {
        this.ratingMapper = ratingMapper;
    }

    // CRUD
    public ResponseEntity<Map<String, ?>> create(@NotNull GameRating rating, @NotNull MultipartFile logo) {
        GameRating newItem = null;
        try {
            rating.setGameRatingEntity(ratingEntityRepository.findByName(
                    rating.getGameRatingEntity() != null ? rating.getGameRatingEntity().getName() : null
            ));

            if (!this.uploadLogoImage(logo, rating))
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(ResponseUtility.error("Unable to upload image file."));

            GameRating result = ratingRepository.save(rating);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtility.result(this.getLogoURI(new MGameRating(result, true))));
        } catch(DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtility.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> update(@NotNull GameRating rating, MultipartFile logo) {
        try {
            GameRating ratingToUpdate = ratingRepository.findById(rating.getId());
            if (ratingToUpdate == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            ratingMapper.partialUpdateRating(ratingToUpdate, rating);

            if (
                    !logo.isEmpty() &&
                    (!this.uploadLogoImage(logo, ratingToUpdate))
            )
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(ResponseUtility.error("Unable to upload image file."));

            if (rating.getGameRatingEntity() != null)
                rating.setGameRatingEntity(
                        ratingEntityRepository.findByName(rating.getGameRatingEntity().getName())
                );

            GameRating result = ratingRepository.save(ratingToUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseUtility.result(this.getLogoURI(new MGameRating(result, true))));
        } catch(DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtility.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> delete(short id) {
        try {
            GameRating rating = ratingRepository.findById(id);
            rating.setDeletedAt(new Date());

            ratingRepository.save(rating);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result("Deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtility.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> hardDelete(short id) {
        try {
            ratingRepository.delete(ratingRepository.findById(id));
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result("Deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtility.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    // Queries
    public ResponseEntity<byte[]> getLogoImage(String name, String filename, Integer width, Integer height) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                            ResourcePathProvider
                                    .getPathOfEntity(ResourcePath.GAME_RATING, String.valueOf(rating.getId()))
            ).resolve(filename);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            BufferedImage image = ImageIO.read(filePath.toFile());
            String extension = FilenameFormatter.getFileExension(filePath.toString());

            if (extension == null)
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();

            image = ImageResourceUploader.resizeImage(
                    image,
                    width != null ? width : image.getWidth(),
                    height != null ? height : image.getHeight()
            );

            String contentType = context.getMimeType(filePath.toString());
            if (contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() + "\""
                    ).body(DataConverter.imageToByteArray(image, extension));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Map<String, ?>> getByName(String name) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null || rating.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtility.result(this.getLogoURI(new MGameRating(rating, true))));
    }

    public ResponseEntity<Map<String, ?>> getByGameRatingEntity(String name, Pageable pageable) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<MGameRating> results = ratingConverter
                .parseToList(ratingRepository.findByGameRatingEntity(ratingEntity, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtility.result("No results."));

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result(this.getLogoURIOfList(results)));
    }

    public ResponseEntity<Map<String, ?>> getAll(Pageable pageable) {
        List<MGameRating> results = ratingConverter
                .parseToList(ratingRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtility.result("No results."));

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result(this.getLogoURIOfList(results)));
    }

}
