package com.cyanx86.bytestream_api2.controller;

import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.model.MGameRating;
import com.cyanx86.bytestream_api2.service.GameRatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ratings")
public class GameRatingController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Autowired
    @Qualifier("game_rating_service")
    private GameRatingService ratingService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping(value="/create", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addNewRating(
            @RequestPart("data") @Validated String gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        GameRating gameRatingObject;
        try {
            gameRatingObject = new ObjectMapper()
                    .readValue(gameRating, GameRating.class);
        } catch (Exception e) { return false; }
        return ratingService.create(gameRatingObject, logoImage);
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean updateRating(
            @RequestPart("data") @Validated String gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        GameRating gameRatingObject;
        try {
            gameRatingObject = new ObjectMapper()
                    .readValue(gameRating, GameRating.class);
        } catch (Exception e) { return false; }
        return ratingService.update(gameRatingObject, logoImage);
    }

    @DeleteMapping("/delete")
    public boolean deleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public boolean hardDeleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public MGameRating getByName(
            @RequestParam("name") String name
    ) {
        return ratingService.getByName(name);
    }

    @GetMapping("/get/byentity")
    public List<MGameRating> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return ratingService.getByGameRatingEntity(name, pageable);
    }

    @GetMapping("/get")
    public List<MGameRating> getAll(
            Pageable pageable
    ) {
        return ratingService.getAll(pageable);
    }

}
