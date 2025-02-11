package com.cyanx86.bytestream_api2.service;

import com.cyanx86.bytestream_api2.component.GameRatingDescriptorMapper;
import com.cyanx86.bytestream_api2.converter.GameRatingDescriptorConverter;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import com.cyanx86.bytestream_api2.model.MGameRatingDescriptor;
import com.cyanx86.bytestream_api2.repository.GameRatingDescriptorRepository;
import com.cyanx86.bytestream_api2.repository.GameRatingEntityRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public boolean create(GameRatingDescriptor ratingDescriptor) {
        try {
            this.ratingDescriptorRepository.save(ratingDescriptor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(GameRatingDescriptor ratingDescriptor) {
        try {
            GameRatingDescriptor descriptorToUpdate = ratingDescriptorRepository.findById(ratingDescriptor.getId());

            ratingDescriptorMapper.partialUpdateRatingDescriptor(descriptorToUpdate, ratingDescriptor);

            this.ratingDescriptorRepository.save(descriptorToUpdate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(UUID id) {
        try {
            GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findById(id);
            ratingDescriptor.setDeletedAt(new Date());

            ratingDescriptorRepository.save(ratingDescriptor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Queries
    public MGameRatingDescriptor getByName(String name) {
        GameRatingDescriptor ratingDescriptor = ratingDescriptorRepository.findByName(name);
        if (ratingDescriptor == null || ratingDescriptor.getDeletedAt() != null)
            return null;

        return new MGameRatingDescriptor(ratingDescriptor, true);
    }

    public List<MGameRatingDescriptor> getByRatingEntity(String name, Pageable pageable) {
        return ratingDescriptorConverter.parseToList(
                ratingDescriptorRepository.findByGameRatingEntity(
                        ratingEntityRepository.findByName(name),
                        pageable
                ).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
    }

    public List<MGameRatingDescriptor> getAll(Pageable pageable) {
        return ratingDescriptorConverter.parseToList(
                ratingDescriptorRepository.findAll(pageable).getContent()
        ).stream().filter(item -> item.getDeletedAt() == null).toList();
    }

}
