package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingDescriptorConverter;
import com.bytestream_api2.games.mapper.GameRatingDescriptorMapper;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingDescriptor;

import com.bytestream_api2.games.utilities.ResponseUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import java.util.Map;

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
    public ResponseEntity<Map<String, ?>> create(GameRatingDescriptor ratingDescriptor) {
        try {
            ratingDescriptor.setGameRatingEntity(
                    ratingEntityRepository.findByName(
                            ratingDescriptor.getGameRatingEntity() != null ?
                                    ratingDescriptor.getGameRatingEntity().getName() : null
                    )
            );

            GameRatingDescriptor result = this.ratingDescriptorRepository.save(ratingDescriptor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtility.result(new MGameRatingDescriptor(result, true)));
        } catch(DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtility.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> update(GameRatingDescriptor ratingDescriptor) {
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
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseUtility.result(new MGameRatingDescriptor(result, true)));
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtility.error(dae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> delete(short id) {
        try {
            GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findById(id);
            ratingDescriptor.setDeletedAt(new Date());

            ratingDescriptorRepository.save(ratingDescriptor);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result("Deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtility.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, ?>> hardDelete(short id) {
        try {
            ratingDescriptorRepository.delete(ratingDescriptorRepository.findById(id));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseUtility.result("Hard deleted successfully!"));
        } catch (EmptyResultDataAccessException erdae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtility.error(erdae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtility.error(e.getMessage()));
        }
    }

    // Queries
    public ResponseEntity<Map<String, ?>> getByName(String name) {
        GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findByName(name);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtility.result(new MGameRatingDescriptor(ratingDescriptor, true)));
    }

    public ResponseEntity<Map<String, ?>> getByRatingEntity(String name, Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter
                .parseToList(ratingDescriptorRepository.findByGameRatingEntity(
                        ratingEntityRepository.findByName(name),
                        pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtility.result("No results."));

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result(results));
    }

    public ResponseEntity<Map<String, ?>> getAll(Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter.parseToList(
                ratingDescriptorRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtility.result("No results."));

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtility.result(results));
    }

}
