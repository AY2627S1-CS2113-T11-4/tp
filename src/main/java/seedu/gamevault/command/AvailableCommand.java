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
 * Marks a game that was marked unavailable as available again, then saves the updated collection
 * to the data file. The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class AvailableCommand extends Command {
    public static final String COMMAND_WORD = "available";
    public static final String USAGE = "available INDEX";

    private static final Logger logger = Logger.getLogger(AvailableCommand.class.getName());

    private final int displayNumber;

    /**
     * Creates a command that makes the game shown with the given number in {@code list} available again.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     */
    public AvailableCommand(int displayNumber) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (!game.isMarkedUnavailable()) {
            // Most likely the wrong INDEX, so report it rather than silently doing nothing.
            throw new GameVaultException(game.getTitle() + " is not marked unavailable.");
        }

        game.markAvailable();
        storage.save(games);
        logger.log(Level.FINE, "Marked " + game.getTitle() + " available again");
        ui.showMessage("Available again: " + game);
    }
}
