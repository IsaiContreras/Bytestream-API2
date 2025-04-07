package com.cyanx86.bytestream_api2.controller;

import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import com.cyanx86.bytestream_api2.model.MGameRatingDescriptor;
import com.cyanx86.bytestream_api2.service.GameRatingDescriptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public boolean addNewRatingDescriptor(
            @RequestBody @Validated GameRatingDescriptor ratingDescriptor
    ) {
        return ratingDescriptorService.create(ratingDescriptor);
    }

    @PatchMapping("/update")
    public boolean updateRatingDescriptor(
            @RequestBody @Validated GameRatingDescriptor ratingDescriptor
    ) {
        return ratingDescriptorService.update(ratingDescriptor);
    }

    @DeleteMapping("/delete")
    public boolean deleteRatingDescriptor(
            @RequestParam("id") UUID id
    ) {
        return ratingDescriptorService.delete(id);
    }

    @DeleteMapping("/harddelete")
    public boolean hardDeleteRatingDescriptor(
            @RequestParam("id") UUID id
    ) {
        return ratingDescriptorService.hardDelete(id);
    }

    // Queries
    @GetMapping("/get/byname")
    public MGameRatingDescriptor getByName(
            @RequestParam("name") String name
    ) {
        return ratingDescriptorService.getByName(name);
    }

    @GetMapping("/get/byentity")
    public List<MGameRatingDescriptor> getByRatingEntity(
            @RequestParam("name") String name,
            Pageable pageable
    ) {
        return ratingDescriptorService.getByRatingEntity(name, pageable);
    }

    @GetMapping("/get")
    public List<MGameRatingDescriptor> getAll(
            Pageable pageable
    ) {
        return ratingDescriptorService.getAll(pageable);
    }

}
