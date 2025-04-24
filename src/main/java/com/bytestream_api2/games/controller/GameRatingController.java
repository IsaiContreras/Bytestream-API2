package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.service.GameRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    public ResponseEntity<Map<String, ?>> addNewRating(
            @Valid @RequestPart("data") GameRating gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        return ratingService.create(gameRating, logoImage);
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> updateRating(
            @Valid @RequestPart("data") GameRating gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        return ratingService.update(gameRating, logoImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRating(
            @RequestParam("id") short id
    ) {
        return ratingService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        return ratingService.getByName(name);
    }

    @GetMapping("/get/byentity")
    public ResponseEntity<Map<String, ?>> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return ratingService.getByGameRatingEntity(name, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(
            Pageable pageable
    ) {
        return ratingService.getAll(pageable);
    }

}
