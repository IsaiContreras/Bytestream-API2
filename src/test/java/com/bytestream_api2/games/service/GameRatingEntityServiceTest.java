package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingEntityConverter;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.exception.services.MediaUploadFailedException;
import com.bytestream_api2.games.exception.imageresource.ImagePathUnresolvedException;
import com.bytestream_api2.games.exception.imageresource.UncaughtImageExtensionException;
import com.bytestream_api2.games.exception.imageresource.UnreadableResourceException;
import com.bytestream_api2.games.mapper.GameRatingEntityMapper;
import com.bytestream_api2.games.utilities.classes.ImageResourcePackage;
import com.bytestream_api2.games.model.MGameRatingEntity;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.utilities.statics.ImageResourceManager;
import jakarta.servlet.ServletContext;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameRatingEntityServiceTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Mock
    private GameRatingEntityRepository gameRatingEntityRepository;

    @Mock
    private GameRatingEntityConverter gameRatingEntityConverter;

    @Spy
    private final GameRatingEntityMapper gameRatingEntityMapper = Mappers.getMapper(GameRatingEntityMapper.class);

    @InjectMocks
    private GameRatingEntityService gameRatingEntityService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // CUD
    @Test
    public void createValidGameRatingEntityTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[] {1}
        );

        MGameRatingEntity result;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

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
            result = gameRatingEntityService.create(gameRatingEntity, mockLogoFile);
        }

        assertNotNull(result);
        assertEquals("ESRB", result.getName());
        assertEquals("Entertainment Software Rating Board", result.getLongName());
        assertEquals("America", result.getLocation());
        assertEquals("Rating organization of america.", result.getDescription());
    }

    @Test
    public void createGameRatingEntityWithInvalidLogoImageTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRatingEntity.class),
                    nullable(String[].class)
            )).thenReturn(false);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameRatingEntityService.create(
                            gameRatingEntity,
                            mockLogoFile
                    )
            );
        }
    }

    @Test
    public void createGameRatingEntityWithEmptyLogoImageTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "",
                "",
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.uploadEntityImage(
                    any(MultipartFile.class),
                    any(MGameRatingEntity.class),
                    nullable(String[].class)
            )).thenReturn(true);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameRatingEntityService.create(
                            gameRatingEntity,
                            mockLogoFile
                    )
            );
        }
    }

    @Test
    public void updateValidGameRatingEntityTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB_",
                "Entertainment Software Rating Board",
                "America",
                "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico."
        );
        GameRatingEntity oldData = new GameRatingEntity(
                (short)501,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );
        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[] {1}
        );

        MGameRatingEntity result;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

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
            result = gameRatingEntityService.update(gameRatingEntity, mockLogoFile);
        }

        assertNotNull(result);
        assertEquals("ESRB_", result.getName());
        assertEquals("Entertainment Software Rating Board", result.getLongName());
        assertEquals("America", result.getLocation());
        assertEquals(
                "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico.",
                result.getDescription()
        );
    }

    @Test
    public void updateNonExistentGameRatingEntityTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB_",
                "Entertainment Software Rating Board",
                "America",
                "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingEntityService.update(gameRatingEntity, mockLogoFile)
        );
    }

    @Test
    public void updateGameRatingEntityWithInvalidLogoImageTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB_",
                "Entertainment Software Rating Board",
                "America",
                "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico."
        );
        GameRatingEntity oldData = new GameRatingEntity(
                (short)501,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.uploadEntityImage(
                            any(MultipartFile.class),
                            any(MGameRatingEntity.class),
                            nullable(String[].class)
                    )
            ).thenReturn(false);

        // Assertions
            assertThrows(
                    MediaUploadFailedException.class,
                    () -> gameRatingEntityService.update(gameRatingEntity, mockLogoFile)
            );
        }
    }

    @Test
    public void updateGameRatingEntityWithEmptyLogoImageTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB_",
                "Entertainment Software Rating Board",
                "America",
                "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico."
        );
        GameRatingEntity oldData = new GameRatingEntity(
                (short)501,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MultipartFile mockLogoFile = new MockMultipartFile(
                "logo",
                "",
                null,
                new byte[0]
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.uploadEntityImage(
                            any(MultipartFile.class),
                            any(MGameRatingEntity.class),
                            any(String[].class)
                    )
            ).thenReturn(true);

        // Assertions
            assertDoesNotThrow(() -> gameRatingEntityService.update(gameRatingEntity, mockLogoFile));
        }
    }

    @Test
    public void deleteValidGameRatingEntityTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(gameRatingEntity);
        when(gameRatingEntityRepository.save(any(GameRatingEntity.class)))
                .thenReturn(null);

        // Assertions
        assertDoesNotThrow(() -> gameRatingEntityService.delete((short)501));
    }

    @Test
    public void deleteNonExistentGameRatingEntityTest() {
        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingEntityService.delete((short)501));
    }

    @Test
    public void deleteDeletedGameRatingEntityTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)501,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );
        gameRatingEntity.setDeletedAt(new Date());

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findById(anyShort()))
                .thenReturn(gameRatingEntity);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingEntityService.delete((short)501));
    }

    // Queries
    @Test
    public void getValidImageLogoTest() throws Exception {
        // Preparation
        ImageResourcePackage resourcePackage = new ImageResourcePackage(
                "ESRB.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[] {1}
        );

        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        ImageResourcePackage result;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.getResourceImage(
                    nullable(ServletContext.class),
                    any(MGameRatingEntity.class),
                    anyString(),
                    anyInt(),
                    anyInt()
            )).thenReturn(resourcePackage);

        // Assertions
             result = gameRatingEntityService.getLogoImage(
                    "ESRB",
                    "ESRB.png",
                    32,
                    32
            );
        }

        assertNotNull(result);
        assertEquals("ESRB.png", result.getFilname());
        assertEquals(MediaType.IMAGE_PNG_VALUE, result.getMediaType().toString());
    }

    @Test
    public void getNonExistentImageLogoTest() {
        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingEntityService.getLogoImage("ESRB", "ESRB.png", null, null)
        );
    }

    @Test
    public void getIOExceptionImageLogoTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.getResourceImage(
                    nullable(ServletContext.class),
                    any(MGameRatingEntity.class),
                    anyString(),
                    nullable(Integer.class),
                    nullable(Integer.class)
            )).thenThrow(new IOException());

            // Assertions
             assertThrows(
                    IOException.class,
                    () -> gameRatingEntityService.getLogoImage(
                            "ESRB",
                            "ESRB.png",
                            null,
                            null
                    )
            );
        }
    }

    @Test
    public void getUnresolvedPathImageLogoTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        Exception exception;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.getResourceImage(
                    nullable(ServletContext.class),
                    any(MGameRatingEntity.class),
                    anyString(),
                    nullable(Integer.class),
                    nullable(Integer.class)
            )).thenThrow(new ImagePathUnresolvedException("Couldn't resolve image file path."));

        // Assertions
            exception = assertThrows(
                    ImagePathUnresolvedException.class,
                    () -> gameRatingEntityService.getLogoImage(
                            "ESRB",
                            "ESRB.png",
                            null,
                            null
                    )
            );
        }

        assertEquals(exception.getMessage(), "Couldn't resolve image file path.");
    }

    @Test
    public void getUnreadableResourceImageLogoTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        Exception exception;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.getResourceImage(
                    nullable(ServletContext.class),
                    any(MGameRatingEntity.class),
                    anyString(),
                    nullable(Integer.class),
                    nullable(Integer.class)
            )).thenThrow(new UnreadableResourceException("Resource couldn't load or is unreadable."));

            // Assertions
            exception = assertThrows(
                    UnreadableResourceException.class,
                    () -> gameRatingEntityService.getLogoImage(
                            "ESRB",
                            "ESRB.png",
                            null,
                            null
                    )
            );
        }

        assertEquals(exception.getMessage(), "Resource couldn't load or is unreadable.");
    }

    @Test
    public void getUncaughtExtensionImageLogoTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        Exception exception;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(() -> ImageResourceManager.getResourceImage(
                    nullable(ServletContext.class),
                    any(MGameRatingEntity.class),
                    anyString(),
                    nullable(Integer.class),
                    nullable(Integer.class)
            )).thenThrow(new UncaughtImageExtensionException("Couldn't caught image file extension."));

            // Assertions
            exception = assertThrows(
                    UncaughtImageExtensionException.class,
                    () -> gameRatingEntityService.getLogoImage(
                            "ESRB",
                            "ESRB.png",
                            null,
                            null
                    )
            );
        }

        assertEquals(exception.getMessage(), "Couldn't caught image file extension.");
    }

    @Test
    public void getValidGameRatingEntityByName() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                (short)101,
                "ESRB",
                "Entertainment Software Rating Board",
                "America",
                "Rating organization of america."
        );

        MGameRatingEntity result;

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRatingEntity.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingEntityService.getByName("ESRB");
        }

        assertNotNull(result);
        assertEquals("ESRB", result.getName());
        assertEquals("Entertainment Software Rating Board", result.getLongName());
        assertEquals("America", result.getLocation());
        assertEquals("Rating organization of america.", result.getDescription());
    }

    @Test
    public void getNonExistentGameRatingEntityByName() {
        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingEntityService.getByName("HSRB"));
    }

    @Test
    public void getAllGameRatingEntitiesTest() {
        // Preparation
        List<MGameRatingEntity> modelEntities = List.of(
                new MGameRatingEntity(
                        (short)101,
                        "ESRB",
                        "Entertainment Software Rating Board",
                        "America",
                        "Rating organization of America."
                ),
                new MGameRatingEntity(
                        (short)102,
                        "PEGI",
                        "Pan European Game Information",
                        "Europe",
                        "Rating organization of Europe."
                )
        );
        Page<GameRatingEntity> pageContent = new PageImpl<>(new ArrayList<>());

        List<MGameRatingEntity> result;

        // Dependency and method call handlers
        when(gameRatingEntityConverter.parseToList(anyList()))
                .thenReturn(modelEntities);
        doReturn(pageContent).when(gameRatingEntityRepository)
                .findAll(any(Pageable.class));

        try (MockedStatic<ImageResourceManager> imageResourceManagerMocked = mockStatic(ImageResourceManager.class)) {
            imageResourceManagerMocked.when(
                    () -> ImageResourceManager.getURIsOfEntityImage(any(MGameRatingEntity.class))
            ).thenAnswer(invocation -> null);

        // Assertions
            result = gameRatingEntityService.getAll(PageRequest.of(0, 10));
        }

        assertNotNull(result);
        assertEquals("ESRB", result.get(0).getName());
        assertEquals("Entertainment Software Rating Board", result.get(0).getLongName());

        assertEquals("PEGI", result.get(1).getName());
        assertEquals("Pan European Game Information", result.get(1).getLongName());
    }

    @Test
    public void getEmptyAllGameRatingEntitiesTest() {
        // Preparation
        Page<GameRatingEntity> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingEntityConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRatingEntityRepository)
                .findAll(any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingEntityService.getAll(PageRequest.of(0, 10))
        );
    }

}
