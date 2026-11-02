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
import java.util.List;

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
 * Tests for the rate command.
 */
class RateCommandTest {
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
    }

    @Test
    void execute_thumbsUp_addsOneAndShowsNewRating() throws GameVaultException {
        new RateCommand(1, Thumb.UP).execute(games, ui, storage);

        assertEquals(1, games.get(0).getThumbsUp());
        assertEquals(0, games.get(0).getThumbsDown());
        String printed = output.toString();
        assertTrue(printed.contains("Thumbs up for Catan!"));
        assertTrue(printed.contains("Catan is now rated +1/-0"));
    }

    @Test
    void execute_thumbsDown_addsOneAndShowsNewRating() throws GameVaultException {
        new RateCommand(2, Thumb.DOWN).execute(games, ui, storage);

        assertEquals(0, games.get(1).getThumbsUp());
        assertEquals(1, games.get(1).getThumbsDown());
        assertTrue(output.toString().contains("Chess is now rated +0/-1"));
    }

    @Test
    void execute_ratedSeveralTimes_countsAddUp() throws GameVaultException {
        new RateCommand(1, Thumb.UP).execute(games, ui, storage);
        new RateCommand(1, Thumb.UP).execute(games, ui, storage);
        new RateCommand(1, Thumb.DOWN).execute(games, ui, storage);

        assertEquals("+2/-1", games.get(0).describeRatings());
        assertFalse(games.get(1).isRated());
    }

    @Test
    void execute_validRating_savesRatingsToDataFile() throws GameVaultException, IOException {
        new RateCommand(1, Thumb.UP).execute(games, ui, storage);
        new RateCommand(1, Thumb.DOWN).execute(games, ui, storage);

        List<String> savedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        assertEquals("Catan | 3 | 4 |  |  | ratings=1/1", savedLines.get(0));
        assertEquals("Chess | 2 | 2", savedLines.get(1));
    }

    @Test
    void execute_noSuchGame_throwsExceptionAndChangesNothing() {
        GameVaultException exception = assertThrows(GameVaultException.class,
                () -> new RateCommand(3, Thumb.UP).execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), exception.getMessage());
        assertFalse(games.get(0).isRated());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_countAlreadyAtLimit_throwsExceptionInsteadOfOverflowing() {
        games.get(0).setRatings(Integer.MAX_VALUE, 0);

        GameVaultException exception = assertThrows(GameVaultException.class,
                () -> new RateCommand(1, Thumb.UP).execute(games, ui, storage));

        assertTrue(exception.getMessage().contains("most thumbs up"));
        assertEquals(Integer.MAX_VALUE, games.get(0).getThumbsUp());
    }

    @Test
    void execute_ratedGame_shownWithRatingInList() throws GameVaultException {
        new RateCommand(1, Thumb.UP).execute(games, ui, storage);
        output.reset();

        new ListCommand().execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("1. Catan (3-4 players) +1/-0"));
        assertTrue(printed.contains("2. Chess (2 players)"));
        assertFalse(printed.contains("Chess (2 players) +"));
    }

    @Test
    void execute_ratedGameThenEdited_keepsRatings() throws GameVaultException {
        games.get(0).setRatings(4, 2);

        new EditCommand(1, "Catan Deluxe", null, null).execute(games, ui, storage);

        assertEquals("Catan Deluxe", games.get(0).getTitle());
        assertEquals("+4/-2", games.get(0).describeRatings());
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new RateCommand(1, Thumb.UP).isExit());
    }
}
