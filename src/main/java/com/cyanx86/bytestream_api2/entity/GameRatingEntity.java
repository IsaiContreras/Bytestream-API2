package com.cyanx86.bytestream_api2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="game_rating_entities")
public class GameRatingEntity implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="entity_id") @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(name="name", unique=true, nullable=false, length=15)
    private String name;

    @Column(name="long_name", nullable=false, length=127)
    private String longName;

    @Column(name="location", nullable=false, length=511)
    private String location;

    @Column(name="description", nullable=false, length=511)
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
    @OneToMany(cascade=CascadeType.MERGE, fetch=FetchType.LAZY, mappedBy="gameRatingEntity")
    private List<GameRating> gameRatings;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="gameRatingEntity")
    private List<GameRatingDescriptor> gameRatingDescriptors;

    // -- PUBLIC --

    // -- [[ METHODS  ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRatingEntity() {}
    public GameRatingEntity(@NotNull GameRatingEntity gameRatingEntity) {
        this.id = gameRatingEntity.getId();
        this.name = gameRatingEntity.getName();
        this.longName = gameRatingEntity.getLongName();
        this.location = gameRatingEntity.getLocation();
        this.description = gameRatingEntity.getDescription();
        this.createdAt = gameRatingEntity.getCreatedAt();
        this.updatedAt = gameRatingEntity.getUpdatedAt();
        this.deletedAt = gameRatingEntity.getDeletedAt();

        this.gameRatings = gameRatingEntity.getGameRatings();
        this.gameRatingDescriptors = gameRatingEntity.getGameRatingDescriptors();
    }
    public GameRatingEntity(
            @NotNull String name, @NotNull String longName, @NotNull String location, @NotNull String description
    ) {
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLongName(String longName) {
        this.longName = longName;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getLongName() {
        return this.longName;
    }
    public String getLocation() {
        return this.location;
    }
    public String getDescription() {
        return this.description;
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

    public List<GameRating> getGameRatings() {
        return (this.gameRatings == null) ? null : new ArrayList<>(this.gameRatings);
    }
    public List<GameRatingDescriptor> getGameRatingDescriptors() {
        return (this.gameRatingDescriptors == null) ? null : new ArrayList<>(this.gameRatingDescriptors);
    }

}
