package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Tests for the help and exit commands.
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
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }
}
