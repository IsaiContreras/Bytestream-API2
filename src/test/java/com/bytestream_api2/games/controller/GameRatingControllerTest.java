package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.exception.services.MediaUploadFailedException;
import com.bytestream_api2.games.model.MGameRating;
import com.bytestream_api2.games.service.GameRatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRatingController.class)
public class GameRatingControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_rating_service")
    private GameRatingService gameRatingService;

    // Class Components
    private ObjectMapper mapper;

    private final String controllerCreateURI = "/ratings/create";
    private final String controllerUpdateURI = "/ratings/update";
    private final String controllerDeleteURI = "/ratings/delete";

    private final String controllerGetByNameURI = "/ratings/get/byname";
    private final String controllerGetByEntityURI = "/ratings/get/byentity";
    private final String controllerGetAllURI = "/ratings/get";

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
    }

    // CUD
    @Test
    public void createValidGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                "e-everyone",
                "Content suitable for all ages.",
                "ESRB"
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );
        MockMultipartFile logoPart = new MockMultipartFile(
                "logo",
                "logo.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[] {1}
        );

        // Dependency call handlers
        when(gameRatingService.create(any(GameRating.class), nullable(MultipartFile.class)))
                .thenReturn(rating);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(dataPart)
                .file(logoPart)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.result.name").value("e-everyone"))
        .andExpect(jsonPath("$.result.description").value("Content suitable for all ages."))
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("ESRB"));
    }

    @Test
    public void createInvalidGameRatingNoNameTest() throws Exception {
        // Preparation
        MockMultipartFile ratingNoName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        "",
                        "Content suitable for all ages.",
                        "esrb"
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingNoName)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory."));
    }

    @Test
    public void createInvalidGameRatingInvalidNameTest() throws Exception {
        // Preparation
        MockMultipartFile ratingInvalidName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        "E Everyone",
                        "Content suitable for all ages.",
                        "esrb"
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                        .file(ratingInvalidName)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
                ));
    }

    @Test
    public void createInvalidGameRatingNoDescriptionTest() throws Exception {
        // Preparation
        MockMultipartFile ratingNoDescription = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        "e-everyone",
                        "",
                        "esrb"
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingNoDescription)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'description' is mandatory."));
    }

    @Test
    public void createInvalidGameRatingNoRatingTest() throws Exception {
        // Preparation
        MockMultipartFile ratingNoEntity = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        "e-everyone",
                        "Content suitable for all ages.",
                        ""
                ))
        );

        // Dependency call handlers
        when(gameRatingService.create(any(GameRating.class), nullable(MultipartFile.class)))
                .thenThrow(new DataAccessException("") {});

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingNoEntity)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidGameRatingInvalidEntityTest() throws Exception {
        // Preparation
        MockMultipartFile ratingInvalidEntity = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        "e-everyone",
                        "Content suitable for all ages.",
                        "ce"
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingInvalidEntity)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void createMediaErrorGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                "e-everyone",
                "Content suitable for all ages.",
                "ESRB"
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );
        MockMultipartFile logoPart = new MockMultipartFile(
                "logo",
                "logo.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[] {1}
        );

        // Dependency call handlers
        when(gameRatingService.create(any(GameRating.class), nullable(MultipartFile.class)))
                .thenThrow(new MediaUploadFailedException("Couldn't upload Logo image file."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(dataPart)
                .file(logoPart)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("Couldn't upload Logo image file."));
    }

    @Test
    public void updateValidGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)502,
                "e10+everyone",
                "Content suitable for ages over 10."
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );

        // Dependency call handlers
        when(gameRatingService.update(any(GameRating.class), nullable(MultipartFile.class)))
                .thenReturn(rating);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.name").value("e10+everyone"))
        .andExpect(jsonPath("$.result.description").value("Content suitable for ages over 10."));
    }

    @Test
    public void updateInvalidGameRatingTest() throws Exception {
        // Preparation
        MockMultipartFile rating = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRating(
                        (short)501,
                        "E 10+",
                        "Content suitable for ages over 10."
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(rating)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void updateValidGameRatingEntityOfGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)501,
                "",
                "",
                "cero"
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );

        // Dependency call handlers
        when(gameRatingService.update(any(GameRating.class), nullable(MultipartFile.class)))
                .thenReturn(rating);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("cero"));
    }

    @Test
    public void updateNonExistentRatingEntityOfGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)501,
                "",
                "",
                "ce"
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );

        // Dependency call handlers
        when(gameRatingService.update(any(GameRating.class), nullable(MultipartFile.class)))
                .thenThrow(new DataAccessException("") {});

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateNonExistentGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)101,
                "e10+",
                "Content suitable for ages over 10."
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );

        // Dependency call handlers
        when(gameRatingService.update(any(GameRating.class), nullable(MultipartFile.class)))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating with this ID."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating with this ID."));
    }

    @Test
    public void updateMediaErrorGameRatingTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)501,
                "e10+",
                "Content suitable for ages over 10."
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(rating)
        );

        // Dependency call handlers
        when(gameRatingService.update(any(GameRating.class), nullable(MultipartFile.class)))
                .thenThrow(new MediaUploadFailedException("Couldn't upload Logo image file."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("Couldn't upload Logo image file."));
    }

    @Test
    public void deleteValidGameRatingTest() throws Exception {
        // Dependency call handlers
        doNothing().when(gameRatingService).delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentGameRatingTest() throws Exception {
        // Dependency call handlers
        doThrow(new EntityNotFoundException("Couldn't find a Rating with this ID"))
                .when(gameRatingService).delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating with this ID"));
    }

    // Queries
    @Test
    public void searchValidGameRatingByNameTest() throws Exception {
        // Preparation
        MGameRating rating = new MGameRating(
                (short)501,
                "e-everyone",
                "Content suitable for all ages.",
                "esrb"
        );

        // Dependency call handlers
        when(gameRatingService.getByName(Mockito.anyString()))
                .thenReturn(rating);

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "e-everyone")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingByNameTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getByName(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating entity with this name."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "e-everyone")
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating entity with this name."));
    }

    @Test
    public void searchValidGameRatingsByEntityTest() throws Exception {
        // Preparation
        List<MGameRating> ratings = List.of(
                new MGameRating(
                        (short)501,
                        "e-everyone",
                        "Content suitable for all ages.",
                        "esrb"
                ),
                new MGameRating(
                        (short)502,
                        "e10+",
                        "Content suitable for ages over 10.",
                        "esrb"
                ),
                new MGameRating(
                        (short)503,
                        "t-teen",
                        "Content suitable for teenagers.",
                        "esrb"
                )
        );

        // Dependency call handlers
        when(gameRatingService.getByGameRatingEntity(anyString(), any(Pageable.class)))
                .thenReturn(ratings);

        // Perform and assert
        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "esrb")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingsByEntityTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getByGameRatingEntity(anyString(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "esrb")
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchAllGameRatingsTest() throws Exception {
        // Preparation
        List<MGameRating> ratings = List.of(
                new MGameRating(
                        (short)501,
                        "e-everyone",
                        "Content suitable for all ages.",
                        "esrb"
                ),
                new MGameRating(
                        (short)502,
                        "e10+",
                        "Content suitable for ages over 10.",
                        "esrb"
                ),
                new MGameRating(
                        (short)503,
                        "t-teen",
                        "Content suitable for teenagers.",
                        "esrb"
                )
        );

        // Dependency call handlers
        when(gameRatingService.getAll(any(Pageable.class)))
                .thenReturn(ratings);

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());
    }

    @Test
    public void searchEmptyAllsTest() throws Exception {
        // Dependency call handlers
        when(gameRatingService.getAll(any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());
    }

}
