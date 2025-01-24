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
@Table(name="game_ratings")
public class GameRating implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="rating_id") @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="name", nullable = false, length=31)
    private String name;

    @Column(name="title", nullable=false, length=31)
    private String title;

    @Column(name="description", nullable=false, length=31)
    private String description;

    @Column(name="created_at", nullable = false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // Relations
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL, optional=false)
    @JoinColumn(name="entity_id")
    private GameRatingEntity gameRatingEntity;

    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Game> games;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRating() {}
    public GameRating(GameRating gameRating) {
        this.id = gameRating.getId();
        this.name = gameRating.getName();
        this.title = gameRating.getTitle();
        this.description = gameRating.getDescription();
        this.createdAt = gameRating.getCreatedAt();
        this.updatedAt = gameRating.getUpdatedAt();
        this.deletedAt = gameRating.getDeletedAt();

        gameRatingEntity = gameRating.getGameRatingEntity();
        games = gameRating.getGames();
    }
    public GameRating(String name, String title, String description) {
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }
    public String getName() { return name; }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
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

    public GameRatingEntity getGameRatingEntity() {
        return new GameRatingEntity(gameRatingEntity);
    }
    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

}
