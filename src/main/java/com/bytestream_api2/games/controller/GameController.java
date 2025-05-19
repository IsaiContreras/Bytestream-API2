package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.InvalidEntityRelationsException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.utilities.BodyFormatter;
import com.bytestream_api2.games.validation_groups.Game.OnCreate;
import com.bytestream_api2.games.validation_groups.Game.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.service.GameService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class GameController {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    @Autowired
    @Qualifier("game_service")
    private GameService gameService;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    // CUD
    @PostMapping(value="/create", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> addNewGame(
            @Validated(OnCreate.class) @RequestPart("data") Game game,
            @RequestPart(value="cover", required=false) MultipartFile coverImage,
            @RequestPart(value="landscape", required=false) MultipartFile landscapeImage
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BodyFormatter.result(
                            this.gameService.create(game, coverImage, landscapeImage)
                    ));
        } catch (MediaUploadFailedException mufe) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(mufe.getMessage()));
        } catch (DataAccessException | InvalidEntityRelationsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @PatchMapping(value="/update", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, ?>> updateGame(
            @Validated(OnUpdate.class) @RequestPart("data") Game game,
            @RequestPart(value="cover", required=false) MultipartFile coverImage,
            @RequestPart(value="landscape", required=false) MultipartFile landscapeImage
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameService.update(game, coverImage, landscapeImage)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (MediaUploadFailedException mufe) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(BodyFormatter.error(mufe.getMessage()));
        } catch (DataAccessException | InvalidEntityRelationsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, ?>> deleteGame(
            @RequestParam("id") long id
    ) {
        try {
            gameService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(BodyFormatter.result("Deleted successfully!"));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.error(e.getMessage()));
        }
    }

    @DeleteMapping("/harddelete")
    public ResponseEntity<Map<String, ?>> hardDeleteGame(
            @RequestParam("id") long id
    ) {
        try {
            gameService.hardDelete(id);
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
    public ResponseEntity<Map<String, ?>> getGameByName(
            @RequestParam("name") String name
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameService.getByName(name)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BodyFormatter.error(enfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/bytitle")
    public ResponseEntity<Map<String, ?>> getGameByTitle(
            @RequestParam("title") String title,
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameService.getByTitle(title, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/bycategories")
    public ResponseEntity<Map<String, ?>> getGamesByCategories(
            @RequestParam("categories") List<String> categories,
            Pageable pageable
    ) {
        try {
            List<GameCategory> fetchedCategos = new ArrayList<>();
            for (String category : categories)
                fetchedCategos.add(new GameCategory(category));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameService.getByGameCategories(fetchedCategos, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/byratings")
    public ResponseEntity<Map<String, ?>> getGamesByRatings(
            @RequestParam("ratings") List<String> ratings,
            Pageable pageable
    ) {
        try {
            List<GameRating> fetchedRatings = new ArrayList<>();
            for (String rating : ratings)
                fetchedRatings.add(new GameRating(rating));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameService.getByGameRatings(fetchedRatings, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get/bydescriptors")
    public ResponseEntity<Map<String, ?>> getGamesByDescriptors(
            @RequestParam("descriptors") List<String> ratingDescriptors,
            Pageable pageable
    ) {
        try {
            List<GameRatingDescriptor> fetchedDescriptors = new ArrayList<>();
            for (String ratingDescriptor : ratingDescriptors)
                fetchedDescriptors.add(new GameRatingDescriptor(ratingDescriptor));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            gameService.getByGameRatingDescriptors(fetchedDescriptors, pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BodyFormatter.result(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, ?>> getAllGames(
            Pageable pageable
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BodyFormatter.result(
                            this.gameService.getAll(pageable)
                    ));
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyFormatter.result(e.getMessage()));
        }
    }

}
