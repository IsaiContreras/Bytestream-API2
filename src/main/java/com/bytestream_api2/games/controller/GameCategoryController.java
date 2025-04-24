package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.service.GameCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/categories")
public class GameCategoryController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    @Autowired
    @Qualifier("game_category_service")
    private GameCategoryService gameCategoryService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping("/create")
    public ResponseEntity<Map<String, ?>> addNewGameCategory(
            @Valid @RequestBody GameCategory gameCategory
    ) {
        return this.gameCategoryService.create(gameCategory);
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, ?>> updateGameCategory(
            @Valid @RequestBody GameCategory gameCategory
    ) {
        return this.gameCategoryService.update(gameCategory);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteGameCategory(
            @RequestParam("id") short id
    ) {
        return this.gameCategoryService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteGameCategory(
            @RequestParam("id") short id
    ) {
        return gameCategoryService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        return this.gameCategoryService.getByName(name);
    }

    @GetMapping("/get/bynamematch")
    public ResponseEntity<Map<String, ?>> getByNameContains(
            @RequestParam("name") String name, Pageable pageable
    ) {
        return this.gameCategoryService.getByNameContains(name, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAllCategories(
            Pageable pageable
    ) {
        return this.gameCategoryService.getAll(pageable);
    }

}
