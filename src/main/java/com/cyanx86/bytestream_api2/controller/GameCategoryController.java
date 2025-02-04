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

    @Autowired
    @Qualifier("game_category_service")
    private GameCategoryService gameCategoryService;

    @PostMapping("/new")
    public boolean addNewGameCategory(@RequestBody @Validated GameCategory gameCategory) {
        return this.gameCategoryService.create(gameCategory);
    }

    @PatchMapping("/update")
    public boolean updateGameCategory(@RequestBody @Validated GameCategory gameCategory) {
        return this.gameCategoryService.update(gameCategory);
    }

    @DeleteMapping("/delete")
    public boolean deleteGameCategory(@RequestParam("id") UUID id) {
        return this.gameCategoryService.delete(id);
    }

    @GetMapping("/get")
    public List<MGameCategory> getAllCategories(Pageable pageable) {
        return this.gameCategoryService.getAll(pageable);
    }

    @GetMapping("/byname")
    public List<MGameCategory> getByName(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return this.gameCategoryService.getByName(name, pageable);
    }

}
