package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.service.GameCategoryService;
import com.bytestream_api2.games.utilities.BodyFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BodyFormatter.result(
                            this.gameCategoryService.create(gameCategory)
                    ));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, ?>> updateGameCategory(
            @Valid @RequestBody GameCategory gameCategory
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameCategoryService.update(gameCategory)
                    ));
        } catch (EntityNotFoundException | DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteGameCategory(
            @RequestParam("id") short id
    ) {
        try {
            this.gameCategoryService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteGameCategory(
            @RequestParam("id") short id
    ) {
        try {
            gameCategoryService.hardDelete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameCategoryService.getByName(name)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/bynamematch")
    public ResponseEntity<Map<String, ?>> getByNameContains(
            @RequestParam("name") String name, Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameCategoryService.getByNameContains(name, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAllCategories(
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameCategoryService.getAll(pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

}
