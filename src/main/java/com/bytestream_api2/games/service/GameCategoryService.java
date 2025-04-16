package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameCategoryConverter;
import com.bytestream_api2.games.mapper.GameCategoryMapper;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.model.MGameCategory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    @Autowired
    @Qualifier("game_category_repository")
    private GameCategoryRepository gameCategoryRepository;

    @Autowired
    @Qualifier("game_category_converter")
    private GameCategoryConverter gameCategoryConverter;

    private final GameCategoryMapper categoryMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameCategoryService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameCategoryService(GameCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    // CUD
    public boolean create(GameCategory gameCategory) {
        try {
            gameCategoryRepository.save(gameCategory);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(GameCategory gameCategory) {
        try {
            GameCategory categoryToUpdate = gameCategoryRepository.findById(gameCategory.getId());
            if (categoryToUpdate == null)
                return false;

            categoryMapper.partialUpdateCategory(categoryToUpdate, gameCategory);

            gameCategoryRepository.save(categoryToUpdate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(short id) {
        try {
            GameCategory category = gameCategoryRepository.findById(id);
            category.setDeletedAt(new Date());

            gameCategoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hardDelete(short id) {
        try {
            gameCategoryRepository.delete(gameCategoryRepository.findById(id));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public MGameCategory getByName(String name){
        GameCategory category = gameCategoryRepository.findByName(name);
        if (category == null || category.getDeletedAt() != null)
            return null;

        return new MGameCategory(category);
    }

    public List<MGameCategory> getByNameContains(String name, Pageable pageable) {
        return gameCategoryConverter.parseToList(
                gameCategoryRepository.findByNameContains(name, pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
    }

    public List<MGameCategory> getAll(Pageable pageable) {
        return gameCategoryConverter.parseToList(
                gameCategoryRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
    }

}
