package seedu.gamevault.game;

import java.util.Locale;

//@@author KRAZYZECTRON
/**
 * Rules for the tags a user can put on a game, e.g. {@code party} or {@code co-op}.
 * The rules are kept in one place so that tags typed by the user ({@link seedu.gamevault.parser.Parser})
 * and tags read from the data file ({@link seedu.gamevault.storage.GameDecoder}) are checked the same way.
 */
public final class Tag {
    /** Describes a valid tag, for use in error messages. */
    public static final String RULES = "one word of letters and digits, optionally joined by hyphens "
            + "(e.g. party or co-op)";

    // Lower case only, because tags are normalised before they are checked.
    // Hyphens may join parts of a tag but cannot start or end it, so "co-op" is valid but "-" is not.
    private static final String VALID_TAG_REGEX = "[a-z0-9]+(-[a-z0-9]+)*";

    private Tag() {
        // Only holds static helpers, so it should not be instantiated.
    }

    /**
     * Returns the tag in the form it is stored and compared in: trimmed and in lower case,
     * so that {@code Party} and {@code party} count as the same tag.
     *
     * @param text Tag as typed by the user or read from the data file.
     * @return The normalised tag, which may still be invalid (see {@link #isValid}).
     */
    public static String normalise(String text) {
        assert text != null : "Tag text should not be null";
        return text.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Returns whether a normalised tag follows {@link #RULES}.
     * Besides keeping tags simple to type, this guarantees a tag never contains the {@code |} or {@code ,}
     * characters that separate fields and tags in the data file.
     *
     * @param tag A tag returned by {@link #normalise}.
     */
    public static boolean isValid(String tag) {
        return tag != null && tag.matches(VALID_TAG_REGEX);
    }
}
