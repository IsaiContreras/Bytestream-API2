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
        this.id = gameRatingDescriptor.getId();
        this.name = gameRatingDescriptor.getName();
        this.description = gameRatingDescriptor.getDescription();
        this.createdAt = gameRatingDescriptor.getCreatedAt();
        this.updatedAt = gameRatingDescriptor.getUpdatedAt();
        this.deletedAt = gameRatingDescriptor.getDeletedAt();

        if (recursive)
            this.gameRatingEntity = new MGameRatingEntity(gameRatingDescriptor.getGameRatingEntity());
    }
    public MGameRatingDescriptor(@NotNull MGameRatingDescriptor gameRatingDescriptor) {
        this.id = gameRatingDescriptor.getId();
        this.name = gameRatingDescriptor.getName();
        this.description = gameRatingDescriptor.getDescription();

        this.createdAt = gameRatingDescriptor.getCreatedAt();
        this.updatedAt = gameRatingDescriptor.getUpdatedAt();
        this.deletedAt = gameRatingDescriptor.getDeletedAt();

        this.gameRatingEntity = gameRatingDescriptor.getGameRatingEntity();
    }
    public MGameRatingDescriptor(@NotNull String name) {
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

    public Short getId() {
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
