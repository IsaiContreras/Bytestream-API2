package com.cyanx86.bytestream_api2.repository;

import com.cyanx86.bytestream_api2.entity.GameCategory;
import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cyanx86.bytestream_api2.entity.Game;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Repository("game_repository")
public interface GameRepository
        extends JpaRepository<Game, Serializable>,
        PagingAndSortingRepository<Game, Serializable>
{

    // -- [[ METHODS ]] --
    public abstract Game findById(UUID id);

    public abstract Game findByName(String name);

    public abstract Page<Game> findByTitleContains(String title, Pageable pageable);

    public abstract Page<Game> findByGameCategoriesContains(
            List<GameCategory> gameCategories,
            Pageable pageable
    );

    public abstract Page<Game> findByGameRatingsContains(
            List<GameRating> gameRatings,
            Pageable pageable
    );

    public abstract Page<Game> findByGameRatingDescriptorsContains(
            List<GameRatingDescriptor> gameRatingDescriptors,
            Pageable pageable
    );

    public abstract @NotNull Page<Game> findAll(@NotNull Pageable pageable);

}
