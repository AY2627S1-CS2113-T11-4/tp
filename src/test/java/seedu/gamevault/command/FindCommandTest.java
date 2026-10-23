package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Tests for the find command.
 */
class FindCommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Monopoly", 2, 8));
        games.add(new Game("Chess", 2, 2));
    }

    private void executeFind(String keyword, GameList gamesToSearch) {
        new FindCommand(keyword).execute(gamesToSearch, ui, new Storage("unused.txt"));
    }

    @Test
    void execute_partOfTitle_showsMatchWithItsListNumber() {
        executeFind("mono", games);

        String expected = FindCommand.getHeader("mono") + System.lineSeparator()
                + "2. Monopoly (2-8 players)";
        assertTrue(output.toString().contains(expected));
    }

    @Test
    void execute_differentCase_stillMatches() {
        executeFind("CaTaN", games);

        assertTrue(output.toString().contains("1. Catan (3-4 players)"));
    }

    @Test
    void execute_severalMatches_showsAllInListOrder() {
        executeFind("c", games);

        String expected = FindCommand.getHeader("c") + System.lineSeparator()
                + "1. Catan (3-4 players)" + System.lineSeparator()
                + "3. Chess (2 players)";
        String printed = output.toString();
        assertTrue(printed.contains(expected));
        assertFalse(printed.contains("Monopoly"));
    }

    @Test
    void execute_keywordWithSpace_matchesWholePhrase() {
        games.add(new Game("Ticket to Ride", 2, 5));

        executeFind("ticket to", games);

        assertTrue(output.toString().contains("4. Ticket to Ride (2-5 players)"));
    }

    @Test
    void execute_noMatch_showsNoMatchMessage() {
        executeFind("zzz", games);

        String printed = output.toString();
        assertTrue(printed.contains(FindCommand.getNoMatchMessage("zzz")));
        assertFalse(printed.contains(FindCommand.getHeader("zzz")));
    }

    @Test
    void execute_emptyCollection_showsNoMatchMessage() {
        executeFind("mono", new GameList());

        assertTrue(output.toString().contains(FindCommand.getNoMatchMessage("mono")));
    }

    @Test
    void execute_anyKeyword_leavesCollectionUnchanged() {
        executeFind("mono", games);

        assertEquals(3, games.size());
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new FindCommand("mono").isExit());
    }
}
