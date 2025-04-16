package com.bytestream_api2.games.converter;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("game_rating_descriptor_converter")
public class GameRatingDescriptorConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public List<MGameRatingDescriptor> parseToList(List<GameRatingDescriptor> ratingDescriptors) {
        List<MGameRatingDescriptor> result = new ArrayList<>();

        for (GameRatingDescriptor ratingDescriptorItem : ratingDescriptors)
            result.add(new MGameRatingDescriptor(ratingDescriptorItem, true));

        return result;
    }

}
