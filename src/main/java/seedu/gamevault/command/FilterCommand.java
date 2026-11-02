package seedu.gamevault.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Shows the games that can be played by a given number of players and, optionally,
 * that fit in a given number of minutes and were not played in the last given number of days.
 * The numbers are validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";
    public static final String USAGE = "filter COUNT [/time MINUTES] [/notplayedin DAYS]";
    //@@author Xandurs
    /** Value of {@code notPlayedInDays} when the user did not ask to hide recently played games. */
    public static final int NO_DAYS_LIMIT = 0;
    //@@author yy261

    /** Value of {@code minutes} when the user did not give a time limit. */
    private static final int NO_TIME_LIMIT = GameList.NO_TIME_LIMIT;

    private final int playerCount;
    private final int minutes;
    //@@author Xandurs
    /** Games played within this many days are hidden, or {@link #NO_DAYS_LIMIT} to show them all. */
    private final int notPlayedInDays;
    /** Today's date, used to work out how long ago each game was played. */
    private final LocalDate today;
    //@@author yy261

    /**
     * Creates a command that shows the games fitting the given number of players, however long they take.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     */
    public FilterCommand(int playerCount) {
        this(playerCount, NO_TIME_LIMIT);
    }

    /**
     * Creates a command that shows the games fitting the given number of players
     * that take at most the given number of minutes.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes; already checked to be at least 1, or 0 for no limit.
     */
    public FilterCommand(int playerCount, int minutes) {
        this(playerCount, minutes, NO_DAYS_LIMIT);
    }

    //@@author Xandurs
    /**
     * Creates a command that shows the games fitting the given number of players and time,
     * leaving out games played within the last {@code notPlayedInDays} days.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes; already checked to be at least 1, or 0 for no limit.
     * @param notPlayedInDays Hide games played within this many days; at least 1, or {@link #NO_DAYS_LIMIT}.
     */
    public FilterCommand(int playerCount, int minutes, int notPlayedInDays) {
        this(playerCount, minutes, notPlayedInDays, LocalDate.now());
    }

    /**
     * Creates a command like {@link #FilterCommand(int, int, int)}, treating {@code today} as today's date,
     * so that tests can check which games are hidden with a fixed date.
     */
    FilterCommand(int playerCount, int minutes, int notPlayedInDays, LocalDate today) {
        assert playerCount >= 1 : "Player count should be at least 1";
        assert minutes >= NO_TIME_LIMIT : "Time available should not be negative";
        assert notPlayedInDays >= NO_DAYS_LIMIT : "Number of days should not be negative";
        assert today != null : "Today's date should not be null";
        this.playerCount = playerCount;
        this.minutes = minutes;
        this.notPlayedInDays = notPlayedInDays;
        this.today = today;
    }
    //@@author yy261

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        boolean hasTimeLimit = minutes != NO_TIME_LIMIT;
        ArrayList<Game> matches = hasTimeLimit
                ? games.filterAvailableByPlayersAndTime(playerCount, minutes)
                : games.filterAvailableByPlayers(playerCount);
        String criteria = playerCount + " players" + (hasTimeLimit ? " in " + minutes + " minutes or less" : "");
        //@@author Xandurs
        boolean hasDaysLimit = notPlayedInDays != NO_DAYS_LIMIT;
        int recentlyPlayedCount = hasDaysLimit ? removeRecentlyPlayed(matches) : 0;
        if (hasDaysLimit) {
            criteria += " not played in the last " + notPlayedInDays + " day(s)";
        }
        //@@author yy261

        StringBuilder message = new StringBuilder();
        if (matches.isEmpty()) {
            message.append("No games fit ").append(criteria).append(".");
        } else {
            // Results are not numbered: 'delete INDEX' uses positions from 'list', not from this filtered view.
            message.append("Games that fit ").append(criteria).append(":");
            for (Game game : matches) {
                message.append(System.lineSeparator()).append("  ").append(game);
            }
        }
        if (hasTimeLimit) {
            appendUntimedNote(message, games);
        }
        //@@author Xandurs
        if (recentlyPlayedCount > 0) {
            // Worded like the other notes, e.g. "(2 game(s) for 4 players were left out because ...)".
            message.append(System.lineSeparator())
                    .append("(").append(recentlyPlayedCount)
                    .append(" game(s) for ").append(playerCount)
                    .append(" players were left out because they were played in the last ")
                    .append(notPlayedInDays).append(" day(s).)");
        }
        message.append(GameList.describeLeftOut(findLentNotRecentlyPlayed(games, hasTimeLimit), "shown"));
        //@@author yy261
        ui.showMessage(message.toString());
    }

    //@@author Xandurs
    /**
     * Removes the games played within the last {@code notPlayedInDays} days from {@code matches}.
     * Games that have never been played are kept, since they have not been played recently.
     *
     * @param matches Games that fit tonight's group; a new list from {@link GameList}, so it is safe to change.
     * @return How many games were removed, so the user can be told why they are missing.
     */
    private int removeRecentlyPlayed(ArrayList<Game> matches) {
        int countBefore = matches.size();
        matches.removeIf(this::isRecentlyPlayed);
        return countBefore - matches.size();
    }

    /**
     * Returns whether {@code game} is left out by {@code /notplayedin}, i.e. there is a day limit
     * and the game was played within it. Without a day limit, no game counts as recently played.
     */
    private boolean isRecentlyPlayed(Game game) {
        return notPlayedInDays != NO_DAYS_LIMIT && game.wasPlayedWithin(notPlayedInDays, today);
    }

    /**
     * Returns the lent games that would otherwise have been shown, for the note that names them.
     * Games that {@code /notplayedin} would hide anyway are not included, so the note never suggests
     * that a recently played game would have been shown if it were not lent out.
     *
     * @param hasTimeLimit Whether the user gave {@code /time}, so the lent games must also fit the time.
     */
    private List<Game> findLentNotRecentlyPlayed(GameList games, boolean hasTimeLimit) {
        ArrayList<Game> lentGames = hasTimeLimit
                ? games.findLentFitting(playerCount, minutes)
                : games.findLentFitting(playerCount);
        lentGames.removeIf(this::isRecentlyPlayed);
        return lentGames;
    }

    /**
     * Returns how many available games fit the player count but have no play time recorded, leaving out
     * games that {@code /notplayedin} would hide anyway, so the note about them only counts games
     * that a time limit really left out.
     */
    private int countUntimedNotRecentlyPlayed(GameList games) {
        int untimedCount = 0;
        for (Game game : games.filterAvailableByPlayers(playerCount)) {
            if (!game.hasPlayTime() && !isRecentlyPlayed(game)) {
                untimedCount++;
            }
        }
        return untimedCount;
    }

    //@@author yy261
    /**
     * Tells the user how many games fit the player count but were left out only because
     * their play time was never recorded, so they know why a game they expected is missing.
     */
    private void appendUntimedNote(StringBuilder message, GameList games) {
        //@@author Xandurs
        int untimedCount = countUntimedNotRecentlyPlayed(games);
        //@@author yy261
        if (untimedCount > 0) {
            message.append(System.lineSeparator())
                    .append("(").append(untimedCount)
                    .append(" game(s) for ").append(playerCount)
                    .append(" players were left out because they have no play time recorded.)");
        }
    }
}
