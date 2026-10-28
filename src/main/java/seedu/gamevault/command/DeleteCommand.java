package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Deletes a game from the collection and saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 */
public class DeleteCommand extends Command {
    public static final String COMMAND_WORD = "delete";
    public static final String USAGE = "delete INDEX";

    private final int displayNumber;

    /**
     * Creates a command that deletes the game shown with the given number in {@code list}.
     * Whether a game with that number exists is only checked when the command runs,
     * because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     */
    public DeleteCommand(int displayNumber) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
    }

    /**
     * Returns the message shown when no game has the given number.
     *
     * @param displayNumber The number the user asked for.
     * @return Message telling the user how to find the right number.
     */
    public static String getNoSuchGameMessage(String displayNumber) {
        return "There is no game numbered " + displayNumber + ". Use 'list' to see the current numbering.";
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        int index = displayNumber - 1; // The list shows games from 1, while GameList counts from 0.
        Game deletedGame = games.delete(index);
        storage.save(games);
        ui.showMessage("Deleted: " + deletedGame + System.lineSeparator()
                + "You now have " + games.size() + " game(s) in your collection.");
    }
}
