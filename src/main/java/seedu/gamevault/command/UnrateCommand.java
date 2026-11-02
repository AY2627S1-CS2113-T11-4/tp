package seedu.gamevault.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Thumb;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author MohamedFarsat
/**
 * Removes one thumbs up or thumbs down from a game, e.g. to undo a {@code rate} given to the wrong game,
 * then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class UnrateCommand extends Command {
    public static final String COMMAND_WORD = "unrate";
    public static final String USAGE = "unrate INDEX up|down";

    private static final Logger logger = Logger.getLogger(UnrateCommand.class.getName());

    private final int displayNumber;
    private final Thumb thumb;

    /**
     * Creates a command that removes one rating from the game shown with the given number in {@code list}.
     * Whether that game exists, and whether it has a rating of that kind to remove, is only checked when the
     * command runs, because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param thumb Whether to remove a thumbs up or a thumbs down.
     */
    public UnrateCommand(int displayNumber, Thumb thumb) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert thumb != null : "Thumb should not be null";
        this.displayNumber = displayNumber;
        this.thumb = thumb;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (!game.canRemoveRating(thumb)) {
            throw new GameVaultException(game.getTitle() + " has no thumbs " + thumb.getKeyword()
                    + " to remove. It is rated " + game.describeRatings() + ".");
        }

        game.removeRating(thumb);
        storage.save(games);
        logger.log(Level.FINE, "Removed a thumbs " + thumb.getKeyword() + " from " + game.getTitle());
        ui.showMessage("Removed a thumbs " + thumb.getKeyword() + " from " + game.getTitle() + "."
                + System.lineSeparator() + RateCommand.describeNewRating(game));
    }
}
