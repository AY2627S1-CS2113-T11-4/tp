package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void fitsPlayerCount_countWithinRange_returnsTrue() {
        Game game = new Game("Catan", 3, 5);
        // The minimum and maximum themselves are included in the range.
        assertTrue(game.fitsPlayerCount(3));
        assertTrue(game.fitsPlayerCount(4));
        assertTrue(game.fitsPlayerCount(5));
    }

    @Test
    void fitsPlayerCount_countOutsideRange_returnsFalse() {
        Game game = new Game("Catan", 3, 5);
        assertFalse(game.fitsPlayerCount(2));
        assertFalse(game.fitsPlayerCount(6));
    }

    @Test
    void fitsPlayerCount_sameMinAndMax_fitsOnlyThatCount() {
        Game game = new Game("Chess", 2, 2);
        assertTrue(game.fitsPlayerCount(2));
        assertFalse(game.fitsPlayerCount(1));
        assertFalse(game.fitsPlayerCount(3));
    }
}
