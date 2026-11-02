package seedu.gamevault.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@@author KRAZYZECTRON
/**
 * The reasons a game can be marked unavailable with the {@code unavailable} command,
 * e.g. {@code unavailable 2 /reason missing}. Each reason has the keyword the user types,
 * which is also how the reason is written in the data file.
 *
 * <p>Lent-out games are not a reason here, because the {@code lend} command already records who has them.</p>
 */
public enum UnavailableReason {
    MISSING("missing", "missing pieces"),
    DAMAGED("damaged", "damaged");

    private final String keyword;
    private final String description;

    UnavailableReason(String keyword, String description) {
        this.keyword = keyword;
        this.description = description;
    }

    /** Returns the word the user types and the data file stores, e.g. {@code missing}. */
    public String getKeyword() {
        return keyword;
    }

    /** Returns the reason in words, e.g. {@code missing pieces}, for showing to the user. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the reason whose keyword matches the given text, ignoring case and surrounding spaces.
     *
     * @param text Keyword typed by the user or read from the data file, e.g. {@code missing} or {@code DAMAGED}.
     * @return The matching reason, or an empty Optional if no reason has that keyword.
     */
    public static Optional<UnavailableReason> fromKeyword(String text) {
        assert text != null : "Reason keyword should not be null";
        String keywordToFind = text.trim().toLowerCase();
        for (UnavailableReason reason : values()) {
            if (reason.keyword.equals(keywordToFind)) {
                return Optional.of(reason);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns every reason's keyword in order, e.g. {@code [missing, damaged]}.
     * The usage line and error messages are built from this list, so a new reason only needs adding here.
     */
    public static List<String> keywords() {
        List<String> keywords = new ArrayList<>();
        for (UnavailableReason reason : values()) {
            keywords.add(reason.keyword);
        }
        return keywords;
    }

    /**
     * Returns every keyword in words, e.g. {@code missing or damaged}, for use in error messages.
     */
    public static String describeKeywords() {
        List<String> keywords = keywords();
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < keywords.size(); i++) {
            if (i == keywords.size() - 1 && i > 0) {
                description.append(" or ");
            } else if (i > 0) {
                description.append(", ");
            }
            description.append(keywords.get(i));
        }
        return description.toString();
    }
}
