package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.service.GameRatingEntityService;
import com.bytestream_api2.games.service.GameRatingService;
import com.bytestream_api2.games.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/media")
public class EntityStaticResourceController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Autowired
    @Qualifier("game_rating_entity_service")
    private GameRatingEntityService ratingEntityService;

    @Autowired
    @Qualifier("game_rating_service")
    private GameRatingService ratingService;

    @Autowired
    @Qualifier("game_service")
    private GameService gameService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @GetMapping("/rating_entity/{name}/{filename}")
    public ResponseEntity<byte[]> ratingEntityImages(
        @PathVariable("name") String name,
        @PathVariable("filename") String filename,
        @RequestParam(name="width", required=false) Integer width,
        @RequestParam(name="height", required=false) Integer height
    ) {
        return ratingEntityService.getLogoImage(name, filename, width, height);
    }

    @GetMapping("/rating/{name}/{filename}")
    public ResponseEntity<byte[]> ratingImages(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename,
            @RequestParam(name="width", required=false) Integer width,
            @RequestParam(name="height", required=false) Integer height
    ) {
        return ratingService.getLogoImage(name, filename, width, height);
    }

    @GetMapping("/game_art/{name}/{filename}")
    public ResponseEntity<byte[]> gameLogoImage(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename,
            @RequestParam(name="width", required=false) Integer width,
            @RequestParam(name="height", required=false) Integer height
    ) {
        return gameService.getImage(name, filename, width, height);
    }

}
