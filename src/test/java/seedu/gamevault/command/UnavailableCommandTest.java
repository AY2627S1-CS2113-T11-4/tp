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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.UnavailableReason;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Tests for the unavailable and available commands, which share the same setup.
 */
class UnavailableCommandTest {
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
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 4, 8));
    }

    @Test
    void unavailable_availableGame_marksItAndSavesToDataFile() throws GameVaultException, IOException {
        new UnavailableCommand(1, UnavailableReason.MISSING).execute(games, ui, storage);

        assertEquals(Optional.of(UnavailableReason.MISSING), games.get(0).getUnavailableReason());
        assertTrue(output.toString().contains(
                "Marked unavailable: Catan (3-4 players, 60 min) (unavailable: missing pieces)"));
        assertTrue(output.toString().contains("Use 'available 1' once it can be played again."));
        assertEquals(List.of("Catan | 3 | 4 | 60 |  | unavailable=missing", "Codenames | 4 | 8"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void unavailable_differentReason_replacesReasonAndReportsOldOne() throws GameVaultException {
        games.get(1).markUnavailable(UnavailableReason.MISSING);

        new UnavailableCommand(2, UnavailableReason.DAMAGED).execute(games, ui, storage);

        assertEquals(Optional.of(UnavailableReason.DAMAGED), games.get(1).getUnavailableReason());
        assertTrue(output.toString().contains("(Was: missing pieces.)"));
    }

    @Test
    void unavailable_sameReason_throwsExceptionAndDoesNotSave() {
        games.get(0).markUnavailable(UnavailableReason.DAMAGED);

        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new UnavailableCommand(1, UnavailableReason.DAMAGED).execute(games, ui, storage));

        assertEquals("Catan is already marked unavailable (damaged).", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void unavailable_indexBeyondList_throwsExceptionAndChangesNothing() {
        assertThrows(GameVaultException.class, () ->
                new UnavailableCommand(3, UnavailableReason.MISSING).execute(games, ui, storage));
        assertFalse(games.get(0).isMarkedUnavailable());
        assertFalse(games.get(1).isMarkedUnavailable());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void available_unavailableGame_clearsReasonAndSaves() throws GameVaultException, IOException {
        games.get(0).markUnavailable(UnavailableReason.MISSING);

        new AvailableCommand(1).execute(games, ui, storage);

        assertFalse(games.get(0).isMarkedUnavailable());
        assertTrue(output.toString().contains("Available again: Catan (3-4 players, 60 min)"));
        assertEquals(List.of("Catan | 3 | 4 | 60", "Codenames | 4 | 8"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void available_gameNotMarkedUnavailable_throwsExceptionAndDoesNotSave() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new AvailableCommand(2).execute(games, ui, storage));

        assertEquals("Codenames is not marked unavailable.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void available_indexBeyondList_throwsException() {
        assertThrows(GameVaultException.class, () -> new AvailableCommand(5).execute(games, ui, storage));
    }

    @Test
    void list_unavailableGame_showsReason() {
        games.get(1).markUnavailable(UnavailableReason.DAMAGED);

        new ListCommand().execute(games, ui, storage);

        assertTrue(output.toString().contains("1. Catan (3-4 players, 60 min)" + System.lineSeparator()));
        assertTrue(output.toString().contains("2. Codenames (4-8 players) (unavailable: damaged)"));
    }

    @Test
    void edit_unavailableGame_keepsReason() throws GameVaultException {
        games.get(0).markUnavailable(UnavailableReason.MISSING);

        new EditCommand(1, "Catan: Seafarers", null, null).execute(games, ui, storage);

        assertEquals("Catan: Seafarers", games.get(0).getTitle());
        assertEquals(Optional.of(UnavailableReason.MISSING), games.get(0).getUnavailableReason());
    }

    @Test
    void filter_unavailableGame_leftOut() {
        games.get(0).markUnavailable(UnavailableReason.DAMAGED);

        new FilterCommand(4).execute(games, ui, storage);

        assertFalse(output.toString().contains("Catan"));
        assertTrue(output.toString().contains("Codenames (4-8 players)"));
    }

    @Test
    void pick_onlyOtherGameUnavailable_alwaysPicksAvailableGame() {
        games.get(1).markUnavailable(UnavailableReason.MISSING);

        new PickCommand(4).execute(games, ui, storage);

        assertTrue(output.toString().contains("How about: Catan (3-4 players, 60 min)"));
        assertTrue(output.toString().contains("(picked from 1 game(s) that fit 4 players)"));
    }

    @Test
    void available_afterMarkedUnavailable_filterShowsGameAgain() throws GameVaultException {
        games.get(0).markUnavailable(UnavailableReason.MISSING);
        new AvailableCommand(1).execute(games, ui, storage);
        output.reset();

        new FilterCommand(4).execute(games, ui, storage);

        assertTrue(output.toString().contains("Catan (3-4 players, 60 min)"));
    }
}
