package com.bytestream_api2.games.mapper;

import com.bytestream_api2.games.entity.Game;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE
)
public interface GameMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)

    @Named("toNullIfEmpty")
    static String toNullIfEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName="toNullIfEmpty")
    @Mapping(target = "title", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName="toNullIfEmpty")
    @Mapping(target = "synopsis", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            qualifiedByName="toNullIfEmpty")
    @Mapping(target = "releaseDate", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameCategories", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameRatings", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameRatingDescriptors", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void partialUpdateGame(@MappingTarget Game destiny, Game source);

}
