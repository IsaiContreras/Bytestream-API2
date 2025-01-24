package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameCategory;
import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGame {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE
    // Data
    private UUID id;
    private String title;
    private String name;
    private String synopsis;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private List<MGameCategory> gameCategories;
    private List<MGameRating> gameRatings;
    private List<MGameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC

    // -- [[ METHODS ]] --

    // -- PRIVATE

    // -- PUBLIC

    public MGame() {}
    public MGame(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.name = game.getName();
        this.synopsis = game.getSynopsis();

        this.createdAt = game.getCreatedAt();
        this.updatedAt = game.getUpdatedAt();
        this.deletedAt = game.getDeletedAt();

        this.gameCategories = new ArrayList<>();
        for (GameCategory itemGameCategory : game.getGameCategories())
            this.gameCategories.add(new MGameCategory(itemGameCategory));

        this.gameRatings = new ArrayList<>();
        for(GameRating itemGameRating : game.getGameRatings())
            this.gameRatings.add(new MGameRating(itemGameRating));

        this.gameRatingDescriptors = new ArrayList<>();
        for (GameRatingDescriptor itemGameRating : game.getGameRatingDescriptors())
            this.gameRatingDescriptors.add(new MGameRatingDescriptor(itemGameRating));
    }
    public MGame(MGame game) {
        this.id = game.getId();
        this.name = game.getName();
        this.title = game.getTitle();
        this.synopsis = game.getSynopsis();

        this.createdAt = game.getCreatedAt();
        this.updatedAt = game.getUpdatedAt();
        this.deletedAt = game.getDeletedAt();

        this.gameCategories = game.getGameCategories();
        this.gameRatings = game.getGameRatings();
        this.gameRatingDescriptors = game.getGameRatingDescriptors();
    }
    public MGame(String name, String title, String synopsis) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
    }

    public void setName(String name) { this.name = name; }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() { return this.name; }
    public String getTitle() {
        return this.title;
    }
    public String getSynopsis() {
        return this.synopsis;
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

    public List<MGameCategory> getGameCategories() {
        return new ArrayList<>(this.gameCategories);
    }
    public List<MGameRating> getGameRatings() {
        return new ArrayList<>(this.gameRatings);
    }
    public List<MGameRatingDescriptor> getGameRatingDescriptors() {
        return new ArrayList<>(this.gameRatingDescriptors);
    }

}
