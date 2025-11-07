package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.entity.GameRatingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MGameRatingDescriptor {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
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
    public MGameRatingDescriptor() {}
    public MGameRatingDescriptor(@NotNull GameRatingDescriptor gameRatingDescriptor, boolean recursive) {
        id = gameRatingDescriptor.getId();
        name = gameRatingDescriptor.getName();
        description = gameRatingDescriptor.getDescription();
        createdAt = gameRatingDescriptor.getCreatedAt();
        updatedAt = gameRatingDescriptor.getUpdatedAt();
        deletedAt = gameRatingDescriptor.getDeletedAt();

        if (recursive)
            gameRatingEntity = new MGameRatingEntity(gameRatingDescriptor.getGameRatingEntity());
    }
    public MGameRatingDescriptor(@NotNull MGameRatingDescriptor gameRatingDescriptor) {
        id = gameRatingDescriptor.getId();
        name = gameRatingDescriptor.getName();
        description = gameRatingDescriptor.getDescription();

        createdAt = gameRatingDescriptor.getCreatedAt();
        updatedAt = gameRatingDescriptor.getUpdatedAt();
        deletedAt = gameRatingDescriptor.getDeletedAt();

        gameRatingEntity = gameRatingDescriptor.getGameRatingEntity();
    }
    public MGameRatingDescriptor(@NotNull String name) {
        this.name = name;
    }
    public MGameRatingDescriptor(Short id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
    public MGameRatingDescriptor(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }
    public MGameRatingDescriptor(@NotNull String name, @NotNull String description, String entityName) {
        this.name = name;
        this.description = description;
        if (entityName != null && !entityName.isEmpty())
            this.gameRatingEntity = new MGameRatingEntity(entityName);
    }
    public MGameRatingDescriptor(@NotNull Short id, @NotNull String name, @NotNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public MGameRatingDescriptor(
            @NotNull Short id, @NotNull String name, @NotNull String description, String entityName
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (entityName != null && !entityName.isEmpty())
            gameRatingEntity = new MGameRatingEntity(entityName);
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

    public Short getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public Date getDeletedAt() {
        return deletedAt;
    }

    public MGameRatingEntity getGameRatingEntity() {
        return gameRatingEntity;
    }

}
