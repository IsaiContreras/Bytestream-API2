package com.bytestream_api2.games.configuration.enums;

import com.bytestream_api2.games.utilities.enums.FilenameFormat;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FilenameFormatConfigurer {

    private final Environment environment;
    private static final Log logger = LogFactory.getLog(FilenameFormatConfigurer.class);

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
            } catch (Exception e) {
                logger.error("FilenameFormat " + value.getPath() + " couldn't be loaded or does not exist", e);
            }
        }
    }

}
