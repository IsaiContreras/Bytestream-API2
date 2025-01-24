package com.cyanx86.bytestream_api2.repository;

import com.cyanx86.bytestream_api2.entity.GameCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;

@Repository("game_category_repository")
public interface GameCategoryRepository
        extends JpaRepository<GameCategory, Serializable>, PagingAndSortingRepository<GameCategory, Serializable>
{

    // -- [[ METHODS ]] --
    public abstract GameCategory findById(UUID id);

    public abstract Page<GameCategory> findByNameContains(String name, Pageable pageable);

    public abstract Page<GameCategory> findAll(Pageable pagable);

}
