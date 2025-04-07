package com.cyanx86.bytestream_api2.model;

import com.cyanx86.bytestream_api2.entity.Game;
import com.cyanx86.bytestream_api2.entity.GameCategory;
import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MGame {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE
    // Data
    private UUID id;
    private String title;
    private String name;
    private String synopsis;
    private Date releaseDate;

    private List<String> logoURIList = new ArrayList<>();
    private List<String> coverURIList = new ArrayList<>();
    private List<String> landscapeURIList = new ArrayList<>();

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
    public MGame(@NotNull Game game, boolean recursive) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.name = game.getName();
        this.synopsis = game.getSynopsis();
        this.releaseDate = game.getReleaseDate();

        this.createdAt = game.getCreatedAt();
        this.updatedAt = game.getUpdatedAt();
        this.deletedAt = game.getDeletedAt();

        if (recursive) {
            this.gameCategories = new ArrayList<>();
            for (GameCategory itemGameCategory : game.getGameCategories())
                this.gameCategories.add(new MGameCategory(itemGameCategory));

            this.gameRatings = new ArrayList<>();
            for (GameRating itemGameRating : game.getGameRatings())
                this.gameRatings.add(new MGameRating(itemGameRating, false));

            this.gameRatingDescriptors = new ArrayList<>();
            for (GameRatingDescriptor itemGameRating : game.getGameRatingDescriptors())
                this.gameRatingDescriptors.add(new MGameRatingDescriptor(itemGameRating, false));
        }
    }
    public MGame(@NotNull MGame game) {
        this.id = game.getId();
        this.name = game.getName();
        this.title = game.getTitle();
        this.synopsis = game.getSynopsis();
        this.releaseDate = game.getReleaseDate();

        this.createdAt = game.getCreatedAt();
        this.updatedAt = game.getUpdatedAt();
        this.deletedAt = game.getDeletedAt();

        this.gameCategories = game.getGameCategories();
        this.gameRatings = game.getGameRatings();
        this.gameRatingDescriptors = game.getGameRatingDescriptors();
    }
    public MGame(@NotNull String name, @NotNull String title, @NotNull String synopsis, @NotNull Date releaseDate) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }
    public void setTitle(@NotNull String title) {
        this.title = title;
    }
    public void setSynopsis(@NotNull String synopsis) {
        this.synopsis = synopsis;
    }
    public void setReleaseDate(@NotNull Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setLogoURIList(List<String> logoURIList) {
        this.logoURIList = new ArrayList<>(logoURIList);
    }
    public void setCoverURIList(List<String> coverURIList) {
        this.coverURIList = new ArrayList<>(coverURIList);
    }
    public void setLandscapeURIList(List<String> landscapeURIList) {
        this.landscapeURIList = new ArrayList<>(landscapeURIList);
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getTitle() {
        return this.title;
    }
    public String getSynopsis() {
        return this.synopsis;
    }
    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public List<String> getLogoURIList() {
        return this.logoURIList;
    }
    public List<String> getCoverURIList() {
        return this.coverURIList;
    }
    public List<String> getLandscapeURIList() {
        return this.landscapeURIList;
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
        return (this.gameCategories == null) ? null : new ArrayList<>(this.gameCategories);
    }
    public List<MGameRating> getGameRatings() {
        return (this.gameRatings == null) ? null : new ArrayList<>(this.gameRatings);
    }
    public List<MGameRatingDescriptor> getGameRatingDescriptors() {
        return (this.gameRatingDescriptors == null) ? null : new ArrayList<>(this.gameRatingDescriptors);
    }

}
