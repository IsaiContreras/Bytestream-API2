package com.cyanx86.bytestream_api2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="games")
public class Game implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="game_id") @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="name", nullable = false, length=63)
    private String name;

    @Column(name="title", nullable=false, length=63)
    private String title;

    @Column(name="synopsis", nullable=false, length=1023)
    private String synopsis;

    @Column(name="created_at", nullable=false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // Relations
    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="games")
    private List<GameCategory> gameCategories;

    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="games")
    private List<GameRating> gameRatings;

    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="games")
    private List<GameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public Game() {}
    public Game(Game game) {
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
    public Game(String name, String title, String synopsis) {
        this.name = name;
        this.title = title;
        this.synopsis = synopsis;
    }

    public UUID getId() {
        return id;
    }
    public String getName() { return name; }
    public String getTitle() {
        return title;
    }
    public String getSynopsis() {
        return synopsis;
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

    public List<GameCategory> getGameCategories() {
        return new ArrayList<>(gameCategories);
    }
    public List<GameRating> getGameRatings() {
        return new ArrayList<>(gameRatings);
    }
    public List<GameRatingDescriptor> getGameRatingDescriptors() {
        return new ArrayList<>(gameRatingDescriptors);
    }

}
