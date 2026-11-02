package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

//@@author yy261
/**
 * Tests for {@link Difficulty} and how a {@link Game} uses it.
 */
class DifficultyTest {

    @Test
    void fromKeyword_anyCaseAndSpaces_returnsLevel() {
        assertEquals(Optional.of(Difficulty.EASY), Difficulty.fromKeyword("easy"));
        assertEquals(Optional.of(Difficulty.MEDIUM), Difficulty.fromKeyword("  Medium "));
        assertEquals(Optional.of(Difficulty.HARD), Difficulty.fromKeyword("HARD"));
    }

    @Test
    void fromKeyword_unknownOrEmpty_returnsEmpty() {
        assertEquals(Optional.empty(), Difficulty.fromKeyword("impossible"));
        assertEquals(Optional.empty(), Difficulty.fromKeyword(""));
    }

    @Test
    void describe_returnsLevelInWords() {
        assertEquals("easy difficulty", Difficulty.EASY.describe());
    }

    @Test
    void keywordsInWords_listsEveryLevel() {
        for (Difficulty difficulty : Difficulty.values()) {
            assertTrue(Difficulty.KEYWORDS_IN_WORDS.contains(difficulty.getKeyword()));
        }
    }

    @Test
    void setDifficulty_replacesEarlierLevel() {
        Game game = new Game("Catan", 3, 4);
        assertFalse(game.hasDifficulty());

        game.setDifficulty(Difficulty.EASY);
        game.setDifficulty(Difficulty.HARD);

        assertTrue(game.hasDifficulty());
        assertEquals(Difficulty.HARD, game.getDifficulty());
    }

    @Test
    void fitsDifficulty_onlyTheRecordedLevelFits() {
        Game game = new Game("Catan", 3, 4);
        assertFalse(game.fitsDifficulty(Difficulty.EASY));

        game.setDifficulty(Difficulty.MEDIUM);

        assertTrue(game.fitsDifficulty(Difficulty.MEDIUM));
        assertFalse(game.fitsDifficulty(Difficulty.EASY));
    }

    @Test
    void toString_withDifficulty_showsItInsideTheBrackets() {
        Game timed = new Game("Catan", 3, 4, 60);
        timed.setDifficulty(Difficulty.MEDIUM);
        assertEquals("Catan (3-4 players, 60 min, medium)", timed.toString());

        Game untimed = new Game("Chess", 2, 2);
        untimed.setDifficulty(Difficulty.HARD);
        untimed.addTag("strategy");
        assertEquals("Chess (2 players, hard) [strategy]", untimed.toString());
    }

    @Test
    void toFileString_withDifficulty_usesNamedField() {
        Game game = new Game("Catan", 3, 4, 60);
        game.setDifficulty(Difficulty.EASY);
        assertEquals("Catan | 3 | 4 | 60 |  | difficulty=easy", game.toFileString());
    }

    @Test
    void toFileString_noDifficulty_unchanged() {
        assertEquals("Catan | 3 | 4 | 60", new Game("Catan", 3, 4, 60).toFileString());
    }
}
