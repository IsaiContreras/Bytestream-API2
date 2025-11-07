package com.bytestream_api2.games.converter;

import com.bytestream_api2.games.utilities.interfaces.ImageContentEntity;
import org.springframework.stereotype.Component;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.model.MGame;

import java.util.ArrayList;
import java.util.List;

@Component("game_converter")
public class GameConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public List<MGame> parseToList(List<Game> games) {
        List<MGame> result = new ArrayList<>();

        for (Game gameItem : games)
            result.add(new MGame(gameItem, true));

        return result;
    }

    public List<MGame> parseICEToEntity(List<ImageContentEntity> imageContentEntities) {
        List<MGame> result = new ArrayList<>();

        for (ImageContentEntity itemEntity : imageContentEntities)
            result.add((MGame)itemEntity);

        return result;
    }

    public List<ImageContentEntity> parseEntityToICE(List<MGame> games) {
        List<ImageContentEntity> result = new ArrayList<>();

        for (MGame itemGameRatingEntity : games)
            result.add((ImageContentEntity)itemGameRatingEntity);

        return result;
    }

}
