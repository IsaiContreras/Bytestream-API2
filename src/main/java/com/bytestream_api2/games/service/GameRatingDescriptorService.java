package com.bytestream_api2.games.service;

import com.bytestream_api2.games.converter.GameRatingDescriptorConverter;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.mapper.GameRatingDescriptorMapper;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingEntityRepository;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jetbrains.annotations.NotNull;
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
    private final GameRatingDescriptorRepository gameRatingDescriptorRepository;
    private final GameRatingEntityRepository gameRatingEntityRepository;
    private final GameRatingDescriptorConverter gameRatingDescriptorConverter;
    private final GameRatingDescriptorMapper gameRatingDescriptorMapper;

    // Class Components
    private static final Log logger = LogFactory.getLog(GameRatingDescriptorService.class);

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Autowired
    public GameRatingDescriptorService(
            @Qualifier("game_rating_descriptor_repository") GameRatingDescriptorRepository gameRatingDescriptorRepository,
            @Qualifier("game_rating_entity_repository") GameRatingEntityRepository gameRatingEntityRepository,
            @Qualifier("game_rating_descriptor_converter") GameRatingDescriptorConverter gameRatingDescriptorConverter,
            GameRatingDescriptorMapper gameRatingDescriptorMapper
    ) {
        this.gameRatingDescriptorRepository = gameRatingDescriptorRepository;
        this.gameRatingEntityRepository = gameRatingEntityRepository;
        this.gameRatingDescriptorConverter = gameRatingDescriptorConverter;
        this.gameRatingDescriptorMapper = gameRatingDescriptorMapper;
    }

    // CUD
    public MGameRatingDescriptor create(@NotNull GameRatingDescriptor ratingDescriptor) {
        ratingDescriptor.setGameRatingEntity(
                gameRatingEntityRepository.findByName(
                        ratingDescriptor.getGameRatingEntity() != null ?
                                ratingDescriptor.getGameRatingEntity().getName() : null
                )
        );

        return new MGameRatingDescriptor(gameRatingDescriptorRepository.save(ratingDescriptor), true);
    }

    public MGameRatingDescriptor update(@NotNull GameRatingDescriptor ratingDescriptor) {
        GameRatingDescriptor descriptorToUpdate = gameRatingDescriptorRepository.findById(ratingDescriptor.getId());
        if (descriptorToUpdate == null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this ID.");

        if (ratingDescriptor.getGameRatingEntity() != null)
            ratingDescriptor.setGameRatingEntity(
                    gameRatingEntityRepository.findByName(ratingDescriptor.getGameRatingEntity().getName())
            );

        gameRatingDescriptorMapper.partialUpdateRatingDescriptor(descriptorToUpdate, ratingDescriptor);
        return new MGameRatingDescriptor(gameRatingDescriptorRepository.save(descriptorToUpdate), true);
    }

    public void delete(short id) {
        GameRatingDescriptor ratingDescriptor = gameRatingDescriptorRepository.findById(id);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this ID.");

        ratingDescriptor.setDeletedAt(new Date());
        gameRatingDescriptorRepository.save(ratingDescriptor);
    }

    public void hardDelete(short id) {
        gameRatingDescriptorRepository.delete(gameRatingDescriptorRepository.findById(id));
    }

    // Queries
    public MGameRatingDescriptor getByName(String name) {
        GameRatingDescriptor ratingDescriptor = gameRatingDescriptorRepository.findByName(name);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            throw new EntityNotFoundException("Couldn't find a Rating descriptor with this name.");

        return new MGameRatingDescriptor(ratingDescriptor, true);
    }

    public List<MGameRatingDescriptor> getByRatingEntity(String name, Pageable pageable) {
        List<MGameRatingDescriptor> results = gameRatingDescriptorConverter
                .parseToList(gameRatingDescriptorRepository.findByGameRatingEntity(
                        gameRatingEntityRepository.findByName(name), pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

    public List<MGameRatingDescriptor> getAll(Pageable pageable) {
        List<MGameRatingDescriptor> results = gameRatingDescriptorConverter.parseToList(
                gameRatingDescriptorRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
        if (results.isEmpty())
            throw new EntityNotFoundException("No results for this search.");

        return results;
    }

}
