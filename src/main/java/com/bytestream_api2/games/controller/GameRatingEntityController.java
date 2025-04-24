package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingEntity;
import com.bytestream_api2.games.service.GameRatingEntityService;
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
    public ResponseEntity<?> addNewRatingEntity(
            @RequestPart("data") @Validated String gameRatingEntity,
            @RequestPart("logo") MultipartFile logoImage
    )  {
        GameRatingEntity gameRatingEntityObject;
        try {
            gameRatingEntityObject = new ObjectMapper()
                    .readValue(gameRatingEntity, GameRatingEntity.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return ratingEntityService.create(gameRatingEntityObject, logoImage);
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRatingEntity (
            @RequestPart("data") @Validated String gameRatingEntity,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        GameRatingEntity gameRatingEntityObject;
        try {
            gameRatingEntityObject = new ObjectMapper()
                    .readValue(gameRatingEntity, GameRatingEntity.class);
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); }
        return ratingEntityService.update(gameRatingEntityObject, logoImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRatingEntity (
            @RequestParam("id") short id
    ) {
        return ratingEntityService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<?> hardDeleteRatingEntity(
            @RequestParam("id") short id
    ) {
        return ratingEntityService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<MGameRatingEntity> getByName(
            @RequestParam("name") String name
    ) {
        return ratingEntityService.getByName(name);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ratingEntityService.getAll(pageable);
    }

}
