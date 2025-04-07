package com.cyanx86.bytestream_api2.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="game_ratings")
public class GameRating implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="rating_id") @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="name", unique=true, nullable = false, length=31)
    private String name;

    @Column(name="description", nullable=false, length=1023)
    private String description;

    @Column(name="created_at", nullable = false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // Relations
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE, optional=false)
    @JoinColumn(name="entity_id")
    private GameRatingEntity gameRatingEntity;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRating() {}
    public GameRating(@NotNull GameRating gameRating) {
        this.id = gameRating.getId();
        this.name = gameRating.getName();
        this.description = gameRating.getDescription();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        this.gameRatingEntity = gameRating.getGameRatingEntity();
    }
    public GameRating(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setGameRatingEntity(GameRatingEntity gameRatingEntity) {
        this.gameRatingEntity = gameRatingEntity;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() { return this.name; }
    public String getDescription() {
        return this.description;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }

    public GameRatingEntity getGameRatingEntity() {
        return this.gameRatingEntity;
    }

}
