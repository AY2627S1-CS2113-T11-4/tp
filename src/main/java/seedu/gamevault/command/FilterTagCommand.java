package seedu.gamevault.command;

import java.util.ArrayList;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Tag;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Shows the games that have a given tag, e.g. every {@code party} game.
 * This is the {@code filter /tag TAG} form of the {@code filter} command; filtering by player count
 * is handled by {@link FilterCommand}.
 */
public class FilterTagCommand extends Command {
    public static final String USAGE = "filter /tag TAG";

    private final String tag;

    /**
     * Creates a command that shows the games with the given tag.
     *
     * @param tag Tag to look for; already normalised and checked to be valid by the parser.
     */
    public FilterTagCommand(String tag) {
        assert Tag.isValid(tag) : "Tag should be valid and normalised";
        this.tag = tag;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ArrayList<Game> matches = games.filterByTag(tag);
        if (matches.isEmpty()) {
            ui.showMessage("No games are tagged '" + tag + "'.");
            return;
        }

        // Results are not numbered: commands such as 'tag INDEX TAG' use positions from 'list', not from this view.
        StringBuilder message = new StringBuilder("Games tagged '" + tag + "':");
        for (Game game : matches) {
            message.append(System.lineSeparator()).append("  ").append(game);
        }
        ui.showMessage(message.toString());
    }
}
