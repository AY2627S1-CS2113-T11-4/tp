package seedu.gamevault.game;

import java.util.Comparator;
import java.util.Optional;

//@@author SwaggyDaddy67
/**
 * The orders the collection can be sorted in with the {@code sort} command, e.g. {@code sort time}.
 * Each order has the keyword the user types, a description shown after sorting, and the
 * {@link Comparator} that puts the games in that order.
 *
 * <p>Games that tie (e.g. two games for 2-4 players) are ordered by title, so the result
 * does not depend on the order the games were in before sorting.</p>
 */
public enum SortOrder {
    TITLE("title", "title (A to Z)",
            Comparator.comparing(Game::getTitle, String.CASE_INSENSITIVE_ORDER)),
    PLAYERS("players", "player count (fewest players first)",
            Comparator.comparingInt(Game::getMinPlayers)
                    .thenComparingInt(Game::getMaxPlayers)
                    .thenComparing(Game::getTitle, String.CASE_INSENSITIVE_ORDER)),
    TIME("time", "play time (shortest first, games with no play time last)",
            Comparator.comparing(SortOrder::playTimeForSorting)
                    .thenComparing(Game::getTitle, String.CASE_INSENSITIVE_ORDER));

    private final String keyword;
    private final String description;
    private final Comparator<Game> comparator;

    SortOrder(String keyword, String description, Comparator<Game> comparator) {
        this.keyword = keyword;
        this.description = description;
        this.comparator = comparator;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    public Comparator<Game> getComparator() {
        return comparator;
    }

    /**
     * Returns the order whose keyword matches the given text, ignoring case and surrounding spaces.
     *
     * @param text Keyword typed by the user, e.g. {@code time} or {@code TITLE}.
     * @return The matching order, or an empty Optional if no order has that keyword.
     */
    public static Optional<SortOrder> fromKeyword(String text) {
        assert text != null : "Sort keyword should not be null";
        String keywordToFind = text.trim().toLowerCase();
        for (SortOrder order : values()) {
            if (order.keyword.equals(keywordToFind)) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns every keyword, e.g. {@code title, players or time}, for use in error messages.
     */
    public static String describeKeywords() {
        SortOrder[] orders = values();
        StringBuilder keywords = new StringBuilder();
        for (int i = 0; i < orders.length; i++) {
            if (i == orders.length - 1) {
                keywords.append(" or ");
            } else if (i > 0) {
                keywords.append(", ");
            }
            keywords.append(orders[i].keyword);
        }
        return keywords.toString();
    }

    /**
     * Returns the number used to sort a game by play time. A game with no play time recorded
     * gets the largest possible value, so it comes after every game whose play time is known.
     */
    private static int playTimeForSorting(Game game) {
        if (game.hasPlayTime()) {
            return game.getPlayTime();
        }
        return Integer.MAX_VALUE;
    }
}
