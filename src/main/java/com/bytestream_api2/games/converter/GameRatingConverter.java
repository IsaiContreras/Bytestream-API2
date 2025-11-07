package com.bytestream_api2.games.converter;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.utilities.interfaces.ImageContentEntity;
import com.bytestream_api2.games.model.MGameRating;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("game_rating_converter")
public class GameRatingConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public List<MGameRating> parseToList(List<GameRating> ratings) {
        List<MGameRating> result = new ArrayList<>();

        for (GameRating ratingItem : ratings)
            result.add(new MGameRating(ratingItem, true));

        return result;
    }

    public List<MGameRating> parseICEToEntity(List<ImageContentEntity> imageContentEntities) {
        List<MGameRating> result = new ArrayList<>();

        for (ImageContentEntity itemEntity : imageContentEntities)
            result.add((MGameRating)itemEntity);

        return result;
    }

    public List<ImageContentEntity> parseEntityToICE(List<MGameRating> gameRatings) {
        List<ImageContentEntity> result = new ArrayList<>();

        for (MGameRating itemGameRatingEntity : gameRatings)
            result.add((ImageContentEntity)itemGameRatingEntity);

        return result;
    }

}
