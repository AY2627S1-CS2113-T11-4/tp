package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Adds a game to the collection and saves the updated collection to the data file.
 * The game is built and validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class AddCommand extends Command {
    public static final String COMMAND_WORD = "add";
    public static final String USAGE = "add TITLE /min MIN /max MAX";

    private final Game game;

    /**
     * Creates a command that adds the given game.
     *
     * @param game The game to add; already checked to be valid.
     */
    public AddCommand(Game game) {
        assert game != null : "Game to add should not be null";
        this.game = game;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        games.add(game);
        storage.save(games);
        ui.showMessage("Added: " + game + System.lineSeparator()
                + "You now have " + games.size() + " game(s) in your collection.");
    }
}
