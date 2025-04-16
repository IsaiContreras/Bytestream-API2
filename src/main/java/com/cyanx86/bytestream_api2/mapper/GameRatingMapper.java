package com.cyanx86.bytestream_api2.mapper;

import com.cyanx86.bytestream_api2.entity.GameRating;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GameRatingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "description", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void partialUpdateRating(@MappingTarget GameRating destiny, GameRating source);

}
