package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameRating;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameRating {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private UUID id;
    private String name;
    private String description;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private MGameRatingEntity gameRatingEntity;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameRating() {}
    public MGameRating(@NotNull GameRating gameRating, boolean recursive) {
        this.id = gameRating.getId();
        this.name = gameRating.getName();
        this.description = gameRating.getDescription();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        if (recursive)
            this.gameRatingEntity = new MGameRatingEntity(gameRating.getGameRatingEntity());
    }
    public MGameRating(@NotNull MGameRating gameRating) {
        this.id = gameRating.getId();
        this.name = gameRating.getName();
        this.description = gameRating.getDescription();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        this.gameRatingEntity = gameRating.getGameRatingEntity();
    }
    public MGameRating(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
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

    public MGameRatingEntity getGameRatingEntity() {
        return this.gameRatingEntity;
    }

}
