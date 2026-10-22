package seedu.gamevault.command;

import java.util.ArrayList;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Tag;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Shows the games that have a given tag, e.g. every {@code party} game, optionally only those that also fit
 * a player count and/or a time limit, e.g. party games for 6 players that take 30 minutes or less.
 * This is the {@code /tag} form of the {@code filter} command; filtering by player count alone
 * is handled by {@link FilterCommand}.
 */
public class FilterTagCommand extends Command {
    public static final String USAGE = "filter [COUNT] [/time MINUTES] /tag TAG";

    /** Value of {@code playerCount} or {@code minutes} when the user did not give that condition. */
    private static final int NOT_GIVEN = 0;

    private final String tag;
    private final int playerCount;
    private final int minutes;

    /**
     * Creates a command that shows every game with the given tag.
     *
     * @param tag Tag to look for; already normalised and checked to be valid by the parser.
     */
    public FilterTagCommand(String tag) {
        this(tag, NOT_GIVEN, NOT_GIVEN);
    }

    /**
     * Creates a command that shows the games with the given tag that also fit the given conditions.
     *
     * @param tag Tag to look for; already normalised and checked to be valid by the parser.
     * @param playerCount Number of players (at least 1), or 0 to ignore player counts.
     * @param minutes Time available in minutes (at least 1), or 0 for no time limit.
     */
    public FilterTagCommand(String tag, int playerCount, int minutes) {
        assert Tag.isValid(tag) : "Tag should be valid and normalised";
        assert playerCount >= NOT_GIVEN && minutes >= NOT_GIVEN : "Conditions should not be negative";
        this.tag = tag;
        this.playerCount = playerCount;
        this.minutes = minutes;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ArrayList<Game> matches = new ArrayList<>();
        for (Game game : games.filterByTag(tag)) {
            if (fitsConditions(game)) {
                matches.add(game);
            }
        }

        String criteria = describeCriteria();
        if (matches.isEmpty()) {
            ui.showMessage("No games " + criteria + ".");
            return;
        }

        // Results are not numbered: commands such as 'tag INDEX TAG' use positions from 'list', not from this view.
        StringBuilder message = new StringBuilder("Games " + criteria + ":");
        for (Game game : matches) {
            message.append(System.lineSeparator()).append("  ").append(game);
        }
        ui.showMessage(message.toString());
    }

    /**
     * Returns whether a game with the tag also fits the player count and time limit, if they were given.
     * As in {@link FilterCommand}, a game with no play time recorded never fits a time limit.
     */
    private boolean fitsConditions(Game game) {
        boolean fitsPlayers = playerCount == NOT_GIVEN || game.fitsPlayerCount(playerCount);
        boolean fitsTime = minutes == NOT_GIVEN || game.fitsTime(minutes);
        return fitsPlayers && fitsTime;
    }

    /**
     * Returns the conditions in words, e.g. {@code tagged 'party' for 6 players in 30 minutes or less}.
     */
    private String describeCriteria() {
        String criteria = "tagged '" + tag + "'";
        if (playerCount != NOT_GIVEN) {
            criteria += " for " + playerCount + " players";
        }
        if (minutes != NOT_GIVEN) {
            criteria += " in " + minutes + " minutes or less";
        }
        return criteria;
    }
}
