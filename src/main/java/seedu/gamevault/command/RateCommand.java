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
 * Records a thumbs up or thumbs down for a game after the group has played it,
 * then saves the updated collection to the data file.
 * Ratings make {@code pick} favour games the group enjoyed (see {@link Game#getPickWeight()}).
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * {@link UnrateCommand} undoes a rating given by mistake.
 */
public class RateCommand extends Command {
    public static final String COMMAND_WORD = "rate";
    public static final String USAGE = "rate INDEX up|down";

    private static final Logger logger = Logger.getLogger(RateCommand.class.getName());

    private final int displayNumber;
    private final Thumb thumb;

    /**
     * Creates a command that rates the game shown with the given number in {@code list}.
     * Whether that game exists is only checked when the command runs, because the collection may change
     * before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param thumb Whether to give a thumbs up or a thumbs down.
     */
    public RateCommand(int displayNumber, Thumb thumb) {
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
        if (!game.canAddRating(thumb)) {
            throw new GameVaultException(game.getTitle() + " already has the most thumbs " + thumb.getKeyword()
                    + " GameVault can count (" + game.getRatingCount(thumb) + ").");
        }

        game.addRating(thumb);
        storage.save(games);
        logger.log(Level.FINE, "Rated " + game.getTitle() + " " + thumb.getKeyword() + ", now "
                + game.describeRatings());
        ui.showMessage("Thumbs " + thumb.getKeyword() + " for " + game.getTitle() + "!" + System.lineSeparator()
                + describeNewRating(game));
    }

    /**
     * Returns the game's counts after a change, e.g. {@code Catan is now rated +5/-1 (thumbs up/thumbs down).}
     * Shared with {@link UnrateCommand}.
     */
    static String describeNewRating(Game game) {
        return game.getTitle() + " is now rated " + game.describeRatings() + " (thumbs up/thumbs down).";
    }
}
