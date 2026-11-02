package seedu.gamevault.command;

import java.time.LocalDate;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Borrower;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Records who lent or gave a game to the user, and optionally when it must be given back,
 * then saves the updated collection to the data file. For example, {@code borrow 2 /from Bob /due 2026-11-10}
 * records a game borrowed from Bob, and {@code borrow 2 /from Bob} a game Bob gave the user.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * Running it again replaces the earlier record, e.g. to extend a loan.
 *
 * <p>This is the opposite of {@code lend}, which records a game the user has lent out.</p>
 */
public class BorrowCommand extends Command {
    public static final String COMMAND_WORD = "borrow";
    public static final String USAGE = "borrow INDEX /from NAME [/due YYYY-MM-DD]";

    private final int displayNumber;
    private final String lender;
    /** Date the game must be given back, or null if it does not have to be given back. */
    private final LocalDate dueBack;

    /**
     * Creates a command that records who the game shown with the given number in {@code list} is from.
     * Whether that game exists is only checked when the command runs, because the collection may change
     * before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param lender Name of the person the game is from; already normalised and checked by the parser.
     * @param dueBack Date the game must be given back (checked by the parser not to be in the past),
     *     or null if it does not have to be given back.
     */
    public BorrowCommand(int displayNumber, String lender, LocalDate dueBack) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert Borrower.isValid(lender) : "Lender name should be valid and normalised";
        this.displayNumber = displayNumber;
        this.lender = lender;
        this.dueBack = dueBack;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        String oldRecord = game.hasLender() ? game.describeLender() : null;
        game.recordLender(lender, dueBack);
        if (game.describeLender().equals(oldRecord)) {
            // Nothing would change, so tell the user instead of rewriting the data file.
            throw new GameVaultException(game.getTitle() + " is already recorded as " + oldRecord + ".");
        }

        storage.save(games);
        String message = "Recorded: " + game;
        if (oldRecord != null) {
            message += System.lineSeparator() + "(Was: " + oldRecord + ".)";
        }
        if (dueBack != null) {
            message += System.lineSeparator() + "'list' will remind you if it is not given back by " + dueBack
                    + ". Use 'giveback " + displayNumber + "' once you have given it back.";
        }
        ui.showMessage(message);
    }
}
