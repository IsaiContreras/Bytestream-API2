package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingEntity;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.model.MGameRatingEntity;
import com.bytestream_api2.games.service.GameRatingEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRatingEntityController.class)
public class GameRatingEntityControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_rating_entity_service")
    private GameRatingEntityService gameRatingEntityService;

    // Class Components
    private ObjectMapper mapper;

    private final String controllerCreateURI = "/rating_entity/create";
    private final String controllerUpdateURI = "/rating_entity/update";
    private final String controllerDeleteURI = "/rating_entity/delete";

    private final String controllerGetByNameURI = "/rating_entity/get/byname";
    private final String controllerGetAllURI = "/rating_entity/get";

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    @BeforeEach
    public void setup() {
        this.mapper = new ObjectMapper();
    }

    // CUD
    @Test
    public void createValidGameRatingEntityTest() throws Exception {
        MGameRatingEntity ratingEntity = new MGameRatingEntity(
                "mock",
                "Mocking Entity",
                "Location",
                "A mock rating entity"
        );

        Mockito.when(
                gameRatingEntityService.create(
                        Mockito.any(GameRatingEntity.class),
                        Mockito.nullable(MultipartFile.class)
                )
        ).thenReturn(ratingEntity);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(ratingEntity)
        );

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(dataPart)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.results.name").value("mock"))
        .andExpect(jsonPath("$.results.longName").value("Mocking Entity"))
        .andExpect(jsonPath("$.results.location").value("Location"))
        .andExpect(jsonPath("$.results.description").value("A mock rating entity"));
    }

    @Test
    public void createInvalidGameRatingEntityTest() throws Exception {
        MockMultipartFile ratingEntityNoName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        "",
                        "Mocking Entity",
                        "Location",
                        "A mock rating entity"
                ))
        );
        MockMultipartFile ratingEntityNoLongName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        "mock",
                        "",
                        "Location",
                        "A mock rating entity"
                ))
        );
        MockMultipartFile ratingEntityNoLocation = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        "mock",
                        "Mocking Entity",
                        "",
                        "A mock rating entity"
                ))
        );
        MockMultipartFile ratingEntityNoDescription = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        "mock",
                        "Mocking Entity",
                        "Location",
                        ""
                ))
        );

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingEntityNoName)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory."));

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingEntityNoLongName)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'longName' is mandatory."));

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingEntityNoLocation)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'location' is mandatory."));

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingEntityNoDescription)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'description' is mandatory."));
    }

    @Test
    public void createMediaErrorGameRatingEntityTest() throws Exception {
        Mockito.when(
                gameRatingEntityService.create(
                        Mockito.any(GameRatingEntity.class),
                        Mockito.nullable(MultipartFile.class)
                )
        ).thenThrow(new MediaUploadFailedException("Couldn't upload Logo image file."));

        MockMultipartFile ratingEntity = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        "mock",
                        "Mocking Entity",
                        "Location",
                        "A mock rating entity"
                ))
        );

        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(ratingEntity)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("Couldn't upload Logo image file."));
    }

    @Test
    public void updateValidGameRatingEntityTest() throws Exception {
        MGameRatingEntity ratingEntity = new MGameRatingEntity(
                (short)501,
                "cero",
                "Computer Entertainment Rating Organization",
                "Japan",
                "A Japanese entertainment rating organization based in Tokyo."
        );

        Mockito.when(
                gameRatingEntityService.update(
                        Mockito.any(GameRatingEntity.class),
                        Mockito.nullable(MultipartFile.class)
                )
        ).thenReturn(ratingEntity);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(ratingEntity)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.name").value("cero"))
        .andExpect(jsonPath("$.result.longName").value(
                "Computer Entertainment Rating Organization"
        ))
        .andExpect(jsonPath("$.result.location").value("Japan"))
        .andExpect(jsonPath("$.result.description").value(
                "A Japanese entertainment rating organization based in Tokyo."
        ));
    }

    @Test
    public void updateNonExistentGameRatingEntityTest() throws Exception {
        Mockito.when(
                gameRatingEntityService.update(
                        Mockito.any(GameRatingEntity.class),
                        Mockito.nullable(MultipartFile.class))
        ).thenThrow(new EntityNotFoundException("Couldn't find a Rating entity with this ID."));

        MockMultipartFile ratingEntity = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        (short)101,
                        "cero",
                        "Computer Entertainment Rating Organization",
                        "Japan",
                        "A Japanese entertainment rating organization based in Tokyo."
                ))
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(ratingEntity)
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating entity with this ID."));
    }

    @Test
    public void updateMediaErrorGameRatingEntityTest() throws Exception{
        Mockito.when(
                gameRatingEntityService.update(
                        Mockito.any(GameRatingEntity.class),
                        Mockito.nullable(MultipartFile.class)
                )
        ).thenThrow(new MediaUploadFailedException("Couldn't upload Logo image file."));

        MockMultipartFile ratingEntity = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGameRatingEntity(
                        (short)501,
                        "cero",
                        "Computer Entertainment Rating Organization",
                        "Japan",
                        "A Japanese entertainment rating organization based in Tokyo."
                ))
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(ratingEntity)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("Couldn't upload Logo image file."));
    }

    @Test
    public void deleteValidGameRatingEntityTest() throws Exception{
        Mockito.doNothing().when(gameRatingEntityService).delete(Mockito.anyShort());

        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentGameRatingEntityTest() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Couldn't find a Rating entity with this ID."))
                .when(gameRatingEntityService).delete(Mockito.anyShort());

        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating entity with this ID."));
    }

    // Queries
    @Test
    public void searchValidGameRatingEntityByNameTest() throws Exception {
        MGameRatingEntity ratingEntity = new MGameRatingEntity(
                (short)501,
                "cero",
                "Computer Entertainment Rating Organization",
                "Japan",
                "A Japanese entertainment rating organization based in Tokyo."
        );

        Mockito.when(gameRatingEntityService.getByName(Mockito.anyString()))
                .thenReturn(ratingEntity);

        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingEntityByNameTest() throws Exception{
        Mockito.when(gameRatingEntityService.getByName(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating entity with this name."));

        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating entity with this name."));
    }

    @Test
    public void searchAllGameRatingEntitiesTest() throws Exception{
        List<MGameRatingEntity> ratingEntities = List.of(
                new MGameRatingEntity(
                        (short)501,
                        "cero",
                        "Computer Entertainment Rating Organization",
                        "Japan",
                        "A Japanese entertainment rating organization based in Tokyo."
                ),
                new MGameRatingEntity(
                        (short)502,
                        "esrb",
                        "Entertainment Software Rating Board",
                        "America",
                        "Is a self-regulatory organization that assigns age and content ratings to consumer video games in Canada, the United States, and Mexico."
                ),
                new MGameRatingEntity(
                        (short)503,
                        "pegi",
                        "Pan European Game Information",
                        "Europe",
                        "Is a European video game content rating system established to help European consumers make informed decisions when buying video games or apps"
                )
        );

        Mockito.when(gameRatingEntityService.getAll(Mockito.any(Pageable.class)))
                .thenReturn(ratingEntities);

        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());
    }

    @Test
    public void searchEmptyAllGameRatingEntitiesTest() throws Exception{
        Mockito.when(gameRatingEntityService.getAll(Mockito.any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());
    }

}
