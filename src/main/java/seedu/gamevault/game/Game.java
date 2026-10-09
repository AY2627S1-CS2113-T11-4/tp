package seedu.gamevault.game;

/**
 * Represents a single game in the user's collection, with the range of player counts it supports.
 */
public class Game {
    private final String title;
    private final int minPlayers;
    private final int maxPlayers;

    /**
     * Creates a game. Callers must validate user input first; the checks here only catch programming errors.
     *
     * @param title Name of the game, e.g. "Catan".
     * @param minPlayers Smallest number of players the game supports (at least 1).
     * @param maxPlayers Largest number of players the game supports (at least {@code minPlayers}).
     */
    public Game(String title, int minPlayers, int maxPlayers) {
        assert title != null && !title.isBlank() : "Title should not be blank";
        assert minPlayers >= 1 : "Minimum players should be at least 1";
        assert maxPlayers >= minPlayers : "Maximum players should not be less than minimum players";
        this.title = title;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getTitle() {
        return title;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Returns this game as one line of the data file, in the format {@code TITLE | MIN | MAX}.
     * {@link seedu.gamevault.storage.GameDecoder} reads this format back.
     *
     * @return Line to write to the data file.
     */
    public String toFileString() {
        return title + " | " + minPlayers + " | " + maxPlayers;
    }

    /**
     * Returns this game as it is shown to the user, e.g. {@code Catan (3-4 players)},
     * or {@code Chess (2 players)} when the minimum and maximum are the same.
     */
    @Override
    public String toString() {
        String players = minPlayers == maxPlayers
                ? String.valueOf(minPlayers)
                : minPlayers + "-" + maxPlayers;
        return title + " (" + players + " players)";
    }
}
