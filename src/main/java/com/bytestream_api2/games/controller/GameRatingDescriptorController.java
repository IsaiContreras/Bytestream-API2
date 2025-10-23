package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.service.GameRatingDescriptorService;
import com.bytestream_api2.games.utilities.statics.BodyFormatter;
import com.bytestream_api2.games.validation_groups.GameRatingDescriptor.OnCreate;
import com.bytestream_api2.games.validation_groups.GameRatingDescriptor.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rating_descriptor")
public class GameRatingDescriptorController {

    // -- [[ ATTRIBUES ]] --

    // -- PRIVATE --
    @Autowired
    @Qualifier("game_rating_descriptor_service")
    private GameRatingDescriptorService gameRatingDescriptorService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping("/create")
    public ResponseEntity<Map<String, ?>> addNewRatingDescriptor(
            @Validated(OnCreate.class) @RequestBody GameRatingDescriptor ratingDescriptor
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BodyFormatter.result(gameRatingDescriptorService.create(ratingDescriptor)));
        } catch(DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, ?>> updateRatingDescriptor(
            @Validated(OnUpdate.class) @RequestBody GameRatingDescriptor ratingDescriptor
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(gameRatingDescriptorService.update(ratingDescriptor)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteRatingDescriptor(
            @RequestParam("id") short id
    ) {
        try {
            gameRatingDescriptorService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRatingDescriptor(
            @RequestParam("id") short id
    ) {
        try {
            gameRatingDescriptorService.hardDelete(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result("Hard deleted successfully!"));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
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
                    .body(BodyFormatter.result(gameRatingDescriptorService.getByName(name)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(e.getMessage()));
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
                    .body(BodyFormatter.result(gameRatingDescriptorService.getByRatingEntity(name, pageable)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(e.getMessage()));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(gameRatingDescriptorService.getAll(pageable)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

}
