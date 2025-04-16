package com.bytestream_api2.games.converter;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.model.MGameRating;
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
