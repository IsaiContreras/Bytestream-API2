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
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private List<MGame> getMediaURIsOfList(List<MGame> gameList) {
        for (MGame gameItem : gameList)
            this.getMediaURIs(gameItem);
        return gameList;
    }

    private MGame getMediaURIs(MGame game) {
        File[] files;
        try {
            files = new File(
                    Objects.requireNonNull(ResourcePathProvider.getPathOfEntity(
                            ResourcePath.GAME_ART, String.valueOf(game.getId())
                    )).toString()
            ).listFiles();
        } catch (Exception e) { return game; }
        if (files == null)
            return game;

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
                                game.getName(),
                                fileItem.getName()
                        }
                );
            } catch (Exception e) { return game; }

            switch(artType) {
                case COVER_ART -> game.setCoverURI(fileURI);
                case LANDSCAPE_ART -> game.setLandsapeURI(fileURI);
            }
        }

        return game;
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
    public ResponseEntity<?> create(
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
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Game must have at least one related category.");
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
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("Game must be related to only one rating from a rating entity.");
                }
            }

            // Try to upload images.
            if(
                !this.uploadImage(coverImage, newItem, GameArtType.COVER_ART) ||
                !this.uploadImage(landscapeImage, newItem, GameArtType.LANDSCAPE_ART)
            ) {
                gameRepository.delete(newItem);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Unable to upload image file.");
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(this.getMediaURIsOfList(List.of(new MGame(newItem, true))));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            if(newItem != null)
                gameRepository.delete(newItem);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> update(
            Game game,
            MultipartFile coverImage,
            MultipartFile landscapeImage
    ) {
        Game gameCurrentState = null;
        try {
            gameCurrentState = gameRepository.findById(game.getId());
            if (gameCurrentState == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            this.resolveRelatedEntities(game);

            Game updateData = new Game(gameCurrentState);
            gameMapper.partialUpdateGame(updateData, game);

            updateData = gameRepository.saveAndFlush(updateData);
            entityManager.refresh(updateData);

            // Check if it has at least one GameCategory and one GameRating.
            if (updateData.getGameCategories().isEmpty()) {
                gameRepository.saveAndFlush(gameCurrentState);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Game must have at least one related category.");
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
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("Game must be related to only one rating from a rating entity.");
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
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Unable to upload image file.");
            }

            return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIs(new MGame(updateData, true)));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            if (gameCurrentState != null)
                gameRepository.saveAndFlush(gameCurrentState);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> delete(long id) {
        try {
            Game game = gameRepository.findById(id);
            game.setDeletedAt(new Date());

            gameRepository.save(game);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> hardDelete(long id) {
        try {
            this.gameRepository.delete(this.gameRepository.findById(id));
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Queries
    public ResponseEntity<byte[]> getImage(String name, String filename, Integer width, Integer height) {
        Game game = gameRepository.findByName(name);
        if (game == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // Obtiene la ruta de la imagen
        Path filePath;
        try {
            filePath = Objects.requireNonNull(
                    ResourcePathProvider
                            .getPathOfEntity(ResourcePath.GAME_ART, game.getId().toString())
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

            MediaType type = MediaType.parseMediaType(contentType);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(type)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() + "\""
                    ).body(DataConverter.imageToByteArray(image, extension));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<MGame> getByName(String name) {
        Game game = gameRepository.findByName(name);
        if (game == null || game.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIs(new MGame(game, true)));
    }

    public ResponseEntity<?> getByTitle(String title, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByTitleContains(title, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIsOfList(results));
    }

    public ResponseEntity<?> getByGameCategories(List<GameCategory> categories, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByGameCategoriesContains(categories, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIsOfList(results));
    }

    public ResponseEntity<?> getByGameRatings(List<GameRating> ratings, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByGameRatingsContains(ratings, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIsOfList(results));
    }

    public ResponseEntity<?> getByGameRatingDescriptors(List<GameRatingDescriptor> ratingDescriptors, Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findByGameRatingDescriptorsContains(ratingDescriptors, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIsOfList(results));
    }

    public ResponseEntity<?> getAll(Pageable pageable) {
        List<MGame> results = gameConverter
                .parseToList(gameRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(this.getMediaURIsOfList(results));
    }

}
