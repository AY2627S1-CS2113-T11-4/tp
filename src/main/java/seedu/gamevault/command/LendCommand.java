package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Borrower;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Records that a game has been lent to someone, then saves the updated collection to the data file.
 * While a game is lent out, {@code filter} and {@code pick} leave it out, so the host is not offered a game
 * that is not on the shelf. The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class LendCommand extends Command {
    public static final String COMMAND_WORD = "lend";
    public static final String USAGE = "lend INDEX /to NAME";

    private final int displayNumber;
    private final String borrower;

    /**
     * Creates a command that lends the game shown with the given number in {@code list} to the given person.
     * Whether that game exists, and whether it is already lent, is only checked when the command runs,
     * because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param borrower Name of the person borrowing the game; already normalised and checked by the parser.
     */
    public LendCommand(int displayNumber, String borrower) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert Borrower.isValid(borrower) : "Borrower name should be valid and normalised";
        this.displayNumber = displayNumber;
        this.borrower = borrower;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }
        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (game.isLent()) {
            // Lending a game that is already out is most likely a mistake, e.g. the wrong INDEX,
            // so the earlier borrower is kept and the user is told how to change it.
            throw new GameVaultException(game.getTitle() + " is already lent to " + game.getBorrower()
                    + ". Use 'return " + displayNumber + "' first if it has come back.");
        }

        game.lendTo(borrower);
        storage.save(games);
        ui.showMessage("Lent to " + borrower + ": " + game.getTitle() + System.lineSeparator()
                + "It will be left out of 'filter' and 'pick' until you 'return " + displayNumber + "'.");
    }
}
