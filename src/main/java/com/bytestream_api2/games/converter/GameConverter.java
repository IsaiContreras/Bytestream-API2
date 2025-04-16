package com.bytestream_api2.games.converter;

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

}
