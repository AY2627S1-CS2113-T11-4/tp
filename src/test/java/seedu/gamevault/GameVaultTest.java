package seedu.gamevault;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.ui.Ui;

/**
 * End-to-end tests that feed a sequence of commands into the main loop and check the output.
 */
class GameVaultTest {
    /** JUnit creates a fresh empty folder for each test, so tests never touch the real data file. */
    @TempDir
    Path tempDir;

    private String runWithInput(String input) {
        return runWithInput(input, tempDir.resolve("games.txt"));
    }

    private String runWithInput(String input, Path dataFile) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(input.getBytes()), new PrintStream(output));
        new GameVault(dataFile.toString(), ui).run();
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

    @Test
    void run_dataFileWithInvalidLines_warnsAfterWelcomeAndKeepsRunning() throws IOException {
        Path dataFile = tempDir.resolve("games.txt");
        Files.writeString(dataFile, "Catan | 3 | 4\nCatan | 3\nCodenames | 2 | 8\n");

        String printed = runWithInput("help\nexit\n", dataFile);

        assertTrue(printed.indexOf("Welcome to GameVault!") < printed.indexOf("Warning: 1 line(s)"));
        assertTrue(printed.contains("Line 2: \"Catan | 3\""));
        assertTrue(printed.contains("Here are the commands you can use:"));
        assertTrue(printed.contains("Bye!"));
    }

    @Test
    void run_validDataFile_showsNoWarning() throws IOException {
        Path dataFile = tempDir.resolve("games.txt");
        Files.writeString(dataFile, "Catan | 3 | 4\n");
        assertFalse(runWithInput("exit\n", dataFile).contains("Warning"));
    }
}
