package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingDescriptorConverter;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.mapper.GameRatingDescriptorMapper;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
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
    public MGameRatingDescriptor create(GameRatingDescriptor ratingDescriptor) {
        ratingDescriptor.setGameRatingEntity(
                ratingEntityRepository.findByName(
                        ratingDescriptor.getGameRatingEntity() != null ?
                                ratingDescriptor.getGameRatingEntity().getName() : null
                )
        );

        return new MGameRatingDescriptor(this.ratingDescriptorRepository.save(ratingDescriptor), true);
    }

    public MGameRatingDescriptor update(GameRatingDescriptor ratingDescriptor) {
        GameRatingDescriptor descriptorToUpdate = ratingDescriptorRepository.findById(ratingDescriptor.getId());
        if (descriptorToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this ID.");

        if (ratingDescriptor.getGameRatingEntity() != null)
            ratingDescriptor.setGameRatingEntity(
                    ratingEntityRepository.findByName(ratingDescriptor.getGameRatingEntity().getName())
            );

        ratingDescriptorMapper.partialUpdateRatingDescriptor(descriptorToUpdate, ratingDescriptor);

        return new MGameRatingDescriptor(this.ratingDescriptorRepository.save(descriptorToUpdate), true);
    }

    public void delete(short id) {
        GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findById(id);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this ID.");

        ratingDescriptor.setDeletedAt(new Date());

        ratingDescriptorRepository.save(ratingDescriptor);
    }

    public void hardDelete(short id) {
        ratingDescriptorRepository.delete(ratingDescriptorRepository.findById(id));
    }

    // Queries
    public MGameRatingDescriptor getByName(String name) {
        GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findByName(name);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this name.");

        return new MGameRatingDescriptor(ratingDescriptor, true);
    }

    public List<MGameRatingDescriptor> getByRatingEntity(String name, Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter
                .parseToList(ratingDescriptorRepository.findByGameRatingEntity(
                        ratingEntityRepository.findByName(name), pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

    public List<MGameRatingDescriptor> getAll(Pageable pageable) {
        List<MGameRatingDescriptor> results = ratingDescriptorConverter.parseToList(
                ratingDescriptorRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

}
