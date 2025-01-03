package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import com.cyanx86.bytestream_api2.entity.GameRatingEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameRatingEntity {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private UUID id;
    private String name;
    private String longName;
    private String location;
    private String description;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private List<MGameRating> gameRatings;
    private List<MGameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameRatingEntity() {}
    public MGameRatingEntity(GameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.location = gameRatingEntity.getLocation();
        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();

        gameRatings = new ArrayList<>();
        for (GameRating itemGameRating : gameRatingEntity.getGameRatings())
            gameRatings.add(new MGameRating(itemGameRating));

        gameRatingDescriptors = new ArrayList<>();
        for (GameRatingDescriptor itemGameRatingDescriptor : gameRatingEntity.getGameRatingDescriptors())
            gameRatingDescriptors.add(new MGameRatingDescriptor(itemGameRatingDescriptor));
    }
    public MGameRatingEntity(MGameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.location = gameRatingEntity.getLocation();
        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();

        gameRatings = gameRatingEntity.getGameRatings();
        gameRatingDescriptors = gameRatingEntity.getGameRatingDescriptors();
    }
    public MGameRatingEntity(String name, String longName, String location, String description) {
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLongName(String longName) { this.longName = longName; }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLongName() { return longName; }
    public String getLocation() {
        return location;
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

    public List<MGameRating> getGameRatings() {
        return new ArrayList<>(gameRatings);
    }
    public List<MGameRatingDescriptor> getGameRatingDescriptors() {
        return new ArrayList<>(gameRatingDescriptors);
    }

}
