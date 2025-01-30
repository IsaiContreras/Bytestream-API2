package com.cyanx86.bytestream_api2.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesMapping implements WebMvcConfigurer {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Game rating entity image mapping

        /*registry.addResourceHandler(StaticResourcesPaths.GAME_RATING_ENTITY_LOGOS.getStaticPath() + "**")
                .addResourceLocations("file:" + MediaPaths.getPathOfEntity(
                        MediaEntity.GAME_RATING_ENTITIES, null
                ));*/

        //
    }

}
