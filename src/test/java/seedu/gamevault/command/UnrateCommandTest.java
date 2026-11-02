package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Thumb;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author MohamedFarsat
/**
 * Tests for the unrate command.
 */
class UnrateCommandTest {
    @TempDir
    Path tempDir;

    private ByteArrayOutputStream output;
    private Ui ui;
    private Path dataFile;
    private Storage storage;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        dataFile = tempDir.resolve("games.txt");
        storage = new Storage(dataFile.toString());
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.get(0).setRatings(2, 1);
    }

    @Test
    void execute_wrongGameRatedDown_undoesThatRatingOnly() throws GameVaultException {
        new RateCommand(2, Thumb.DOWN).execute(games, ui, storage); // Meant for game 1.
        new UnrateCommand(2, Thumb.DOWN).execute(games, ui, storage);
        new RateCommand(1, Thumb.DOWN).execute(games, ui, storage);

        assertFalse(games.get(1).isRated());
        assertEquals("+2/-2", games.get(0).describeRatings());
    }

    @Test
    void execute_ratedGame_removesOneAndShowsNewRating() throws GameVaultException {
        new UnrateCommand(1, Thumb.UP).execute(games, ui, storage);

        assertEquals("+1/-1", games.get(0).describeRatings());
        String printed = output.toString();
        assertTrue(printed.contains("Removed a thumbs up from Catan."));
        assertTrue(printed.contains("Catan is now rated +1/-1 (thumbs up/thumbs down)."));
    }

    @Test
    void execute_lastRatingRemoved_ratingsFieldLeftOutOfDataFile() throws GameVaultException, IOException {
        games.get(0).setRatings(0, 1);
        new UnrateCommand(1, Thumb.DOWN).execute(games, ui, storage);

        assertFalse(games.get(0).isRated());
        assertEquals("Catan | 3 | 4", Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void execute_noRatingOfThatKind_throwsExceptionAndChangesNothing() {
        GameVaultException exception = assertThrows(GameVaultException.class,
                () -> new UnrateCommand(2, Thumb.UP).execute(games, ui, storage));

        assertEquals("Chess has no thumbs up to remove. It is rated +0/-0.", exception.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_noSuchGame_throwsException() {
        GameVaultException exception = assertThrows(GameVaultException.class,
                () -> new UnrateCommand(3, Thumb.UP).execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), exception.getMessage());
        assertEquals("+2/-1", games.get(0).describeRatings());
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new UnrateCommand(1, Thumb.UP).isExit());
    }
}
