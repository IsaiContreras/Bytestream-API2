package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.exception.imageresource.ImagePathUnresolvedException;
import com.bytestream_api2.games.exception.imageresource.UncaughtImageExtensionException;
import com.bytestream_api2.games.exception.imageresource.UnreadableResourceException;
import com.bytestream_api2.games.utilities.classes.ImageResourcePackage;
import com.bytestream_api2.games.service.GameRatingEntityService;
import com.bytestream_api2.games.service.GameRatingService;
import com.bytestream_api2.games.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntityStaticResourceController.class)
public class EntityStaticResourceControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_rating_entity_service")
    private GameRatingEntityService gameRatingEntityService;

    @MockitoBean(name="game_rating_service")
    private GameRatingService gameRatingService;

    @MockitoBean(name="game_service")
    private GameService gameService;

    // Class Components
    private ObjectMapper mapper;

    private final String root = "/public/media";

    private final String controllerRatingEntity = "/rating_entity/{name}/{filename}";
    private final String controllerRating = "/rating/{name}/{filename}";
    private final String controllerGameArt = "/game_art/{name}/{filename}";

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
    }

    // Rating Entity Images
    @Test
    public void getValidRatingEntityImageTest() throws Exception {
        // Preparation
        ImageResourcePackage resourcePackage = new ImageResourcePackage(
                "ESRB.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                anyInt(),
                anyInt()
        )).thenReturn(resourcePackage);

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png")
                .param("width", mapper.writeValueAsString(32))
                .param("height", mapper.writeValueAsString(32))
        ).andExpect(status().isOk());
    }

    @Test
    public void getRatingEntityImageIOErrorTest() throws Exception {
        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new IOException("Couldn't write file."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getNonExistentRatingEntityImageTest() throws Exception{
        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new EntityNotFoundException("Couldn't find a Game with this name."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Couldn't find a Game with this name."));
    }

    @Test
    public void getRatingEntityImagePathUnresolvedTest() throws Exception {
        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new ImagePathUnresolvedException("Couldn't resolve image file path."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't resolve image file path."));
    }

    @Test
    public void getRatingEntityImageUnreadableResourceTest() throws Exception {
        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UnreadableResourceException("Resource couldn't load or is unreadable."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Resource couldn't load or is unreadable."));
    }

    @Test
    public void getRatingEntityImageUncaughtExtensionTest() throws Exception {
        // Dependency call handlers
        when(gameRatingEntityService.getLogoImage(
                eq("ESRB"),
                eq("ESRB.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UncaughtImageExtensionException("Couldn't caught image file extension."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRatingEntity, "ESRB", "ESRB.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't caught image file extension."));
    }

    // Rating Images
    @Test
    public void getValidRatingImageTest() throws Exception {
        // Preparation
        ImageResourcePackage resourcePackage = new ImageResourcePackage(
                "everyone.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                anyInt(),
                anyInt()
        )).thenReturn(resourcePackage);

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png")
                .param("width", mapper.writeValueAsString(32))
                .param("height", mapper.writeValueAsString(32))
        ).andExpect(status().isOk());
    }

    @Test
    public void getRatingImageIOErrorTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new IOException("Couldn't write file."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getNonExistentRatingImageTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new EntityNotFoundException("Couldn't find a Game with this name."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Couldn't find a Game with this name."));
    }

    @Test
    public void getRatingImageUnresolvedTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new ImagePathUnresolvedException("Couldn't resolve image file path."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't resolve image file path."));
    }

    @Test
    public void getRatingImageUnreadableResourceTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UnreadableResourceException("Resource couldn't load or is unreadable."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Resource couldn't load or is unreadable."));
    }

    @Test
    public void getRatingImageUncaughtExtensionTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getLogoImage(
                eq("everyone"),
                eq("everyone.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UncaughtImageExtensionException("Couldn't caught image file extension."));

        // Perform and assert
        mockMvc.perform(get(root + controllerRating, "everyone", "everyone.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't caught image file extension."));
    }

    // GameArt Images
    @Test
    public void getValidGameImageTest() throws Exception {
        // Preparation
        ImageResourcePackage resourcePackage = new ImageResourcePackage(
                "1001_sonic-mania.png",
                "image/png",
                new byte[] {1}
        );

        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                anyInt(),
                anyInt()
        )).thenReturn(resourcePackage);

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png")
                .param("width", mapper.writeValueAsString(32))
                .param("height", mapper.writeValueAsString(32))
        ).andExpect(status().isOk());
    }

    @Test
    public void getGameImageIOErrorTest() throws Exception {
        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new IOException("Couldn't write file."));

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getNonExistentGameImageTest() throws Exception {
        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new EntityNotFoundException("Couldn't find a Game with this name."));

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Couldn't find a Game with this name."));
    }

    @Test
    public void getGameImageUnresolvedTest() throws Exception {
        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new ImagePathUnresolvedException("Couldn't resolve image file path."));

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't resolve image file path."));
    }

    @Test
    public void getGameImageUnreadableResourceTest() throws Exception {
        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UnreadableResourceException("Resource couldn't load or is unreadable."));

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Resource couldn't load or is unreadable."));
    }

    @Test
    public void getGameImageUncaughtExtensionTest() throws Exception {
        // Dependency call handlers
        when(gameService.getImage(
                eq("sonic-mania"),
                eq("1001_sonic-mania.png"),
                nullable(Integer.class),
                nullable(Integer.class)
        )).thenThrow(new UncaughtImageExtensionException("Couldn't caught image file extension."));

        // Perform and assert
        mockMvc.perform(get(root + controllerGameArt, "sonic-mania", "1001_sonic-mania.png"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Couldn't caught image file extension."));
    }

}
