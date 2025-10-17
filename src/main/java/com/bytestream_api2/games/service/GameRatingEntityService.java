package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingEntityConverter;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.mapper.GameRatingEntityMapper;
import com.bytestream_api2.games.misc.ImageResourcePackage;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.utilities.*;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.model.MGameRatingEntity;

import jakarta.servlet.ServletContext;

import jakarta.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;

@Service("game_rating_entity_service")
public class GameRatingEntityService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private ServletContext context;

    // Entity Components
    private final GameRatingEntityRepository gameRatingEntityRepository;
    private final GameRatingEntityConverter gameRatingEntityConverter;
    private final GameRatingEntityMapper gameRatingEntityMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingEntityService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameRatingEntityService(
            @Qualifier("game_rating_entity_repository") GameRatingEntityRepository gameRatingEntityRepository,
            @Qualifier("game_rating_entity_converter") GameRatingEntityConverter gameRatingEntityConverter,
            GameRatingEntityMapper gameRatingEntityMapper
    ) {
        this.gameRatingEntityRepository = gameRatingEntityRepository;
        this.gameRatingEntityConverter = gameRatingEntityConverter;
        this.gameRatingEntityMapper = gameRatingEntityMapper;
    }

    // CUD
    @Transactional
    public MGameRatingEntity create(
            @NotNull GameRatingEntity gameRatingEntity, @NotNull MultipartFile logoImage
    ) {
        MGameRatingEntity result = new MGameRatingEntity(gameRatingEntityRepository.save(gameRatingEntity));

        if (logoImage.isEmpty() || !ImageResourceManager.uploadEntityImage(logoImage, result, null))
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    @Transactional
    public MGameRatingEntity update(@NotNull GameRatingEntity gameRatingEntity, MultipartFile logoImage) {
        GameRatingEntity ratingEntityToUpdate = gameRatingEntityRepository.findById(gameRatingEntity.getId());
        if (ratingEntityToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this ID.");

        gameRatingEntityMapper.partialUpdateRatingEntity(ratingEntityToUpdate, gameRatingEntity);

        MGameRatingEntity result = new MGameRatingEntity(gameRatingEntityRepository.save(ratingEntityToUpdate));
        if (
                !logoImage.isEmpty() &&
                !ImageResourceManager.uploadEntityImage(logoImage, result, null)
        )
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    public void delete(short id) {
        GameRatingEntity gameRatingEntity = gameRatingEntityRepository.findById(id);
        if (gameRatingEntity == null || gameRatingEntity.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this ID.");

        gameRatingEntity.setDeletedAt(new Date());
        gameRatingEntityRepository.save(gameRatingEntity);
    }

    public void hardDelete(short id) {
        gameRatingEntityRepository.delete(gameRatingEntityRepository.findById(id));
    }

    // Queries
    public ImageResourcePackage getLogoImage(String name, String filename, Integer width, Integer height)
            throws IOException {
        GameRatingEntity ratingEntity = gameRatingEntityRepository.findByName(name);
        if (ratingEntity == null)
            throw new EntityNotFoundException("Couldn't find a Game with this name.");

        return ImageResourceManager.getResourceImage(
                context,
                (ImageContentEntity)ratingEntity,
                filename,
                width,
                height
        );
    }

    public MGameRatingEntity getByName(String name) {
        GameRatingEntity ratingEntity = gameRatingEntityRepository.findByName(name);
        if (ratingEntity == null || ratingEntity.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating entity with this name.");

        MGameRatingEntity result = new MGameRatingEntity(ratingEntity);
        ImageResourceManager.getURIsOfEntityImage(result);

        return result;
    }

    public List<MGameRatingEntity> getAll(Pageable pageable) {
        List<MGameRatingEntity> results = gameRatingEntityConverter
                .parseToList(gameRatingEntityRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

}
