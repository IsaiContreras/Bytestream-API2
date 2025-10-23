package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import com.bytestream_api2.games.service.GameRatingDescriptorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameRatingDescriptorController.class)
public class GameRatingDescriptorControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_rating_descriptor_service")
    private GameRatingDescriptorService gameRatingDescriptorService;

    // Class Components
    private ObjectMapper mapper;

    private final String controllerCreateURI = "/rating_descriptor/create";
    private final String controllerUpdateURI = "/rating_descriptor/update";
    private final String controllerDeleteURI = "/rating_descriptor/delete";

    private final String controllerGetByNameURI = "/rating_descriptor/get/byname";
    private final String controllerGetByEntityURI = "/rating_descriptor/get/byentity";
    private final String controllerGetAllURI = "/rating_descriptor/get";

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
    public void createValidGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                "cero"
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.create(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.result.name").value("mock-descriptor"))
        .andExpect(jsonPath("$.result.description").value("Descriptor content mock."))
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("cero"));
    }

    @Test
    public void createInvalidGameRatingDescriptorNoNameTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorNoName = new MGameRatingDescriptor(
                "",
                "Descriptor content mock.",
                "cero"
        );

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoName))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory."));
    }

    @Test
    public void createInvalidGameRatingDescriptorInvalidNameTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorInvalidName = new MGameRatingDescriptor(
                "mock Descriptor",
                "Descriptor content mock.",
                ""
        );

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameRatingDescriptorInvalidName))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
                ));
    }

    @Test
    public void createInvalidGameRatingDescriptorNoDescriptionTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorNoDescription = new MGameRatingDescriptor(
                "mock-descriptor",
                "",
                "cero"
        );

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoDescription))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'description' is mandatory."));
    }

    @Test
    public void createInvalidGameRatingDescriptorNoEntityTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorNoEntity = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                ""
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.create(any(GameRatingDescriptor.class)))
                .thenThrow(new DataAccessException("") {});

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoEntity))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidGameRatingDescriptorInvalidEntityTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorInvalidEntity = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                "ce"
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.create(any(GameRatingDescriptor.class)))
                .thenThrow(new DataAccessException("") {});

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorInvalidEntity))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateValidGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.update(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isOk());
    }

    @Test
    public void updateInvalidGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptorInvalidName = new MGameRatingDescriptor(
                (short)501,
                "use of Alcohol",
                ""
        );

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorInvalidName))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void updateValidGameRatingEntityOfGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "",
                "",
                "cero"
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.update(any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("cero"));
    }

    @Test
    public void updateNonExistentGameRatingEntityOfGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "",
                "",
                "ce"
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.update(any(GameRatingDescriptor.class)))
                .thenThrow(new DataAccessException("") {});

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateNonExistentGameRatingDescriptorTest() throws Exception {
        // Preparation
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.update(any(GameRatingDescriptor.class)))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this ID."));

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating descriptor with this ID."));
    }

    @Test
    public void deleteValidGameRatingDescriptorTest() throws Exception {
        // Dependency call handlers
        doNothing().when(gameRatingDescriptorService).delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentGameRatingDescriptorTest() throws Exception {
        // Dependency call handlers
        doThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this ID."))
                .when(gameRatingDescriptorService).delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating descriptor with this ID."));
    }

    // Queries
    @Test
    public void searchValidGameRatingDescriptorByNameTest() throws Exception {
        // Preparation
        MGameRatingDescriptor ratingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.getByName(anyString()))
                .thenReturn(ratingDescriptor);

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "use-of-alcohol")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingDescriptorByNameTest() throws Exception {
        // Dependency call handlers
        when(gameRatingDescriptorService.getByName(anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this name."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "use-of-alcohol")
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value(
                "Couldn't find a Rating descriptor with this name."
        ));
    }

    @Test
    public void searchValidGameRatingDescriptorsByEntityTest() throws Exception {
        // Preparation
        List<MGameRatingDescriptor> ratingDescriptors = List.of(
                new MGameRatingDescriptor(
                        (short)501,
                        "use-of-alcohol",
                        "Depictions of consumption of alcohol."
                ),
                new MGameRatingDescriptor(
                        (short)501,
                        "use-of-tobbaco",
                        "Depictions of consumption of tobbaco."
                )
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.getByRatingEntity(anyString(), any(Pageable.class))
        ).thenReturn(ratingDescriptors);

        // Perform and assert
        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingDescriptorsByEntityTest() throws Exception {
        // Dependency call handlers
        when(gameRatingDescriptorService.getByRatingEntity(anyString(), any(Pageable.class)))
            .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchAllGameRatingDescriptorsTest() throws Exception {
        // Preparation
        List<MGameRatingDescriptor> ratingDescriptors = List.of(
                new MGameRatingDescriptor(
                        (short)501,
                        "use-of-alcohol",
                        "Depictions of consumption of alcohol."
                ),
                new MGameRatingDescriptor(
                        (short)501,
                        "use-of-tobbaco",
                        "Depictions of consumption of tobbaco."
                )
        );

        // Dependency call handlers
        when(gameRatingDescriptorService.getAll(any(Pageable.class)))
                .thenReturn(ratingDescriptors);

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());
    }

    @Test
    public void searchEmptyAllGameRatingDescriptorsTest() throws Exception {
        // Dependency call handlers
        when(gameRatingDescriptorService.getAll(any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());
    }

}
