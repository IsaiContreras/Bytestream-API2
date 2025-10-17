package com.bytestream_api2.games.converter;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.interfaces.ImageContentEntity;
import com.bytestream_api2.games.model.MGameRatingEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("game_rating_entity_converter")
public class GameRatingEntityConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public List<MGameRatingEntity> parseToList(List<GameRatingEntity> gameRatingEntities) {
        List<MGameRatingEntity> result = new ArrayList<>();

        for (GameRatingEntity itemGameRatingEntity : gameRatingEntities)
            result.add(new MGameRatingEntity(itemGameRatingEntity));

        return result;
    }

    public List<MGameRatingEntity> parseICEToEntity(List<ImageContentEntity> imageContentEntities) {
        List<MGameRatingEntity> result = new ArrayList<>();

        for (ImageContentEntity itemEntity : imageContentEntities)
            result.add((MGameRatingEntity)itemEntity);

        return result;
    }

    public List<ImageContentEntity> parseEntityToICE(List<MGameRatingEntity> gameRatingEntities) {
        List<ImageContentEntity> result = new ArrayList<>();

        for (MGameRatingEntity itemGameRatingEntity : gameRatingEntities)
            result.add((ImageContentEntity)itemGameRatingEntity);

        return result;
    }

}
