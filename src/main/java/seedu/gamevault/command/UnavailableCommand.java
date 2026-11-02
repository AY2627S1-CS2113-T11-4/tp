package seedu.gamevault.command;

import java.util.Optional;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.UnavailableReason;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Marks a game unavailable (e.g. pieces are missing), then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * Running it again with a different reason replaces the earlier reason, so the user does not have to
 * make the game available first.
 */
public class UnavailableCommand extends Command {
    public static final String COMMAND_WORD = "unavailable";
    public static final String USAGE = "unavailable INDEX /reason " + String.join("|", UnavailableReason.keywords());

    private final int displayNumber;
    private final UnavailableReason reason;

    /**
     * Creates a command that marks the game shown with the given number in {@code list} unavailable.
     * Whether that game exists is only checked when the command runs, because the collection may change
     * before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param reason Why the game cannot be played at the moment.
     */
    public UnavailableCommand(int displayNumber, UnavailableReason reason) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert reason != null : "Reason should not be null";
        this.displayNumber = displayNumber;
        this.reason = reason;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        Optional<UnavailableReason> oldReason = game.getUnavailableReason();
        if (oldReason.isPresent() && oldReason.get() == reason) {
            // Nothing would change, so tell the user instead of rewriting the data file.
            throw new GameVaultException(game.getTitle() + " is already marked unavailable ("
                    + reason.getDescription() + ").");
        }

        game.markUnavailable(reason);
        storage.save(games);
        String message = "Marked unavailable: " + game;
        if (oldReason.isPresent()) {
            message += System.lineSeparator() + "(Was: " + oldReason.get().getDescription() + ".)";
        }
        message += System.lineSeparator() + "Use 'available " + displayNumber + "' once it can be played again.";
        ui.showMessage(message);
    }
}
