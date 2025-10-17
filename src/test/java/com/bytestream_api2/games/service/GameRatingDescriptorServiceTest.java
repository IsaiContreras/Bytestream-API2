package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingDescriptorConverter;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.mapper.GameRatingDescriptorMapper;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameRatingDescriptorServiceTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Mock
    private GameRatingEntityRepository gameRatingEntityRepository;

    @Mock
    private GameRatingDescriptorRepository gameRatingDescriptorRepository;

    @Mock
    private GameRatingDescriptorConverter gameRatingDescriptorConverter;

    @Spy
    private final GameRatingDescriptorMapper gameRatingDescriptorMapper =
            Mappers.getMapper(GameRatingDescriptorMapper.class);

    @InjectMocks
    private GameRatingDescriptorService gameRatingDescriptorService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // CUD
    @Test
    public void createValidGameRatingDescriptorTest() {
        // Preparation
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB"
        );
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                "alcohol-ref",
                "Depictions of consumption of alcohol.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(nullable(String.class)))
                .thenReturn(gameRatingEntity);
        when(gameRatingDescriptorRepository.save(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        // Assertions
        MGameRatingDescriptor result = gameRatingDescriptorService.create(gameRatingDescriptor);

        assertNotNull(result);
        assertEquals("alcohol-ref", result.getName());
        assertEquals("Depictions of consumption of alcohol.", result.getDescription());
        assertEquals("ESRB", gameRatingDescriptor.getGameRatingEntity().getName());
    }

    @Test
    public void createGameRatingDescriptorWithNonExistentGameRatingEntityTest() {
        // Preparation
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                "alcohol-ref",
                "Depictions of consumption of alcohol.",
                "HFAD"
        );

        // Dependency and method call handlers
        when(gameRatingEntityRepository.findByName(nullable(String.class)))
                .thenReturn(null);
        when(gameRatingDescriptorRepository.save(any(GameRatingDescriptor.class)))
                .thenThrow(DataAccessResourceFailureException.class);

        // Assertions
        assertThrows(DataAccessResourceFailureException.class, () -> gameRatingDescriptorService.create(gameRatingDescriptor));
    }

    @Test
    public void updateValidGameRatingDescriptorTest() {
        // Preparation
        GameRatingDescriptor oldData = new GameRatingDescriptor(
                "alcohol-ref",
                "Depictions of consumption of alcohol.",
                "ESRB"
        );
        GameRatingEntity gameRatingEntity = new GameRatingEntity(
                "ESRB"
        );
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                (short)102,
                "alcoholref",
                "Depictions of alcohol.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(gameRatingEntity);
        when(gameRatingDescriptorRepository.save(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        // Assertions
        MGameRatingDescriptor updated = gameRatingDescriptorService.update(gameRatingDescriptor);

        assertNotNull(updated);
        assertEquals("alcoholref", updated.getName());
        assertEquals("Depictions of alcohol.", updated.getDescription());
    }

    @Test
    public void updateNonExistentGameRatingDescriptorTest() {
        // Preparation
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                (short)102,
                "alcoholref",
                "Depictions of alcohol.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                        .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingDescriptorService.update(gameRatingDescriptor));
    }

    @Test
    public void updateGameRatingDescriptorWithNonExistentGameRatingEntityTest() {
        // Preparation
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                (short)102,
                "alcoholref",
                "Depictions of alcohol.",
                "HSRG"
        );
        GameRatingDescriptor gameRatingDescriptorNoChanges = new GameRatingDescriptor(
                (short)102,
                "alcoholref",
                "Depictions of alcohol.",
                "ESRB"
        );
        GameRatingDescriptor oldData = new GameRatingDescriptor(
                "alcohol-ref",
                "Depictions of consumption of alcohol.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);
        when(gameRatingDescriptorRepository.save(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptorNoChanges);

        // Assertions
        MGameRatingDescriptor result = gameRatingDescriptorService.update(gameRatingDescriptor);

        assertNotNull(result);
        assertEquals("ESRB", result.getGameRatingEntity().getName());
    }

    @Test
    public void deleteValidGameRatingDescriptorTest() {
        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                .thenReturn(new GameRatingDescriptor("alcohol-ref"));
        when(gameRatingDescriptorRepository.save(any(GameRatingDescriptor.class)))
                .thenReturn(null);

        // Assertions
        assertDoesNotThrow(() -> gameRatingDescriptorService.delete((short)101));
    }

    @Test
    public void deleteNonExistentGameRatingDescriptorTest() {
        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingDescriptorService.delete((short)101));
    }

    @Test
    public void deleteDeletedGameRatingDescriptorTest() {
        // Preparation
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor("alcohol-ref");
        gameRatingDescriptor.setDeletedAt(new Date());

        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findById(anyShort()))
                .thenReturn(gameRatingDescriptor);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingDescriptorService.delete((short)101));
    }

    // Queries
    @Test
    public void getValidGameRatingDescriptorByNameTest() {
        // Preparations
        GameRatingDescriptor gameRatingDescriptor = new GameRatingDescriptor(
                (short)101,
                "violence",
                "Violence content.",
                "ESRB"
        );

        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findByName(anyString()))
                .thenReturn(gameRatingDescriptor);

        // Assertions
        MGameRatingDescriptor result = gameRatingDescriptorService.getByName("violence");

        assertNotNull(result);
        assertEquals("violence", result.getName());
        assertEquals("Violence content.", result.getDescription());
        assertEquals("ESRB", result.getGameRatingEntity().getName());
    }

    @Test
    public void getNonExistentGameRatingDescriptorByNameTest() {
        // Dependency and method call handlers
        when(gameRatingDescriptorRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameRatingDescriptorService.getByName("violence"));
    }

    @Test
    public void getValidGameRatingDescriptorsByGameRatingEntityTest() {
        // Preparation
        List<MGameRatingDescriptor> modelRatingDescriptors = List.of(
                new MGameRatingDescriptor((short)101, "violence"),
                new MGameRatingDescriptor((short)102, "use-of-alcohol")
        );
        Page<GameRatingDescriptor> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingDescriptorConverter.parseToList(anyList()))
                .thenReturn(modelRatingDescriptors);
        doReturn(pageContent).when(gameRatingDescriptorRepository)
                .findByGameRatingEntity(nullable(GameRatingEntity.class), any(Pageable.class));
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        List<MGameRatingDescriptor> result = gameRatingDescriptorService.getByRatingEntity(
                "ESRB",
                PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals("violence", result.get(0).getName());
        assertEquals("use-of-alcohol", result.get(1).getName());
    }

    @Test
    public void getEmptyGameRatingDescriptorsByGameRatingEntityTest() {
        // Preparation
        Page<GameRatingDescriptor> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingDescriptorConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRatingDescriptorRepository)
                .findByGameRatingEntity(nullable(GameRatingEntity.class), any(Pageable.class));
        when(gameRatingEntityRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingDescriptorService.getByRatingEntity(
                        "f",
                        PageRequest.of(0, 10)
                )
        );
    }

    @Test
    public void getAllGameRatingDescriptorsTest() {
        // Preparation
        List<MGameRatingDescriptor> modelRatingDescriptors = List.of(
                new MGameRatingDescriptor((short)101, "violence"),
                new MGameRatingDescriptor((short)101, "use-of-alcohol"),
                new MGameRatingDescriptor((short)102, "purchases")
        );
        Page<GameRatingDescriptor> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingDescriptorConverter.parseToList(anyList()))
                .thenReturn(modelRatingDescriptors);
        doReturn(pageContent).when(gameRatingDescriptorRepository)
                .findAll(any(Pageable.class));

        // Assertions
        List<MGameRatingDescriptor> result = gameRatingDescriptorService.getAll(
                PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals("violence", result.get(0).getName());
        assertEquals("use-of-alcohol", result.get(1).getName());
        assertEquals("purchases", result.get(2).getName());
    }

    @Test
    public void getEmptyAllGameRatingDescriptorsTest() {
        // Preparation
        Page<GameRatingDescriptor> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameRatingDescriptorConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameRatingDescriptorRepository)
                .findAll(any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameRatingDescriptorService.getAll(PageRequest.of(0, 10))
        );
    }

}
