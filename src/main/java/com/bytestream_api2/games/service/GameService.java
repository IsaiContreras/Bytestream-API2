package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameConverter;
import com.bytestream_api2.games.entity.*;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.repository.GameRepository;
import com.bytestream_api2.games.utilities.ImageResourceManager;
import com.bytestream_api2.games.utilities.ResourcePathProvider;
import com.bytestream_api2.games.misc.ImageResolution;
import com.bytestream_api2.games.misc.ResourcePath;
import com.bytestream_api2.games.misc.StaticResourcesPaths;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bytestream_api2.games.mapper.GameMapper;
import com.bytestream_api2.games.model.MGame;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@Service("game_service")
public class GameService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Server Environment Resources
    @Autowired
    private ServletContext context;

    @Autowired
    private Environment environment;

    @Autowired
    private EntityManager entityManager;

    // Entity Components
    @Autowired
    @Qualifier("game_repository")
    private GameRepository gameRepository;

    @Autowired
    @Qualifier("game_category_repository")
    private GameCategoryRepository gameCategoryRepository;

    @Autowired
    @Qualifier("game_rating_repository")
    private GameRatingRepository gameRatingRepository;

    @Autowired
    @Qualifier("game_rating_descriptor_repository")
    private GameRatingDescriptorRepository gameRatingDescriptorRepository;

    @Autowired
    @Qualifier("game_converter")
    private GameConverter gameConverter;

    private final GameMapper gameMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameService.class);

    private final int logoResolutionConfiguration =
            ImageResolution.ORIGINAL_WIDTH_VALUE | ImageResolution.WIDTH_64_VALUE | ImageResolution.WIDTH_256_VALUE;
    private final int coverResolutionConfiguration =
            ImageResolution.ORIGINAL_WIDTH_VALUE | ImageResolution.WIDTH_64_VALUE | ImageResolution.WIDTH_256_VALUE;
    private final int landscapeResolutionConfiguration =
            ImageResolution.ORIGINAL_WIDTH_VALUE;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private void getMediaURIs(List<MGame> gameList, ResourcePath mediaType) {
        for (MGame gameItem : gameList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                mediaType, String.valueOf(gameItem.getId())
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            List<String> mediaURIList = new ArrayList<>();
            for (File fileItem : files) {
                try {
                    mediaURIList.add(
                            Objects.requireNonNull(StaticResourcesPaths.getByResourcePath(mediaType)).constructFullURI(
                                    environment,
                                    new String[]{
                                            gameItem.getName(),
                                            fileItem.getName()
                                    }
                            )
                    );
                } catch (Exception e) { return; }
            }

            switch (mediaType) {
                case GAME_LOGO_ART -> gameItem.setLogoURIList(mediaURIList);
                case GAME_COVER_ART -> gameItem.setCoverURIList(mediaURIList);
                case GAME_LANDSCAPE_ART -> gameItem.setLandscapeURIList(mediaURIList);
                default -> { return; }
            }
        }
    }

    private void resolveRelatedEntities(Game game) {
        List<GameCategory> retrievedCategos = game.getGameCategories().stream()
                .map(item -> gameCategoryRepository.findByName(item.getName()))
                .toList();
        List<GameRating> retrievedRatings = game.getGameRatings().stream()
                .map(item -> gameRatingRepository.findByName(item.getName()))
                .toList();
        List<GameRatingDescriptor> retrievedRatingDescriptors = game.getGameRatingDescriptors().stream()
                .map(item -> gameRatingDescriptorRepository.findByName(item.getName()))
                .toList();

        game.setGameCategories(retrievedCategos);
        game.setGameRatings(retrievedRatings);
        game.setGameRatingDescriptors(retrievedRatingDescriptors);
    }

    // -- PUBLIC --
    @Autowired
    public GameService(GameMapper gameMapper) {
        this.gameMapper = gameMapper;
    }

    // CUD
    @Transactional
    public boolean create(
            Game game,
            MultipartFile logoImage,
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        Game newItem = null;
        try {
            this.resolveRelatedEntities(game);

            newItem = this.gameRepository.saveAndFlush(game);
            entityManager.refresh(newItem);

            // Check if it has at least one GameCategory.
            if (newItem.getGameCategories().isEmpty()) {
                gameRepository.delete(newItem);
                return false;
            }

            // Check if there is no more than one GameRating from a RatingEntity assigned to this Game.
            for (
                    GameRatingEntity ratingEntityItem :
                    newItem.getGameRatings().stream().map(GameRating::getGameRatingEntity).toList()
            ) {
                if (
                        newItem.getGameRatings().stream()
                                .filter(item -> item.getGameRatingEntity() == ratingEntityItem)
                                .count() > 1
                ) {
                    gameRepository.delete(newItem);
                    return false;
                }
            }

            // Try to upload images.
            if(
                !ImageResourceManager.uploadImageFile(
                        logoImage, newItem.getId().toString(),
                        ResourcePath.GAME_LOGO_ART, this.logoResolutionConfiguration
                ) ||
                !ImageResourceManager.uploadImageFile(
                        coverImage, newItem.getId().toString(),
                        ResourcePath.GAME_COVER_ART, this.coverResolutionConfiguration
                ) ||
                !ImageResourceManager.uploadImageFile(
                        landscapeImage, newItem.getId().toString(),
                        ResourcePath.GAME_LANDSCAPE_ART, this.landscapeResolutionConfiguration
                )
            ) {
                gameRepository.delete(newItem);
                return false;
            }

            return true;
        } catch (Exception e) {
            if(newItem != null)
                gameRepository.delete(newItem);
            return false;
        }
    }

    @Transactional
    public boolean update(
            Game game,
            MultipartFile logoImage,
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        Game previousStageGame = null;
        try {
            previousStageGame = gameRepository.findById(game.getId());
            if (previousStageGame == null)
                return false;

            Game updateData = new Game(previousStageGame);
            gameMapper.partialUpdateGame(updateData, game);

            this.resolveRelatedEntities(updateData);

            updateData = gameRepository.saveAndFlush(updateData);
            entityManager.refresh(updateData);

            // Check if it has at least one GameCategory and one GameRating.
            if (updateData.getGameCategories().isEmpty()) {
                gameRepository.saveAndFlush(previousStageGame);
                return false;
            }

            // Check if there is no more than one GameRating from a RatingEntity assigned to this Game.
            for (
                    GameRatingEntity ratingEntityItem :
                    updateData.getGameRatings().stream().map(GameRating::getGameRatingEntity).toList()
            ) {
                if (
                        updateData.getGameRatings().stream()
                                .filter(item -> item.getGameRatingEntity() == ratingEntityItem)
                                .count() > 1
                ) {
                    gameRepository.saveAndFlush(previousStageGame);
                    return false;
                }
            }

            // Try to upload images
            if (
                    !(logoImage.isEmpty() || coverImage.isEmpty() || landscapeImage.isEmpty()) &&
                    (
                            !ImageResourceManager.uploadImageFile(
                                    logoImage, updateData.getId().toString(),
                                    ResourcePath.GAME_LOGO_ART, this.logoResolutionConfiguration
                            ) ||
                            !ImageResourceManager.uploadImageFile(
                                    coverImage, updateData.getId().toString(),
                                    ResourcePath.GAME_COVER_ART, this.coverResolutionConfiguration
                            ) ||
                            !ImageResourceManager.uploadImageFile(
                                    landscapeImage, updateData.getId().toString(),
                                    ResourcePath.GAME_LANDSCAPE_ART, this.landscapeResolutionConfiguration
                            )
                    )
            ) {
                gameRepository.saveAndFlush(previousStageGame);
                return false;
            }

            return true;
        } catch (Exception e) {
            if (previousStageGame != null)
                gameRepository.saveAndFlush(previousStageGame);
            return false;
        }
    }

    public boolean delete(long id) {
        try {
            Game game = gameRepository.findById(id);
            game.setDeletedAt(new Date());

            gameRepository.save(game);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hardDelete(long id) {
        try {
            this.gameRepository.delete(this.gameRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public ResponseEntity<Resource> getImage(ResourcePath resourceType, String name, String filename) {
        if (!(
                resourceType.equals(ResourcePath.GAME_LOGO_ART) ||
                resourceType.equals(ResourcePath.GAME_COVER_ART) ||
                resourceType.equals(ResourcePath.GAME_LANDSCAPE_ART)
        ))
            return ResponseEntity.notFound().build();

        Game game = gameRepository.findByName(name);
        if (game == null)
            return ResponseEntity.notFound().build();

        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                    ResourcePathProvider
                            .getPathOfEntity(resourceType, String.valueOf(game.getId()))
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

    public MGame getByName(String name) {
        Game game = gameRepository.findByName(name);
        if (game == null)
            return null;

        return new MGame(game, true);
    }

    public List<MGame> getByTitle(String title, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByTitleContains(title, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results, ResourcePath.GAME_LOGO_ART);
        this.getMediaURIs(results, ResourcePath.GAME_COVER_ART);
        this.getMediaURIs(results, ResourcePath.GAME_LANDSCAPE_ART);

        return results;
    }

    public List<MGame> getByGameCategories(List<GameCategory> categories, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameCategoriesContains(categories, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results, ResourcePath.GAME_LOGO_ART);
        this.getMediaURIs(results, ResourcePath.GAME_COVER_ART);
        this.getMediaURIs(results, ResourcePath.GAME_LANDSCAPE_ART);

        return results;
    }

    public List<MGame> getByGameRatings(List<GameRating> ratings, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameRatingsContains(ratings, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results, ResourcePath.GAME_LOGO_ART);
        this.getMediaURIs(results, ResourcePath.GAME_COVER_ART);
        this.getMediaURIs(results, ResourcePath.GAME_LANDSCAPE_ART);

        return results;
    }

    public List<MGame> getByGameRatingDescriptors(List<GameRatingDescriptor> ratingDescriptors, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameRatingDescriptorsContains(ratingDescriptors, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results, ResourcePath.GAME_LOGO_ART);
        this.getMediaURIs(results, ResourcePath.GAME_COVER_ART);
        this.getMediaURIs(results, ResourcePath.GAME_LANDSCAPE_ART);

        return results;
    }

    public List<MGame> getAll(Pageable pageable) {

        List<MGame> results = gameConverter.parseToList(gameRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results, ResourcePath.GAME_LOGO_ART);
        this.getMediaURIs(results, ResourcePath.GAME_COVER_ART);
        this.getMediaURIs(results, ResourcePath.GAME_LANDSCAPE_ART);

        return results;
    }

}
