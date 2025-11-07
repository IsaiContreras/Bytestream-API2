package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.exception.services.EntityNotFoundException;
import com.bytestream_api2.games.model.MGameCategory;
import com.bytestream_api2.games.service.GameCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameCategoryController.class)
public class GameCategoryControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_category_service")
    private GameCategoryService gameCategoryService;

    // Class Components
    private ObjectMapper mapper;

    private final String controllerCreateURI = "/categories/create";
    private final String controllerUpdateURI = "/categories/update";
    private final String controllerDeleteURI = "/categories/delete";

    private final String controllerGetByNameURI = "/categories/get/byname";
    private final String controllerGetByNameContainsURI = "/categories/get/bynamematch";
    private final String controllerGetAllURI = "/categories/get";

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
    public void createValidCategoryTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory("mock-category");

        // Dependency call handlers
        when(gameCategoryService.create(any(GameCategory.class)))
                .thenReturn(category);

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.result.name").value("mock-category"));
    }

    @Test
    public void createInvalidCategoryTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory("mock Category");

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category))
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void createEmptyNameCategoryTest() throws Exception {
        // Preparation
        MGameCategory categoryEmptyString = new MGameCategory("");
        MGameCategory categoryNullString = new MGameCategory();

        // Perform and assert
        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoryEmptyString))
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory"));

        mockMvc.perform(post(controllerCreateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoryNullString))
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory"));
    }

    @Test
    public void updateValidCategoryTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory((short)501, "simulated-mock");

        // Dependency call handlers
        when(gameCategoryService.update(any(GameCategory.class)))
                .thenReturn(category);

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category))
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result.name").value("simulated-mock"));
    }

    @Test
    public void updateInvalidCategoryTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory(
                (short)500,
                "simulated Mock"
        );

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category))
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void updateNonExistentCategoryTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory((short)101, "simulated-mock");

        // Dependency call handlers
        when(gameCategoryService.update(any(GameCategory.class)))
                .thenThrow(new EntityNotFoundException("Couldn't find a Game category with this ID."));

        // Perform and assert
        mockMvc.perform(patch(controllerUpdateURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category))
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game category with this ID."));
    }

    @Test
    public void deleteValidCategoryTest() throws Exception {
        // Dependency call handlers
        doNothing().when(gameCategoryService)
                .delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)500))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentCategoryTest() throws Exception {
        // Dependency call handlers
        doThrow(new EntityNotFoundException("Couldn't find a Game category with this ID."))
                .when(gameCategoryService).delete(anyShort());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((short)101))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game category with this ID."));
    }

    // Queries
    @Test
    public void searchValidCategoryByNameTest() throws Exception {
        // Preparation
        MGameCategory category = new MGameCategory((short) 501, "mock-category");

        // Dependency call handlers
        when(gameCategoryService.getByName(Mockito.anyString()))
                .thenReturn(category);

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "mock-category")
        ).andDo(handler -> System.out.println(content()))
        .andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentCategoryByNameTest() throws Exception {
        // Dependency call handlers
        when(gameCategoryService.getByName(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Game category with this name."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "mock-category")
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game category with this name."));
    }

    @Test
    public void searchValidCategoriesByNameContainsTest() throws Exception {
        // Preparation
        List<MGameCategory> categories = List.of(
                new MGameCategory((short)501, "mock-categories"),
                new MGameCategory((short)502, "metroidvania")
        );

        // Dependency call handlers
        when(gameCategoryService.getByNameContains(anyString(), any(Pageable.class)))
                .thenReturn(categories);

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameContainsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "m")
        ).andExpect(status().isOk());
    }

    @Test
    public void searchNonExistentCategoriesByNameContainsTest() throws Exception {
        // Dependency call handlers
        when(gameCategoryService.getByNameContains(Mockito.anyString(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameContainsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "m")
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchAllCategoriesTest() throws Exception {
        // Preparation
        List<MGameCategory> categories = List.of(
                new MGameCategory((short)501, "mock-categories"),
                new MGameCategory((short)502, "metroidvania")
        );

        // Dependency call handlers
        when(gameCategoryService.getAll(any(Pageable.class)))
                .thenReturn(categories);

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI))
                .andExpect(status().isOk());
    }

    @Test
    public void searchEmptyAllCategoriesTest() throws Exception {
        // Dependency call handlers
        when(gameCategoryService.getAll(any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI))
                .andExpect(status().isNoContent());
    }

}