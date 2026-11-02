package seedu.gamevault.game;

import java.util.Optional;

//@@author MohamedFarsat
/**
 * The two ratings a group can give a game after playing it: a thumbs up or a thumbs down,
 * together with the word the user types for each ({@code up} or {@code down}).
 * Using an enum instead of a boolean such as {@code isThumbsUp} makes calls like
 * {@code game.addRating(Thumb.UP)} easy to read.
 */
public enum Thumb {
    UP("up"),
    DOWN("down");

    private final String keyword;

    Thumb(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the thumb whose keyword matches {@code text}, ignoring case and surrounding spaces,
     * e.g. {@code " UP "} gives {@link #UP}.
     *
     * @param text Text typed by the user.
     * @return The matching thumb, or an empty Optional if {@code text} is neither {@code up} nor {@code down}.
     */
    public static Optional<Thumb> fromKeyword(String text) {
        assert text != null : "Rating keyword should not be null";
        String keywordToFind = text.trim().toLowerCase();
        for (Thumb thumb : values()) {
            if (thumb.keyword.equals(keywordToFind)) {
                return Optional.of(thumb);
            }
        }
        return Optional.empty();
    }
}
