package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingConverter;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.mapper.GameRatingMapper;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.utilities.*;
import com.bytestream_api2.games.entity.GameRating;
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
    public MGameRating create(@NotNull GameRating rating, @NotNull MultipartFile logo) {
        rating.setGameRatingEntity(ratingEntityRepository.findByName(
                rating.getGameRatingEntity() != null ? rating.getGameRatingEntity().getName() : null
        ));

        if (!this.uploadLogoImage(logo, rating))
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        return this.getLogoURI(new MGameRating(ratingRepository.save(rating), true));
    }

    public MGameRating update(@NotNull GameRating rating, MultipartFile logo) {
        GameRating ratingToUpdate = ratingRepository.findById(rating.getId());
        if (ratingToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating with this ID.");

        if (
                !logo.isEmpty() &&
                (!this.uploadLogoImage(logo, ratingToUpdate))
        )
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        if (rating.getGameRatingEntity() != null)
            rating.setGameRatingEntity(
                    ratingEntityRepository.findByName(rating.getGameRatingEntity().getName())
            );

        ratingMapper.partialUpdateRating(ratingToUpdate, rating);

        return this.getLogoURI(new MGameRating(ratingRepository.save(ratingToUpdate), true));
    }

    public void delete(short id) {
        GameRating rating = ratingRepository.findById(id);
        if (rating == null || rating.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating with this ID");

        rating.setDeletedAt(new Date());

        ratingRepository.save(rating);
    }

    public void hardDelete(short id) {
        ratingRepository.delete(ratingRepository.findById(id));
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

    public MGameRating getByName(String name) {
        GameRating rating = ratingRepository.findByName(name);
        if (rating == null || rating.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating with this name.");

        return this.getLogoURI(new MGameRating(rating, true));
    }

    public List<MGameRating> getByGameRatingEntity(String name, Pageable pageable) {
        List<MGameRating> results = ratingConverter
                .parseToList(ratingRepository.findByGameRatingEntity(
                        ratingEntityRepository.findByName(name), pageable
                ).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return this.getLogoURIOfList(results);
    }

    public List<MGameRating> getAll(Pageable pageable) {
        List<MGameRating> results = ratingConverter
                .parseToList(ratingRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return this.getLogoURIOfList(results);
    }

}
