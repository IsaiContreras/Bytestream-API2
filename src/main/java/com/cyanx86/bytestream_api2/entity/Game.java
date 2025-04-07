package com.cyanx86.bytestream_api2.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    @Column(name="game_id") @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="name", unique=true, nullable=false, length=63)
    private String name;

    @Column(name="title", nullable=false, length=63)
    private String title;

    @Column(name="synopsis", nullable=false, length=4095)
    private String synopsis;

    @Column(name="release_date", nullable=false)
    @Temporal(TemporalType.DATE) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
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
        this.gameCategories = new ArrayList<>(gameCategories);
    }
    public void setGameRatings(List<GameRating> gameRatings) {
        this.gameRatings = new ArrayList<>(gameRatings);
    }
    public void setGameRatingDescriptors(List<GameRatingDescriptor> gameRatingDescriptors) {
        this.gameRatingDescriptors = new ArrayList<>(gameRatingDescriptors);
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
