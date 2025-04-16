package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameRatingEntity {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
    private String name;
    private String longName;
    private String location;
    private String description;

    private List<String> logoURIList = new ArrayList<>();

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameRatingEntity() {}
    public MGameRatingEntity(@NotNull GameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.longName = gameRatingEntity.getLongName();
        this.location = gameRatingEntity.getLocation();
        this.description = gameRatingEntity.getDescription();

        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();
    }
    public MGameRatingEntity(@NotNull MGameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.longName = gameRatingEntity.getLongName();
        this.location = gameRatingEntity.getLocation();
        this.description = gameRatingEntity.getDescription();

        this.logoURIList = gameRatingEntity.getLogoURIList();

        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();
    }
    public MGameRatingEntity(
            @NotNull String name, @NotNull String longName, @NotNull String location, @NotNull String description
    ) {
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
    public void setLongName(@NotNull String longName) {
        this.longName = longName;
    }
    public void setLocation(@NotNull String location) {
        this.location = location;
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
    public String getLongName() {
        return this.longName;
    }
    public String getLocation() {
        return this.location;
    }
    public String getDescription() {
        return this.description;
    }

    public List<String> getLogoURIList() {
        return this.logoURIList;
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

}
