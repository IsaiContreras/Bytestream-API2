package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameConverter;
import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.InvalidEntityRelationsException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.mapper.GameMapper;
import com.bytestream_api2.games.model.MGame;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.repository.GameRepository;
import com.bytestream_api2.games.utilities.EntityResolver;
import com.bytestream_api2.games.utilities.ImageResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameConverter gameConverter;

    @Spy
    private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

    @InjectMocks
    private GameService gameService;

    private SimpleDateFormat formatter;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @BeforeEach
    public void setup() {
        this.formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    // CUD
    @Test
    public void createValidGameTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        List<GameRating> ratings = List.of(
                new GameRating("e-everyone", "", "ESRB"),
                new GameRating("pegi-3", "", "PEGI")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);
        game.setGameRatings(ratings);

        MultipartFile mockCover = new MockMultipartFile(
                "cover",
                "cover.png",
                "image/png",
                new byte[]{1}
        );
        MultipartFile mockLandscape = new MockMultipartFile(
                "landscape",
                "landscape.png",
                "image/png",
                new byte[]{1}
        );

        MGame result;

        // Dependency and method call handlers
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(true);
            imageResourceManagerMocked.when(() -> ImageResourceManager.getURIsOfEntityImage(
                    any(MGame.class)
            )).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.create(game, mockCover, mockLandscape);
        }

        assertNotNull(result);
        assertEquals("metroid", result.getName());
        assertEquals("Metroid", result.getTitle());
        assertEquals("Lorem ipsum", result.getSynopsis());
        assertEquals("e-everyone", result.getGameRatings().get(0).getName());
        assertEquals("pegi-3", result.getGameRatings().get(1).getName());
        assertEquals("metroidvania", result.getGameCategories().get(0).getName());
        assertEquals("single-player", result.getGameCategories().get(1).getName());
    }

    @Test
    public void createGameNoCategoriesTest() throws Exception {
        // Preparation
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        RuntimeException exception;

        // Dependency and method call handlers
        try (MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class)) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

        // Assertions
            exception = assertThrows(
                    InvalidEntityRelationsException.class,
                    () -> gameService.create(game, null, null)
            );
        }

        assertEquals("Game must have at least one related category.", exception.getMessage());
    }

    @Test
    public void createGameWithMoreThanOneGameRatingFromARatingEntityTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        List<GameRating> ratings = List.of(
                new GameRating("e-everyone", "", "ESRB"),
                new GameRating("e10+", "", "ESRB")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);
        game.setGameRatings(ratings);

        RuntimeException exception;

        // Dependency and method call handlers
        try (MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class)) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

        // Assertions
            exception = assertThrows(
                    InvalidEntityRelationsException.class,
                    () -> gameService.create(game, null, null)
            );
        }

        assertEquals("Game must be related to only one rating from a rating entity.", exception.getMessage());
    }

    @Test
    public void createGameWithInvalidCoverImageTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile coverMockImage = new MockMultipartFile(
                "cover",
                "cover.png",
                "image/png",
                new byte[]{1}
        );

        // Dependency and method call handlers
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(coverMockImage),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(false);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.create(game, coverMockImage, null)
            );
        }
    }

    @Test
    public void createGameWithEmptyCoverImageTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile coverMockImage = new MockMultipartFile(
                "cover",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(coverMockImage),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(true);

            // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.create(game, coverMockImage, null)
            );
        }
    }

    @Test
    public void createGameWithInvalidLandscapeImageTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile landscapeMockImage = new MockMultipartFile(
                "landscape",
                "landscape.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency and method call handlers
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(landscapeMockImage),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(false);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.create(game, null, landscapeMockImage)
            );
        }
    }

    @Test
    public void createGameWithEmptyLandscapeImageTest() throws Exception {
        // Preparation
        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        Game game = new Game(
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile landscapeMockImage = new MockMultipartFile(
                "landscape",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(landscapeMockImage),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(true);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.create(game, null, landscapeMockImage)
            );
        }
    }

    @Test
    public void updateValidGameTest() throws Exception {
        // Preparation
        List<GameCategory> oldCategories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("single-player")
        );
        List<GameRating> oldRatings = List.of(
                new GameRating("e-everyone", "", "ESRB"),
                new GameRating("pegi-3", "", "PEGI")
        );
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        oldData.setGameCategories(oldCategories);
        oldData.setGameRatings(oldRatings);

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        List<GameRating> ratings = List.of(
                new GameRating("e10+", "", "ESRB"),
                new GameRating("pegi-7", "", "PEGI")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);
        game.setGameRatings(ratings);

        MultipartFile mockCover = new MockMultipartFile(
                "cover",
                "cover.png",
                "image/png",
                new byte[]{1}
        );
        MultipartFile mockLandscape = new MockMultipartFile(
                "landscape",
                "landscape.png",
                "image/png",
                new byte[]{1}
        );

        MGame result;

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);
        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenReturn(null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(true);
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

            result = gameService.update(game, mockCover, mockLandscape);
        }

        assertNotNull(result);
        assertEquals("metroid1", result.getName());
        assertEquals("Metroid", result.getTitle());
        assertEquals("Lorem ipsum xd", result.getSynopsis());
        assertEquals(formatter.parse("1986-08-06"), result.getReleaseDate());
        assertEquals("action-adventure", result.getGameCategories().get(1).getName());
        assertEquals("e10+", result.getGameRatings().get(0).getName());
        assertEquals("pegi-7", result.getGameRatings().get(1).getName());
    }

    @Test
    public void updateNonExistentGameTest() throws Exception {
        // Preparation
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(null);

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.update(game, null, null)
        );
    }

    @Test
    public void updateGameNoCategoriesTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );

        RuntimeException exception;

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class)) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

        // Assertions
            exception = assertThrows(
                    InvalidEntityRelationsException.class,
                    () -> gameService.update(game, null, null)
            );
        }

        assertEquals("Game must have at least one related category.", exception.getMessage());
    }

    @Test
    public void updateGameMoreThanOneGameRatingFromARatingEntityTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        List<GameRating> ratings = List.of(
                new GameRating("e10+", "", "ESRB"),
                new GameRating("pegi-7", "", "PEGI"),
                new GameRating("pegi-18", "", "PEGI")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);
        game.setGameRatings(ratings);

        RuntimeException exception;

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class)) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

        // Assertions
            exception = assertThrows(
                    InvalidEntityRelationsException.class,
                    () -> gameService.update(game, null, null)
            );
        }

        assertEquals("Game must be related to only one rating from a rating entity.", exception.getMessage());
    }

    @Test
    public void updateGameWithInvalidCoverImageTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile coverMockImage = new MockMultipartFile(
                "cover",
                "cover.png",
                "image/png",
                new byte[]{1}
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

            when(gameRepository.save(any(Game.class))).thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(false);

            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.update(game, coverMockImage, null)
            );
        }
    }

    @Test
    public void updateGameWithEmptyCoverImageTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile coverMockImage = new MockMultipartFile(
                "cover",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

            when(gameRepository.save(any(Game.class))).thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGame.class),
                    any(String[].class)
            )).thenReturn(true);

            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.update(game, coverMockImage, null)
            );
        }
    }

    @Test
    public void updateGameWithInvalidLandscapeImageTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile landscapeMockImage = new MockMultipartFile(
                "landscape",
                "landscape.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(landscapeMockImage),
                    any(ImageContentEntity.class),
                    any(String[].class)
            )).thenReturn(false);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.update(game, null, landscapeMockImage)
            );
        }
    }

    @Test
    public void updateGameWithEmptyLandscapeImageTest() throws Exception {
        // Preparation
        Game oldData = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        List<GameCategory> categories = List.of(
                new GameCategory("metroidvania"),
                new GameCategory("action-adventure")
        );
        Game game = new Game(
                (long)1003,
                "metroid1",
                "Metroid",
                "Lorem ipsum xd",
                formatter.parse("1986-08-06")
        );
        game.setGameCategories(categories);

        MultipartFile landscapeMockImage = new MockMultipartFile(
                "landscape",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(oldData);

        try (
                MockedStatic<EntityResolver> entityResolverMocked = mockStatic(EntityResolver.class);
                MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)
        ) {
            entityResolverMocked.when(() -> EntityResolver.resolveGameEntities(
                    any(GameCategoryRepository.class),
                    any(GameRatingRepository.class),
                    any(GameRatingDescriptorRepository.class),
                    any(Game.class)
            )).thenAnswer(invocation -> null);

            when(gameRepository.save(any(Game.class)))
                    .thenReturn(game);

            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    eq(landscapeMockImage),
                    any(ImageContentEntity.class),
                    any(String[].class)
            )).thenReturn(true);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameService.update(game, null, landscapeMockImage)
            );
        }
    }

    @Test
    public void deleteValidGameTest() throws Exception {
        // Preparation
        Game game = new Game(
                (long)1005,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenReturn(null);

        // Assertions
        assertDoesNotThrow(() -> gameService.delete(1005));
    }

    @Test
    public void deleteNonExistentGameTest() {
        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameService.delete((long)1005));
    }

    @Test
    public void deleteDeletedGameTest() throws ParseException {
        // Preparation
        Game game = new Game(
                (long)1005,
                "metroid",
                "Metroid",
                "Lorem ipsum",
                formatter.parse("1986-08-06")
        );
        game.setDeletedAt(new Date());

        // Dependency and method call handlers
        when(gameRepository.findById(anyLong()))
                .thenReturn(game);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameService.delete((long)1005));
    }

    // Queries
    @Test
    public void getValidGameByNameTest() throws Exception {
        // Preparation
        Game game = new Game(
                (long)1003,
                "metroid",
                "Metroid",
                "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                formatter.parse("1986-08-06")
        );

        MGame result;

        // Dependency and method call handlers
        when(gameRepository.findByName(anyString())).thenReturn(game);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getByName("metroid");
        }

        assertNotNull(result);
        assertEquals("metroid", result.getName());
        assertEquals("Metroid", result.getTitle());
        assertEquals(
                "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                result.getSynopsis()
        );
        assertEquals(formatter.parse("1986-08-06"), result.getReleaseDate());
    }

    @Test
    public void getNonExistentGameByNameTest() {
        // Dependency and method call handlers
        when(gameRepository.findByName(anyString())).thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameService.getByName("metroid"));
    }

    @Test
    public void getValidGameByTitleTest() throws ParseException {
        // Preparation
        List<MGame> games = List.of(
                new MGame(
                        (long)1003,
                        "metroid",
                        "Metroid",
                        "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                        formatter.parse("1986-08-06")
                ),
                new MGame(
                        (long)1003,
                        "metroid-dread",
                        "Metroid Dread",
                        "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                        formatter.parse("2021-10-08")
                )
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGame> result;

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(games);
        doReturn(pageContent).when(gameRepository)
                .findByTitleContains(anyString(), any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getByTitle("Metroid", PageRequest.of(0, 10));
        }

        assertNotNull(result);
        assertEquals("metroid", result.get(0).getName());
        assertEquals("Metroid", result.get(0).getTitle());
        assertEquals(
                "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                result.get(0).getSynopsis()
        );
        assertEquals(formatter.parse("1986-08-06"), result.get(0).getReleaseDate());
        assertEquals("metroid-dread", result.get(1).getName());
        assertEquals("Metroid Dread", result.get(1).getTitle());
        assertEquals(
                "The Space Pirates attack a Galactic Federation-owned space research vessel and seize samples of Metroid creatures—the predatory lifeforms discovered on the planet SR388.",
                result.get(1).getSynopsis()
        );
        assertEquals(formatter.parse("2021-10-08"), result.get(1).getReleaseDate());
    }

    @Test
    public void getNonExistentGameByTitleTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRepository)
                .findByTitleContains(anyString(), any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.getByTitle("Metroid", PageRequest.of(0, 10))
        );
    }

    @Test
    public void getValidGameByCategoriesTest() {
        // Preparation
        List<MGame> games = List.of(
                new MGame((long)1003, "metroid", "Metroid", "Lorem ipsum"),
                new MGame((long)1004, "metroid-zero-mission", "Metroid: Zero Mission", "Lorem")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGame> result;

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(games);
        doReturn(pageContent).when(gameRepository)
                .findByGameCategoriesContains(anyList(), any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getByGameCategories(
                    List.of(new GameCategory("metroidvania"), new GameCategory("action-adventure")),
                    PageRequest.of(0, 10)
            );
        }

        assertNotNull(result);
        assertEquals("metroid", result.get(0).getName());
        assertEquals("metroid-zero-mission", result.get(1).getName());
    }

    @Test
    public void getNonExistentGameByCategoriesTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRepository)
                .findByGameCategoriesContains(anyList(), any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.getByGameCategories(
                        List.of(new GameCategory("metroidvania"), new GameCategory("action-adventure")),
                        PageRequest.of(0, 10)
                )
        );
    }

    @Test
    public void getValidGameByRatingsTest() {
        // Preparation
        List<MGame> games = List.of(
                new MGame((long)1003, "metroid", "Metroid", "Lorem ipsum"),
                new MGame((long)1004, "metroid-zero-mission", "Metroid: Zero Mission", "Lorem")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGame> result;

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(games);
        doReturn(pageContent).when(gameRepository)
                .findByGameRatingsContains(anyList(), any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getByGameRatings(
                    List.of(new GameRating("e-everyone"), new GameRating("pegi-3")),
                    PageRequest.of(0, 10)
            );
        }

        assertNotNull(result);
        assertEquals("metroid", result.get(0).getName());
        assertEquals("metroid-zero-mission", result.get(1).getName());
    }

    @Test
    public void getNonExistentGameByRatingsTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRepository)
                .findByGameRatingsContains(anyList(), any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.getByGameRatings(
                        List.of(new GameRating("e-everyone"), new GameRating("pegi-3")),
                        PageRequest.of(0, 10)
                )
        );
    }

    @Test
    public void getValidGameByGameRatingDescriptorsTest() {
        // Preparation
        List<MGame> games = List.of(
                new MGame((long)1003, "metroid", "Metroid", "Lorem ipsum"),
                new MGame((long)1004, "metroid-zero-mission", "Metroid: Zero Mission", "Lorem")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGame> result;

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(games);
        doReturn(pageContent).when(gameRepository)
                .findByGameRatingDescriptorsContains(anyList(), any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getByGameRatingDescriptors(
                    List.of(
                            new GameRatingDescriptor("mild-violence"),
                            new GameRatingDescriptor("violence-p")
                    ),
                    PageRequest.of(0, 10)
            );
        }

        assertNotNull(result);
        assertEquals("metroid", result.get(0).getName());
        assertEquals("metroid-zero-mission", result.get(1).getName());
    }

    @Test
    public void getNonExistentGameByRatingDescriptorsTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRepository)
                .findByGameRatingDescriptorsContains(anyList(), any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.getByGameRatingDescriptors(
                        List.of(
                                new GameRatingDescriptor("mild-violence"),
                                new GameRatingDescriptor("violence-p")
                        ),
                        PageRequest.of(0, 10)
                )
        );
    }

    @Test
    public void getAllGamesTest() {
        // Preparation
        List<MGame> games = List.of(
                new MGame((long)1003, "metroid", "Metroid", "Lorem ipsum"),
                new MGame((long)1004, "metroid-zero-mission", "Metroid: Zero Mission", "Lorem")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGame> result;

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(games);
        doReturn(pageContent).when(gameRepository)
                .findAll(any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGame.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameService.getAll(PageRequest.of(0, 10));
        }

        assertNotNull(result);
        assertEquals("metroid", result.get(0).getName());
        assertEquals("metroid-zero-mission", result.get(1).getName());
    }

    @Test
    public void getEmptyAllGamesTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRepository)
                .findAll(any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameService.getAll(PageRequest.of(0, 10))
        );
    }

}
