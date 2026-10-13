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
}
