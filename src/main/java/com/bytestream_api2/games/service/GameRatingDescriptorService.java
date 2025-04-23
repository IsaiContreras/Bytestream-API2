package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingDescriptorConverter;
import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.mapper.GameRatingDescriptorMapper;
import com.bytestream_api2.games.model.MGameRating;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("game_rating_descriptor_service")
public class GameRatingDescriptorService {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Entity Components
    @Autowired
    @Qualifier("game_rating_descriptor_repository")
    private GameRatingDescriptorRepository ratingDescriptorRepository;

    @Autowired
    @Qualifier("game_rating_entity_repository")
    private GameRatingEntityRepository ratingEntityRepository;

    @Autowired
    @Qualifier("game_rating_descriptor_converter")
    private GameRatingDescriptorConverter ratingDescriptorConverter;

    private final GameRatingDescriptorMapper ratingDescriptorMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingDescriptorService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameRatingDescriptorService(GameRatingDescriptorMapper gameRatingDescriptorMapper) {
        this.ratingDescriptorMapper = gameRatingDescriptorMapper;
    }

    // CUD
    public ResponseEntity<?> create(GameRatingDescriptor ratingDescriptor) {
        try {
            ratingDescriptor.setGameRatingEntity(
                    ratingEntityRepository.findByName(
                            ratingDescriptor.getGameRatingEntity() != null ?
                                    ratingDescriptor.getGameRatingEntity().getName() : null
                    )
            );

            GameRatingDescriptor result = this.ratingDescriptorRepository.save(ratingDescriptor);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MGameRatingDescriptor(result, true));
        } catch(DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> update(GameRatingDescriptor ratingDescriptor) {
        try {
            GameRatingDescriptor descriptorToUpdate = ratingDescriptorRepository.findById(ratingDescriptor.getId());
            if (descriptorToUpdate == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            if (ratingDescriptor.getGameRatingEntity() != null)
                ratingDescriptor.setGameRatingEntity(
                        ratingEntityRepository.findByName(ratingDescriptor.getGameRatingEntity().getName())
                );

            ratingDescriptorMapper.partialUpdateRatingDescriptor(descriptorToUpdate, ratingDescriptor);

            GameRatingDescriptor result = ratingDescriptorRepository.save(descriptorToUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(new MGameRatingDescriptor(result, true));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<String> delete(short id) {
        try {
            GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findById(id);
            ratingDescriptor.setDeletedAt(new Date());

            ratingDescriptorRepository.save(ratingDescriptor);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<String> hardDelete(short id) {
        try {
            ratingDescriptorRepository.delete(ratingDescriptorRepository.findById(id));
            return ResponseEntity.status(HttpStatus.OK).body("Hard deleted successfully!");
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erdae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Queries
    public ResponseEntity<MGameRatingDescriptor> getByName(String name) {
        GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findByName(name);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(new MGameRatingDescriptor(ratingDescriptor, true));
    }

    public ResponseEntity<?> getByRatingEntity(String name, Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter
                .parseToList(ratingDescriptorRepository.findByGameRatingEntity(
                        ratingEntityRepository.findByName(name),
                        pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    public ResponseEntity<?> getAll(Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter.parseToList(
                ratingDescriptorRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No results.");

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

}
