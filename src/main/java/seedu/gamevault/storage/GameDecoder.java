package seedu.gamevault.storage;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.Tag;

/**
 * Converts one line of the data file back into a {@link Game}, checking that the line is valid.
 *
 * <p>Each line has the format {@code TITLE | MIN | MAX}, e.g. {@code Catan | 3 | 4}, optionally followed by
 * {@code | TIME}, the play time in minutes, e.g. {@code Catan | 3 | 4 | 60}.
 * Files saved before play time was added have no TIME field and still load.
 * A game with tags has a fifth field, TAGS, holding comma-separated tags, e.g. {@code Catan | 3 | 4 | 60 | strategy}
 * or {@code Codenames | 4 | 8 |  | party,word} (TIME is left blank when there is no play time).
 * Users may edit the data file by hand, so every part of the line is checked and a
 * specific reason is given when something is wrong.</p>
 */
public class GameDecoder {
    public static final String FORMAT = "TITLE | MIN | MAX | TIME | TAGS (TIME and TAGS are optional)";

    private static final String FIELD_SEPARATOR_REGEX = "\\|";
    private static final int FIELD_COUNT_WITHOUT_TIME = 3;
    private static final int FIELD_COUNT_WITH_TIME = 4;
    private static final int FIELD_COUNT_WITH_TAGS = 5;
    private static final String TAG_SEPARATOR = ",";
    private static final int NO_PLAY_TIME = 0;
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
        if (fields.length < FIELD_COUNT_WITHOUT_TIME || fields.length > FIELD_COUNT_WITH_TAGS) {
            throw new GameVaultException("expected " + FIELD_COUNT_WITHOUT_TIME + " to " + FIELD_COUNT_WITH_TAGS
                    + " parts in the format " + FORMAT + ", but found " + fields.length);
        }

        String title = fields[0].trim();
        if (title.isEmpty()) {
            throw new GameVaultException("TITLE is empty");
        }
        int minPlayers = parsePositiveNumber(fields[1], "MIN");
        int maxPlayers = parsePositiveNumber(fields[2], "MAX");
        if (maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") is less than MIN (" + minPlayers + ")");
        }
        int playTime = decodePlayTime(fields);
        Game game = new Game(title, minPlayers, maxPlayers, playTime);
        decodeTags(fields, game);
        return game;
    }

    //@@author KRAZYZECTRON
    /**
     * Adds the tags in the TAGS field, if the line has one, to the game.
     * Blank entries (e.g. from a trailing comma) are ignored, and a tag listed twice is only added once,
     * since neither loses any information. A tag that breaks the rules makes the whole line invalid.
     *
     * @throws GameVaultException If a tag does not follow {@link Tag#RULES}.
     */
    private static void decodeTags(String[] fields, Game game) throws GameVaultException {
        if (fields.length < FIELD_COUNT_WITH_TAGS) {
            return;
        }
        for (String text : fields[FIELD_COUNT_WITH_TAGS - 1].split(TAG_SEPARATOR)) {
            String tag = Tag.normalise(text);
            if (tag.isEmpty() || game.hasTag(tag)) {
                continue;
            }
            if (!Tag.isValid(tag)) {
                throw new GameVaultException("TAGS contains '" + text.trim() + "', but each tag should be "
                        + Tag.RULES);
            }
            game.addTag(tag);
        }
    }

    //@@author

    /**
     * Returns the play time in the TIME field, or 0 if the line has no TIME field or it is left empty.
     */
    private static int decodePlayTime(String[] fields) throws GameVaultException {
        if (fields.length < FIELD_COUNT_WITH_TIME || fields[FIELD_COUNT_WITH_TIME - 1].isBlank()) {
            return NO_PLAY_TIME;
        }
        return parsePositiveNumber(fields[FIELD_COUNT_WITH_TIME - 1], "TIME");
    }

    /**
     * Returns the number in {@code text}, such as a player count or play time, which must be a whole number
     * of at least 1.
     *
     * @param fieldName Name of the field, used in the error message.
     */
    private static int parsePositiveNumber(String text, String fieldName) throws GameVaultException {
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
