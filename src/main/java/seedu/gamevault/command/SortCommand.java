package seedu.gamevault.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.SortOrder;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Reorders the whole collection by title, player count or play time, saves it, and shows the result.
 * Because the collection itself is reordered, the numbers shown by {@code list} follow the new order,
 * so commands such as {@code delete} and {@code edit} use the numbers shown after sorting.
 * The order is validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String USAGE = "sort title|players|time";

    static final String EMPTY_MESSAGE = "No games in your collection to sort yet.";

    private static final Logger logger = Logger.getLogger(SortCommand.class.getName());

    private final SortOrder order;

    /**
     * Creates a command that sorts the collection in the given order.
     *
     * @param order Order to sort the games in.
     */
    public SortCommand(SortOrder order) {
        assert order != null : "Sort order should not be null";
        this.order = order;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        assert games != null : "Game list to sort should not be null";

        if (games.size() == 0) {
            ui.showMessage(EMPTY_MESSAGE);
            return;
        }

        games.sort(order.getComparator());
        storage.save(games);
        logger.log(Level.FINE, "Sorted {0} game(s) by {1}", new Object[]{games.size(), order.getKeyword()});

        ui.showMessage(getHeader(order) + ListCommand.formatNumberedList(games.getGames()));
    }

    /**
     * Returns the line shown above the sorted games, e.g. {@code Sorted your collection by title (A to Z):}.
     *
     * @param order The order the games were sorted in.
     * @return The header line.
     */
    static String getHeader(SortOrder order) {
        return "Sorted your collection by " + order.getDescription() + ":";
    }
}
