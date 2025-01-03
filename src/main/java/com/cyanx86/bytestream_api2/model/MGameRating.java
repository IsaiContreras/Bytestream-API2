package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameRating;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameRating {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private UUID id;
    private String title;
    private String description;
    private String logoURI;

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
    public MGameRating() {}
    public MGameRating(GameRating gameRating) {
        this.id = gameRating.getId();
        this.title = gameRating.getTitle();
        this.description = gameRating.getDescription();
        this.logoURI = gameRating.getLogoURI();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        this.gameRatingEntity = new MGameRatingEntity(gameRating.getGameRatingEntity());

        games = new ArrayList<>();
        for (Game itemGame : gameRating.getGames())
            games.add(new MGame(itemGame));
    }
    public MGameRating(MGameRating gameRating) {
        this.id = gameRating.getId();
        this.title = gameRating.getTitle();
        this.description = gameRating.getDescription();
        this.logoURI = gameRating.getLogoURI();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        this.gameRatingEntity = gameRating.getGameRatingEntity();
        games = gameRating.getGames();
    }
    public MGameRating(String title, String description, String logoURI) {
        this.title = title;
        this.description = description;
        this.logoURI = logoURI;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getLogoURI() {
        return logoURI;
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
