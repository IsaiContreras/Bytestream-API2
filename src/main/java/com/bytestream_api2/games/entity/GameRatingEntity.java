package com.bytestream_api2.games.entity;

import com.bytestream_api2.games.validation_groups.GameRatingEntity.OnCreate;
import com.bytestream_api2.games.validation_groups.GameRatingEntity.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Column(name="entity_id") @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Short id;

    @Column(name="name", unique=true, nullable=false, length=15)
    @NotBlank(message="Field 'name' is mandatory.", groups=OnCreate.class)
    @Size(max=15, message="Field 'name' must be less than 15 characters long.",
            groups={OnCreate.class, OnUpdate.class})
    private String name;

    @Column(name="long_name", nullable=false, length=127)
    @NotBlank(message="Field 'longName' is mandatory.", groups=OnCreate.class)
    @Size(max=127, message="Field 'longName' must be less than 127 characters long.",
            groups={OnCreate.class, OnUpdate.class})
    private String longName;

    @Column(name="location", nullable=false, length=511)
    @NotBlank(message="Field 'location' is mandatory.", groups=OnCreate.class)
    @Size(max=511, message="Field 'location' must be less than 511 characters long.",
            groups={OnCreate.class, OnUpdate.class})
    private String location;

    @Column(name="description", nullable=false, length=511)
    @NotBlank(message="Field 'description' is mandatory.", groups=OnCreate.class)
    @Size(max=511, message="Field 'description' must be less than 511 characters long.",
            groups={OnCreate.class, OnUpdate.class})
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
        id = gameRatingEntity.getId();
        name = gameRatingEntity.getName();
        longName = gameRatingEntity.getLongName();
        location = gameRatingEntity.getLocation();
        description = gameRatingEntity.getDescription();
        createdAt = gameRatingEntity.getCreatedAt();
        updatedAt = gameRatingEntity.getUpdatedAt();
        deletedAt = gameRatingEntity.getDeletedAt();

        gameRatings = gameRatingEntity.getGameRatings();
        gameRatingDescriptors = gameRatingEntity.getGameRatingDescriptors();
    }
    public GameRatingEntity(@NotNull String name) {
        this.name = name;
    }
    public GameRatingEntity(@NotNull String name, @NotNull String longName) {
        this.name = name;
        this.longName = longName;
    }
    public GameRatingEntity(
            @NotNull Short id, @NotNull String name, @NotNull String longName, @NotNull String location,
            @NotNull String description
    ) {
        this.id = id;
        this.name = name;
        this.longName = longName;
        this.location = location;
        this.description = description;
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
        return (gameRatings == null) ? null : new ArrayList<>(gameRatings);
    }
    public List<GameRatingDescriptor> getGameRatingDescriptors() {
        return (gameRatingDescriptors == null) ? null : new ArrayList<>(gameRatingDescriptors);
    }

}
