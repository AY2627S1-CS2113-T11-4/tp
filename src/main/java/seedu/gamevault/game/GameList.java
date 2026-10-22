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
     * Removes the game at the given position and returns it. Games after it move up by one.
     * Callers must check the index first; the assertion here only catches programming errors.
     *
     * @param index Position of the game, counting from 0.
     * @return The game that was removed.
     */
    public Game delete(int index) {
        assert index >= 0 && index < games.size() : "Index to delete should be within the list";
        return games.remove(index);
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

    /**
     * Returns the games that can be played by the given number of players, in their original order.
     * The collection itself is not changed.
     *
     * @param count Number of players to check; must be at least 1.
     * @return A new list of the matching games; empty if none match.
     */
    public ArrayList<Game> filterByPlayers(int count) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : games) {
            if (game.fitsPlayerCount(count)) {
                matches.add(game);
            }
        }
        return matches;
    }

    /**
     * Returns the game at the given position.
     * Callers must check the index first; the assertion here only catches programming errors.
     *
     * @param index Position of the game, counting from 0.
     * @return The game at that position.
     */
    public Game get(int index) {
        assert index >= 0 && index < games.size() : "Index to get should be within the list";
        return games.get(index);
    }

    /**
     * Replaces the game at the given position, keeping its place in the list.
     *
     * @param index Position of the game, counting from 0.
     * @param game The game to put in its place.
     */
    public void set(int index, Game game) {
        assert index >= 0 && index < games.size() : "Index to set should be within the list";
        assert game != null : "Replacement game should not be null";
        games.set(index, game);
    }
    
    /**
     * Returns the games that can be played by the given number of players and are known to take
     * at most the given number of minutes, in their original order. Games with no play time recorded
     * are left out. The collection itself is not changed.
     *
     * @param count Number of players to check; must be at least 1.
     * @param minutes Time available; must be at least 1.
     * @return A new list of the matching games; empty if none match.
     */
    public ArrayList<Game> filterByPlayersAndTime(int count, int minutes) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : filterByPlayers(count)) {
            if (game.fitsTime(minutes)) {
                matches.add(game);
            }
        }
        return matches;
    }
}
