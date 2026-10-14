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

/**
 * Tests for the help, exit and filter commands.
 */
class CommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    @Test
    void helpCommand_execute_showsEveryCommand() {
        new HelpCommand().execute(new GameList(), ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains(HelpCommand.HELP_MESSAGE));
        assertTrue(printed.contains("help"));
        assertTrue(printed.contains("list"));
        assertTrue(printed.contains("filter"));
        assertTrue(printed.contains("delete"));
        assertTrue(printed.contains("exit"));
    }

    @Test
    void helpCommand_isExit_returnsFalse() {
        assertFalse(new HelpCommand().isExit());
    }

    @Test
    void exitCommand_execute_showsGoodbye() {
        new ExitCommand().execute(new GameList(), ui, new Storage("unused.txt"));
        assertTrue(output.toString().contains(ExitCommand.GOODBYE_MESSAGE));
    }

    @Test
    void filterCommand_someGamesFit_showsOnlyMatchingGames() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4).execute(games, ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players:"));
        assertTrue(printed.contains("Catan (3-4 players)"));
        assertTrue(printed.contains("Codenames (2-8 players)"));
        assertFalse(printed.contains("Chess"));
    }

    @Test
    void filterCommand_noGamesFit_showsNoGamesMessage() {
        GameList games = new GameList();
        games.add(new Game("Chess", 2, 2));

        new FilterCommand(5).execute(games, ui, new Storage("unused.txt"));
        assertTrue(output.toString().contains("No games fit 5 players."));
    }

    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
