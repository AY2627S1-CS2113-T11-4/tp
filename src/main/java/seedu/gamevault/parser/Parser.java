package seedu.gamevault.parser;

import java.util.logging.Level;
import java.util.logging.Logger;
    

import seedu.gamevault.command.Command;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.command.AddCommand;
import seedu.gamevault.game.Game;

/**
 * Turns a line typed by the user into the matching {@link Command}.
 * The first word of the line is the command word (case-insensitive); the rest are its arguments.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final String MIN_PREFIX = "/min";
    private static final String MAX_PREFIX = "/max";

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
        case AddCommand.COMMAND_WORD:
            return parseAdd(arguments);
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

    /**
     * Builds an {@link AddCommand} from arguments in the form {@code TITLE /min MIN /max MAX},
     * e.g. {@code Catan /min 3 /max 4}.
     *
     * @throws GameVaultException If a field is missing, a count is not a whole number,
     *     MIN is less than 1, or MAX is less than MIN.
     */
    private static Command parseAdd(String arguments) throws GameVaultException {
        int minIndex = arguments.indexOf(MIN_PREFIX);
        int maxIndex = arguments.indexOf(MAX_PREFIX);
        if (minIndex == -1 || maxIndex == -1 || maxIndex < minIndex) {
            throw new GameVaultException("Please use the format: " + AddCommand.USAGE
                    + " (e.g. add Catan /min 3 /max 4)");
        }

        // "Catan /min 3 /max 4" -> title "Catan", min "3", max "4"
        String title = arguments.substring(0, minIndex).trim();
        String minText = arguments.substring(minIndex + MIN_PREFIX.length(), maxIndex);
        String maxText = arguments.substring(maxIndex + MAX_PREFIX.length());

        if (title.isEmpty()) {
            throw new GameVaultException("TITLE is missing. Format: " + AddCommand.USAGE);
        }
        // The data file uses '|' to separate fields, so a title containing it could not be loaded back.
        if (title.contains("|")) {
            throw new GameVaultException("TITLE cannot contain the '|' character.");
        }
        int minPlayers = parsePlayerCount(minText, "MIN");
        int maxPlayers = parsePlayerCount(maxText, "MAX");
        if (maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") cannot be less than MIN (" + minPlayers + ").");
        }
        return new AddCommand(new Game(title, minPlayers, maxPlayers));
    }

    /**
     * Returns the player count in {@code text}, which must be a whole number of at least 1.
     *
     * @param fieldName Name of the field (MIN or MAX), used in error messages.
     */
    private static int parsePlayerCount(String text, String fieldName) throws GameVaultException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new GameVaultException(fieldName + " is missing. Format: " + AddCommand.USAGE);
        }
        int count;
        try {
            count = Integer.parseInt(trimmedText);
        } catch (NumberFormatException e) {
            throw new GameVaultException(fieldName + " should be a whole number, but was '" + trimmedText + "'.");
        }
        if (count < 1) {
            throw new GameVaultException(fieldName + " should be at least 1, but was " + count + ".");
        }
        return count;
    }
}
