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
@Table(name="game_categories")
public class GameCategory implements Serializable {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    // Columns
    @Column(name="catego_id") @Id @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="title", nullable=false, length=31)
    private String title;

    @Column(name="created_at", nullable=false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at") @Temporal(TemporalType.TIMESTAMP) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;

    // Relations
    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Game> games;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public GameCategory() {}
    public GameCategory(GameCategory gameCategory) {
        this.id = gameCategory.getId();
        this.title = gameCategory.getTitle();
        this.createdAt = gameCategory.getCreatedAt();
        this.updatedAt = gameCategory.getUpdatedAt();
        this.deletedAt = gameCategory.getDeletedAt();

        this.games = gameCategory.getGames();
    }
    public GameCategory(String title) {
        this.title = title;
    }

    public void setDeletedAt(Date deletedAt) { this.deletedAt = deletedAt; }

    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
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

    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

}
