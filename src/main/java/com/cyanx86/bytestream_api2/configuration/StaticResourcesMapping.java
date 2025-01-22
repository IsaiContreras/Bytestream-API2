package com.cyanx86.bytestream_api2.configuration;

import com.cyanx86.bytestream_api2.components.MediaPaths;
import com.cyanx86.bytestream_api2.misc.MediaEntity;
import com.cyanx86.bytestream_api2.misc.StaticResourcesPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesMapping implements WebMvcConfigurer {

    @Autowired
    @Qualifier("media_paths_component")
    private MediaPaths mediaPaths;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Game rating entity image mapping
        registry.addResourceHandler(StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.getStaticPath() + "**")
                .addResourceLocations("file:" + mediaPaths.getPathOfEntity(
                        MediaEntity.GAME_RATING_ENTITIES, null
                ));

        //
    }

}
