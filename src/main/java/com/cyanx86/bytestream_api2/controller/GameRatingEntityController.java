package com.cyanx86.bytestream_api2.controller;

import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import com.cyanx86.bytestream_api2.model.MGameRatingEntity;
import com.cyanx86.bytestream_api2.service.GameRatingEntityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rating_entity")
public class GameRatingEntityController {

    @Autowired
    @Qualifier("game_rating_entity_service")
    private GameRatingEntityService ratingEntityService;

    @PostMapping(value="/new", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addNewRatingEntity(
            @RequestPart("data") @Validated String gameRatingEntity,
            @RequestPart("logo") MultipartFile logoImage
    ) throws JsonProcessingException {
        GameRatingEntity gameRatingEntityObject = new ObjectMapper().readValue(gameRatingEntity, GameRatingEntity.class);
        return ratingEntityService.create(gameRatingEntityObject, logoImage);
    }

    @PatchMapping("/update")
    public boolean updateRatingEntity (
            @RequestBody @Validated GameRatingEntity gameRatingEntity,
            @RequestParam("logo") MultipartFile logoImage
    ) {
        return ratingEntityService.update(gameRatingEntity, logoImage);
    }

    @DeleteMapping("/delete")
    public boolean deleteRatingEntity (
            @RequestParam("id") UUID id
    ) {
        return ratingEntityService.delete(id);
    }

    @GetMapping("/get")
    public List<MGameRatingEntity> getAll(Pageable pageable) {
        return ratingEntityService.getAll(pageable);
    }

    @GetMapping("/byname")
    public MGameRatingEntity getByName(@RequestParam("name") String name) {
        return ratingEntityService.getByName(name);
    }

}
