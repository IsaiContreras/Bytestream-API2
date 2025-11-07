package com.bytestream_api2.games.component;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DBTableAutoincrementFix {

    // -- [[ ATTRIBUTES ]] --

    // -- PRIVATE --
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -- PUBLIC --
    // Class Components
    private static final Log logger = LogFactory.getLog(DBTableAutoincrementFix.class);

    // -- [[ METHODS ]] --

    // -- PRIVATE --

    // -- PUBLIC --
    @PostConstruct
    public void setAutoIncrement() {
        try {
            Integer countRatingEntities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game_rating_entities;", Integer.class);
            Integer countRatingDescriptors = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game_rating_descriptors;", Integer.class);
            Integer countRatings = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game_ratings;", Integer.class);
            Integer countCategories = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game_categories;", Integer.class);
            Integer countGames = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM games;", Integer.class);

           if (countRatingEntities != null && countRatingEntities == 0) {
               jdbcTemplate.execute("ALTER TABLE game_rating_entities AUTO_INCREMENT = 51;");
               logger.info("AUTO_INCREMENT for table game_rating_entities set to 51.");
           } else logger.info("AUTO_INCREMENT already set on table game_rating_entities.");
           if (countRatingDescriptors != null && countRatingDescriptors == 0) {
               jdbcTemplate.execute("ALTER TABLE game_rating_descriptors AUTO_INCREMENT = 101;");
               logger.info("AUTO_INCREMENT for table game_rating_descriptors set to 101.");
           } else logger.info("AUTO_INCREMENT already set on table game_rating_descriptors.");
           if (countRatings != null && countRatings == 0) {
               jdbcTemplate.execute("ALTER TABLE game_ratings AUTO_INCREMENT = 101;");
               logger.info("AUTO_INCREMENT for table game_ratings set to 101.");
           } else logger.info("AUTO_INCREMENT already set on table game_ratings.");
           if (countCategories != null && countCategories == 0) {
               jdbcTemplate.execute("ALTER TABLE game_categories AUTO_INCREMENT = 501;");
               logger.info("AUTO_INCREMENT for table game_categories set to 501.");
           } else logger.info("AUTO_INCREMENT already set on table game_categories.");
           if (countGames != null && countGames == 0) {
               jdbcTemplate.execute("ALTER TABLE games AUTO_INCREMENT = 1001;");
               logger.info("AUTO_INCREMENT for table games set to 1001.");
           } else logger.info("AUTO_INCREMENT already set on table games.");

        } catch(Exception e) {
            logger.error("Failed to catch row count in a table.", e);
        }
    }

}
