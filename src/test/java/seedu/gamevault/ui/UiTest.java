package seedu.gamevault.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private Ui createUi(String input) {
        return new Ui(new ByteArrayInputStream(input.getBytes()), new PrintStream(output));
    }

    @Test
    void readCommand_linesAvailable_returnsEachLineInOrder() {
        Ui ui = createUi("help\nexit\n");
        assertEquals("help", ui.readCommand());
        assertEquals("exit", ui.readCommand());
    }

    @Test
    void readCommand_noMoreInput_returnsExit() {
        Ui ui = createUi("");
        assertEquals("exit", ui.readCommand());
    }

    @Test
    void showError_anyMessage_printsWithErrorPrefix() {
        createUi("").showError("Something went wrong.");
        assertTrue(output.toString().contains("Error: Something went wrong."));
    }

    @Test
    void showWelcome_printsGreeting() {
        createUi("").showWelcome();
        assertTrue(output.toString().contains("Welcome to GameVault!"));
    }
}
