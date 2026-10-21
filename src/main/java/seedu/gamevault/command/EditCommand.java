package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Changes the title and/or player range of a game, then saves the updated collection to the data file.
 * Fields that are not given keep the game's existing values. The game keeps its position in the list.
 */
public class EditCommand extends Command {
    public static final String COMMAND_WORD = "edit";
    public static final String USAGE = "edit INDEX [/title TITLE] [/min MIN] [/max MAX]";

    private final int displayNumber;
    private final String newTitle;
    private final Integer newMinPlayers;
    private final Integer newMaxPlayers;

    /**
     * Creates a command that edits the game shown with the given number in {@code list}.
     * A null field means "keep the existing value". Whether the game exists, and whether
     * MAX is still at least MIN, is only checked when the command runs, because both
     * depend on the game's current values.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param newTitle New title, or null to keep the current one.
     * @param newMinPlayers New minimum players, or null to keep the current value.
     * @param newMaxPlayers New maximum players, or null to keep the current value.
     */
    public EditCommand(int displayNumber, String newTitle, Integer newMinPlayers, Integer newMaxPlayers) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert newTitle != null || newMinPlayers != null || newMaxPlayers != null
                : "At least one field should be edited";
        this.displayNumber = displayNumber;
        this.newTitle = newTitle;
        this.newMinPlayers = newMinPlayers;
        this.newMaxPlayers = newMaxPlayers;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        int index = displayNumber - 1; // The list shows games from 1, while GameList counts from 0.
        Game oldGame = games.get(index);
        String title = newTitle != null ? newTitle : oldGame.getTitle();
        int minPlayers = newMinPlayers != null ? newMinPlayers : oldGame.getMinPlayers();
        int maxPlayers = newMaxPlayers != null ? newMaxPlayers : oldGame.getMaxPlayers();

        if (maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") cannot be less than MIN (" + minPlayers
                    + "). The game is currently: " + oldGame);
        }

        Game editedGame = new Game(title, minPlayers, maxPlayers);
        games.set(index, editedGame);
        storage.save(games);
        ui.showMessage("Edited: " + oldGame + System.lineSeparator() + "Now: " + editedGame);
    }
}
