package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public boolean addNewGame(
            @RequestPart("data") @Validated String game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        Game gameObject;
        try {
            gameObject = new ObjectMapper()
                    .readValue(game, Game.class);
        } catch (Exception e) { return false; }
        return this.gameService.create(gameObject, coverImage, landscapeImage);
    }

    @PatchMapping("/update")
    public boolean updateGame(
            @RequestPart("data") @Validated String game,
            @RequestPart("cover") MultipartFile coverImage,
            @RequestPart("landscape") MultipartFile landscapeImage
    ) {
        Game gameObject;
        try {
            gameObject = new ObjectMapper()
                    .readValue(game, Game.class);
        } catch (Exception e) { return false; }
        return this.gameService.update(gameObject, coverImage, landscapeImage);
    }

    @DeleteMapping("/delete")
    public boolean deleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public boolean hardDeleteGame(
            @RequestParam("id") long id
    ) {
        return this.gameService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public MGame getGameByName(
            @RequestParam("name") String name
    ) {
        return this.gameService.getByName(name);
    }

    @GetMapping("/get/bytitle")
    public List<MGame> getGameByTitle(
            @RequestParam("title") String title,
            Pageable pageable
    ) {
        return gameService.getByTitle(title, pageable);
    }

    @GetMapping("/get/bycategories")
    public List<MGame> getGamesByCategories(
            @RequestParam("categories") List<GameCategory> categories,
            Pageable pageable
    ) {
        return gameService.getByGameCategories(categories, pageable);
    }

    @GetMapping("/get/byratings")
    public List<MGame> getGamesByRatings(
            @RequestParam("ratings") List<GameRating> ratings,
            Pageable pageable
    ) {
        return gameService.getByGameRatings(ratings, pageable);
    }

    @GetMapping("/get/bydescriptors")
    public List<MGame> getGamesByDescriptors(
            @RequestParam("descriptors") List<GameRatingDescriptor> ratingDescriptors,
            Pageable pageable
    ) {
        return gameService.getByGameRatingDescriptors(ratingDescriptors, pageable);
    }

    @GetMapping("/get")
    public List<MGame> getAllGames(
            Pageable pageable
    ) {
        return this.gameService.getAll(pageable);
    }

}
