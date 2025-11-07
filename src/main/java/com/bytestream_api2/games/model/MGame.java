package com.bytestream_api2.games.model;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.utilities.interfaces.ImageContentEntity;
import com.bytestream_api2.games.utilities.enums.FilenameFormat;
import com.bytestream_api2.games.utilities.enums.ResourcePath;
import com.bytestream_api2.games.utilities.enums.StaticResourcesPaths;
import com.bytestream_api2.games.utilities.statics.FilenameFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MGame implements ImageContentEntity {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE
    // Data
    private Long id;
    private String title;
    private String name;
    private String synopsis;
    private Date releaseDate;

    private String coverURI;
    private String landscapeURI;

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
        id = game.getId();
        title = game.getTitle();
        name = game.getName();
        synopsis = game.getSynopsis();
        releaseDate = game.getReleaseDate();

        createdAt = game.getCreatedAt();
        updatedAt = game.getUpdatedAt();
        deletedAt = game.getDeletedAt();

        if (recursive) {
            gameCategories = new ArrayList<>();
            if (game.getGameCategories() != null)
                for (GameCategory itemGameCategory : game.getGameCategories())
                    gameCategories.add(new MGameCategory(itemGameCategory));

            gameRatings = new ArrayList<>();
            if (game.getGameRatings() != null)
                for (GameRating itemGameRating : game.getGameRatings())
                    gameRatings.add(new MGameRating(itemGameRating, false));

            gameRatingDescriptors = new ArrayList<>();
            if (game.getGameRatingDescriptors() != null)
                for (GameRatingDescriptor itemGameRating : game.getGameRatingDescriptors())
                    gameRatingDescriptors.add(new MGameRatingDescriptor(itemGameRating, false));
        }
    }
    public MGame(@NotNull MGame game) {
        id = game.getId();
        name = game.getName();
        title = game.getTitle();
        synopsis = game.getSynopsis();
        releaseDate = game.getReleaseDate();

        createdAt = game.getCreatedAt();
        updatedAt = game.getUpdatedAt();
        deletedAt = game.getDeletedAt();

        gameCategories = game.getGameCategories();
        gameRatings = game.getGameRatings();
        gameRatingDescriptors = game.getGameRatingDescriptors();
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
        gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis,
            @NotNull Date releaseDate, List<MGameCategory> categories, List<MGameRating> ratings
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
        gameRatings = (ratings != null) ? new ArrayList<>(ratings) : null;
    }
    public MGame(
            Long id, @NotNull String name, @NotNull String title, @NotNull String synopsis, @NotNull Date releaseDate,
            List<MGameCategory> categories, List<MGameRating> ratings, List<MGameRatingDescriptor> descriptors
    ) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        gameCategories = (categories != null) ? new ArrayList<>(categories) : null;
        gameRatings = (ratings != null) ? new ArrayList<>(ratings) : null;
        gameRatingDescriptors = (descriptors != null) ? new ArrayList<>(descriptors) : null;
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
    public void setLandscapeURI(String landscapeURI) {
        this.landscapeURI = landscapeURI;
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

    @Override
    public void setImageURIsFromFilenames(String[] filenames) {
        if (filenames.length == 0)
            return;

        String coverFilename = Arrays.stream(filenames).filter(uri -> uri.contains("cover"))
                .findFirst().orElse(null);
        String landscapeFilename = Arrays.stream(filenames).filter(uri -> uri.contains("landscape"))
                .findFirst().orElse(null);

        if (coverFilename != null)
            StaticResourcesPaths.GAME_ART.constructRelativeURI(
                    new String[] {this.getName(), coverFilename}
            );

        if (landscapeFilename != null)
            StaticResourcesPaths.GAME_ART.constructRelativeURI(
                    new String[] {this.getName(), landscapeFilename}
            );
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getTitle() {
        return title;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getCoverURI() {
        return coverURI;
    }
    public String getLandscapeURI() {
        return landscapeURI;
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

    public List<MGameCategory> getGameCategories() {
        return (gameCategories == null) ? null : new ArrayList<>(gameCategories);
    }
    public List<MGameRating> getGameRatings() {
        return (gameRatings == null) ? null : new ArrayList<>(gameRatings);
    }
    public List<MGameRatingDescriptor> getGameRatingDescriptors() {
        return (gameRatingDescriptors == null) ? null : new ArrayList<>(gameRatingDescriptors);
    }

    @Override
    @JsonIgnore
    public String getSubDirectory() {
        return id != null ? id.toString() : "";
    }
    @Override
    @JsonIgnore
    public ResourcePath getResourcePath() {
        return ResourcePath.GAME_ART;
    }

    @Override
    public String constructFilename(String extension, String[] addit) throws NullPointerException {
        return FilenameFormatter.formatFilename(
                FilenameFormat.GAME_ART_FORMAT,
                new String[]{
                        getId().toString().concat(getName()),
                        Objects.requireNonNull(addit[0]),
                        extension
                }
        );
    }

}
