package seedu.gamevault.command;

import java.util.ArrayList;
import java.util.Random;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Picks one random game that can be played by a given number of players, optionally within a time limit,
 * so the group does not have to spend time deciding what to play.
 * The arguments are validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class PickCommand extends Command {
    public static final String COMMAND_WORD = "pick";
    public static final String USAGE = "pick COUNT [/time MINUTES]";

    /** Value of {@code minutes} when the user did not give a time limit. */
    private static final int NO_TIME_LIMIT = 0;

    private final int playerCount;
    private final int minutes;
    /** Source of randomness; passed in so tests can control which game is picked. */
    private final Random random;

    /**
     * Creates a command that picks a random game fitting the given number of players, however long it takes.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     */
    public PickCommand(int playerCount) {
        this(playerCount, NO_TIME_LIMIT);
    }

    /**
     * Creates a command that picks a random game fitting the given number of players
     * that takes at most the given number of minutes.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes; already checked to be at least 1, or 0 for no limit.
     */
    public PickCommand(int playerCount, int minutes) {
        this(playerCount, minutes, new Random());
    }

    /**
     * Creates a command that uses the given {@link Random}, so tests can make the pick predictable.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param minutes Time available in minutes, or 0 for no limit.
     * @param random Source of randomness used to choose the game.
     */
    PickCommand(int playerCount, int minutes, Random random) {
        assert playerCount >= 1 : "Player count should be at least 1";
        assert minutes >= NO_TIME_LIMIT : "Time available should not be negative";
        assert random != null : "Random should not be null";
        this.playerCount = playerCount;
        this.minutes = minutes;
        this.random = random;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        boolean hasTimeLimit = minutes != NO_TIME_LIMIT;
        ArrayList<Game> matches = hasTimeLimit
                ? games.filterByPlayersAndTime(playerCount, minutes)
                : games.filterByPlayers(playerCount);
        String criteria = describeCriteria(playerCount, minutes);

        StringBuilder message = new StringBuilder();
        if (matches.isEmpty()) {
            message.append(getNoMatchMessage(criteria));
        } else {
            Game pickedGame = matches.get(random.nextInt(matches.size()));
            message.append("How about: ").append(pickedGame).append(System.lineSeparator())
                    .append("(picked from ").append(matches.size()).append(" game(s) that fit ")
                    .append(criteria).append(")");
        }

        // Like filter, explain games left out only because they have no play time recorded.
        int untimedCount = hasTimeLimit ? games.countWithoutPlayTime(playerCount) : 0;
        if (untimedCount > 0) {
            message.append(System.lineSeparator())
                    .append("(").append(untimedCount).append(" game(s) for ").append(playerCount)
                    .append(" players were not considered because they have no play time recorded.)");
        }
        ui.showMessage(message.toString());
    }

    /**
     * Returns a description of what the picked game must fit, e.g. {@code 4 players in 45 minutes or less}.
     *
     * @param minutes Time available in minutes, or 0 for no limit.
     */
    static String describeCriteria(int playerCount, int minutes) {
        String criteria = playerCount + " players";
        return minutes == NO_TIME_LIMIT ? criteria : criteria + " in " + minutes + " minutes or less";
    }

    static String getNoMatchMessage(String criteria) {
        return "No games fit " + criteria + ", so there is nothing to pick from.";
    }
}
