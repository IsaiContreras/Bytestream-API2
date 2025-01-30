package com.cyanx86.bytestream_api2.misc;

import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Objects;

public enum StaticResourcesPaths {

    // -- [[ VALUES ]] --
    GAME_RATING_ENTITY_LOGOS("/public/media/rating_entity/");

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    private final String staticPath;

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    StaticResourcesPaths(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getStaticPath() { return this.staticPath; }

    public String constructFullURI(Environment environment, String[] inputs) {
        StringBuilder uri;
        try {
           uri = new StringBuilder(InetAddress.getLoopbackAddress().getHostAddress().concat(":")
                   .concat(Objects.requireNonNull(environment.getProperty("server.port")))
                   .concat(this.getStaticPath()));
        } catch (Exception e) { return null; }

        for (int i = 0; i < inputs.length; i++) {
            if (i == (inputs.length - 1)) uri.append(inputs[i]);
            else uri.append(inputs[i]).append("/");
        }

        return uri.toString();
    }

}
