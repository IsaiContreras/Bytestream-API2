package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameConverter;
import com.bytestream_api2.games.entity.*;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.InvalidEntityRelationsException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.enums.GameArtType;
import com.bytestream_api2.games.misc.ImageResourcePackage;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.repository.GameRepository;
import com.bytestream_api2.games.utilities.*;

import jakarta.servlet.ServletContext;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bytestream_api2.games.mapper.GameMapper;
import com.bytestream_api2.games.model.MGame;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;

@Service("game_service")
public class GameService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Server Environment Resources
    @Autowired
    private ServletContext context;

    // Entity Components
    private final GameRepository gameRepository;
    private final GameCategoryRepository gameCategoryRepository;
    private final GameRatingRepository gameRatingRepository;
    private final GameRatingDescriptorRepository gameRatingDescriptorRepository;
    private final GameConverter gameConverter;
    private final GameMapper gameMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameService(
            @Qualifier("game_repository") GameRepository gameRepository,
            @Qualifier("game_category_repository") GameCategoryRepository gameCategoryRepository,
            @Qualifier("game_rating_repository") GameRatingRepository gameRatingRepository,
            @Qualifier("game_rating_descriptor_repository") GameRatingDescriptorRepository gameRatingDescriptorRepository,
            @Qualifier("game_converter") GameConverter gameConverter,
            GameMapper gameMapper
    ) {
        this.gameRepository = gameRepository;
        this.gameCategoryRepository = gameCategoryRepository;
        this.gameRatingRepository = gameRatingRepository;
        this.gameRatingDescriptorRepository = gameRatingDescriptorRepository;
        this.gameConverter = gameConverter;
        this.gameMapper = gameMapper;
    }

    // CUD
    @Transactional
    public MGame create(
            @NotNull Game game,
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        EntityResolver.resolveGameEntities(
                gameCategoryRepository,
                gameRatingRepository,
                gameRatingDescriptorRepository,
                game
        );

        // Check if it has at least one GameCategory.
        if (game.getGameCategories() == null || game.getGameCategories().isEmpty())
            throw new InvalidEntityRelationsException("Game must have at least one related category.");

        // Check if there is no more than one GameRating from a RatingEntity assigned to this Game.
        if (game.getGameRatings() != null)
            for (
                    GameRatingEntity ratingEntityItem :
                    game.getGameRatings().stream().map(GameRating::getGameRatingEntity).toList()
            ) {
                if (
                        game.getGameRatings().stream().filter(
                                item -> Objects.equals(
                                        item.getGameRatingEntity().getName(),
                                        ratingEntityItem.getName())
                        ).count() > 1
                )
                    throw new InvalidEntityRelationsException(
                            "Game must be related to only one rating from a rating entity."
                    );
            }

        MGame result = new MGame(gameRepository.save(game), true);
        // Try to upload images.
        if (
                (
                        coverImage != null && !(!coverImage.isEmpty() &&
                        ImageResourceManager.uploadEntityImage(
                                coverImage, result, new String[] {GameArtType.COVER_ART.toString()}
                        ))
                ) ||
                (
                        landscapeImage != null && !(!landscapeImage.isEmpty() &&
                        ImageResourceManager.uploadEntityImage(
                                landscapeImage, result, new String[] {GameArtType.LANDSCAPE_ART.toString()}
                        ))
                )
        )
            throw new MediaUploadFailedException("Either Cover image or landscape image couldn't be uploaded.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    @Transactional
    public MGame update(
            @NotNull Game game,
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        Game gameCurrentState = gameRepository.findById(game.getId());
        if (gameCurrentState == null)
            throw new EntityNotFoundException("Couldn't find a Game with this ID.");

        EntityResolver.resolveGameEntities(
                gameCategoryRepository,
                gameRatingRepository,
                gameRatingDescriptorRepository,
                game
        );

        // Check if it has at least one GameCategory and one GameRating.
        if (game.getGameCategories() == null || game.getGameCategories().isEmpty())
            throw new InvalidEntityRelationsException("Game must have at least one related category.");

        // Check if there is no more than one GameRating from a RatingEntity assigned to this Game.
        if (game.getGameRatings() != null)
            for (
                    GameRatingEntity ratingEntityItem :
                    game.getGameRatings().stream().map(GameRating::getGameRatingEntity).toList()
            ) {
                if (
                        game.getGameRatings().stream()
                                .filter(
                                        item -> Objects.equals(
                                                item.getGameRatingEntity().getName(),
                                                ratingEntityItem.getName()
                                        )
                                )
                                .count() > 1
                )
                    throw new InvalidEntityRelationsException(
                            "Game must be related to only one rating from a rating entity."
                    );
            }

        Game updateData = new Game(gameCurrentState);
        gameMapper.partialUpdateGame(updateData, game);

        MGame result = new MGame(gameRepository.save(updateData), true);
        // Try to upload images
        if (
                (
                        coverImage != null && !(!coverImage.isEmpty() &&
                        ImageResourceManager.uploadEntityImage(
                                coverImage, result, new String[] {GameArtType.COVER_ART.toString()}
                        ))
                ) ||
                (
                        landscapeImage != null && !(!landscapeImage.isEmpty() &&
                        ImageResourceManager.uploadEntityImage(
                                landscapeImage, result, new String[] {GameArtType.LANDSCAPE_ART.toString()}
                        ))
                )
        )
            throw new MediaUploadFailedException("Either Cover image or landscape image couldn't be uploaded.");

        ImageResourceManager.getURIsOfEntityImage(result);
        return result;
    }

    public void delete(long id) {
        Game game = gameRepository.findById(id);
        if (game == null || game.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Game with this ID.");

        game.setDeletedAt(new Date());
        gameRepository.save(game);
    }

    public void hardDelete(long id) {
        gameRepository.delete(gameRepository.findById(id));
    }

    // Queries
    public ImageResourcePackage getImage(String name, String filename, Integer width, Integer height)
            throws IOException {
        Game game = gameRepository.findByName(name);
        if (game == null)
            throw new EntityNotFoundException("Couldn't find a Game with this name.");

        MGame result = new MGame(game, false);
        return ImageResourceManager.getResourceImage(context, result, filename, width, height);
    }

    public MGame getByName(String name) {
        Game game = gameRepository.findByName(name);
        if (game == null || game.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Game with this name.");

        MGame result = new MGame(game, true);
        ImageResourceManager.getURIsOfEntityImage(result);

        return result;
    }

    public List<MGame> getByTitle(String title, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByTitleContains(title, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

    public List<MGame> getByGameCategories(List<GameCategory> categories, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByGameCategoriesContains(categories, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

    public List<MGame> getByGameRatings(List<GameRating> ratings, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByGameRatingsContains(ratings, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

    public List<MGame> getByGameRatingDescriptors(
            List<GameRatingDescriptor> ratingDescriptors, Pageable pageable
    ) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameRatingDescriptorsContains(
                        ratingDescriptors, pageable
                ).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

    public List<MGame> getAll(Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        results.forEach(ImageResourceManager::getURIsOfEntityImage);
        return results;
    }

}
