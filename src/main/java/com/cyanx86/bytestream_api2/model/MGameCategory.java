package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MGameCategory {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private UUID id;
    private String name;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private List<MGame> games;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameCategory() {}
    public MGameCategory(GameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.name = gameCategory.getName();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();

        games = new ArrayList<>();
        for (Game itemGame : gameCategory.getGames())
            games.add(new MGame(itemGame));
    }
    public MGameCategory(MGameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.name = gameCategory.getName();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();

        games = gameCategory.getGames();
    }
    public MGameCategory(String name) {
        this.name = name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
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

    public List<MGame> getGames() {
        return new ArrayList<>(games);
    }

}
