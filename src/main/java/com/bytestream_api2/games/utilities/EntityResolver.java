package com.bytestream_api2.games.utilities;

import com.bytestream_api2.games.entity.Game;
import com.bytestream_api2.games.entity.GameCategory;
import com.bytestream_api2.games.entity.GameRating;
import com.bytestream_api2.games.entity.GameRatingDescriptor;
import com.bytestream_api2.games.repository.GameCategoryRepository;
import com.bytestream_api2.games.repository.GameRatingDescriptorRepository;
import com.bytestream_api2.games.repository.GameRatingRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("entity_resolver")
public class EntityResolver {

    public static Game resolveGameEntities(
            @NotNull GameCategoryRepository gameCategoryRepository,
            @NotNull GameRatingRepository gameRatingRepository,
            @NotNull GameRatingDescriptorRepository gameRatingDescriptorRepository,
            @NotNull Game game
    ) {
        List<GameCategory> retrievedCategos = game.getGameCategories() != null ?
                game.getGameCategories().stream()
                        .map(item -> gameCategoryRepository.findByName(item.getName()))
                        .toList()
                : null;
        List<GameRating> retrievedRatings = game.getGameRatings() != null ?
                game.getGameRatings().stream()
                        .map(item -> gameRatingRepository.findByName(item.getName()))
                        .toList()
                : null;
        List<GameRatingDescriptor> retrievedRatingDescriptors = game.getGameRatingDescriptors() != null ?
                game.getGameRatingDescriptors().stream()
                        .map(item -> gameRatingDescriptorRepository.findByName(item.getName()))
                        .toList()
                : null;

        game.setGameCategories(retrievedCategos);
        game.setGameRatings(retrievedRatings);
        game.setGameRatingDescriptors(retrievedRatingDescriptors);

        return game;
    }

}
