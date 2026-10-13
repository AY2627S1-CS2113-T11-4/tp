package seedu.gamevault.command;

import java.util.ArrayList;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Shows the games that can be played by a given number of players.
 * The count is validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";
    public static final String USAGE = "filter COUNT";

    private final int playerCount;

    /**
     * Creates a command that shows the games fitting the given number of players.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     */
    public FilterCommand(int playerCount) {
        assert playerCount >= 1 : "Player count should be at least 1";
        this.playerCount = playerCount;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ArrayList<Game> matches = games.filterByPlayers(playerCount);
        if (matches.isEmpty()) {
            ui.showMessage("No games fit " + playerCount + " players.");
            return;
        }

        // Results are not numbered: 'delete INDEX' uses positions from 'list', not from this filtered view.
        StringBuilder message = new StringBuilder("Games that fit " + playerCount + " players:");
        for (Game game : matches) {
            message.append(System.lineSeparator()).append("  ").append(game);
        }
        ui.showMessage(message.toString());
    }
}
