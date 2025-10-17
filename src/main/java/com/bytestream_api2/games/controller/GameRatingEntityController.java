package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.service.GameRatingEntityService;
import com.bytestream_api2.games.utilities.BodyFormatter;
import com.bytestream_api2.games.validation_groups.GameRatingEntity.OnCreate;
import com.bytestream_api2.games.validation_groups.GameRatingEntity.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    private GameRatingEntityService gameRatingEntityService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping(value="/create", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> addNewRatingEntity(
            @Validated(OnCreate.class) @RequestPart("data") GameRatingEntity gameRatingEntity,
            @RequestPart(value="logo", required=false) MultipartFile logoImage
    )  {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BodyFormatter.result(
                            gameRatingEntityService.create(gameRatingEntity, logoImage)
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
    public ResponseEntity<Map<String, ?>> updateRatingEntity (
            @Validated(OnUpdate.class) @RequestPart("data") GameRatingEntity gameRatingEntity,
            @RequestPart(value="logo", required=false) MultipartFile logoImage
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameRatingEntityService.update(gameRatingEntity, logoImage)
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
    public ResponseEntity<Map<String, ?>> deleteRatingEntity (
            @RequestParam("id") short id
    ) {
        try {
            gameRatingEntityService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRatingEntity(
            @RequestParam("id") short id
    ) {
        try {
            gameRatingEntityService.hardDelete(id);
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
                            gameRatingEntityService.getByName(name)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(Pageable pageable) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameRatingEntityService.getAll(pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

}
