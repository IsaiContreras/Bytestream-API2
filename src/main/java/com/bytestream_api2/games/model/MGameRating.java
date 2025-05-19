package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.GameRating;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MGameRating {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
    private String name;
    private String description;

    private String logoURI;

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
    public MGameRating(@NotNull String name) {
        this.name = name;
    }
    public MGameRating(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }
    public MGameRating(@NotNull String name, @NotNull String description, String entityName) {
        this.name = name;
        this.description = description;
        if (entityName != null && !entityName.isEmpty())
            this.gameRatingEntity = new MGameRatingEntity(entityName);
    }
    public MGameRating(@NotNull Short id, @NotNull String name, @NotNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public MGameRating(@NotNull Short id, @NotNull String name, @NotNull String description, String entityName) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (entityName != null && !entityName.isEmpty())
            this.gameRatingEntity = new MGameRatingEntity(entityName);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setGameRatingEntity(MGameRatingEntity gameRatingEntity) {
        this.gameRatingEntity = gameRatingEntity;
    }

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    public Short getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }

    public String getLogoURI() {
        return this.logoURI;
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
