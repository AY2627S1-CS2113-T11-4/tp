package seedu.gamevault.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

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
    //@@author Xandurs
    /**
     * Adds a game to the end of the list.
     *
     * @param game The game to add.
     */
    public void add(Game game) {
        assert game != null : "Game to add should not be null";
        games.add(game);
    }

    //@@author SwaggyDaddy67
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

    //@@author
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

    //@@author SwaggyDaddy67
    /**
     * Reorders the games using the given comparator, e.g. one from {@link SortOrder}.
     * The sort is stable, so games the comparator considers equal keep their current order.
     *
     * @param comparator Decides which of two games comes first.
     */
    public void sort(Comparator<Game> comparator) {
        assert comparator != null : "Comparator to sort by should not be null";
        games.sort(comparator);
    }

    //@@author yy261
    /**
     * Returns the games on the shelf that can be played by the given number of players, in their original order.
     * Games that are lent out are left out, since they cannot be played tonight (see {@link #findLentFitting}).
     * The collection itself is not changed.
     *
     * @param count Number of players to check; must be at least 1.
     * @return A new list of the matching games; empty if none match.
     */
    public ArrayList<Game> filterByPlayers(int count) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : games) {
            if (!game.isLent() && game.fitsPlayerCount(count)) {
                matches.add(game);
            }
        }
        return matches;
    }

    /**
     * Returns the games that are lent out but would otherwise fit the given number of players,
     * in their original order, so commands can tell the user why those games were left out.
     *
     * @param count Number of players to check; must be at least 1.
     * @return A new list of such games; empty if there are none.
     */
    public ArrayList<Game> findLentFitting(int count) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : games) {
            if (game.isLent() && game.fitsPlayerCount(count)) {
                matches.add(game);
            }
        }
        return matches;
    }

    /**
     * Returns the games that are lent out but would otherwise fit the given number of players
     * and the given time limit, in their original order.
     *
     * @param count Number of players to check; must be at least 1.
     * @param minutes Time available; must be at least 1.
     * @return A new list of such games; empty if there are none.
     */
    public ArrayList<Game> findLentFitting(int count, int minutes) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : findLentFitting(count)) {
            if (game.fitsTime(minutes)) {
                matches.add(game);
            }
        }
        return matches;
    }
    //@@author Xandurs
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
    
    //@@author yy261
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

    //@@author
    /**
     * Returns how many games on the shelf fit the given number of players but have no play time recorded.
     * These are the games that a time limit leaves out, so commands can tell the user about them.
     *
     * @param count Number of players to check; must be at least 1.
     * @return Number of such games.
     */
    public int countWithoutPlayTime(int count) {
        int untimedCount = 0;
        for (Game game : filterByPlayers(count)) {
            if (!game.hasPlayTime()) {
                untimedCount++;
            }
        }
        return untimedCount;
    }

    //@@author KRAZYZECTRON
    /**
     * Returns the games that have the given tag, in their original order.
     * The collection itself is not changed.
     *
     * @param tag A normalised tag (see {@link Tag#normalise}).
     * @return A new list of the matching games; empty if none match.
     */
    public ArrayList<Game> filterByTag(String tag) {
        assert Tag.isValid(tag) : "Tag to filter by should be valid and normalised";
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : games) {
            if (game.hasTag(tag)) {
                matches.add(game);
            }
        }
        return matches;
    }

    /**
     * Returns every tag used in the collection, mapped to the number of games that have it.
     * A TreeMap is used so that the tags come out in alphabetical order.
     *
     * @return A new map from tag to game count; empty if no game has a tag.
     */
    public TreeMap<String, Integer> countTags() {
        TreeMap<String, Integer> tagCounts = new TreeMap<>();
        for (Game game : games) {
            for (String tag : game.getTags()) {
                tagCounts.merge(tag, 1, Integer::sum);
            }
        }
        return tagCounts;
    }
}
