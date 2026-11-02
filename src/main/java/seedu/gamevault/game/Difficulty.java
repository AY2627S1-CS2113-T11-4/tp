package seedu.gamevault.game;

import java.util.Optional;

//@@author yy261
/**
 * How hard a game is to learn and play, e.g. {@code /diff easy}. Hosts use this to avoid choosing
 * something too complicated for a session with new players.
 * Each level has the keyword the user types, which is also how the level is written in the data file.
 */
public enum Difficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    /** Every keyword in words, for error messages. */
    public static final String KEYWORDS_IN_WORDS = "easy, medium or hard";

    private final String keyword;

    Difficulty(String keyword) {
        this.keyword = keyword;
    }

    /** Returns the word the user types and the data file stores, e.g. {@code easy}. */
    public String getKeyword() {
        return keyword;
    }

    /** Returns this level in words, e.g. {@code easy difficulty}, for messages. */
    public String describe() {
        return keyword + " difficulty";
    }

    /**
     * Returns the level whose keyword matches the given text, ignoring case and surrounding spaces.
     *
     * @param text Keyword typed by the user or read from the data file, e.g. {@code easy} or {@code HARD}.
     * @return The matching level, or an empty Optional if no level has that keyword.
     */
    public static Optional<Difficulty> fromKeyword(String text) {
        assert text != null : "Difficulty keyword should not be null";
        String keywordToFind = text.trim().toLowerCase();
        for (Difficulty difficulty : values()) {
            if (difficulty.keyword.equals(keywordToFind)) {
                return Optional.of(difficulty);
            }
        }
        return Optional.empty();
    }
}
