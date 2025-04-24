package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.service.GameService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, ?>> addNewGame(
            @Valid @RequestPart("data") Game game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        return this.gameService.create(game, coverImage, landscapeImage);
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, ?>> updateGame(
            @Valid @RequestPart("data") Game game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        return this.gameService.update(game, coverImage, landscapeImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getGameByName(
            @RequestParam("name") String name
    ) {
        return this.gameService.getByName(name);
    }

    @GetMapping("/get/bytitle")
    public ResponseEntity<Map<String, ?>> getGameByTitle(
            @RequestParam("title") String title,
            Pageable pageable
    ) {
        return gameService.getByTitle(title, pageable);
    }

    @GetMapping("/get/bycategories")
    public ResponseEntity<Map<String, ?>> getGamesByCategories(
            @RequestParam("categories") List<GameCategory> categories,
            Pageable pageable
    ) {
        return gameService.getByGameCategories(categories, pageable);
    }

    @GetMapping("/get/byratings")
    public ResponseEntity<Map<String, ?>> getGamesByRatings(
            @RequestParam("ratings") List<GameRating> ratings,
            Pageable pageable
    ) {
        return gameService.getByGameRatings(ratings, pageable);
    }

    @GetMapping("/get/bydescriptors")
    public ResponseEntity<Map<String, ?>> getGamesByDescriptors(
            @RequestParam("descriptors") List<GameRatingDescriptor> ratingDescriptors,
            Pageable pageable
    ) {
        return gameService.getByGameRatingDescriptors(ratingDescriptors, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAllGames(
            Pageable pageable
    ) {
        return this.gameService.getAll(pageable);
    }

}
