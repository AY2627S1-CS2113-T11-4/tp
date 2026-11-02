package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Forgets who lent or gave a game to the user, e.g. once a borrowed game has been given back,
 * then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 *
 * <p>The game stays in the collection, since the user may want to keep its play history and ratings;
 * {@code delete} removes it entirely.</p>
 */
public class GiveBackCommand extends Command {
    public static final String COMMAND_WORD = "giveback";
    public static final String USAGE = "giveback INDEX";

    private final int displayNumber;

    /**
     * Creates a command that forgets who the game shown with the given number in {@code list} is from.
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

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (!game.hasLender()) {
            // Most likely the wrong INDEX, so report it rather than silently doing nothing.
            throw new GameVaultException(game.getTitle() + " is not recorded as being from anyone.");
        }

        String lender = game.getLender();
        game.clearLender();
        storage.save(games);
        ui.showMessage("Given back to " + lender + ": " + game.getTitle() + System.lineSeparator()
                + "It stays in your collection. Use 'delete " + displayNumber + "' if you want to remove it.");
    }
}
