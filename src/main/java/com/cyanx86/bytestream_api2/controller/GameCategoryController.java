package com.cyanx86.bytestream_api2.controller;

import com.cyanx86.bytestream_api2.entity.GameCategory;
import com.cyanx86.bytestream_api2.model.MGameCategory;
import com.cyanx86.bytestream_api2.service.GameCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public boolean addNewGameCategory(
            @RequestBody @Validated GameCategory gameCategory
    ) {
        return this.gameCategoryService.create(gameCategory);
    }

    @PatchMapping("/update")
    public boolean updateGameCategory(
            @RequestBody @Validated GameCategory gameCategory
    ) {
        return this.gameCategoryService.update(gameCategory);
    }

    @DeleteMapping("/delete")
    public boolean deleteGameCategory(
            @RequestParam("id") UUID id
    ) {
        return this.gameCategoryService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public boolean hardDeleteGameCategory(
            @RequestParam("id") UUID id
    ) {
        return gameCategoryService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public MGameCategory getByName(
            @RequestParam("name") String name
    ) {
        return this.gameCategoryService.getByName(name);
    }

    @GetMapping("/get/bynamematch")
    public List<MGameCategory> getByNameContains(
            @RequestParam("name") String name, Pageable pageable
    ) {
        return this.gameCategoryService.getByNameContains(name, pageable);
    }

    @GetMapping("/get")
    public List<MGameCategory> getAllCategories(
            Pageable pageable
    ) {
        return this.gameCategoryService.getAll(pageable);
    }

}
