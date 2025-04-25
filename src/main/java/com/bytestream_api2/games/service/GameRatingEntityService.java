package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingEntityConverter;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.mapper.GameRatingEntityMapper;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.misc.ResourcePath;
import com.bytestream_api2.games.misc.StaticResourcesPaths;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.utilities.*;
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

    private List<MGameRatingEntity> getLogoURIOfList(List<MGameRatingEntity> ratingEntityList) {
        for (MGameRatingEntity ratingEntityItem : ratingEntityList)
            this.getLogoURI(ratingEntityItem);
        return ratingEntityList;
    }

    private MGameRatingEntity getLogoURI(MGameRatingEntity ratingEntity) {
        File[] files;
        try {
            files = new File(
                    Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                            ResourcePath.GAME_RATING_ENTITIES,
                            String.valueOf(ratingEntity.getId())
                    )).toString()
            ).listFiles();
        } catch (Exception e) { return ratingEntity; }
        if (files == null)
            return ratingEntity;

        if (files.length > 0)
            ratingEntity.setLogoURI(
                    StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.constructFullURI(
                            environment,
                            new String[] {
                                    ratingEntity.getName(),
                                    files[0].getName()
                            }
                    )
            );

        return ratingEntity;
    }

    // -- PUBLIC --
    @Autowired
    public GameRatingEntityService(GameRatingEntityMapper ratingEntityMapper) {
        this.ratingEntityMapper = ratingEntityMapper;
    }

    // CUD
    public MGameRatingEntity create(
            @NotNull GameRatingEntity gameRatingEntity, @NotNull MultipartFile logoImage
    ) {
        if (!this.uploadLogoImage(logoImage, gameRatingEntity))
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        return this.getLogoURI(new MGameRatingEntity(ratingEntityRepository.save(gameRatingEntity)));
    }

    public MGameRatingEntity update(@NotNull GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        GameRatingEntity ratingEntityToUpdate = ratingEntityRepository.findById(gameRatingEntity.getId());
        if (ratingEntityToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this ID.");

        if (!logoImage.isEmpty() && (!this.uploadLogoImage(logoImage, ratingEntityToUpdate)))
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        ratingEntityMapper.partialUpdateRatingEntity(ratingEntityToUpdate, gameRatingEntity);

        return this.getLogoURI(new MGameRatingEntity(ratingEntityRepository.save(ratingEntityToUpdate)));
    }

    public void delete(short id) {
        GameRatingEntity gameRatingEntity = ratingEntityRepository.findById(id);
        if (gameRatingEntity == null || gameRatingEntity.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this ID.");

        gameRatingEntity.setDeletedAt(new Date());

        ratingEntityRepository.save(gameRatingEntity);
    }

    public void hardDelete(short id) {
        ratingEntityRepository.delete(ratingEntityRepository.findById(id));
    }

    // Queries
    public ResponseEntity<byte[]> getLogoImage(String name, String filename, Integer width, Integer height) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                    ResourcePath.GAME_RATING_ENTITIES,
                    String.valueOf(ratingEntity.getId())
            )).resolve(filename);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            BufferedImage image = ImageIO.read(filePath.toFile());
            String extension = FilenameFormatter.getFileExension(filePath.toString());

            if(extension == null)
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public MGameRatingEntity getByName(String name) {
        GameRatingEntity ratingEntity = ratingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this name.");

        return this.getLogoURI(new MGameRatingEntity(ratingEntity));
    }

    public List<MGameRatingEntity> getAll(Pageable pageable) {
        List<MGameRatingEntity> results = ratingEntityConverter
                .parseToList(ratingEntityRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return this.getLogoURIOfList(results);
    }

}
