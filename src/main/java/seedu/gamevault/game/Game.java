package seedu.gamevault.game;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;


/**
 * Represents a single game in the user's collection, with the range of player counts it supports,
 * optionally how many minutes one round usually takes, any tags the user has given it,
 * and who it is lent to, if anyone.
 */
public class Game {
    /** Name of the data-file field that holds the play sessions, e.g. {@code sessions=2026-10-30@4}. */
    public static final String SESSIONS_FILE_KEY = "sessions";
    /** Separates the sessions within the sessions field, e.g. {@code 2026-06-01@3;2026-10-30@4}. */
    public static final String SESSION_FILE_SEPARATOR = ";";
    /** Name of the data-file field that holds who the game is lent to, e.g. {@code lent=Alice}. */
    public static final String LENT_FILE_KEY = "lent";
    /** Name of the data-file field that holds the thumbs-up and thumbs-down counts, e.g. {@code ratings=5/1}. */
    public static final String RATINGS_FILE_KEY = "ratings";
    /** Separates the thumbs-up count from the thumbs-down count in the ratings field, e.g. {@code 5/1}. */
    public static final String RATINGS_FILE_SEPARATOR = "/";
    /** Value of {@code playTime} for a game whose play time has not been recorded. */
    private static final int NO_PLAY_TIME = 0;

    /** Play sessions logged for this game, kept oldest first. */
    private final ArrayList<PlaySession> sessions = new ArrayList<>();
    private final String title;
    private final int minPlayers;
    private final int maxPlayers;
    private final int playTime;
    /** Normalised tags (see {@link Tag}); a TreeSet keeps them unique and in alphabetical order. */
    private final TreeSet<String> tags = new TreeSet<>();
    /** Normalised name of the person this game is lent to (see {@link Borrower}), or null if it is on the shelf. */
    private String borrower;
    /** Number of times the group gave this game a thumbs up after playing it (see {@code rate}). */
    private int thumbsUp;
    /** Number of times the group gave this game a thumbs down after playing it. */
    private int thumbsDown;

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
    /**
     * Returns whether this game is lent to someone, i.e. it is not on the shelf.
     */
    public boolean isLent() {
        return borrower != null;
    }

    /**
     * Returns the name of the person this game is lent to.
     * Only meaningful when {@link #isLent()} is true.
     */
    public String getBorrower() {
        assert isLent() : "Check isLent() before asking for the borrower";
        return borrower;
    }

    /**
     * Records that this game has been lent to the given person.
     * Callers must check {@link #isLent()} first so that the user can be told who already has the game;
     * the assertions here only catch programming errors.
     *
     * @param name A valid, normalised name (see {@link Borrower}).
     */
    public void lendTo(String name) {
        assert Borrower.isValid(name) : "Borrower name should be valid and normalised";
        assert !isLent() : "Game should not already be lent";
        borrower = name;
    }

    /**
     * Records that this game is back on the shelf. Callers must check {@link #isLent()} first.
     */
    public void markReturned() {
        assert isLent() : "Only a lent game can be returned";
        borrower = null;
    }
    //@@author Xandurs
    /**
     * Returns this game as one line of the data file, in the format {@code TITLE | MIN | MAX},
     * followed by {@code | TIME} when a play time is recorded, by {@code | TAGS} when the game has tags,
     * and by {@code | BORROWER} when the game is lent to someone.
     * TAGS is a comma-separated list, e.g. {@code Codenames | 4 | 8 |  | party,word}. A field that is not set is
     * left blank when a later field is set, so that each field is always in the same position,
     * e.g. {@code Hive | 2 | 2 |  |  | Alice} for a lent game with no play time or tags.
     * {@link seedu.gamevault.storage.GameDecoder} reads this format back.
     *
     * @return Line to write to the data file.
     */
    public String toFileString() {
        String line = title + " | " + minPlayers + " | " + maxPlayers;
        List<String> namedFields = getNamedFileFields();
        if (tags.isEmpty() && namedFields.isEmpty()) {
            return hasPlayTime() ? line + " | " + playTime : line;
        }
        // TIME and TAGS are found by position, so write both (maybe blank) before named fields.
        String time = hasPlayTime() ? String.valueOf(playTime) : "";
        line += " | " + time + " | " + String.join(",", tags);
        for (String field : namedFields) {
            line += " | " + field;
        }
        return line;
    }

