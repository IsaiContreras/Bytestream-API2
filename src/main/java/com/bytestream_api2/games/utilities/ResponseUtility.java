package com.bytestream_api2.games.utilities;

import java.util.Map;

public class ResponseUtility {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    public static Map<String, ?> result(Object content) {
        return Map.of("result", content);
    }

    public static Map<String, ?> error(Object content) {
        return Map.of("error", content);
    }

}
