package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameRatingDescriptor {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private UUID id;
    private String name;
    private String title;
    private String description;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private MGameRatingEntity gameRatingEntity;
    private List<MGame> games;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameRatingDescriptor() {}
    public MGameRatingDescriptor(GameRatingDescriptor gameRatingDescriptor) {
        this.id = gameRatingDescriptor.getId();
        this.name = gameRatingDescriptor.getName();
        this.title = gameRatingDescriptor.getTitle();
        this.description = gameRatingDescriptor.getDescription();
        this.createdAt = gameRatingDescriptor.getCreatedAt();
        this.updatedAt = gameRatingDescriptor.getUpdatedAt();
        this.deletedAt = gameRatingDescriptor.getDeletedAt();

        gameRatingEntity = new MGameRatingEntity(gameRatingDescriptor.getGameRatingEntity());
        games = new ArrayList<>();
        for (Game itemGame : gameRatingDescriptor.getGames())
            games.add(new MGame(itemGame));
    }
    public MGameRatingDescriptor(MGameRatingDescriptor gameRatingDescriptor) {
        this.id = gameRatingDescriptor.getId();
        this.name = gameRatingDescriptor.getName();
        this.title = gameRatingDescriptor.getTitle();
        this.description = gameRatingDescriptor.getDescription();
        this.createdAt = gameRatingDescriptor.getCreatedAt();
        this.updatedAt = gameRatingDescriptor.getUpdatedAt();
        this.deletedAt = gameRatingDescriptor.getDeletedAt();

        gameRatingEntity = gameRatingDescriptor.getGameRatingEntity();
        games = gameRatingDescriptor.getGames();
    }
    public MGameRatingDescriptor(String name, String title, String description) {
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public String getTitle() {
        return title;
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
        return new MGameRatingEntity(gameRatingEntity);
    }
    public List<MGame> getGames() {
        return new ArrayList<>(games);
    }

}
