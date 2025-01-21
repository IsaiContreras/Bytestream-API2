package com.cyanx86.bytestream_api2.repository;

import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;

@Repository("game_rating_entity_repository")
public interface GameRatingEntityRepository
        extends JpaRepository<GameRatingEntity, Serializable>, PagingAndSortingRepository<GameRatingEntity, Serializable>
{

    public abstract GameRatingEntity findById(UUID id);

    public abstract GameRatingEntity findByName(String name);

    public abstract Page<GameRatingEntity> findAll(Pageable pageable);

}
