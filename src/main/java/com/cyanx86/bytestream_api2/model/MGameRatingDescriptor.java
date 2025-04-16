package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

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
    public MGameRatingDescriptor(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
    public void setDescription(@NotNull String description) {
        this.description = description;
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
