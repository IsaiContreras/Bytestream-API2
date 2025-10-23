package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameCategoryConverter;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.mapper.GameCategoryMapper;
import com.bytestream_api2.games.model.MGameCategory;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameCategoryServiceTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Mock
    private GameCategoryRepository gameCategoryRepository;

    @Mock
    private GameCategoryConverter gameCategoryConverter;

    @Spy
    private final GameCategoryMapper gameCategoryMapper = Mappers.getMapper(GameCategoryMapper.class);

    @InjectMocks
    private GameCategoryService gameCategoryService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public void before() {

    }

    // CUD
    @Test
    public void createValidCategoryTest() {
        // Preparation
        GameCategory gameCategory = new GameCategory("metroidvania");

        // Dependency and method call handlers
        when(gameCategoryRepository.save(any(GameCategory.class)))
                .thenReturn(gameCategory);

        // Assertions
        MGameCategory created = gameCategoryService.create(gameCategory);

        assertNotNull(created);
        assertEquals("metroidvania", created.getName());
    }

    @Test
    public void updateValidCategoryTest() {
        // Preparation
        GameCategory gameCategory = new GameCategory((short)101, "metroid-vania");
        GameCategory oldData = new GameCategory((short)101, "metroidvania");

        // Dependency and method call handlers
        when(gameCategoryRepository.findById(anyShort()))
                .thenReturn(oldData);
        when(gameCategoryRepository.save(any(GameCategory.class)))
                .thenReturn(gameCategory);

        // Assertions
        MGameCategory updated = gameCategoryService.update(gameCategory);

        assertNotNull(updated);
        assertEquals("metroid-vania", updated.getName());
    }

    @Test
    public void updateNonExistentCategoryTest() {
        // Preparation
        GameCategory gameCategory = new GameCategory((short)101, "metroid-vania");

        // Dependency and method call handlers
        when(gameCategoryRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameCategoryService.update(gameCategory));
    }

    @Test
    public void deleteValidCategoryTest() {
        // Dependency and method call handlers
        when(gameCategoryRepository.findById(anyShort()))
                .thenReturn(new GameCategory((short)101, "metroidvania"));
        when(gameCategoryRepository.save(any(GameCategory.class)))
                .thenReturn(null);

        // Assertions
        assertDoesNotThrow(() -> gameCategoryService.delete((short)101));
    }

    @Test
    public void deleteNonExistentCategoryTest() {
        // Dependency and method call handlers
        when(gameCategoryRepository.findById(anyShort()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameCategoryService.delete((short)101));
    }

    @Test
    public void deleteDeletedCategoryTest() {
        // Preparation
        GameCategory gameCategory = new GameCategory((short)101, "metroidvania");
        gameCategory.setDeletedAt(new Date());

        // Dependency and method call handlers
        when(gameCategoryRepository.findById(anyShort()))
                .thenReturn(gameCategory);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameCategoryService.delete((short)101));
    }

    // Queries
    @Test
    public void getValidCategoryByNameTest() {
        // Preparation
        GameCategory gameCategory = new GameCategory((short)101, "metroidvania");

        // Dependency and method call handlers
        when(gameCategoryRepository.findByName(anyString()))
                .thenReturn(gameCategory);

        // Assertions
        MGameCategory result = gameCategoryService.getByName("metroidvania");

        assertNotNull(result);
        assertEquals("metroidvania", result.getName());
    }

    @Test
    public void getNonExistentCategoryByNameTest() {
        // Dependency and method call handlers
        when(gameCategoryRepository.findByName(anyString()))
                .thenReturn(null);

        // Assertions
        assertThrows(EntityNotFoundException.class, () -> gameCategoryService.getByName("metroidvania"));
    }

    @Test
    public void getValidCategoriesByNameContainsTest() {
        // Preparation
        List<MGameCategory> modelCategories = List.of(
                new MGameCategory((short)101, "metroidvania"),
                new MGameCategory((short)105, "multiplayer")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameCategoryConverter.parseToList(anyList()))
                .thenReturn(modelCategories);
        doReturn(pageContent).when(gameCategoryRepository)
                .findByNameContains(anyString(), any(Pageable.class));

        // Assertions
        List<MGameCategory> result = gameCategoryService.getByNameContains(
                "m", PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals("metroidvania", result.get(0).getName());
        assertEquals("multiplayer", result.get(1).getName());
    }

    @Test
    public void getEmptyCategoriesByNameContainsTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameCategoryConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameCategoryRepository)
                .findByNameContains(anyString(), any(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameCategoryService.getByNameContains("m", PageRequest.of(0, 10))
        );
    }

    @Test
    public void getAllCategoriesTest() {
        // Preparation
        List<MGameCategory> modelCategories = List.of(
                new MGameCategory((short)101, "metroidvania"),
                new MGameCategory((short)105, "multiplayer")
        );
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameCategoryConverter.parseToList(anyList()))
                .thenReturn(modelCategories);
        doReturn(pageContent).when(gameCategoryRepository)
                .findAll(nullable(Pageable.class));

        // Assertions
        List<MGameCategory> result = gameCategoryService.getAll(null);

        assertNotNull(result);
        assertEquals("metroidvania", result.get(0).getName());
        assertEquals("multiplayer", result.get(1).getName());
    }

    @Test
    public void getEmptyAllCategoriesTest() {
        // Preparation
        Page<GameCategory> pageContent = new PageImpl<>(new ArrayList<>());

        // Dependency and method call handlers
        when(gameCategoryConverter.parseToList(anyList()))
                .thenReturn(new ArrayList<>());
        doReturn(pageContent).when(gameCategoryRepository)
                .findAll(nullable(Pageable.class));

        // Assertions
        assertThrows(
                EntityNotFoundException.class,
                () -> gameCategoryService.getAll(PageRequest.of(0, 10))
        );
    }

}
