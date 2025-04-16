package com.cyanx86.bytestream_api2.repository;

import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;

@Repository("game_rating_repository")
public interface GameRatingRepository
        extends JpaRepository<GameRating, Serializable>,
        PagingAndSortingRepository<GameRating, Serializable>
{

    // -- [[ METHODS ]] --
    public abstract GameRating findById(Short id);

    public abstract GameRating findByName(String name);

    public abstract Page<GameRating> findByGameRatingEntity(
            GameRatingEntity gameRatingEntity,
            Pageable pageable
    );

    public abstract @NotNull Page<GameRating> findAll(@NotNull Pageable pageable);

}
