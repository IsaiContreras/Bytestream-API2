package com.cyanx86.bytestream_api2.converter;

import com.cyanx86.bytestream_api2.entity.GameCategory;
import com.cyanx86.bytestream_api2.model.MGameCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("game_category_converter")
public class GameCategoryConverter {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public List<MGameCategory> parseToList(List<GameCategory> gameCategories) {
        List<MGameCategory> result = new ArrayList<>();

        for (GameCategory itemGameCategory : gameCategories)
            result.add(new MGameCategory(itemGameCategory));

        return result;
    }

}
