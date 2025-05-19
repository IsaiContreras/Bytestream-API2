package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import com.bytestream_api2.games.service.GameRatingDescriptorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        this.mapper = new ObjectMapper();
    }

    // CUD
    @Test
    public void createValidGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                "cero"
        );

        Mockito.when(gameRatingDescriptorService.create(Mockito.any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.result.name").value("mock-descriptor"))
        .andExpect(jsonPath("$.result.description").value("Descriptor content mock."))
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("cero"));
    }

    @Test
    public void createInvalidGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptorNoName = new MGameRatingDescriptor(
                "",
                "Descriptor content mock.",
                "cero"
        );
        MGameRatingDescriptor gameRatingDescriptorNoDescription = new MGameRatingDescriptor(
                "mock-descriptor",
                "",
                "cero"
        );
        MGameRatingDescriptor gameRatingDescriptorNoEntity = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                ""
        );
        MGameRatingDescriptor gameRatingDescriptorInvalidEntity = new MGameRatingDescriptor(
                "mock-descriptor",
                "Descriptor content mock.",
                "ce"
        );
        MGameRatingDescriptor gameRatingDescriptorInvalidName = new MGameRatingDescriptor(
                "mock Descriptor",
                "Descriptor content mock.",
                ""
        );

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoName))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory."));

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoDescription))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'description' is mandatory."));

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorInvalidName))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));

        Mockito.when(gameRatingDescriptorService.create(Mockito.any(GameRatingDescriptor.class)))
                .thenThrow(new DataAccessException("") {});

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorNoEntity))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptorInvalidEntity))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateValidGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        Mockito.when(gameRatingDescriptorService.update(Mockito.any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        System.out.println(mapper.writeValueAsString(gameRatingDescriptor));

        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isOk());
    }

    @Test
    public void updateInvalidGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptorInvalidName = new MGameRatingDescriptor(
                (short)501,
                "use of Alcohol",
                ""
        );

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
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "",
                "",
                "cero"
        );

        Mockito.when(gameRatingDescriptorService.update(Mockito.any(GameRatingDescriptor.class)))
                .thenReturn(gameRatingDescriptor);

        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameRatingEntity.name").value("cero"));
    }

    @Test
    public void updateNonExistentGameRatingEntityOfGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "",
                "",
                "ce"
        );

        Mockito.when(gameRatingDescriptorService.update(Mockito.any(GameRatingDescriptor.class)))
                .thenThrow(new DataAccessException("") {});

        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateNonExistentGameRatingDescriptorTest() throws Exception {
        MGameRatingDescriptor gameRatingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        Mockito.when(gameRatingDescriptorService.update(Mockito.any(GameRatingDescriptor.class)))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this ID."));

        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(gameRatingDescriptor))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Rating descriptor with this ID."));
    }

    @Test
    public void deleteValidGameRatingDescriptorTest() throws Exception {
        Mockito.doNothing().when(gameRatingDescriptorService).delete(Mockito.anyShort());

        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentGameRatingDescriptorTest() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this ID."))
                .when(gameRatingDescriptorService).delete(Mockito.anyShort());

        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)501))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.").value("Couldn't find a Rating descriptor with this ID."));
    }

    // Queries
    @Test
    public void searchValidGameRatingDescriptorByNameTest() throws Exception {
        MGameRatingDescriptor ratingDescriptor = new MGameRatingDescriptor(
                (short)501,
                "use-of-alcohol",
                "Depictions of consumption of alcohol."
        );

        Mockito.when(gameRatingDescriptorService.getByName(Mockito.anyString()))
                .thenReturn(ratingDescriptor);

        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "use-of-alcohol")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingDescriptorByNameTest() throws Exception {
        Mockito.when(gameRatingDescriptorService.getByName(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Rating descriptor with this name."));

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

        Mockito.when(
                gameRatingDescriptorService.getByRatingEntity(Mockito.anyString(), Mockito.any(Pageable.class))
        ).thenReturn(ratingDescriptors);

        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentGameRatingDescriptorsByEntityTest() throws Exception {
        Mockito.when(
                gameRatingDescriptorService.getByRatingEntity(Mockito.anyString(), Mockito.any(Pageable.class))
        ).thenThrow(new EntityNotFoundException("No results for this search."));

        mockMvc.perform(get(controllerGetByEntityURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "cero")
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchAllGameRatingDescriptorsTest() throws Exception {
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

        Mockito.when(
                gameRatingDescriptorService.getAll(Mockito.any(Pageable.class))
        ).thenReturn(ratingDescriptors);

        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());
    }

    @Test
    public void searchEmptyAllGameRatingDescriptorsTest() throws Exception {
        Mockito.when(
                gameRatingDescriptorService.getAll(Mockito.any(Pageable.class))
        ).thenThrow(new EntityNotFoundException("No results for this search."));

        mockMvc.perform(get(controllerGetAllURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isNoContent());
    }

}
