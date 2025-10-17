package com.bytestream_api2.games.controller;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.exception.EntityNotFoundException;
import com.bytestream_api2.games.exception.InvalidEntityRelationsException;
import com.bytestream_api2.games.exception.MediaUploadFailedException;
import com.bytestream_api2.games.model.MGame;
import com.bytestream_api2.games.model.MGameCategory;
import com.bytestream_api2.games.model.MGameRating;
import com.bytestream_api2.games.model.MGameRatingDescriptor;
import com.bytestream_api2.games.service.GameService;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --

    // Mock Objects
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name="game_service")
    private GameService gameService;

    // Class Components
    private ObjectMapper mapper;
    private SimpleDateFormat formatter;

    private final String controllerCreateURI = "/games/create";
    private final String controllerUpdateURI = "/games/update";
    private final String controllerDeleteURI = "/games/delete";

    private final String controllerGetByNameURI = "/games/get/byname";
    private final String controllerGetByTitleURI = "/games/get/bytitle";
    private final String controllerGetByCategoriesURI = "/games/get/bycategories";
    private final String controllerGetByRatingsURI = "/games/get/byratings";
    private final String controllerGetByDescriptorsURI = "/games/get/bydescriptors";
    private final String controllerGetAllURI = "/games/get";

    // -- PUBLIC --

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    // CUD
    @Test
    public void createValidGameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                null,
                "metroid-dread",
                "Metroid Dread",
                "Mocking description text field.",
                formatter.parse("2021-10-08")
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(game)
        );

        // Dependency call handlers
        when(gameService.create(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenReturn(game);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(dataPart)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.result.name").value("metroid-dread"))
        .andExpect(jsonPath("$.result.title").value("Metroid Dread"))
        .andExpect(jsonPath("$.result.synopsis").value("Mocking description text field."));
    }

    @Test
    public void createInvalidGameNoNameTest() throws Exception {
        // Preparation
        MockMultipartFile gameNoName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "",
                        "Metroid Dread",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(gameNoName)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'name' is mandatory."));
    }

    @Test
    public void createInvalidGameInvalidNameTest() throws Exception {
        // Preparation
        MockMultipartFile gameInvalidName = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid Dread",
                        "Metroid Dread",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(gameInvalidName)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void createInvalidGameNoTitleTest() throws Exception {
        // Preparation
        MockMultipartFile gameNoTitle = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(gameNoTitle)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'title' is mandatory."));
    }

    @Test
    public void createInvalidGameNoDescriptionTest() throws Exception {
        // Preparation
        MockMultipartFile gameNoDescription = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "Metroid Dread",
                        "",
                        formatter.parse("2021-10-08")
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(gameNoDescription)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'synopsis' is mandatory."));
    }

    @Test
    public void createInvalidGameNoReleaseDateTest() throws Exception {
        // Preparation
        MockMultipartFile gameNoReleaseDate = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "Metroid Dread",
                        "Mocking description text field."
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(gameNoReleaseDate)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Field 'releaseDate' is mandatory."));
    }

    @Test
    public void createInvalidGameNoCategoriesTest() throws Exception {
        // Preparation
        MockMultipartFile game = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "Metroid Dread",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Dependency call handlers
        when(gameService.create(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new InvalidEntityRelationsException("Game must have at least one related category."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(game)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Game must have at least one related category."));
    }

    @Test
    public void createInvalidGameMoreThanOneGameRatingFromARatingEntityTest() throws Exception {
        // Preparation
        MockMultipartFile game = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "Metroid Dread",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Dependency call handlers
        when(gameService.create(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new InvalidEntityRelationsException(
                "Game must be related to only one rating from a rating entity."
        ));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                        .file(game)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Game must be related to only one rating from a rating entity."
        ));
    }

    @Test
    public void createMediaErrorGameTest() throws Exception {
        // Preparation
        MockMultipartFile game = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid-dread",
                        "Metroid Dread",
                        "Mocking description text field.",
                        formatter.parse("2021-10-08")
                ))
        );

        // Dependency call handlers
        when(gameService.create(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new MediaUploadFailedException("Either Cover image or landscape image couldn't be uploaded."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.POST, controllerCreateURI)
                .file(game)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value(
                "Either Cover image or landscape image couldn't be uploaded."
        ));
    }

    @Test
    public void updateValidGameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                (long)1001,
                "metroid-5",
                "Metroid 5: Dread",
                "",
                formatter.parse("2021-10-08")
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(game)
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenReturn(game);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.name").value("metroid-5"))
        .andExpect(jsonPath("$.result.title").value("Metroid 5: Dread"));
    }

    @Test
    public void updateInvalidGameInvalidNameTest() throws Exception {
        // Preparation
        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        null,
                        "metroid 5",
                        "Metroid 5: Dread",
                        "",
                        formatter.parse("2021-10-08")
                ))
        );

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Field 'name' must not contain spaces or uppercases and must be separated with '-'."
        ));
    }

    @Test
    public void updateValidGameCategoriesOfGameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                (long)1001,
                "metroid-5",
                "Metroid 5: Dread",
                "",
                formatter.parse("2021-10-08"),
                List.of(
                        new MGameCategory("metroidvania"),
                        new MGameCategory("shooter")
                )
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(game)
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenReturn(game);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameCategories[0].name").value("metroidvania"))
        .andExpect(jsonPath("$.result.gameCategories[1].name").value("shooter"));
    }

    @Test
    public void updateInvalidGameCategoriesOfGameTest() throws Exception {
        // Preparation
        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        (long)1001,
                        "metroid-5",
                        "Metroid 5: Dread",
                        "",
                        formatter.parse("2021-10-08"),
                        null
                ))
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new InvalidEntityRelationsException("Game must have at least one related category."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Game must have at least one related category."));
    }

    @Test
    public void updateValidGameRatingsOfGameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                (long)1001,
                "metroid-5",
                "Metroid 5: Dread",
                "",
                formatter.parse("2021-10-08"),
                null,
                List.of(
                        new MGameRating("t-teen"),
                        new MGameRating("pegi-12")
                )
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(game)
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenReturn(game);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameRatings[0].name").value("t-teen"))
        .andExpect(jsonPath("$.result.gameRatings[1].name").value("pegi-12"));
    }

    @Test
    public void updateInvalidGameRatingsOfGameTest() throws Exception {
        // Preparation
        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        (long)1001,
                        "metroid-5",
                        "Metroid 5: Dread",
                        "",
                        formatter.parse("2021-10-08"),
                        null,
                        List.of(
                                new MGameRating("pegi-16"),
                                new MGameRating("pegi-12")
                        )
                ))
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new InvalidEntityRelationsException(
                "Game must be related to only one rating from a rating entity."
        ));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
                "Game must be related to only one rating from a rating entity."
        ));
    }

    @Test
    public void updateValidGameRatingDescriptorsOfGameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                (long)1001,
                "metroid-5",
                "Metroid 5: Dread",
                "",
                formatter.parse("2021-10-08"),
                null,
                null,
                List.of(
                        new MGameRatingDescriptor("violence-p"),
                        new MGameRatingDescriptor("mild-cartoon-violence")
                )
        );

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(game)
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenReturn(game);

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(dataPart)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.gameRatingDescriptors[0].name").value("violence-p"))
        .andExpect(jsonPath("$.result.gameRatingDescriptors[1].name")
                .value("mild-cartoon-violence"));
    }

    @Test
    public void updateNonExistentGameTest() throws Exception {
        // Preparation
        MockMultipartFile game = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        (long)1019,
                        "metroid-5",
                        "Metroid 5: Dread",
                        "",
                        formatter.parse("2021-10-08")
                ))
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new EntityNotFoundException("Couldn't find a Game with this ID."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(game)
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game with this ID."));
    }

    @Test
    public void updateMediaErrorGameTest() throws Exception {
        // Preparation
        MockMultipartFile game = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(new MGame(
                        (long)1001,
                        "metroid-5",
                        "Metroid 5: Dread",
                        "",
                        formatter.parse("2021-10-08")
                ))
        );

        // Dependency call handlers
        when(gameService.update(
                any(Game.class),
                nullable(MultipartFile.class),
                nullable(MultipartFile.class)
        )).thenThrow(new MediaUploadFailedException("Either Cover image or landscape image couldn't be uploaded."));

        // Perform and assert
        mockMvc.perform(multipart(HttpMethod.PATCH, controllerUpdateURI)
                .file(game)
        ).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value(
                "Either Cover image or landscape image couldn't be uploaded."
        ));
    }

    @Test
    public void deleteValidGameTest() throws Exception {
        // Dependency call handlers
        doNothing().when(gameService).delete(anyLong());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((long)1001))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("Deleted successfully!"));
    }

    @Test
    public void deleteNonExistentGameTest() throws Exception {
        // Dependency call handlers
        doThrow(new EntityNotFoundException("Couldn't find a Game with this ID."))
                .when(gameService).delete(anyLong());

        // Perform and assert
        mockMvc.perform(delete(controllerDeleteURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("id", String.valueOf((long)1016))
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game with this ID."));
    }

    // Queries
    @Test
    public void searchValidGameByNameTest() throws Exception {
        // Preparation
        MGame game = new MGame(
                (long)1001,
                "metroid-dread",
                "Metroid Dread",
                "Mocking description test field.",
                formatter.parse("2021-10-08")
        );

        // Dependency call handlers
        when(gameService.getByName(Mockito.anyString()))
                .thenReturn(game);

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "metroid-dread")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.name").value("metroid-dread"))
        .andExpect(jsonPath("$.result.title").value("Metroid Dread"))
        .andExpect(jsonPath("$.result.synopsis").value("Mocking description test field."));
    }

    @Test
    public void searchNonExistentGameByNameTest() throws Exception {
        // Dependency call handlers
        when(gameService.getByName(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Couldn't find a Game with this name."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByNameURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "metroid-dread")
        ).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Couldn't find a Game with this name."));
    }

    @Test
    public void searchValidGamesByTitleTest() throws Exception {
        // Preparation
        List<MGame> games = List.of(
            new MGame(
                    (long)1002,
                    "sonic-mania",
                    "Sonic Mania",
                    "Sonic and Tails detect a powerful energy reading on Angel Island,",
                    formatter.parse("2017-08-29")
            ),
            new MGame(
                    (long)1003,
                    "sonic-robo-blast-2",
                    "Sonic Robo Blast 2",
                    "Dr. Eggman took an interest in harnessing the Black Rock's energy.",
                    formatter.parse("1998-02-01")
            )
        );

        // Dependency call handlers
        when(gameService.getByTitle(Mockito.anyString(), any(Pageable.class)))
            .thenReturn(games);

        // Perform and assert
        mockMvc.perform(get(controllerGetByTitleURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", "S")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result[0].name").value("sonic-mania"))
        .andExpect(jsonPath("$.result[1].name").value("sonic-robo-blast-2"));
    }

    @Test
    public void searchNonExistentGamesByTitleTest() throws Exception {
        // Dependency call handlers
        when(gameService.getByTitle(Mockito.anyString(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByTitleURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("title", "L")
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchValidGamesByCategoriesTest() throws Exception {
        // Preparation
        List<MGame> games = List.of(
            new MGame(
                    (long)1020,
                    "signalis",
                    "SIGNALIS",
                    "A dystopian future where humanity has uncovered a dark secret.",
                    formatter.parse("2022-10-27")
            ),
            new MGame(
                    (long)1021,
                    "the-evil-within",
                    "The Evil Within",
                    "Detective Sebastian Castellanos and his partners encounter a mysterious and powerful force.",
                    formatter.parse("2014-10-14")
            )
        );

        // Dependency call handlers
        when(gameService.getByGameCategories(Mockito.anyList(), any(Pageable.class)))
                .thenReturn(games);

        // Perform and assert
        mockMvc.perform(get(controllerGetByCategoriesURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "categories",
                        mapper.writeValueAsString(List.of(
                                "survival-horror",
                                "shooter"
                        ))
                )
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result[0].name").value("signalis"))
        .andExpect(jsonPath("$.result[1].name").value("the-evil-within"));
    }

    @Test
    public void searchNonExistentGamesByCategoriesTest() throws Exception {
        // Dependency call handlers
        when(gameService.getByGameCategories(Mockito.anyList(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByCategoriesURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "categories",
                        mapper.writeValueAsString(List.of(
                                "survival-horror",
                                "shooter"
                        ))
                )
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchValidGamesByRatingsTest() throws Exception {
        // Preparation
        List<MGame> games = List.of(
                new MGame(
                        (long)1022,
                        "mega-man-11",
                        "Mega Man 11",
                        "Mega Man is back! The newest entry in this iconic series blends classic.",
                        formatter.parse("2018-10-02")
                ),
                new MGame(
                        (long)1023,
                        "sonic-x-shadow-generations",
                        "SONIC X SHADOW GENERATIONS",
                        "Shadow the Hedgehog is back with Classic and Modern Sonic.",
                        formatter.parse("2024-10-24")
                )
        );

        // Dependency call handlers
        when(gameService.getByGameRatings(Mockito.anyList(), any(Pageable.class)))
                .thenReturn(games);

        // Perform and assert
        mockMvc.perform(get(controllerGetByRatingsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "ratings",
                        mapper.writeValueAsString(List.of(
                                new MGameRating("e10+"),
                                new MGameRating("pegi-7")
                        ))
                )
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result[0].name").value("mega-man-11"))
        .andExpect(jsonPath("$.result[1].name").value("sonic-x-shadow-generations"));
    }

    @Test
    public void searchNonExistentGamesByRatingsTest() throws Exception {
        // Dependency call handlers
        when(gameService.getByGameRatings(Mockito.anyList(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByRatingsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "ratings",
                        mapper.writeValueAsString(List.of(
                                "e10+",
                                "pegi-7"
                        ))
                )
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchValidGamesByDescriptorsTest() throws Exception {
        // Preparation
        List<MGame> games = List.of(
                new MGame(
                        (long)1025,
                        "killing-floor-2",
                        "Killing Floor 2",
                        "In KILLING FLOOR 2, players descend into continental Europe after it has been overrun by horrific, murderous clones called Zeds.",
                        formatter.parse("2016-11-18")
                ),
                new MGame(
                        (long)1026,
                        "doom-eternal",
                        "DOOM Eternal",
                        "Hell’s armies have invaded Earth. Become the Slayer in an epic single-player campaign to conquer demons.",
                        formatter.parse("2020-03-19")
                )
        );

        // Dependency call handlers
        when(gameService.getByGameRatingDescriptors(Mockito.anyList(), any(Pageable.class)))
                .thenReturn(games);

        // Perform and assert
        mockMvc.perform(get(controllerGetByDescriptorsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "descriptors",
                        mapper.writeValueAsString(List.of(
                                "violence-p",
                                "intense-violence"
                        ))
                )
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result[0].name").value("killing-floor-2"))
        .andExpect(jsonPath("$.result[1].name").value("doom-eternal"));
    }

    @Test
    public void searchNonExistentGamesByDescriptorsTest() throws Exception {
        // Dependency call handlers
        when(gameService.getByGameRatingDescriptors(Mockito.anyList(), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetByDescriptorsURI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(
                        "descriptors",
                        mapper.writeValueAsString(List.of(
                                "violence-p",
                                "intense-violence"
                        ))
                )
        ).andExpect(status().isNoContent());
    }

    @Test
    public void searchAllGamesTest() throws Exception {
        // Preparation
        List<MGame> games = List.of(
                new MGame(
                        (long)1001,
                        "metroid-dread",
                        "Metroid Dread",
                        "Mocking description test field.",
                        formatter.parse("2021-10-08")
                ),
                new MGame(
                        (long)1002,
                        "sonic-mania",
                        "Sonic Mania",
                        "Sonic and Tails detect a powerful energy reading on Angel Island,",
                        formatter.parse("2017-08-29")
                ),
                new MGame(
                        (long)1003,
                        "sonic-robo-blas-2",
                        "Sonic Robo Blast 2",
                        "Dr. Eggman took an interest in harnessing the Black Rock's energy.",
                        formatter.parse("1998-02-01")
                ),
                new MGame(
                        (long)1020,
                        "signalis",
                        "SIGNALIS",
                        "A dystopian future where humanity has uncovered a dark secret.",
                        formatter.parse("2022-10-27")
                ),
                new MGame(
                        (long)1021,
                        "the-evil-within",
                        "The Evil Within",
                        "Detective Sebastian Castellanos and his partners encounter a mysterious and powerful force.",
                        formatter.parse("2014-10-14")
                ),
                new MGame(
                        (long)1022,
                        "mega-man-11",
                        "Mega Man 11",
                        "Mega Man is back! The newest entry in this iconic series blends classic.",
                        formatter.parse("2018-10-02")
                ),
                new MGame(
                        (long)1023,
                        "sonic-x-shadow-generations",
                        "SONIC X SHADOW GENERATIONS",
                        "Shadow the Hedgehog is back with Classic and Modern Sonic.",
                        formatter.parse("2024-10-24")
                ),
                new MGame(
                        (long)1025,
                        "killing-floor-2",
                        "Killing Floor 2",
                        "In KILLING FLOOR 2, players descend into continental Europe after it has been overrun by horrific, murderous clones called Zeds.",
                        formatter.parse("2016-11-18")
                ),
                new MGame(
                        (long)1026,
                        "doom-eternal",
                        "DOOM Eternal",
                        "Hell’s armies have invaded Earth. Become the Slayer in an epic single-player campaign to conquer demons.",
                        formatter.parse("2020-03-19")
                )
        );

        // Dependency call handlers
        when(gameService.getAll(any(Pageable.class)))
                .thenReturn(games);

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("metroid-dread"))
                .andExpect(jsonPath("$.result[1].name").value("sonic-mania"))
                .andExpect(jsonPath("$.result[2].name").value("sonic-robo-blas-2"))
                .andExpect(jsonPath("$.result[3].name").value("signalis"))
                .andExpect(jsonPath("$.result[4].name").value("the-evil-within"))
                .andExpect(jsonPath("$.result[5].name").value("mega-man-11"))
                .andExpect(jsonPath("$.result[6].name").value("sonic-x-shadow-generations"))
                .andExpect(jsonPath("$.result[7].name").value("killing-floor-2"))
                .andExpect(jsonPath("$.result[8].name").value("doom-eternal"));
    }

    @Test
    public void searchEmptyAllGamesTest() throws Exception {
        // Dependency call handlers
        when(gameService.getAll(any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No results for this search."));

        // Perform and assert
        mockMvc.perform(get(controllerGetAllURI))
                .andExpect(status().isNoContent());
    }

}
