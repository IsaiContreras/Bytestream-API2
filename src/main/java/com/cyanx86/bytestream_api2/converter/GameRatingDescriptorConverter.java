package com.cyanx86.bytestream_api2.converter;

import com.cyanx86.bytestream_api2.entity.GameRatingDescriptor;
import com.cyanx86.bytestream_api2.model.MGameRatingDescriptor;
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
