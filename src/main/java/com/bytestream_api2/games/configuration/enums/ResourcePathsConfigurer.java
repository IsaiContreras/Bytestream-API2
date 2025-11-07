package com.bytestream_api2.games.configuration.enums;

import com.bytestream_api2.games.utilities.enums.ResourcePath;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ResourcePathsConfigurer {

    private final Environment environment;
    private static final Log logger = LogFactory.getLog(ResourcePathsConfigurer.class);

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
            } catch (Exception e) {
                logger.error("ResourcePath " + value.getKey() + " couldn't be loaded or does not exists.", e);
            }
        }
    }

}
