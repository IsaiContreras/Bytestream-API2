package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.enums.FilenameFormat;
import com.bytestream_api2.games.enums.ResourcePath;
import com.bytestream_api2.games.enums.StaticResourcesPaths;
import com.bytestream_api2.games.utilities.FilenameFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MGameRating implements ImageContentEntity {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
    private String name;
    private String description;

    private String logoURI;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    // Relations
    private MGameRatingEntity gameRatingEntity;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public MGameRating() {}
    public MGameRating(@NotNull GameRating gameRating, boolean recursive) {
        id = gameRating.getId();
        name = gameRating.getName();
        description = gameRating.getDescription();
        createdAt = gameRating.getCreatedAt();
        updatedAt = gameRating.getUpdatedAt();
        deletedAt = gameRating.getDeletedAt();

        if (recursive)
            gameRatingEntity = new MGameRatingEntity(gameRating.getGameRatingEntity());
    }
    public MGameRating(@NotNull MGameRating gameRating) {
        id = gameRating.getId();
        name = gameRating.getName();
        description = gameRating.getDescription();
        createdAt = gameRating.getCreatedAt();
        updatedAt = gameRating.getUpdatedAt();
        deletedAt = gameRating.getDeletedAt();

        gameRatingEntity = gameRating.getGameRatingEntity();
    }
    public MGameRating(@NotNull String name) {
        this.name = name;
    }
    public MGameRating(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }
    public MGameRating(@NotNull String name, @NotNull String description, String entityName) {
        this.name = name;
        this.description = description;
        if (entityName != null && !entityName.isEmpty())
            gameRatingEntity = new MGameRatingEntity(entityName);
    }
    public MGameRating(@NotNull Short id, @NotNull String name, @NotNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public MGameRating(@NotNull Short id, @NotNull String name, @NotNull String description, String entityName) {
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

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    @Override
    public void setImageURIsFromFilenames(String[] filenames) {
        if (filenames.length == 0)
            return;

        this.setLogoURI(
                StaticResourcesPaths.GAME_RATING_LOGOS.constructRelativeURI(
                        new String[] {this.getName(), filenames[0]}
                )
        );
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
        return gameRatingEntity;
    }

    @Override
    public String getSubDirectory() {
        return id != null ? id.toString() : null;
    }
    @Override
    public ResourcePath getResourcePath() {
        return ResourcePath.GAME_RATING;
    }

    @Override
    public String constructFilename(String extension, String[] addit) {
        return FilenameFormatter.formatFilename(
                FilenameFormat.ENTITY_FILE_FORMAT,
                new String[]{
                        getId().toString().concat(getName()),
                        extension
                }
        );
    }

}
