package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.GameCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MGameCategory {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
    private String name;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameCategory() {}
    public MGameCategory(@NotNull GameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.name = gameCategory.getName();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();
    }
    public MGameCategory(@NotNull MGameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.name = gameCategory.getName();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();
    }
    public MGameCategory(@NotNull Short id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
    public MGameCategory(@NotNull Short id) {
        this.id = id;
    }
    public MGameCategory(@NotNull String name) {
        this.name = name;
    }

    public void setId(Short id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Short getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
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
