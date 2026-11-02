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
    /** Name of the data-file field that says why a game is unavailable, e.g. {@code unavailable=missing}. */
    public static final String UNAVAILABLE_FILE_KEY = "unavailable";
    /** Name of the data-file field that holds who the game is lent to, e.g. {@code lent=Alice}. */
    public static final String LENT_FILE_KEY = "lent";
    /** Name of the data-file field that holds the thumbs-up and thumbs-down counts, e.g. {@code ratings=5/1}. */
    public static final String RATINGS_FILE_KEY = "ratings";
    /** Separates the thumbs-up count from the thumbs-down count in the ratings field, e.g. {@code 5/1}. */
    public static final String RATINGS_FILE_SEPARATOR = "/";
    /** Largest weight {@code pick} gives a game, so that a long-time favourite cannot crowd out every other game. */
    public static final double MAX_PICK_WEIGHT = 5;
    /** Smallest weight {@code pick} gives a game, the mirror of {@link #MAX_PICK_WEIGHT}. */
    public static final double MIN_PICK_WEIGHT = 1 / MAX_PICK_WEIGHT;
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
    /** Why the game cannot be played at the moment, or null while it is available. */
    private UnavailableReason unavailableReason;
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

    /**
     * Returns whether the user marked this game unavailable, e.g. because pieces are missing.
     */
    public boolean isMarkedUnavailable() {
        return unavailableReason != null;
    }

    /**
     * Returns why this game is unavailable, or an empty Optional if it is not marked unavailable.
     */
    public Optional<UnavailableReason> getUnavailableReason() {
        return Optional.ofNullable(unavailableReason);
    }

    /**
     * Marks this game unavailable for the given reason, replacing any earlier reason.
     *
     * @param reason Why the game cannot be played at the moment.
     */
    public void markUnavailable(UnavailableReason reason) {
        assert reason != null : "Reason should not be null; use markAvailable() to clear it";
        unavailableReason = reason;
    }

    /**
     * Marks this game available again. Callers must check {@link #isMarkedUnavailable()} first,
     * so that the user can be told when the game was already available.
     */
    public void markAvailable() {
        assert isMarkedUnavailable() : "Game should be marked unavailable before it is made available";
        unavailableReason = null;
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
     * Returns whether this game can be played tonight, i.e. whether {@code filter} and {@code pick} may suggest it.
     * Suggestions should use this method instead of checking each reason a game might be unavailable.
     */
    public boolean isAvailable() {
        return !isLent();
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
        //@@author KRAZYZECTRON
        if (isMarkedUnavailable()) {
            fields.add(UNAVAILABLE_FILE_KEY + "=" + unavailableReason.getKeyword());
        }
        //@@author yy261
        if (isLent()) {
            fields.add(LENT_FILE_KEY + "=" + borrower);
        }
        //@@author MohamedFarsat
        if (isRated()) {
            fields.add(RATINGS_FILE_KEY + "=" + thumbsUp + RATINGS_FILE_SEPARATOR + thumbsDown);
        }
        //@@author Xandurs
        return fields;
    }
    //@@author
    /**
     * Returns this game as it is shown to the user, e.g.
     * {@code Catan (3-4 players, 60 min) [strategy] +5/-1 (lent to Alice)},
     * or {@code Chess (2 players)} when the minimum and maximum are the same and nothing else is set.
     * The rating ({@code +5/-1}) is only shown once the game has been rated.
     * An unavailable game ends with its reason, e.g. {@code Catan (3-4 players) - unavailable: missing pieces}.
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
        String availability = isMarkedUnavailable()
                ? " - unavailable: " + unavailableReason.getDescription()
                : "";
        return title + " (" + players + " players" + time + ")" + tagList + rating + lent + availability;
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
     * Returns how many of the given thumb this game has, e.g. its number of thumbs up for {@link Thumb#UP}.
     */
    public int getRatingCount(Thumb thumb) {
        return thumb == Thumb.UP ? thumbsUp : thumbsDown;
    }

    /**
     * Returns whether one more of the given thumb can be recorded. This is only false when the count has reached
     * {@link Integer#MAX_VALUE} (possible only by editing the data file), where adding 1 would wrap round to a
     * negative number.
     */
    public boolean canAddRating(Thumb thumb) {
        return getRatingCount(thumb) < Integer.MAX_VALUE;
    }

    /**
     * Records one more thumbs up or thumbs down.
     * Callers must check {@link #canAddRating} first, so the user can be told why nothing changed;
     * the assertion here only catches programming errors.
     */
    public void addRating(Thumb thumb) {
        assert canAddRating(thumb) : "Rating count should not overflow";
        if (thumb == Thumb.UP) {
            thumbsUp++;
        } else {
            thumbsDown++;
        }
    }

    /**
     * Returns whether this game has at least one of the given thumb that could be removed.
     */
    public boolean canRemoveRating(Thumb thumb) {
        return getRatingCount(thumb) > 0;
    }

    /**
     * Removes one thumbs up or thumbs down, e.g. to undo a rating given to the wrong game.
     * Callers must check {@link #canRemoveRating} first.
     */
    public void removeRating(Thumb thumb) {
        assert canRemoveRating(thumb) : "Rating count should not go below 0";
        if (thumb == Thumb.UP) {
            thumbsUp--;
        } else {
            thumbsDown--;
        }
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
     * Returns how strongly {@code pick} should favour this game: {@code (thumbsUp + 1) / (thumbsDown + 1)},
     * kept between {@link #MIN_PICK_WEIGHT} and {@link #MAX_PICK_WEIGHT}.
     * An unrated game has weight 1, a game rated +3/-0 has weight 4 (4 times as likely to be picked),
     * and a game rated +0/-3 has weight 0.25. Adding 1 to both counts means the weight is never 0, and the limits
     * stop a game rated e.g. +30/-0 from being picked almost every time, so {@code pick} still feels random.
     */
    public double getPickWeight() {
        double weight = (thumbsUp + 1.0) / (thumbsDown + 1.0);
        return Math.max(MIN_PICK_WEIGHT, Math.min(MAX_PICK_WEIGHT, weight));
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
