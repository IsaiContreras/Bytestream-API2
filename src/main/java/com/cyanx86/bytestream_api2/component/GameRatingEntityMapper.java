package com.cyanx86.bytestream_api2.component;

import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
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

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "longName", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "location", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "description", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void partialUpdateRatingEntity(@MappingTarget GameRatingEntity destiny, GameRatingEntity source);

}
