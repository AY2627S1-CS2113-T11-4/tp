package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Tag;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Removes a tag from a game, then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class UntagCommand extends Command {
    public static final String COMMAND_WORD = "untag";
    public static final String USAGE = "untag INDEX TAG";

    private final int displayNumber;
    private final String tag;

    /**
     * Creates a command that removes a tag from the game shown with the given number in {@code list}.
     * Whether that game exists, and whether it has the tag, is only checked when the command runs.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param tag Tag to remove; already normalised and checked to be valid by the parser.
     */
    public UntagCommand(int displayNumber, String tag) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert Tag.isValid(tag) : "Tag should be valid and normalised";
        this.displayNumber = displayNumber;
        this.tag = tag;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        if (!game.hasTag(tag)) {
            throw new GameVaultException(game.getTitle() + " does not have the tag '" + tag + "'.");
        }
        game.removeTag(tag);
        storage.save(games);
        ui.showMessage("Removed tag '" + tag + "': " + game);
    }
}
