package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.imageresource.ImagePathUnresolvedException;
import com.bytestream_api2.games.exception.imageresource.UncaughtImageExtensionException;
import com.bytestream_api2.games.exception.imageresource.UnreadableResourceException;
import com.bytestream_api2.games.misc.ImageResourcePackage;
import com.bytestream_api2.games.service.GameRatingEntityService;
import com.bytestream_api2.games.service.GameRatingService;
import com.bytestream_api2.games.service.GameService;
import com.bytestream_api2.games.utilities.BodyFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/public/media")
public class EntityStaticResourceController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Autowired
    @Qualifier("game_rating_entity_service")
    private GameRatingEntityService gameRatingEntityService;

    @Autowired
    @Qualifier("game_rating_service")
    private GameRatingService gameRatingService;

    @Autowired
    @Qualifier("game_service")
    private GameService gameService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @GetMapping("/rating_entity/{name}/{filename}")
    public ResponseEntity<?> ratingEntityImages(
        @PathVariable("name") String name,
        @PathVariable("filename") String filename,
        @RequestParam(name="width", required=false) Integer width,
        @RequestParam(name="height", required=false) Integer height
    ) {
        try {
            ImageResourcePackage image = gameRatingEntityService.getLogoImage(
                    name,
                    filename,
                    width,
                    height
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(image.getMediaType())
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    image.getFilname() + "\""
                    ).body(image.getImageByteArray());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (
                ImagePathUnresolvedException |
                UnreadableResourceException |
                UncaughtImageExtensionException e
        ) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @GetMapping("/rating/{name}/{filename}")
    public ResponseEntity<?> ratingImages(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename,
            @RequestParam(name="width", required=false) Integer width,
            @RequestParam(name="height", required=false) Integer height
    ) {
        try {
            ImageResourcePackage image = gameRatingService.getLogoImage(
                    name,
                    filename,
                    width,
                    height
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(image.getMediaType())
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    image.getFilname() + "\""
                    ).body(image.getImageByteArray());
        } catch(IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (
                ImagePathUnresolvedException |
                UnreadableResourceException |
                UncaughtImageExtensionException e
        ) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @GetMapping("/game_art/{name}/{filename}")
    public ResponseEntity<?> gameLogoImage(
            @PathVariable("name") String name,
            @PathVariable("filename") String filename,
            @RequestParam(name="width", required=false) Integer width,
            @RequestParam(name="height", required=false) Integer height
    ) {
        try {
            ImageResourcePackage image = gameService.getImage(
                    name,
                    filename,
                    width,
                    height
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(image.getMediaType())
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    image.getFilname() + "\""
                    ).body(image.getImageByteArray());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (
                ImagePathUnresolvedException |
                UnreadableResourceException |
                UncaughtImageExtensionException e
        ) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(e.getMessage()));
        }
    }

}
