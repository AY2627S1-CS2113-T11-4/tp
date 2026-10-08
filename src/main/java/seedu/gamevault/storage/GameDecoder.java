package seedu.gamevault.storage;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;

/**
 * Converts one line of the data file back into a {@link Game}, checking that the line is valid.
 *
 * <p>Each line has the format {@code TITLE | MIN | MAX}, e.g. {@code Catan | 3 | 4}.
 * Users may edit the data file by hand, so every part of the line is checked and a
 * specific reason is given when something is wrong.</p>
 */
public class GameDecoder {
    public static final String FORMAT = "TITLE | MIN | MAX";

    private static final String FIELD_SEPARATOR_REGEX = "\\|";
    private static final int FIELD_COUNT = 3;
    /** Invisible marker some editors (e.g. Windows Notepad) put at the start of a file. */
    private static final String BYTE_ORDER_MARK = "﻿";

    /**
     * Returns the game described by one line of the data file.
     *
     * @param line A single line from the data file.
     * @return The decoded game.
     * @throws GameVaultException If the line is not in the expected format; the message says why.
     */
    public static Game decodeLine(String line) throws GameVaultException {
        assert line != null : "Line to decode should not be null";

        // A limit of -1 keeps empty trailing fields, so "Catan | 3 |" is reported as a missing MAX.
        String[] fields = line.replace(BYTE_ORDER_MARK, "").split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length != FIELD_COUNT) {
            throw new GameVaultException("expected " + FIELD_COUNT + " parts in the format " + FORMAT
                    + ", but found " + fields.length);
        }

        String title = fields[0].trim();
        if (title.isEmpty()) {
            throw new GameVaultException("TITLE is empty");
        }
        int minPlayers = parsePlayerCount(fields[1], "MIN");
        int maxPlayers = parsePlayerCount(fields[2], "MAX");
        if (maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") is less than MIN (" + minPlayers + ")");
        }
        return new Game(title, minPlayers, maxPlayers);
    }

    /**
     * Returns the player count in {@code text}, which must be a whole number of at least 1.
     *
     * @param fieldName Name of the field, used in the error message.
     */
    private static int parsePlayerCount(String text, String fieldName) throws GameVaultException {
        String trimmedText = text.trim();
        int count;
        try {
            count = Integer.parseInt(trimmedText);
        } catch (NumberFormatException e) {
            throw new GameVaultException(fieldName + " should be a whole number, but was '" + trimmedText + "'");
        }
        if (count < 1) {
            throw new GameVaultException(fieldName + " should be at least 1, but was " + count);
        }
        return count;
    }
}
