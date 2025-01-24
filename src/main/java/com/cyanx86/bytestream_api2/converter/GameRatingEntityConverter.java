package com.cyanx86.bytestream_api2.converter;

import com.cyanx86.bytestream_api2.entity.GameRatingEntity;
import com.cyanx86.bytestream_api2.model.MGameRatingEntity;
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

}
