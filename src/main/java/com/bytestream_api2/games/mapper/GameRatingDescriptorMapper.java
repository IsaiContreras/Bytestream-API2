package com.bytestream_api2.games.mapper;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GameRatingDescriptorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)

    @Named("toNullIfEmpty")
    static String toNullIfEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName="toNullIfEmpty")
    @Mapping(target = "description", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            qualifiedByName="toNullIfEmpty")
    void partialUpdateRatingDescriptor(@MappingTarget GameRatingDescriptor destiny, GameRatingDescriptor source);

}
