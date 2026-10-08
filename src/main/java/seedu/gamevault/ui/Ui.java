package seedu.gamevault.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all interaction with the user: reading commands and printing messages.
 * Keeping all input/output in one class means the rest of the program never touches
 * {@code System.in} or {@code System.out} directly, which also makes it easy to test.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String LOGO = """
              ____                    __     __          _ _
             / ___| __ _ _ __ ___   __\\ \\   / /_ _ _   _| | |_
            | |  _ / _` | '_ ` _ \\ / _ \\ \\ / / _` | | | | | __|
            | |_| | (_| | | | | | |  __/\\ V / (_| | |_| | | |_
             \\____|\\__,_|_| |_| |_|\\___| \\_/ \\__,_|\\__,_|_|\\__|
            """;

    /** Command returned when there is no more input (e.g. the input stream was closed). */
    private static final String END_OF_INPUT_COMMAND = "exit";

    private final Scanner in;
    private final PrintStream out;

    /**
     * Creates a Ui that reads from standard input and writes to standard output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a Ui that reads from and writes to the given streams.
     * Mainly used by tests to supply fake input and capture the output.
     *
     * @param in Stream to read user commands from.
     * @param out Stream to print messages to.
     */
    public Ui(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    /**
     * Prints the welcome message shown when the program starts.
     */
    public void showWelcome() {
        out.print(LOGO);
        showMessage("Welcome to GameVault! Type 'help' to see what I can do.");
    }

    /**
     * Prints a prompt and returns the next line typed by the user.
     * If there is no more input, {@code "exit"} is returned so the program ends cleanly instead of crashing.
     *
     * @return The raw command line entered by the user.
     */
    public String readCommand() {
        out.print("> ");
        if (!in.hasNextLine()) {
            out.println();
            return END_OF_INPUT_COMMAND;
        }
        return in.nextLine();
    }

    /**
     * Prints a message between two divider lines.
     *
     * @param message Text to show; may span several lines.
     */
    public void showMessage(String message) {
        out.println(DIVIDER);
        out.println(message);
        out.println(DIVIDER);
    }

    /**
     * Prints an error message, prefixed so the user can tell it apart from normal output.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        showMessage("Error: " + message);
    }
}
