package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameListTest {
    private GameList games;

    @BeforeEach
    void setUp() {
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.add(new Game("Codenames", 2, 8));
    }

    @Test
    void filterByPlayers_someGamesFit_returnsMatchesInOriginalOrder() {
        ArrayList<Game> matches = games.filterByPlayers(4);
        assertEquals(2, matches.size());
        assertEquals("Catan", matches.get(0).getTitle());
        assertEquals("Codenames", matches.get(1).getTitle());
    }

    @Test
    void filterByPlayers_noGamesFit_returnsEmptyList() {
        assertTrue(games.filterByPlayers(9).isEmpty());
    }

    @Test
    void filterByPlayers_anyCount_leavesCollectionUnchanged() {
        games.filterByPlayers(4);
        assertEquals(3, games.size());
    }

    @Test
    void delete_middleGame_returnsItAndShiftsLaterGamesUp() {
        Game deleted = games.delete(1);

        assertEquals("Chess", deleted.getTitle());
        assertEquals(2, games.size());
        assertEquals("Catan", games.getGames().get(0).getTitle());
        assertEquals("Codenames", games.getGames().get(1).getTitle());
    }

    @Test
    void delete_firstAndLastGames_removesThem() {
        assertEquals("Codenames", games.delete(2).getTitle());
        assertEquals("Catan", games.delete(0).getTitle());

        assertEquals(1, games.size());
        assertEquals("Chess", games.getGames().get(0).getTitle());
    }

    @Test
    void filterByPlayersAndTime_mixOfTimes_returnsOnlyTimedGamesWithinLimit() {
        GameList timedGames = new GameList();
        timedGames.add(new Game("Catan", 3, 4, 60));
        timedGames.add(new Game("Love Letter", 2, 4, 20));
        timedGames.add(new Game("Codenames", 2, 8));
        timedGames.add(new Game("Werewolf", 8, 18, 30));

        ArrayList<Game> matches = timedGames.filterByPlayersAndTime(4, 60);
        assertEquals(2, matches.size());
        assertEquals("Catan", matches.get(0).getTitle());
        assertEquals("Love Letter", matches.get(1).getTitle());

        assertEquals(1, timedGames.filterByPlayersAndTime(4, 59).size());
        assertTrue(timedGames.filterByPlayersAndTime(4, 10).isEmpty());
    }
}
