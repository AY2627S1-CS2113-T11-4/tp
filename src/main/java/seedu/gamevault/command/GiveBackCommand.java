package seedu.gamevault.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Records that a borrowed game has been given back to its lender by removing it from the collection,
 * then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 *
 * <p>Only borrowed games (recorded with {@code borrow ... /due DATE}) can be given back. A gift recorded without a
 * due date is the user's to keep, so giving it back is refused; {@code delete} removes it if needed.</p>
 */
public class GiveBackCommand extends Command {
    public static final String COMMAND_WORD = "giveback";
    public static final String USAGE = "giveback INDEX";

    private static final Logger logger = Logger.getLogger(GiveBackCommand.class.getName());

    private final int displayNumber;

    /**
     * Creates a command that gives back the game shown with the given number in {@code list}.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     */
    public GiveBackCommand(int displayNumber) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        int index = displayNumber - 1; // The list shows games from 1, while GameList counts from 0.
        Game game = games.get(index);
        if (!game.hasLender()) {
            // Most likely the wrong INDEX, so report it rather than silently doing nothing.
            throw new GameVaultException(game.getTitle() + " is not borrowed from anyone.");
        }
        if (game.getDueBack().isEmpty()) {
            // Without a due date the game was recorded as a gift, so there is nothing to give back.
            throw new GameVaultException(game.getTitle() + " was a gift from " + game.getLender()
                    + ", so there is nothing to give back. Use 'delete " + displayNumber
                    + "' if you no longer have it.");
        }

        // The game is back with its owner, so it is no longer on the shelf for filter and pick to suggest.
        games.delete(index);
        storage.save(games);
        logger.log(Level.FINE, "Gave " + game.getTitle() + " back to " + game.getLender());
        ui.showMessage("Given back to " + game.getLender() + ": " + game.getTitle() + System.lineSeparator()
                + "It has been removed from your collection. You now have " + games.size() + " game(s).");
    }
}
