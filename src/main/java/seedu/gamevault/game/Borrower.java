package seedu.gamevault.game;

//@@author yy261
/**
 * Rules for the name of the person a game is lent to, e.g. {@code Alice} in {@code lend 2 /to Alice}.
 * Keeping the rules in one place means the parser and the data file decoder accept exactly the same names.
 */
public class Borrower {
    /** Longest name accepted, so that names fit on one line of output. */
    public static final int MAX_LENGTH = 30;
    /** The rules in words, shown to the user when a name breaks them. */
    public static final String RULES = "1 to " + MAX_LENGTH + " characters long and cannot contain '|'.";

    /**
     * Returns the name with surrounding spaces removed and any run of spaces inside it reduced to one,
     * e.g. {@code "  Mary   Tan "} becomes {@code "Mary Tan"}, so that names are stored and shown consistently.
     *
     * @param name Name as typed by the user or read from the data file.
     */
    public static String normalise(String name) {
        assert name != null : "Name to normalise should not be null";
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns whether the name follows {@link #RULES}.
     * The data file uses '|' to separate fields, so a name containing it could not be loaded back.
     *
     * @param name A normalised name (see {@link #normalise}).
     */
    public static boolean isValid(String name) {
        return name != null && !name.isEmpty() && name.length() <= MAX_LENGTH && !name.contains("|");
    }
}
