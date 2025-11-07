package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameCategoryConverter;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.mapper.GameCategoryMapper;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.model.MGameCategory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("game_category_service")
public class GameCategoryService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    private final GameCategoryRepository gameCategoryRepository;
    private final GameCategoryConverter gameCategoryConverter;
    private final GameCategoryMapper gameCategoryMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameCategoryService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameCategoryService(
            @Qualifier("game_category_repository") GameCategoryRepository gameCategoryRepository,
            @Qualifier("game_category_converter") GameCategoryConverter gameCategoryConverter,
            GameCategoryMapper categoryMapper
    ) {
        this.gameCategoryRepository = gameCategoryRepository;
        this.gameCategoryConverter = gameCategoryConverter;
        this.gameCategoryMapper = categoryMapper;
    }

    // CUD
    public MGameCategory create(@NotNull GameCategory gameCategory) {
        return new MGameCategory(gameCategoryRepository.save(gameCategory));
    }

    public MGameCategory update(@NotNull GameCategory gameCategory) {
        GameCategory categoryToUpdate = gameCategoryRepository.findById(gameCategory.getId());
        if (categoryToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Game category with this ID.");

        gameCategoryMapper.partialUpdateCategory(categoryToUpdate, gameCategory);

        return new MGameCategory(gameCategoryRepository.save(categoryToUpdate));
    }

    public void delete(short id) {
        GameCategory category = gameCategoryRepository.findById(id);
        if (category == null || category.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Game category with this ID.");

        category.setDeletedAt(new Date());
        gameCategoryRepository.save(category);
    }

    public void hardDelete(short id) {
        gameCategoryRepository.delete(gameCategoryRepository.findById(id));
    }

    // Queries
    public MGameCategory getByName(String name){
        GameCategory category = gameCategoryRepository.findByName(name);
        if (category == null || category.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Game category with this name.");

        return new MGameCategory(category);
    }

    public List<MGameCategory> getByNameContains(String name, Pageable pageable) {
        List<MGameCategory> results = gameCategoryConverter
                .parseToList(gameCategoryRepository.findByNameContains(name, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

    public List<MGameCategory> getAll(Pageable pageable) {
        List<MGameCategory> results = gameCategoryConverter
                .parseToList(gameCategoryRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

}
