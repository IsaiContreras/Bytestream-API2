package com.cyanx86.bytestream_api2.converter;

import com.cyanx86.bytestream_api2.entity.GameRating;
import com.cyanx86.bytestream_api2.model.MGameRating;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("game_rating_converter")
public class GameRatingConverter {

    public List<MGameRating> parseToList(List<GameRating> ratings) {
        List<MGameRating> result = new ArrayList<>();

        for (GameRating ratingItem : ratings)
            result.add(new MGameRating(ratingItem, true));

        return result;
    }

}
