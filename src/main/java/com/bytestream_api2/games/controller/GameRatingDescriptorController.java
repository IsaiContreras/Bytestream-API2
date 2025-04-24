package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.service.GameRatingDescriptorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rating_descriptor")
public class GameRatingDescriptorController {

    // -- [[ ATTRIBUES ]] --

    // -- PRIVATE --
    @Autowired
    @Qualifier("game_rating_descriptor_service")
    private GameRatingDescriptorService ratingDescriptorService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping("/create")
    public ResponseEntity<Map<String, ?>> addNewRatingDescriptor(
            @Valid @RequestBody GameRatingDescriptor ratingDescriptor
    ) {
        return ratingDescriptorService.create(ratingDescriptor);
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, ?>> updateRatingDescriptor(
            @Valid @RequestBody GameRatingDescriptor ratingDescriptor
    ) {
        return ratingDescriptorService.update(ratingDescriptor);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteRatingDescriptor(
            @RequestParam("id") short id
    ) {
        return ratingDescriptorService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteRatingDescriptor(
            @RequestParam("id") short id
    ) {
        return ratingDescriptorService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public ResponseEntity<Map<String, ?>> getByName(
            @RequestParam("name") String name
    ) {
        return ratingDescriptorService.getByName(name);
    }

    @GetMapping("/get/byentity")
    public ResponseEntity<Map<String, ?>> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return ratingDescriptorService.getByRatingEntity(name, pageable);
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAll(
            Pageable pageable
    ) {
        return ratingDescriptorService.getAll(pageable);
    }

}
