package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameConverter;
import com.bytestream_api2.games.entity.*;
import com.bytestream_api2.games.misc.FilenameFormat;
import com.bytestream_api2.games.misc.GameArtType;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.repository.GameRepository;
import com.bytestream_api2.games.utilities.DataConverter;
import com.bytestream_api2.games.utilities.FilenameFormatter;
import com.bytestream_api2.games.utilities.ImageResourceUploader;
import com.bytestream_api2.games.utilities.ResourcePathProvider;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --
    private boolean uploadImage(MultipartFile imageMultiPartFile, Game gameData, GameArtType type) {
        String filename;
        try {
            filename = FilenameFormatter.formatFilename(
                    FilenameFormat.GAME_ART_FORMAT,
                    new String[]{
                            gameData.getId().toString().concat(gameData.getName()),
                            type.getName(),
                            Objects.requireNonNull(imageMultiPartFile.getContentType()).split("/")[1]
                    }
            );
        } catch (Exception e) { return false; }
        if (filename == null)
            return false;

        return ImageResourceUploader.uploadImageFile(
                imageMultiPartFile,
                ResourcePathProvider.getPathOfEntity(ResourcePath.GAME_ART, gameData.getId().toString()),
                filename
        );
    }

    private void getMediaURIs(List<MGame> gameList) {
        for (MGame gameItem : gameList) {
            File[] files;
            try {
                files = new File(
                        Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                                ResourcePath.GAME_ART, String.valueOf(gameItem.getId())
                        )).toString()
                ).listFiles();
            } catch (Exception e) { return; }
            if (files == null)
                return;

            for (File fileItem : files) {
                GameArtType artType = Arrays.stream(GameArtType.values()).filter(
                        type -> fileItem.getName().contains(type.getName())
                ).findFirst().orElse(null);
                if (artType == null)
                    continue;

                String fileURI;
                try {
                    fileURI = Objects.requireNonNull(
                            StaticResourcesPaths.getByResourcePath(ResourcePath.GAME_ART)
                    ).constructFullURI(
                            environment,
                            new String[] {
                                    gameItem.getName(),
                                    fileItem.getName()
                            }
                    );
                } catch (Exception e) { return; }

                switch(artType) {
                    case COVER_ART -> gameItem.setCoverURI(fileURI);
                    case LANDSCAPE_ART -> gameItem.setLandsapeURI(fileURI);
                }
            }
        }
    }

    private void resolveRelatedEntities(Game game) {
        List<GameCategory> retrievedCategos = game.getGameCategories() != null ?
                game.getGameCategories().stream()
                        .map(item -> gameCategoryRepository.findByName(item.getName()))
                        .toList()
                : null;
        List<GameRating> retrievedRatings = game.getGameRatings() != null ?
                game.getGameRatings().stream()
                        .map(item -> gameRatingRepository.findByName(item.getName()))
                        .toList()
                : null;
        List<GameRatingDescriptor> retrievedRatingDescriptors = game.getGameRatingDescriptors() != null ?
                game.getGameRatingDescriptors().stream()
                        .map(item -> gameRatingDescriptorRepository.findByName(item.getName()))
                        .toList()
                : null;

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
                !this.uploadImage(coverImage, newItem, GameArtType.COVER_ART) ||
                !this.uploadImage(landscapeImage, newItem, GameArtType.LANDSCAPE_ART)
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
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        Game gameCurrentState = null;
        try {
            gameCurrentState = gameRepository.findById(game.getId());
            if (gameCurrentState == null)
                return false;

            this.resolveRelatedEntities(game);

            Game updateData = new Game(gameCurrentState);
            gameMapper.partialUpdateGame(updateData, game);

            updateData = gameRepository.saveAndFlush(updateData);
            entityManager.refresh(updateData);

            // Check if it has at least one GameCategory and one GameRating.
            if (updateData.getGameCategories().isEmpty()) {
                gameRepository.saveAndFlush(gameCurrentState);
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
                    gameRepository.saveAndFlush(gameCurrentState);
                    return false;
                }
            }

            // Try to upload images
            if (
                    !(coverImage.isEmpty() || landscapeImage.isEmpty()) &&
                    (
                            !this.uploadImage(coverImage, updateData, GameArtType.COVER_ART) ||
                            !this.uploadImage(landscapeImage, updateData, GameArtType.LANDSCAPE_ART)
                    )
            ) {
                gameRepository.saveAndFlush(gameCurrentState);
                return false;
            }

            return true;
        } catch (Exception e) {
            if (gameCurrentState != null)
                gameRepository.saveAndFlush(gameCurrentState);
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
    public ResponseEntity<byte[]> getImage(String name, String filename, Integer width, Integer height) {
        Game game = gameRepository.findByName(name);
        if (game == null)
            return ResponseEntity.notFound().build();

        // Obtiene la ruta de la imagen
        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                    ResourcePathProvider
                            .getPathOfEntity(ResourcePath.GAME_ART, game.getId().toString())
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
            if (extension == null)
                return ResponseEntity.notFound().build();

            image = ImageResourceUploader.resizeImage(
                    image,
                    width != null ? width : image.getWidth(),
                    height != null ? height : image.getHeight()
            );

            String contentType = context.getMimeType(filePath.toString());
            if (contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            MediaType type = MediaType.parseMediaType(contentType);

            return ResponseEntity.ok()
                    .contentType(type)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() + "\""
                    ).body(DataConverter.imageToByteArray(image, extension));
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

        this.getMediaURIs(results);

        return results;
    }

    public List<MGame> getByGameCategories(List<GameCategory> categories, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameCategoriesContains(categories, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results);

        return results;
    }

    public List<MGame> getByGameRatings(List<GameRating> ratings, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameRatingsContains(ratings, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results);

        return results;
    }

    public List<MGame> getByGameRatingDescriptors(List<GameRatingDescriptor> ratingDescriptors, Pageable pageable) {
        List<MGame> results = gameConverter.parseToList(
                gameRepository.findByGameRatingDescriptorsContains(ratingDescriptors, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results);

        return results;
    }

    public List<MGame> getAll(Pageable pageable) {

        List<MGame> results = gameConverter.parseToList(gameRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();

        this.getMediaURIs(results);

        return results;
    }

}
