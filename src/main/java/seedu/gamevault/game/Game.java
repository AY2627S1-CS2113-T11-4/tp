package seedu.gamevault.game;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a single game in the user's collection, with the range of player counts it supports,
 * optionally how many minutes one round usually takes, and any tags the user has given it.
 */
public class Game {
    /** Value of {@code playTime} for a game whose play time has not been recorded. */
    private static final int NO_PLAY_TIME = 0;

    private final String title;
    private final int minPlayers;
    private final int maxPlayers;
    private final int playTime;
    /** Normalised tags (see {@link Tag}); a TreeSet keeps them unique and in alphabetical order. */
    private final TreeSet<String> tags = new TreeSet<>();

    /**
     * Creates a game with no play time recorded.
     * Callers must validate user input first; the checks here only catch programming errors.
     *
     * @param title Name of the game, e.g. "Catan".
     * @param minPlayers Smallest number of players the game supports (at least 1).
     * @param maxPlayers Largest number of players the game supports (at least {@code minPlayers}).
     */
    public Game(String title, int minPlayers, int maxPlayers) {
        this(title, minPlayers, maxPlayers, NO_PLAY_TIME);
        assert !hasPlayTime() : "A game created without a play time should not have one";
    }

    /**
     * Creates a game that usually takes the given number of minutes to play.
     * Callers must validate user input first; the checks here only catch programming errors.
     *
     * @param title Name of the game, e.g. "Catan".
     * @param minPlayers Smallest number of players the game supports (at least 1).
     * @param maxPlayers Largest number of players the game supports (at least {@code minPlayers}).
     * @param playTime Minutes one round usually takes (at least 1), or 0 if not recorded.
     */
    public Game(String title, int minPlayers, int maxPlayers, int playTime) {
        assert title != null && !title.isBlank() : "Title should not be blank";
        assert minPlayers >= 1 : "Minimum players should be at least 1";
        assert maxPlayers >= minPlayers : "Maximum players should not be less than minimum players";
        assert playTime >= NO_PLAY_TIME : "Play time should not be negative";
        this.title = title;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.playTime = playTime;
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

    //@@author yy261
    /**
     * Returns whether the user recorded how long this game takes.
     */
    public boolean hasPlayTime() {
        return playTime != NO_PLAY_TIME;
    }

    /**
     * Returns how many minutes one round of this game usually takes.
     * Only meaningful when {@link #hasPlayTime()} is true.
     */
    public int getPlayTime() {
        assert hasPlayTime() : "Check hasPlayTime() before asking for the play time";
        return playTime;
    }

    //@@author KRAZYZECTRON
    /**
     * Returns whether this game has the given tag.
     *
     * @param tag A normalised tag (see {@link Tag#normalise}).
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * Adds a tag to this game.
     * Callers must check {@link #hasTag} first so that the user can be told when the tag is already there;
     * the assertions here only catch programming errors.
     *
     * @param tag A valid, normalised tag.
     */
    public void addTag(String tag) {
        assert Tag.isValid(tag) : "Tag should be valid and normalised";
        assert !hasTag(tag) : "Game should not already have this tag";
        tags.add(tag);
    }

    /**
     * Removes a tag from this game. Callers must check {@link #hasTag} first.
     *
     * @param tag A normalised tag that this game has.
     */
    public void removeTag(String tag) {
        assert hasTag(tag) : "Game should have the tag being removed";
        tags.remove(tag);
    }

    /**
     * Returns this game's tags in alphabetical order.
     * The returned set is read-only, so callers must use {@link #addTag} and {@link #removeTag} to change it.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    //@@author yy261
    /**
     * Returns whether this game can be played by the given number of players,
     * i.e. the count is within the game's minimum and maximum (both inclusive).
     *
     * @param count Number of players to check; must be at least 1.
     */
    public boolean fitsPlayerCount(int count) {
        assert count >= 1 : "Player count should be at least 1";
        return minPlayers <= count && count <= maxPlayers;
    }

    /**
     * Returns whether this game is known to fit in the given number of minutes.
     * A game with no play time recorded never fits, because we cannot tell how long it takes.
     *
     * @param minutes Time available; must be at least 1.
     */
    public boolean fitsTime(int minutes) {
        assert minutes >= 1 : "Time available should be at least 1 minute";
        return hasPlayTime() && playTime <= minutes;
    }

    //@@author
    /**
     * Returns this game as one line of the data file, in the format {@code TITLE | MIN | MAX},
     * followed by {@code | TIME} when a play time is recorded, and by {@code | TAGS} when the game has tags.
     * TAGS is a comma-separated list, e.g. {@code Codenames | 4 | 8 |  | party,word}; TIME is left blank
     * when a game has tags but no play time, so that TAGS is always the fifth field.
     * {@link seedu.gamevault.storage.GameDecoder} reads this format back.
     *
     * @return Line to write to the data file.
     */
    public String toFileString() {
        String line = title + " | " + minPlayers + " | " + maxPlayers;
        if (!tags.isEmpty()) {
            String time = hasPlayTime() ? String.valueOf(playTime) : "";
            return line + " | " + time + " | " + String.join(",", tags);
        }
        return hasPlayTime() ? line + " | " + playTime : line;
    }

    /**
     * Returns this game as it is shown to the user, e.g. {@code Catan (3-4 players, 60 min) [strategy]},
     * or {@code Chess (2 players)} when the minimum and maximum are the same and there is no play time or tag.
     */
    @Override
    public String toString() {
        String players = minPlayers == maxPlayers
                ? String.valueOf(minPlayers)
                : minPlayers + "-" + maxPlayers;
        String time = hasPlayTime() ? ", " + playTime + " min" : "";
        String tagList = tags.isEmpty() ? "" : " [" + String.join(", ", tags) + "]";
        return title + " (" + players + " players" + time + ")" + tagList;
    }
}
