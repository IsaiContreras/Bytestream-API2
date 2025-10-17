package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingConverter;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.mapper.GameRatingMapper;
import com.bytestream_api2.games.model.MGameRating;
import com.bytestream_api2.games.model.MGameRatingEntity;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import com.bytestream_api2.games.utilities.ImageResourceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameRatingServiceTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Mock
    private GameRatingEntityRepository gameRatingEntityRepository;

    @Mock
    private GameRatingRepository gameRatingRepository;

    @Mock
    private GameRatingConverter gameRatingConverter;

    @Spy
    private final GameRatingMapper gameRatingMapper = Mappers.getMapper(GameRatingMapper.class);

    @InjectMocks
    private GameRatingService gameRatingService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // CUD

    @Test
    public void createValidGameRatingTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity("ESRB");
        GameRating gameRating = new GameRating(
                "e-everyone",
                "Suitable for all ages.",
                "ESRB"
        );

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[]{1}
        );

        MGameRating result;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.uploadEntityImage(
                            any(MultipartFile.class),
                            any(MGameRating.class),
                            nullable(String[].class)
                    )
            ).thenReturn(true);
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRating.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingService.create(gameRating, mockFile);
        }

        assertNotNull(result);
        assertEquals("e-everyone", result.getName());
        assertEquals("Suitable for all ages.", result.getDescription());
        assertEquals("ESRB", result.getGameRatingEntity().getName());
    }

    @Test
    public void createGameRatingWithNonExistentGameRatingEntityTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                "e-everyone",
                "Suitable for all ages.",
                "HSRB"
        );

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[]{1}
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(nullable(String.class)))
                .thenReturn(null);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenThrow(DataAccessResourceFailureException.class);

        // Assertions
        assertThrows(DataAccessResourceFailureException.class, () -> gameRatingService.create(gameRating, mockFile));
    }

    @Test
    public void createGameRatingWithInvalidLogoImageTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                "e-everyone",
                "Suitable for all ages.",
                "ESRB"
        );
        GameRatingEntity gameRatingEntity = new GameRatingEntity("ESRB");

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[]{1}
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.uploadEntityImage(
                            any(MultipartFile.class),
                            any(MGameRatingEntity.class),
                            any(String[].class)
                    )
            ).thenReturn(false);

        // Assertions
            assertThrows(MediaUploadFailedException.class, () -> gameRatingService.create(gameRating, mockFile));
        }
    }

    @Test
    public void createGameRatingWithEmptyLogoImageTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                "e-everyone",
                "Suitable for all ages.",
                "ESRB"
        );
        GameRatingEntity gameRatingEntity = new GameRatingEntity("ESRB");

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.uploadEntityImage(
                            any(MultipartFile.class),
                            any(MGameRatingEntity.class),
                            any(String[].class)
                    )
            ).thenReturn(true);

        // Assertions
            assertThrows(MediaUploadFailedException.class, () -> gameRatingService.create(gameRating, mockFile));
        }
    }

    @Test
    public void updateValidGameRatingTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );
        GameRating oldData = new GameRating(
                (short)101,
                "e-everyone",
                "Suitable for all ages.",
                "PEGI"
        );

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[]{1}
        );

        MGameRating result;

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(new GameRatingEntity("ESRB", "Entertainment Software Rating Board"));
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRating.class),
                    nullable(String[].class)
            )).thenReturn(true);
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRatingEntity.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingService.update(gameRating, mockFile);
        }

        assertNotNull(result);
        assertEquals("everyone", result.getName());
        assertEquals("Suitable.", result.getDescription());
        assertEquals("ESRB", result.getGameRatingEntity().getName());
        assertEquals("Entertainment Software Rating Board", result.getGameRatingEntity().getLongName());
    }

    @Test
    public void updateNonExistentGameRatingTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingService.update(gameRating, null));
    }

    @Test
    public void updateGameRatingWithNonExistentGameRatingEntityTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "HSRB"
        );
        GameRating gameRatingNoChanges = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );
        GameRating oldData = new GameRating(
                (short)101,
                "e-everyone",
                "Suitable for all ages.",
                "ESRB"
        );

        MGameRating result;

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRatingNoChanges);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRatingEntity.class),
                    nullable(String[].class)
            )).thenReturn(true);
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRatingEntity.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingService.update(gameRating, null);
        }

        assertNotNull(result);
        assertEquals("ESRB", result.getGameRatingEntity().getName());
    }

    @Test
    public void updateGameRatingWithInvalidLogoImageTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[]{1}
        );

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(gameRating);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(new GameRatingEntity("ESRB"));
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRatingEntity.class),
                    nullable(String[].class)
            )).thenReturn(false);

        // Assertions
            assertThrows(MediaUploadFailedException.class, () -> gameRatingService.update(gameRating, mockFile));
        }
    }

    @Test
    public void updateGameRatingWithEmptyLogoImageTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );

        MultipartFile mockFile = new MockMultipartFile(
                "logo",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(gameRating);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(new GameRatingEntity("ESRB"));
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRatingEntity.class),
                    nullable(String[].class)
            )).thenReturn(true);

        // Assertions
            assertThrows(MediaUploadFailedException.class, () -> gameRatingService.update(gameRating, mockFile));
        }
    }

    @Test
    public void deleteValidGameRatingTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(gameRating);
        when(gameRatingRepository.save(any(GameRating.class)))
                .thenReturn(null);

        // Assertions
        assertDoesNotThrow(() -> gameRatingService.delete((short)101));
    }

    @Test
    public void deleteNonExistentGameRatingTest() {
        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingService.delete((short)101));
    }

    @Test
    public void deleteDeletedGameRatingTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "everyone",
                "Suitable.",
                "ESRB"
        );
        gameRating.setDeletedAt(new Date());

        // Dependency and method call handlers
        when(gameRatingRepository.findById(anyShort()))
                .thenReturn(gameRating);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingService.delete((short)101));
    }

    // Queries
    @Test
    public void getValidLogoImageTest() {
        // Preparation

        // Dependency and method call handlers

        // Assertions
    }

    @Test
    public void getNonExistentLogoImageTest() {
        // Preparation

        // Dependency and method call handlers

        // Assertions
    }

    @Test
    public void getValidGameRatingByNameTest() {
        // Preparation
        GameRating gameRating = new GameRating(
                (short)101,
                "e-everyone",
                "Suitable for all ages.",
                "ESRB"
        );

        MGameRating result;

        // Dependency and method call handlers
        when(gameRatingRepository.findByName(anyString()))
                .thenReturn(gameRating);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRating.class))
            ).thenAnswer(invocation -> null);

            result = gameRatingService.getByName("e-everyone");
        }

        // Assertions
        assertNotNull(result);
        assertEquals("e-everyone", result.getName());
        assertEquals("Suitable for all ages.", result.getDescription());
    }

    @Test
    public void getNonExistentGameRatingByNameTest() {
        // Dependency and method call handlers
        when(gameRatingRepository.findByName(anyString())).thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingService.getByName("e-everyone"));
    }

    @Test
    public void getGameRatingsByRatingEntityTest() {
        // Preparation
        List<MGameRating> modelRatings = List.of(
                new MGameRating(
                        (short)101,
                        "e-everyone",
                        "Suitable for all ages."
                ),
                new MGameRating(
                        (short)102,
                        "e+10",
                        "Suitable for ages over 10."
                )
        );
        Page<GameRating> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGameRating> result;

        // Dependency and method call handlers
        when(gameRatingConverter.parseToList(anyList()))
                .thenReturn(modelRatings);
        doReturn(pageContent).when(gameRatingRepository)
                .findByGameRatingEntity(nullable(GameRatingEntity.class), any(Pageable.class));
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRating.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingService.getByGameRatingEntity("ESRB", PageRequest.of(0, 10));
        }

        assertNotNull(result);
        assertEquals("e-everyone", result.get(0).getName());
        assertEquals("Suitable for all ages.", result.get(0).getDescription());

        assertEquals("e+10", result.get(1).getName());
        assertEquals("Suitable for ages over 10.", result.get(1).getDescription());
    }

    @Test
    public void getEmptyGameRatingsByRatingEntityTest() {
        // Preparation
        Page<GameRating> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRatingRepository)
                .findByGameRatingEntity(nullable(GameRatingEntity.class), any(Pageable.class));
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingService.getByGameRatingEntity("ESRB", PageRequest.of(0, 10))
        );
    }

    @Test
    public void getAllGameRatingsTest() {
        // Preparation
        List<MGameRating> modelRatings = List.of(
                new MGameRating(
                        (short)101,
                        "e-everyone",
                        "Suitable for all ages."
                ),
                new MGameRating(
                        (short)102,
                        "e+10",
                        "Suitable for ages over 10."
                )
        );
        Page<GameRatingEntity> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGameRating> result;

        // Dependency and method call handlers
        when(gameRatingConverter.parseToList(anyList()))
                .thenReturn(modelRatings);
        doReturn(pageContent).when(gameRatingRepository)
                .findAll(any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRatingEntity.class))
            ).thenAnswer(invocation -> null);

            // Assertions
            result = gameRatingService.getAll(PageRequest.of(0, 10));
        }

        assertNotNull(result);
        assertEquals("e-everyone", result.get(0).getName());
        assertEquals("Suitable for all ages.", result.get(0).getDescription());

        assertEquals("e+10", result.get(1).getName());
        assertEquals("Suitable for ages over 10.", result.get(1).getDescription());
    }

    @Test
    public void getEmtpyAllGameRatingsTest() {
        // Preparation
        Page<GameRatingEntity> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRatingRepository)
                .findAll(any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingService.getAll(PageRequest.of(0, 10))
        );
    }

}
