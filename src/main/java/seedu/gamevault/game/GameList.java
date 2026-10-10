package seedu.gamevault.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * Adds a game to the end of the list.
     *
     * @param game The game to add.
     */
    public void add(Game game) {
        assert game != null : "Game to add should not be null";
        games.add(game);
    }

    /**
     * Returns the number of games in the list.
     *
     * @return Number of games.
     */
    public int size() {
        return games.size();
    }

    /**
     * Returns the games in the list, in order, e.g. so they can be saved to the data file.
     * The returned list is read-only, so callers must use this class's methods to change the collection.
     *
     * @return Read-only view of the games.
     */
    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }

    
}
