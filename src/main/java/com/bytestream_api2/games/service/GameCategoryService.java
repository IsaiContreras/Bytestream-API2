package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameCategoryConverter;
import com.bytestream_api2.games.mapper.GameCategoryMapper;
import com.bytestream_api2.games.model.MGame;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.model.MGameCategory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> create(GameCategory gameCategory) {
        try {
            GameCategory result = gameCategoryRepository.save(gameCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MGameCategory(result));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> update(GameCategory gameCategory) {
        try {
            GameCategory categoryToUpdate = gameCategoryRepository.findById(gameCategory.getId());
            if (categoryToUpdate == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            categoryMapper.partialUpdateCategory(categoryToUpdate, gameCategory);

            gameCategoryRepository.save(categoryToUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(new MGameCategory(categoryToUpdate));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> delete(short id) {
        try {
            GameCategory category = gameCategoryRepository.findById(id);
            category.setDeletedAt(new Date());

            gameCategoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> hardDelete(short id) {
        try {
            gameCategoryRepository.delete(gameCategoryRepository.findById(id));
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Queries
    public ResponseEntity<MGameCategory> getByName(String name){
        GameCategory category = gameCategoryRepository.findByName(name);
        if (category == null || category.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(new MGameCategory(category));
    }

    public ResponseEntity<?> getByNameContains(String name, Pageable pageable) {
        List<MGameCategory> results = gameCategoryConverter
                .parseToList(gameCategoryRepository.findByNameContains(name, pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    public ResponseEntity<?> getAll(Pageable pageable) {
        List<MGameCategory> results = gameCategoryConverter
                .parseToList(gameCategoryRepository.findAll(pageable).getContent())
                .stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

}
