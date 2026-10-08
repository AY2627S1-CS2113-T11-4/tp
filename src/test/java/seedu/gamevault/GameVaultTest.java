package seedu.gamevault;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import seedu.gamevault.ui.Ui;

/**
 * End-to-end tests that feed a sequence of commands into the main loop and check the output.
 */
class GameVaultTest {

    private String runWithInput(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(input.getBytes()), new PrintStream(output));
        new GameVault("data/test-games.txt", ui).run();
        return output.toString();
    }

    @Test
    void run_helpThenExit_showsHelpAndGoodbye() {
        String printed = runWithInput("help\nexit\n");
        assertTrue(printed.contains("Welcome to GameVault!"));
        assertTrue(printed.contains("Here are the commands you can use:"));
        assertTrue(printed.contains("Bye!"));
    }

    @Test
    void run_unknownCommand_showsErrorAndKeepsRunning() {
        String printed = runWithInput("dance\nexit\n");
        assertTrue(printed.contains("Error: Unknown command 'dance'"));
        // The loop must continue after the error, so the later exit command still runs.
        assertTrue(printed.contains("Bye!"));
    }

    @Test
    void run_inputEndsWithoutExit_endsCleanly() {
        String printed = runWithInput("help\n");
        assertTrue(printed.contains("Bye!"));
    }
}
