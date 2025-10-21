package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingConverter;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.mapper.GameRatingMapper;
import com.bytestream_api2.games.misc.ImageResourcePackage;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.utilities.*;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.model.MGameRating;

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

@Service("game_rating_service")
public class GameRatingService {

    // -- [[ ATTRIBUTE ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private ServletContext context;

    // Entity Components
    private final GameRatingRepository gameRatingRepository;
    private final GameRatingEntityRepository gameRatingEntityRepository;
    private final GameRatingConverter gameRatingConverter;
    private final GameRatingMapper gameRatingMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRatingService(
            @Qualifier("game_rating_repository") GameRatingRepository gameRatingRepository,
            @Qualifier("game_rating_entity_repository") GameRatingEntityRepository gameRatingEntityRepository,
            @Qualifier("game_rating_converter") GameRatingConverter gameRatingEntityConverter,
            GameRatingMapper gameRatingMapper,
            @Qualifier("image_resource_manager") ImageResourceManager ImageResourceManager
    ) {
        this.gameRatingRepository = gameRatingRepository;
        this.gameRatingEntityRepository = gameRatingEntityRepository;
        this.gameRatingConverter = gameRatingEntityConverter;
        this.gameRatingMapper = gameRatingMapper;
    }

    // CRUD
    @Transactional
    public MGameRating create(@NotNull GameRating rating, @NotNull MultipartFile logoImage) {
        rating.setGameRatingEntity(gameRatingEntityRepository.findByName(
                rating.getGameRatingEntity() != null ? rating.getGameRatingEntity().getName() : null
        ));

        MGameRating result = new MGameRating(gameRatingRepository.save(rating), true);

        if (logoImage.isEmpty() || !ImageResourceManager.uploadEntityImage(logoImage, result, null))
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    @Transactional
    public MGameRating update(@NotNull GameRating rating, MultipartFile logoImage) {
        GameRating ratingToUpdate = gameRatingRepository.findById(rating.getId());
        if (ratingToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating with this ID.");

        if (rating.getGameRatingEntity() != null)
            rating.setGameRatingEntity(gameRatingEntityRepository.findByName(rating.getGameRatingEntity().getName()));

        gameRatingMapper.partialUpdateRating(ratingToUpdate, rating);

        MGameRating result = new MGameRating(gameRatingRepository.save(ratingToUpdate), true);
        if (
                logoImage != null && !(!logoImage.isEmpty() &&
                ImageResourceManager.uploadEntityImage(logoImage, result, null))
        )
            throw new MediaUploadFailedException("Couldn't upload Logo image file.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    public void delete(short id) {
        GameRating rating = gameRatingRepository.findById(id);
        if (rating == null || rating.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating with this ID");

        rating.setDeletedAt(new Date());

        gameRatingRepository.save(rating);
    }

    public void hardDelete(short id) {
        gameRatingRepository.delete(gameRatingRepository.findById(id));
    }

    // Queries
    public ImageResourcePackage getLogoImage(String name, String filename, Integer width, Integer height)
            throws IOException {
        GameRating rating = gameRatingRepository.findByName(name);
        if (rating == null)
            throw new EntityNotFoundException("Couldn't find a Game with this name.");

        MGameRating result = new MGameRating(rating, false);
        return ImageResourceManager.getResourceImage(context, result, filename, width, height);
    }

    public MGameRating getByName(String name) {
        GameRating rating = gameRatingRepository.findByName(name);
        if (rating == null || rating.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating with this name.");

        MGameRating result = new MGameRating(rating, true);
        ImageResourceManager.getURIsOfEntityImage(result);

        return result;
    }

    public List<MGameRating> getByGameRatingEntity(String name, Pageable pageable) {
        List<MGameRating> results = gameRatingConverter.parseToList(
                gameRatingRepository.findByGameRatingEntity(
                        gameRatingEntityRepository.findByName(name), pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

    public List<MGameRating> getAll(Pageable pageable) {
        List<MGameRating> results = gameRatingConverter
                .parseToList(gameRatingRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

}
