package seedu.gamevault.command;

import java.util.ArrayList;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Shows the games that can be played by a given number of players and, optionally,
 * that fit in a given number of minutes.
 * The numbers are validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";
    public static final String USAGE = "filter COUNT [/time MINUTES]";

    /** Value of {@code minutes} when the user did not give a time limit. */
    private static final int NO_TIME_LIMIT = 0;

    private final int playerCount;
    private final int minutes;

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
        assert playerCount >= 1 : "Player count should be at least 1";
        assert minutes >= NO_TIME_LIMIT : "Time available should not be negative";
        this.playerCount = playerCount;
        this.minutes = minutes;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        boolean hasTimeLimit = minutes != NO_TIME_LIMIT;
        ArrayList<Game> matches = hasTimeLimit
                ? games.filterByPlayersAndTime(playerCount, minutes)
                : games.filterByPlayers(playerCount);
        String criteria = playerCount + " players" + (hasTimeLimit ? " in " + minutes + " minutes or less" : "");

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
        ui.showMessage(message.toString());
    }

    /**
     * Tells the user how many games fit the player count but were left out only because
     * their play time was never recorded, so they know why a game they expected is missing.
     */
    private void appendUntimedNote(StringBuilder message, GameList games) {
        int untimedCount = 0;
        for (Game game : games.filterByPlayers(playerCount)) {
            if (!game.hasPlayTime()) {
                untimedCount++;
            }
        }
        if (untimedCount > 0) {
            message.append(System.lineSeparator())
                    .append("(").append(untimedCount)
                    .append(" game(s) for ").append(playerCount)
                    .append(" players were left out because they have no play time recorded.)");
        }
    }
}
