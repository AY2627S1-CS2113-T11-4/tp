package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

//@@author yy261
class BorrowerTest {

    @Test
    void normalise_extraSpaces_trimmedAndCollapsed() {
        assertEquals("Mary Tan", Borrower.normalise("  Mary    Tan "));
        assertEquals("Alice", Borrower.normalise("\tAlice\t"));
    }

    @Test
    void isValid_ordinaryNames_returnsTrue() {
        assertTrue(Borrower.isValid("Alice"));
        assertTrue(Borrower.isValid("Mary Tan"));
        assertTrue(Borrower.isValid("O'Brien-Lee, Jr."));
        assertTrue(Borrower.isValid("a".repeat(Borrower.MAX_LENGTH)));
    }

    @Test
    void isValid_emptyTooLongOrContainingSeparator_returnsFalse() {
        assertFalse(Borrower.isValid(""));
        assertFalse(Borrower.isValid(null));
        assertFalse(Borrower.isValid("a".repeat(Borrower.MAX_LENGTH + 1)));
        assertFalse(Borrower.isValid("Alice | Ben"));
    }

    @Test
    void lendToAndMarkReturned_changeWhetherGameIsLent() {
        Game game = new Game("Hive", 2, 2);
        assertFalse(game.isLent());

        game.lendTo("Alice");
        assertTrue(game.isLent());
        assertEquals("Alice", game.getBorrower());
        assertEquals("Hive (2 players) (lent to Alice)", game.toString());

        game.markReturned();
        assertFalse(game.isLent());
        assertEquals("Hive (2 players)", game.toString());
    }

    @Test
    void toFileString_lentGame_keepsEachFieldInItsPosition() {
        Game untimed = new Game("Hive", 2, 2);
        untimed.lendTo("Alice");
        assertEquals("Hive | 2 | 2 |  |  | lent=Alice", untimed.toFileString());

        Game timedAndTagged = new Game("Catan", 3, 4, 60);
        timedAndTagged.addTag("strategy");
        timedAndTagged.lendTo("Ben");
        assertEquals("Catan | 3 | 4 | 60 | strategy | lent=Ben", timedAndTagged.toFileString());
    }

    @Test
    void toFileString_notLent_unchangedFromBeforeLendingExisted() {
        // Files written before lending was added must not change when they are saved again.
        assertEquals("Chess | 2 | 2", new Game("Chess", 2, 2).toFileString());
        assertEquals("Catan | 3 | 4 | 60", new Game("Catan", 3, 4, 60).toFileString());
        Game tagged = new Game("Codenames", 4, 8);
        tagged.addTag("party");
        assertEquals("Codenames | 4 | 8 |  | party", tagged.toFileString());
    }
}
