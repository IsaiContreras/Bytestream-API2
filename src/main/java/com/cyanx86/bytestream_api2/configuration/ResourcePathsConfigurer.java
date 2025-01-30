package com.cyanx86.bytestream_api2.configuration;

import com.cyanx86.bytestream_api2.misc.ResourcePaths;
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
        for (ResourcePaths value : ResourcePaths.values()) {
            try {
                value.setValue(
                        this,
                        Objects.requireNonNull(environment.getProperty(value.getPath()))
                );
            } catch (Exception ignored) { } // TODO: LOGGER
        }
    }

}
