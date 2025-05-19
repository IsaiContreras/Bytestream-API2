package com.bytestream_api2.games.entity;

import com.bytestream_api2.games.validation_groups.Game.OnCreate;
import com.bytestream_api2.games.validation_groups.Game.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="games")
public class Game implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="game_id") @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", unique=true, nullable=false, length=63)
    @NotBlank(message="Field 'name' is mandatory.", groups=OnCreate.class)
    @Size(max=63, message="Field 'name' must be less than 63 characters long.", groups={OnCreate.class, OnUpdate.class})
    @Pattern(
            message= "Field 'name' must not contain spaces or uppercases and must be separated with '-'.",
            regexp = "^$|^[a-z0-9\\p{Punct}&&[^_]-]+(-[a-z0-9\\p{Punct}&&[^_]-]+)*$",
            groups={OnCreate.class, OnUpdate.class}
    )
    private String name;

    @Column(name="title", nullable=false, length=63)
    @NotBlank(message="Field 'title' is mandatory.", groups=OnCreate.class)
    @Size(max=63, message="Field 'title' must be less than 63 characters long.", groups={OnCreate.class, OnUpdate.class})
    private String title;

    @Column(name="synopsis", nullable=false, length=4095)
    @NotBlank(message="Field 'synopsis' is mandatory.", groups=OnCreate.class)
    @Size(max=4095, message="Field 'synopsis' must be less than 4095 characters long.",
            groups={OnCreate.class, OnUpdate.class})
    private String synopsis;

    @Column(name="release_date", nullable=false)
    @Temporal(TemporalType.DATE) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    @jakarta.validation.constraints.NotNull(message="Field 'releaseDate' is mandatory.", groups=OnCreate.class)
    private Date releaseDate;

    @Column(name="created_at", nullable=false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // Relations
    @ManyToMany
    @JoinTable(
            name="game_categories_games",
            joinColumns = @JoinColumn(name="game_id"),
            inverseJoinColumns = @JoinColumn(name="catego_id")
    )
    @Size(min=1, message="Field 'gameCategories' must have at least one element in list.")
    private List<GameCategory> gameCategories;

    @ManyToMany
    @JoinTable(
            name="game_ratings_games",
            joinColumns = @JoinColumn(name="game_id"),
            inverseJoinColumns = @JoinColumn(name="rating_id")
    )
    private List<GameRating> gameRatings;

    @ManyToMany
    @JoinTable(
            name="game_rating_descriptors_games",
            joinColumns = @JoinColumn(name="game_id"),
            inverseJoinColumns = @JoinColumn(name="r_descriptor_id")
    )
    private List<GameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public Game() {}
    public Game(@NotNull Game game) {
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
    public Game(@NotNull String name, @NotNull String title, @NotNull String synopsis, @NotNull Date releaseDate) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setGameCategories(List<GameCategory> gameCategories) {
        this.gameCategories = (gameCategories != null) ? new ArrayList<>(gameCategories) : null;
    }
    public void setGameRatings(List<GameRating> gameRatings) {
        this.gameRatings = (gameRatings != null) ? new ArrayList<>(gameRatings) : null;
    }
    public void setGameRatingDescriptors(List<GameRatingDescriptor> gameRatingDescriptors) {
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
        return releaseDate;
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

    public List<GameCategory> getGameCategories() {
        return (this.gameCategories == null) ? null : new ArrayList<>(this.gameCategories);
    }
    public List<GameRating> getGameRatings() {
        return (this.gameRatings == null) ? null : new ArrayList<>(this.gameRatings);
    }
    public List<GameRatingDescriptor> getGameRatingDescriptors() {
        return (this.gameRatingDescriptors == null) ? null : new ArrayList<>(this.gameRatingDescriptors);
    }

}
