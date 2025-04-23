package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.model.MGameRating;
import com.bytestream_api2.games.service.GameRatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<?> addNewRating(
            @RequestPart("data") @Validated String gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        GameRating gameRatingObject;
        try {
            gameRatingObject = new ObjectMapper()
                    .readValue(gameRating, GameRating.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return ratingService.create(gameRatingObject, logoImage);
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRating(
            @RequestPart("data") @Validated String gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        GameRating gameRatingObject;
        try {
            gameRatingObject = new ObjectMapper()
                    .readValue(gameRating, GameRating.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return ratingService.update(gameRatingObject, logoImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<?> hardDeleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<MGameRating> getByName(
            @RequestParam("name") String name
    ) {
        return ratingService.getByName(name);
    }

    @GetMapping("/get/byentity")
    public ResponseEntity<?> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return ratingService.getByGameRatingEntity(name, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAll(
            Pageable pageable
    ) {
        return ratingService.getAll(pageable);
    }

}
