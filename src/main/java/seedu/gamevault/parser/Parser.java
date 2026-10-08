package seedu.gamevault.parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.command.Command;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.exception.GameVaultException;

/**
 * Turns a line typed by the user into the matching {@link Command}.
 * The first word of the line is the command word (case-insensitive); the rest are its arguments.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Returns the command that matches the user's input.
     *
     * @param input Full line typed by the user.
     * @return The command to execute.
     * @throws GameVaultException If the input is empty, the command word is unknown,
     *     or a command that takes no arguments was given some.
     */
    public static Command parse(String input) throws GameVaultException {
        assert input != null : "Input passed to Parser should not be null";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new GameVaultException("Please enter a command. Type 'help' to see all commands.");
        }

        // Split into the command word and everything after it, e.g. "filter 4" -> ["filter", "4"].
        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";
        logger.log(Level.FINE, "Parsing command word: {0}", commandWord);

        switch (commandWord) {
        case HelpCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new HelpCommand();
        case ExitCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new ExitCommand();
        default:
            throw new GameVaultException("Unknown command '" + parts[0]
                    + "'. Type 'help' to see all commands.");
        }
    }

    /**
     * Rejects extra text after a command that takes no arguments, so that a typo like
     * "exit now" is reported instead of being silently accepted.
     */
    private static void checkNoArguments(String commandWord, String arguments) throws GameVaultException {
        if (!arguments.isEmpty()) {
            throw new GameVaultException("The '" + commandWord + "' command does not take any arguments.");
        }
    }
}
