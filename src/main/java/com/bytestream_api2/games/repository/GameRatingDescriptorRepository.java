package com.bytestream_api2.games.repository;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.entity.GameRatingEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("game_rating_descriptor_repository")
public interface GameRatingDescriptorRepository
        extends JpaRepository<GameRatingDescriptor, Serializable>,
        PagingAndSortingRepository<GameRatingDescriptor, Serializable>
{

    // -- [[ METHODS ]] --
    public abstract GameRatingDescriptor findById(Short id);

    public abstract GameRatingDescriptor findByName(String name);

    public abstract Page<GameRatingDescriptor> findByGameRatingEntity(
            GameRatingEntity gameRatingEntity,
            Pageable pageable
    );

    public abstract @NotNull Page<GameRatingDescriptor> findAll(@NotNull Pageable pageable);

}
