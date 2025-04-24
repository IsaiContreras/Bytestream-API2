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

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "title", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "synopsis", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "releaseDate", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameCategories", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameRatings", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "gameRatingDescriptors", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void partialUpdateGame(@MappingTarget Game destiny, Game source);

}
