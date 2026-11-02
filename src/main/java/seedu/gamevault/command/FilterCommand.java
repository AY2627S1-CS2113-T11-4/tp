package seedu.gamevault.command;

import java.time.LocalDate;
import java.util.ArrayList;

import seedu.gamevault.game.Difficulty;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Shows the games that can be played by a given number of players and, optionally,
 * that fit in a given number of minutes, are at a given difficulty, and were not played in the last given
 * number of days.
 * The numbers are validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";
    public static final String USAGE = "filter COUNT [/time MINUTES] [/diff LEVEL] [/notplayedin DAYS]";
    //@@author Xandurs
    /** Value of {@code notPlayedInDays} when the user did not ask to hide recently played games. */
    public static final int NO_DAYS_LIMIT = 0;
    //@@author yy261

    /** Value of {@code minutes} when the user did not give a time limit. */
    private static final int NO_TIME_LIMIT = GameList.NO_TIME_LIMIT;

    private final int playerCount;
    private final int minutes;
    /** Level the games must be at, or null for any level. */
    private final Difficulty difficulty;
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
        this(playerCount, minutes, null, NO_DAYS_LIMIT);
    }

    /**
     * Creates a command that shows the games fitting the given number of players, time limit and level.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes; already checked to be at least 1, or 0 for no limit.
     * @param difficulty Level the games must be at, or null for any level.
     */
    public FilterCommand(int playerCount, int minutes, Difficulty difficulty) {
        this(playerCount, minutes, difficulty, NO_DAYS_LIMIT);
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
        this(playerCount, minutes, null, notPlayedInDays);
    }

    //@@author yy261
    /**
     * Creates a command that shows the games fitting all the given conditions.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes; already checked to be at least 1, or 0 for no limit.
     * @param difficulty Level the games must be at, or null for any level.
     * @param notPlayedInDays Hide games played within this many days; at least 1, or {@link #NO_DAYS_LIMIT}.
     */
    public FilterCommand(int playerCount, int minutes, Difficulty difficulty, int notPlayedInDays) {
        this(playerCount, minutes, difficulty, notPlayedInDays, LocalDate.now());
    }

    //@@author Xandurs
    /**
     * Creates a command like {@link #FilterCommand(int, int, int)}, treating {@code today} as today's date,
     * so that tests can check which games are hidden with a fixed date.
     */
    FilterCommand(int playerCount, int minutes, int notPlayedInDays, LocalDate today) {
        this(playerCount, minutes, null, notPlayedInDays, today);
    }

    /**
     * Creates a command with all the conditions, treating {@code today} as today's date.
     */
    FilterCommand(int playerCount, int minutes, Difficulty difficulty, int notPlayedInDays, LocalDate today) {
        assert playerCount >= 1 : "Player count should be at least 1";
        assert minutes >= NO_TIME_LIMIT : "Time available should not be negative";
        assert notPlayedInDays >= NO_DAYS_LIMIT : "Number of days should not be negative";
        assert today != null : "Today's date should not be null";
        this.playerCount = playerCount;
        this.minutes = minutes;
        this.difficulty = difficulty;
        this.notPlayedInDays = notPlayedInDays;
        this.today = today;
    }
    //@@author yy261

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        boolean hasTimeLimit = minutes != NO_TIME_LIMIT;
        ArrayList<Game> matches = games.filterAvailable(playerCount, minutes, difficulty);
        String criteria = playerCount + " players" + (hasTimeLimit ? " in " + minutes + " minutes or less" : "")
                + (difficulty == null ? "" : " at " + difficulty.describe());
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
        if (difficulty != null) {
            message.append(games.describeNoDifficulty(playerCount, minutes, "left out"));
        }
        //@@author Xandurs
        if (recentlyPlayedCount > 0) {
            message.append(System.lineSeparator())
                    .append("(").append(recentlyPlayedCount)
                    .append(" game(s) were hidden because they were played in the last ")
                    .append(notPlayedInDays).append(" day(s).)");
        }
        //@@author yy261
        message.append(games.describeLeftOut(playerCount, minutes, difficulty, "shown"));
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
        matches.removeIf(game -> game.wasPlayedWithin(notPlayedInDays, today));
        return countBefore - matches.size();
    }

    //@@author yy261
    /**
     * Tells the user how many games fit the player count but were left out only because
     * their play time was never recorded, so they know why a game they expected is missing.
     */
    private void appendUntimedNote(StringBuilder message, GameList games) {
        int untimedCount = games.countWithoutPlayTime(playerCount, difficulty);
        if (untimedCount > 0) {
            message.append(System.lineSeparator())
                    .append("(").append(untimedCount)
                    .append(" game(s) for ").append(playerCount)
                    .append(" players were left out because they have no play time recorded.)");
        }
    }
}
