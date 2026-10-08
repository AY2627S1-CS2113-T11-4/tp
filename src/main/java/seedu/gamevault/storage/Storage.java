package seedu.gamevault.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;

/**
 * Saves the game collection to, and loads it from, a text file on disk.
 * The file stores one game per line in the format {@code TITLE | MIN | MAX} (see {@link GameDecoder}).
 *
 * <p>Saving and creating the data folder are added in issue #11.</p>
 */
public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private final Path filePath;
    /** Descriptions of the lines skipped by the most recent {@link #load()}, e.g. "Line 3: ...". */
    private final List<String> skippedLines = new ArrayList<>();

    /**
     * Creates a Storage that uses the given data file.
     *
     * @param filePath Path to the data file, e.g. {@code data/games.txt}.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Returns the games stored in the data file.
     * Invalid lines are skipped instead of stopping the whole load; use {@link #getSkippedLines()}
     * afterwards to find out which ones. Blank lines are ignored. A missing file means there are no
     * games yet, so an empty list is returned.
     *
     * @return Games read from the valid lines of the file.
     * @throws GameVaultException If the file exists but cannot be read at all.
     */
    public ArrayList<Game> load() throws GameVaultException {
        skippedLines.clear();
        ArrayList<Game> games = new ArrayList<>();
        if (!Files.exists(filePath)) {
            logger.log(Level.FINE, "No data file at {0}; starting with an empty collection", filePath);
            return games;
        }

        List<String> lines = readAllLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                games.add(GameDecoder.decodeLine(line));
            } catch (GameVaultException e) {
                int lineNumber = i + 1; // Users count lines from 1, as text editors do.
                skippedLines.add("Line " + lineNumber + ": \"" + line.trim() + "\" (" + e.getMessage() + ")");
            }
        }

        if (!skippedLines.isEmpty()) {
            logger.log(Level.FINE, "Skipped {0} invalid line(s) in {1}", new Object[]{skippedLines.size(), filePath});
        }
        return games;
    }

    /**
     * Returns descriptions of the lines that the most recent {@link #load()} skipped because they were invalid.
     *
     * @return Read-only list; empty if every line was valid.
     */
    public List<String> getSkippedLines() {
        return Collections.unmodifiableList(skippedLines);
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

    private List<String> readAllLines() throws GameVaultException {
        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.log(Level.FINE, "Could not read data file " + filePath, e);
            throw new GameVaultException("Could not read the data file " + filePath + ".");
        }
    }
}
