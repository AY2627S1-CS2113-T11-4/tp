package seedu.gamevault.game;

import java.time.LocalDate;
import java.util.Objects;

//@@author Xandurs
/**
 * Records one time a game was played: the date and how many people played.
 * Immutable, so a game's play history stays exactly as it was logged.
 */
public class PlaySession {
    /** Separates the date from the player count in the data file, e.g. {@code 2026-10-30@4}. */
    public static final String FILE_SEPARATOR = "@";

    private final LocalDate date;
    private final int playerCount;

    public PlaySession(LocalDate date, int playerCount) {
        assert date != null : "Session date should not be null";
        assert playerCount >= 1 : "A session should have at least 1 player";
        this.date = date;
        this.playerCount = playerCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    /** Returns the session as stored in the data file, e.g. {@code 2026-10-30@4}. */
    public String toFileString() {
        return date + FILE_SEPARATOR + playerCount;
    }

    /** Returns the session as shown to the user, e.g. {@code 2026-10-30 (4 players)}. */
    @Override
    public String toString() {
        return date + " (" + playerCount + (playerCount == 1 ? " player)" : " players)");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlaySession)) {
            return false;
        }
        PlaySession session = (PlaySession) other;
        return playerCount == session.playerCount && date.equals(session.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, playerCount);
    }
}
