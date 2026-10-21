package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void fitsTime_playTimeWithinLimit_returnsTrue() {
        Game game = new Game("Catan", 3, 4, 60);
        assertTrue(game.fitsTime(60));
        assertTrue(game.fitsTime(90));
    }

    @Test
    void fitsTime_playTimeOverLimit_returnsFalse() {
        assertFalse(new Game("Catan", 3, 4, 60).fitsTime(59));
    }

    @Test
    void fitsTime_noPlayTimeRecorded_returnsFalse() {
        Game game = new Game("Catan", 3, 4);
        assertFalse(game.hasPlayTime());
        assertFalse(game.fitsTime(1000));
    }

    @Test
    void toString_withAndWithoutPlayTime_showsTimeOnlyWhenRecorded() {
        assertEquals("Catan (3-4 players, 60 min)", new Game("Catan", 3, 4, 60).toString());
        assertEquals("Chess (2 players)", new Game("Chess", 2, 2).toString());
    }

    @Test
    void toFileString_withAndWithoutPlayTime_addsTimeFieldOnlyWhenRecorded() {
        assertEquals("Catan | 3 | 4 | 60", new Game("Catan", 3, 4, 60).toFileString());
        assertEquals("Chess | 2 | 2", new Game("Chess", 2, 2).toFileString());
    }
}
