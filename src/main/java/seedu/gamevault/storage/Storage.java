package seedu.gamevault.storage;

import java.util.ArrayList;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;

/**
 * Saves the game collection to, and loads it from, a text file on disk.
 *
 * <p>Placeholder so the command loop compiles. Real file reading and writing is added in
 * issue #11; the method signatures here already match the class diagram.</p>
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a Storage that uses the given data file.
     *
     * @param filePath Path to the data file, e.g. {@code data/games.txt}.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the games stored in the data file.
     *
     * @return Games read from the file (currently always empty).
     * @throws GameVaultException If the file cannot be read.
     */
    public ArrayList<Game> load() throws GameVaultException {
        return new ArrayList<>();
    }

    /**
     * Writes all games in the list to the data file.
     *
     * @param games Games to save.
     * @throws GameVaultException If the file cannot be written.
     */
    public void save(GameList games) throws GameVaultException {
        // Implemented in issue #11.
    }
}
