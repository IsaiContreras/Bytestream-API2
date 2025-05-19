package com.bytestream_api2.games.entity;

import com.bytestream_api2.games.validation_groups.GameCategory.OnCreate;
import com.bytestream_api2.games.validation_groups.GameCategory.OnUpdate;
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
@Table(name="game_categories")
public class GameCategory implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="catego_id") @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Short id;

    @Column(name="name", unique=true, nullable=false, length=31)
    @NotBlank(message="Field 'name' is mandatory", groups=OnCreate.class)
    @Size(max=31, message="Field 'name' must be less than 31 characters long.", groups={OnCreate.class, OnUpdate.class})
    @Pattern(
            message= "Field 'name' must not contain spaces or uppercases and must be separated with '-'.",
            regexp = "^$|^[a-z0-9\\p{Punct}&&[^_]-]+(-[a-z0-9\\p{Punct}&&[^_]-]+)*$",
            groups={OnCreate.class, OnUpdate.class}
    )
    private String name;

    @Column(name="created_at", nullable=false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameCategory() {}
    public GameCategory(@NotNull GameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.name = gameCategory.getName();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();
    }
    public GameCategory(@NotNull String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }

}
