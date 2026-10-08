package seedu.gamevault.game;

import java.util.ArrayList;

/**
 * Holds the user's collection of games.
 *
 * <p>Minimal version so the command loop compiles. Operations such as add, delete and filter
 * are added in issues #7 to #10.</p>
 */
public class GameList {
    private final ArrayList<Game> games;

    /**
     * Creates an empty game list.
     */
    public GameList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a game list containing the given games, e.g. those loaded from the data file.
     *
     * @param games Games to start with.
     */
    public GameList(ArrayList<Game> games) {
        assert games != null : "Initial game list should not be null";
        this.games = games;
    }

    /**
     * Returns the number of games in the list.
     *
     * @return Number of games.
     */
    public int size() {
        return games.size();
    }
}
