package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Records that a lent game is back on the shelf, then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class ReturnCommand extends Command {
    public static final String COMMAND_WORD = "return";
    public static final String USAGE = "return INDEX";

    private final int displayNumber;

    /**
     * Creates a command that marks the game shown with the given number in {@code list} as returned.
     * Whether that game exists, and whether it is lent, is only checked when the command runs.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     */
    public ReturnCommand(int displayNumber) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }
        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (!game.isLent()) {
            // Most likely the wrong INDEX, so point the user to the list of lent games.
            throw new GameVaultException(game.getTitle() + " is not lent to anyone."
                    + " Use '" + LentCommand.COMMAND_WORD + "' to see which games are lent out.");
        }

        String borrower = game.getBorrower();
        game.markReturned();
        storage.save(games);
        ui.showMessage("Returned by " + borrower + ": " + game.getTitle() + System.lineSeparator()
                + "It is back on your shelf and can be suggested by 'filter' and 'pick' again.");
    }
}