    //@@author Xandurs
    /** Named KEY=VALUE fields after TAGS. Teammates: add your new field here. */
    private List<String> getNamedFileFields() {
        List<String> fields = new ArrayList<>();
        if (!sessions.isEmpty()) {
            List<String> encoded = new ArrayList<>();
            for (PlaySession session : sessions) {
                encoded.add(session.toFileString());
            }
            fields.add(SESSIONS_FILE_KEY + "=" + String.join(SESSION_FILE_SEPARATOR, encoded));
        }
        //@@author yy261
        if (isLent()) {
            fields.add(LENT_FILE_KEY + "=" + borrower);
        }
        //@@author MohamedFarsat
        if (isRated()) {
            fields.add(RATINGS_FILE_KEY + "=" + thumbsUp + RATINGS_FILE_SEPARATOR + thumbsDown);
        }
        //@@ author Xandurs
        return fields;
    }
    //@@ author
    /**
     * Returns this game as it is shown to the user, e.g.
     * {@code Catan (3-4 players, 60 min) [strategy] +5/-1 (lent to Alice)},
     * or {@code Chess (2 players)} when the minimum and maximum are the same and nothing else is set.
     * The rating ({@code +5/-1}) is only shown once the game has been rated.
     */
    @Override
    public String toString() {
        String players = minPlayers == maxPlayers
                ? String.valueOf(minPlayers)
                : minPlayers + "-" + maxPlayers;
        String time = hasPlayTime() ? ", " + playTime + " min" : "";
        String tagList = tags.isEmpty() ? "" : " [" + String.join(", ", tags) + "]";
        String rating = isRated() ? " " + describeRatings() : "";
        String lent = isLent() ? " (lent to " + borrower + ")" : "";
        return title + " (" + players + " players" + time + ")" + tagList + rating + lent;
    }

    //@@author MohamedFarsat
    public int getThumbsUp() {
        return thumbsUp;
    }

    public int getThumbsDown() {
        return thumbsDown;
    }

    /**
     * Returns whether this game has been given at least one thumbs up or thumbs down.
     */
    public boolean isRated() {
        return thumbsUp > 0 || thumbsDown > 0;
    }

    /**
     * Records one more thumbs up. Callers must check that the count is below {@link Integer#MAX_VALUE} first.
     */
    public void addThumbsUp() {
        assert thumbsUp < Integer.MAX_VALUE : "Thumbs-up count should not overflow";
        thumbsUp++;
    }

    /**
     * Records one more thumbs down. Callers must check that the count is below {@link Integer#MAX_VALUE} first.
     */
    public void addThumbsDown() {
        assert thumbsDown < Integer.MAX_VALUE : "Thumbs-down count should not overflow";
        thumbsDown++;
    }

    /**
     * Replaces both rating counts, e.g. when loading the game from the data file or copying it in {@code edit}.
     *
     * @param thumbsUp Number of thumbs up (at least 0).
     * @param thumbsDown Number of thumbs down (at least 0).
     */
    public void setRatings(int thumbsUp, int thumbsDown) {
        assert thumbsUp >= 0 && thumbsDown >= 0 : "Rating counts should not be negative";
        this.thumbsUp = thumbsUp;
        this.thumbsDown = thumbsDown;
    }

    /**
     * Returns the rating counts as shown to the user, e.g. {@code +5/-1} for 5 thumbs up and 1 thumbs down.
     */
    public String describeRatings() {
        return "+" + thumbsUp + "/-" + thumbsDown;
    }

    /**
     * Returns how strongly {@code pick} should favour this game: {@code (thumbsUp + 1) / (thumbsDown + 1)}.
     * An unrated game has weight 1, a game rated +3/-0 has weight 4 (4 times as likely to be picked),
     * and a game rated +0/-3 has weight 0.25. Adding 1 to both counts means the weight is never 0,
     * so even a disliked game can still come up now and then.
     */
    public double getPickWeight() {
        return (thumbsUp + 1.0) / (thumbsDown + 1.0);
    }

    //@@author Xandurs
    /** Records a session, keeping sessions sorted by date (same-day sessions stay in logged order). */
    public void addSession(PlaySession session) {
        assert session != null : "Session to add should not be null";
        int position = sessions.size();
        while (position > 0 && sessions.get(position - 1).getDate().isAfter(session.getDate())) {
            position--;
        }
        sessions.add(position, session);
    }

    public List<PlaySession> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    public int getPlayCount() {
        return sessions.size();
    }

    /** Returns the date this game was last played, or empty if never played. */
    public Optional<LocalDate> getLastPlayed() {
        if (sessions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sessions.get(sessions.size() - 1).getDate());
    }
}
