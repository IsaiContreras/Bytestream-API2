package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.service.GameRatingEntityService;
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
@RequestMapping("/rating_entity")
public class GameRatingEntityController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    @Autowired
    @Qualifier("game_rating_entity_service")
    private GameRatingEntityService ratingEntityService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping(value="/create", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> addNewRatingEntity(
            @Valid @RequestPart("data") GameRatingEntity gameRatingEntity,
            @RequestPart("logo") MultipartFile logoImage
    )  {
        return ratingEntityService.create(gameRatingEntity, logoImage);
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> updateRatingEntity (
            @Valid @RequestPart("data") GameRatingEntity gameRatingEntity,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        return ratingEntityService.update(gameRatingEntity, logoImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteRatingEntity (
            @RequestParam("id") short id
    ) {
        return ratingEntityService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRatingEntity(
            @RequestParam("id") short id
    ) {
        return ratingEntityService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        return ratingEntityService.getByName(name);
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(Pageable pageable) {
        return ratingEntityService.getAll(pageable);
    }

}
