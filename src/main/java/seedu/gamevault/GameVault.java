package seedu.gamevault;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.command.Command;
import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.GameList;
import seedu.gamevault.parser.Parser;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Entry point of GameVault, a command-line app that helps game-night hosts manage their game collection.
 * It wires together the {@link Ui}, {@link Storage} and {@link GameList}, then runs the
 * read-parse-execute loop until the user exits.
 */
public class GameVault {
    private static final String DEFAULT_FILE_PATH = "data/games.txt";
    private static final Logger logger = Logger.getLogger(GameVault.class.getName());

    private final Storage storage;
    private final Ui ui;
    private GameList games;

    /**
     * Creates the app using standard input/output and the given data file.
     *
     * @param filePath Path to the data file where games are saved.
     */
    public GameVault(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates the app with a custom {@link Ui}, so tests can supply input and capture output.
     */
    GameVault(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        try {
            games = new GameList(storage.load());
        } catch (GameVaultException e) {
            // Don't stop the app just because the data could not be loaded; start empty instead.
            ui.showError(e.getMessage() + " Starting with an empty collection.");
            games = new GameList();
        }
    }

    /**
     * Runs the main loop: reads a command, executes it, and repeats until an exit command is given.
     * Errors from a single command are shown to the user and the loop continues.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            try {
                Command command = Parser.parse(input);
                command.execute(games, ui, storage);
                isExit = command.isExit();
            } catch (GameVaultException e) {
                logger.log(Level.FINE, "Command failed: {0}", e.getMessage());
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Starts GameVault.
     */
    public static void main(String[] args) {
        new GameVault(DEFAULT_FILE_PATH).run();
    }
}
