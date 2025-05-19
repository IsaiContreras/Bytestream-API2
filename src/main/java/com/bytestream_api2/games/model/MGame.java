package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MGame {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE
    // Data
    private Long id;
    private String title;
    private String name;
    private String synopsis;
    private Date releaseDate;

    private String coverURI;
    private String landsapeURI;

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
    public MGame(Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis,
             @NotNull Date releaseDate
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis,
            @NotNull Date releaseDate, List<MGameCategory> categories
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis,
            @NotNull Date releaseDate, List<MGameCategory> categories, List<MGameRating> ratings
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
        this.gameRatings = (ratings != null) ? new ArrayList<>(ratings) : null;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis, @NotNull Date releaseDate,
            List<MGameCategory> categories, List<MGameRating> ratings, List<MGameRatingDescriptor> descriptors
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
        this.gameRatings = (ratings != null) ? new ArrayList<>(ratings) : null;
        this.gameRatingDescriptors = (descriptors != null) ? new ArrayList<>(descriptors) : null;
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

    public void setCoverURI(String coverURI) {
        this.coverURI = coverURI;
    }
    public void setLandsapeURI(String landsapeURI) {
        this.landsapeURI = landsapeURI;
    }

    public void setGameCategories(List<MGameCategory> gameCategories) {
        this.gameCategories = (gameCategories != null) ? new ArrayList<>(gameCategories) : null;
    }
    public void setGameRatings(List<MGameRating> gameRatings) {
        this.gameRatings = (gameRatings != null) ? new ArrayList<>(gameRatings) : null;
    }
    public void setGameRatingDescriptors(List<MGameRatingDescriptor> gameRatingDescriptors) {
        this.gameRatingDescriptors = (gameRatingDescriptors != null) ? new ArrayList<>(gameRatingDescriptors) : null;
    }

    public Long getId() {
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

    public String getCoverURI() {
        return this.coverURI;
    }
    public String getLandsapeURI() {
        return this.landsapeURI;
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
