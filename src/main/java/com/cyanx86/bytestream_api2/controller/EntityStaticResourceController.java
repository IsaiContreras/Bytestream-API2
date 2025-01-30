package com.cyanx86.bytestream_api2.controller;

import com.cyanx86.bytestream_api2.service.GameRatingEntityService;
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

}
