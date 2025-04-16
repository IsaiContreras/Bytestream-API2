package com.bytestream_api2.games.configuration;

import com.bytestream_api2.games.misc.FilenameFormat;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FilenameFormatConfigurer {

    private final Environment environment;

    @Autowired
    public FilenameFormatConfigurer(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        for (FilenameFormat value : FilenameFormat.values()) {
            try {
                value.setValue(
                        this,
                        Objects.requireNonNull(environment.getProperty(value.getPath()))
                );
            } catch (Exception ignored) { } // TODO: LOGGER
        }
    }

}
