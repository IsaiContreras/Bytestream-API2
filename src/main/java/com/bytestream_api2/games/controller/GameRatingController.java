package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.service.GameRatingService;
import com.bytestream_api2.games.utilities.BodyFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BodyFormatter.result(
                            ratingService.create(gameRating, logoImage)
                    ));
        } catch (MediaUploadFailedException mufe) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(mufe.getMessage()));
        } catch(DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> updateRating(
            @Valid @RequestPart("data") GameRating gameRating,
            @RequestPart("logo") MultipartFile logoImage
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            ratingService.update(gameRating, logoImage)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (MediaUploadFailedException mufe) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(mufe.getMessage()));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteRating(
            @RequestParam("id") short id
    ) {
        try {
            ratingService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRating(
            @RequestParam("id") short id
    ) {
        try {
            ratingService.hardDelete(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result("Hard deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.ratingService.getByName(name)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/byentity")
    public ResponseEntity<Map<String, ?>> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.ratingService.getByGameRatingEntity(name, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.ratingService.getAll(pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

}
