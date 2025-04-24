package com.bytestream_api2.games.configuration;

import com.bytestream_api2.games.misc.ResourcePath;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ResourcePathsConfigurer {

    private final Environment environment;

    @Autowired
    public ResourcePathsConfigurer(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        for (ResourcePath value : ResourcePath.values()) {
            try {
                value.setPath(
                        this,
                        Objects.requireNonNull(environment.getProperty(value.getKey()))
                );
            } catch (Exception ignored) { } // TODO: LOGGER
        }
    }

}
