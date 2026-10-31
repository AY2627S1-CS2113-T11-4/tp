package seedu.gamevault.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author MohamedFarsat
/**
 * Records a thumbs up or thumbs down for a game after the group has played it,
 * then saves the updated collection to the data file.
 * Ratings make {@code pick} favour games the group enjoyed (see {@link Game#getPickWeight()}).
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class RateCommand extends Command {
    public static final String COMMAND_WORD = "rate";
    public static final String USAGE = "rate INDEX up|down";
    public static final String UP = "up";
    public static final String DOWN = "down";

    private static final Logger logger = Logger.getLogger(RateCommand.class.getName());

    private final int displayNumber;
    /** True for a thumbs up, false for a thumbs down. */
    private final boolean isThumbsUp;

    /**
     * Creates a command that rates the game shown with the given number in {@code list}.
     * Whether that game exists is only checked when the command runs, because the collection may change
     * before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param isThumbsUp True to give a thumbs up, false to give a thumbs down.
     */
    public RateCommand(int displayNumber, boolean isThumbsUp) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
        this.isThumbsUp = isThumbsUp;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        String direction = isThumbsUp ? UP : DOWN;
        // Only reachable if the data file was edited by hand, but adding 1 would wrap round to a negative count.
        int currentCount = isThumbsUp ? game.getThumbsUp() : game.getThumbsDown();
        if (currentCount == Integer.MAX_VALUE) {
            throw new GameVaultException(game.getTitle() + " already has the most thumbs " + direction
                    + " GameVault can count (" + currentCount + ").");
        }

        if (isThumbsUp) {
            game.addThumbsUp();
        } else {
            game.addThumbsDown();
        }
        storage.save(games);
        logger.log(Level.FINE, "Rated " + game.getTitle() + " " + direction + ", now " + game.describeRatings());
        ui.showMessage("Thumbs " + direction + " for " + game.getTitle() + "!" + System.lineSeparator()
                + game.getTitle() + " is now rated " + game.describeRatings() + " (thumbs up/thumbs down).");
    }
}
