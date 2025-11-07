package com.bytestream_api2.games.mapper;

import com.bytestream_api2.games.entity.GameRatingEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GameRatingEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "gameRatings", ignore = true)
    @Mapping(target = "gameRatingDescriptors", ignore = true)

    @Named("toNullIfEmpty")
    static String toNullIfEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName="toNullIfEmpty")
    @Mapping(target = "longName", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            qualifiedByName="toNullIfEmpty")
    @Mapping(target = "location", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            qualifiedByName="toNullIfEmpty")
    @Mapping(target = "description", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            qualifiedByName= "toNullIfEmpty")
    void partialUpdateRatingEntity(@MappingTarget GameRatingEntity destiny, GameRatingEntity source);

}
