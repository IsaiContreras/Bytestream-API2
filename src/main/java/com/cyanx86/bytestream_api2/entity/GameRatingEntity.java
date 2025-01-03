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
@Table(name="game_rating_entities")
public class GameRatingEntity implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="entity_id") @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="name", nullable=false, length=7)
    private String name;

    @Column(name="long_name", nullable=false, length=31)
    private String longName;

    @Column(name="location", nullable=false, length=511)
    private String location;

    @Column(name="description", nullable=false, length=255)
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
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="gameRatingEntity")
    private List<GameRating> gameRatings;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="gameRatingEntity")
    private List<GameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC --

    // -- [[ METHODS  ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRatingEntity() {}
    public GameRatingEntity(GameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.longName = gameRatingEntity.getLongName();
        this.description = gameRatingEntity.getDescription();
        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();

        this.gameRatings = gameRatingEntity.getGameRatings();
        this.gameRatingDescriptors = getGameRatingDescriptors();
    }
    public GameRatingEntity(String name, String longName, String location, String description) {
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLongName() { return longName; }
    public String getLocation() {
        return location;
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

    public List<GameRating> getGameRatings() {
        return new ArrayList<>(gameRatings);
    }
    public List<GameRatingDescriptor> getGameRatingDescriptors() {
        return new ArrayList<>(gameRatingDescriptors);
    }

}
