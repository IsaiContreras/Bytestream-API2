package com.bytestream_api2.games.entity;

import com.bytestream_api2.games.validation_groups.GameRatingDescriptor.OnCreate;
import com.bytestream_api2.games.validation_groups.GameRatingDescriptor.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="game_rating_descriptors")
public class GameRatingDescriptor implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="r_descriptor_id") @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Short id;

    @Column(name="name", unique=true, nullable=false, length=31)
    @NotBlank(message="Field 'name' is mandatory.", groups=OnCreate.class)
    @Size(max=31, message="Field 'name' must be less than 31 characters long.", groups={OnCreate.class, OnUpdate.class})
    @Pattern(
            message= "Field 'name' must not contain spaces or uppercases and must be separated with '-'.",
            regexp = "^$|^[a-z0-9\\p{Punct}&&[^_]-]+(-[a-z0-9\\p{Punct}&&[^_]-]+)*$",
            groups={OnCreate.class, OnUpdate.class}
    )
    private String name;

    @Column(name="description", nullable=false, length=1023)
    @NotBlank(message="Field 'description' is mandatory.", groups=OnCreate.class)
    @Size(max=1023, message="Field 'description' must be less than 1023 characters long.",
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
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.MERGE, optional=false)
    @JoinColumn(name="entity_id")
    private GameRatingEntity gameRatingEntity;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameRatingDescriptor() {}
    public GameRatingDescriptor(@NotNull GameRatingDescriptor gameRatingDescriptor) {
        this.id = gameRatingDescriptor.getId();
        this.name = gameRatingDescriptor.getName();
        this.description = gameRatingDescriptor.getDescription();
        this.createdAt = gameRatingDescriptor.getCreatedAt();
        this.updatedAt = gameRatingDescriptor.getUpdatedAt();
        this.deletedAt = gameRatingDescriptor.getDeletedAt();

        this.gameRatingEntity = gameRatingDescriptor.getGameRatingEntity();
    }
    public GameRatingDescriptor(@NotNull String name) {
        this.name = name;
    }
    public GameRatingDescriptor(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setGameRatingEntity(GameRatingEntity ratingEntity) {
        this.gameRatingEntity = (ratingEntity != null) ? new GameRatingEntity(ratingEntity) : null;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Short getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
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

    public GameRatingEntity getGameRatingEntity() {
        return this.gameRatingEntity;
    }

}
