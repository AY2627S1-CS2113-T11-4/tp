package seedu.gamevault.command;

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

//@@author yy261
/**
 * Tests for the lentout command.
 */
class ListLentCommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;
    private Storage storage;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        storage = new Storage("unused.txt");
        games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));
    }

    @Test
    void execute_someGamesLent_listsThemWithTheirListNumbers() {
        games.get(0).lendTo("Alice");
        games.get(2).lendTo("Mary Tan");

        new ListLentCommand().execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("2 game(s) are lent out:"));
        // Numbers match 'list', so they can be used with 'return' straight away.
        assertTrue(printed.contains("1. Catan - lent to Alice"));
        assertTrue(printed.contains("3. Love Letter - lent to Mary Tan"));
        assertFalse(printed.contains("Codenames"));
    }

    @Test
    void execute_noGamesLent_saysEverythingIsOnShelf() {
        new ListLentCommand().execute(games, ui, storage);

        assertTrue(output.toString().contains(ListLentCommand.NONE_LENT_MESSAGE));
    }
}
