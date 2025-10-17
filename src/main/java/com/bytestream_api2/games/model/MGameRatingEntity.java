package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.enums.FilenameFormat;
import com.bytestream_api2.games.enums.ResourcePath;
import com.bytestream_api2.games.enums.StaticResourcesPaths;
import com.bytestream_api2.games.utilities.FilenameFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MGameRatingEntity implements ImageContentEntity {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Data
    private Short id;
    private String name;
    private String longName;
    private String location;
    private String description;

    private String logoURI;

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

        this.logoURI = gameRatingEntity.getLogoURI();

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
    public MGameRatingEntity(
            @NotNull Short id, @NotNull String name, @NotNull String longName, @NotNull String location, @NotNull String description
    ) {
        this.id = id;
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
    }
    public MGameRatingEntity(@NotNull String name) {
        this.name = name;
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

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    @Override
    public void setImageURIsFromFilenames(String[] filenames) {
        if (filenames.length == 0)
            return;

        this.setLogoURI(
                StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.constructRelativeURI(
                        new String[]{this.getName(), filenames[0]}
                )
        );
    }

    public Short getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLongName() {
        return longName;
    }
    public String getLocation() {
        return location;
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

    @Override
    public String getSubDirectory() {
        return id != null ? id.toString() : null;
    }
    @Override
    public ResourcePath getResourcePath() {
        return ResourcePath.GAME_RATING_ENTITIES;
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
