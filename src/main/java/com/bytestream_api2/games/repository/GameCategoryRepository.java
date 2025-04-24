package com.bytestream_api2.games.repository;

import com.bytestream_api2.games.entity.GameCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("game_category_repository")
public interface GameCategoryRepository
        extends JpaRepository<GameCategory, Serializable>,
        PagingAndSortingRepository<GameCategory, Serializable>
{

    // -- [[ METHODS ]] --
    public abstract GameCategory findById(Short id);

    public abstract GameCategory findByName(String name);

    public abstract Page<GameCategory> findByNameContains(String name, Pageable pageable);

    public abstract @NotNull Page<GameCategory> findAll(@NotNull Pageable pageable);

}
