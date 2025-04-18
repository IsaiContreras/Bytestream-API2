package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingEntityConverter;
import com.bytestream_api2.games.mapper.GameRatingEntityMapper;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.misc.ResourcePath;
import com.bytestream_api2.games.misc.StaticResourcesPaths;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.utilities.DataConverter;
import com.bytestream_api2.games.utilities.FilenameFormatter;
import com.bytestream_api2.games.utilities.ImageResourceUploader;
import com.bytestream_api2.games.utilities.ResourcePathProvider;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.model.MGameRatingEntity;

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

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private boolean uploadLogoImage(MultipartFile imageMultiPartFile, GameRatingEntity ratingEntityData) {
        String filename;
        try {
            filename = FilenameFormatter.formatFilename(
                    FilenameFormat.ENTITY_FILE_FORMAT,
                    new String[]{
                            ratingEntityData.getId().toString().concat(ratingEntityData.getName()),
                            Objects.requireNonNull(imageMultiPartFile.getContentType()).split("/")[1]
                    }
            );
        } catch (Exception e) { return false; }
        if (filename == null)
            return false;

        return ImageResourceUploader.uploadImageFile(
                imageMultiPartFile,
                ResourcePathProvider.getPathOfEntity(
                        ResourcePath.GAME_RATING_ENTITIES, ratingEntityData.getId().toString()
                ),
                filename
        );
    }

    private void getLogoURIs(List<MGameRatingEntity> ratingEntityList) {
        for (MGameRatingEntity ratingEntityItem : ratingEntityList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                ResourcePath.GAME_RATING_ENTITIES,
                                String.valueOf(ratingEntityItem.getId())
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            if (files.length > 0)
                ratingEntityItem.setLogoURI(
                        StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.constructFullURI(
                                environment,
                                new String[] {
                                        ratingEntityItem.getName(),
                                        files[0].getName()
                                }
                        )
                );
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

            if (!this.uploadLogoImage(logoImage, newItem)) {
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
                            !this.uploadLogoImage(logoImage, ratingEntityToUpdate)
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

    public boolean delete(short id) {
        try {
            GameRatingEntity gameRatingEntity = ratingEntityRepository.findById(id);
            gameRatingEntity.setDeletedAt(new Date());

            ratingEntityRepository.save(gameRatingEntity);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean hardDelete(short id) {
        try {
            ratingEntityRepository.delete(ratingEntityRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public ResponseEntity<byte[]> getLogoImage(String name, String filename, Integer width, Integer height) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                    ResourcePath.GAME_RATING_ENTITIES,
                    String.valueOf(ratingEntity.getId())
            )).resolve(filename);
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
