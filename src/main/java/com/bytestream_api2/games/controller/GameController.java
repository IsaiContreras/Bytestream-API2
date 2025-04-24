package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.model.MGame;
import com.bytestream_api2.games.service.GameService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    @Autowired
    @Qualifier("game_service")
    private GameService gameService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping("/create")
    public ResponseEntity<?> addNewGame(
            @RequestPart("data") @Validated String game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        Game gameObject;
        try {
            gameObject = new ObjectMapper()
                    .readValue(game, Game.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return this.gameService.create(gameObject, coverImage, landscapeImage);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateGame(
            @RequestPart("data") @Validated String game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        Game gameObject;
        try {
            gameObject = new ObjectMapper()
                    .readValue(game, Game.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return this.gameService.update(gameObject, coverImage, landscapeImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<?> hardDeleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<MGame> getGameByName(
            @RequestParam("name") String name
    ) {
        return this.gameService.getByName(name);
    }

    @GetMapping("/get/bytitle")
    public ResponseEntity<?> getGameByTitle(
            @RequestParam("title") String title,
            Pageable pageable
    ) {
        return gameService.getByTitle(title, pageable);
    }

    @GetMapping("/get/bycategories")
    public ResponseEntity<?> getGamesByCategories(
            @RequestParam("categories") List<GameCategory> categories,
            Pageable pageable
    ) {
        return gameService.getByGameCategories(categories, pageable);
    }

    @GetMapping("/get/byratings")
    public ResponseEntity<?> getGamesByRatings(
            @RequestParam("ratings") List<GameRating> ratings,
            Pageable pageable
    ) {
        return gameService.getByGameRatings(ratings, pageable);
    }

    @GetMapping("/get/bydescriptors")
    public ResponseEntity<?> getGamesByDescriptors(
            @RequestParam("descriptors") List<GameRatingDescriptor> ratingDescriptors,
            Pageable pageable
    ) {
        return gameService.getByGameRatingDescriptors(ratingDescriptors, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllGames(
            Pageable pageable
    ) {
        return this.gameService.getAll(pageable);
    }

}
