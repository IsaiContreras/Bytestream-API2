package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.misc.ResourcePath;
import com.bytestream_api2.games.service.GameRatingEntityService;
import com.bytestream_api2.games.service.GameRatingService;
import com.bytestream_api2.games.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Resource> ratingEntityImages(
        @PathVariable("name") String name,
        @PathVariable("filename") String filename
    ) {
        return ratingEntityService.getLogoImage(name, filename);
    }

    @GetMapping("/rating/{name}/{filename}")
    public ResponseEntity<Resource> ratingImages(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename
    ) {
        return ratingService.getLogoImage(name, filename);
    }

    @GetMapping("/game_logo/{name}/{filename}")
    public ResponseEntity<Resource> gameLogoImage(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename
    ) {
        return gameService.getImage(ResourcePath.GAME_LOGO_ART, name, filename);
    }

    @GetMapping("/game_cover/{name}/{filename}")
    public ResponseEntity<Resource> gameCoverImage(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename
    ) {
        return gameService.getImage(ResourcePath.GAME_COVER_ART, name, filename);
    }

    @GetMapping("/game_landscape/{name}/{filename}")
    public ResponseEntity<Resource> gameLandscapeImage(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename
    ) {
        return gameService.getImage(ResourcePath.GAME_LANDSCAPE_ART, name, filename);
    }

}
