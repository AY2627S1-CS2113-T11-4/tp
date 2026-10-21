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
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Tests for the edit command.
 */
class EditCommandTest {
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
    void execute_titleOnly_changesTitleAndKeepsPlayerRange() throws GameVaultException {
        new EditCommand(1, "Catan Deluxe", null, null).execute(games, ui, storage);

        Game edited = games.getGames().get(0);
        assertEquals("Catan Deluxe", edited.getTitle());
        assertEquals(3, edited.getMinPlayers());
        assertEquals(4, edited.getMaxPlayers());
        assertTrue(output.toString().contains("Now: Catan Deluxe (3-4 players)"));
    }

    @Test
    void execute_allFields_changesEverythingAndKeepsPosition() throws GameVaultException {
        new EditCommand(1, "Wingspan", 1, 5).execute(games, ui, storage);

        assertEquals(2, games.size());
        assertEquals("Wingspan (1-5 players)", games.getGames().get(0).toString());
        assertEquals("Chess", games.getGames().get(1).getTitle());
    }

    @Test
    void execute_validEdit_savesToDataFile() throws GameVaultException, IOException {
        new EditCommand(2, null, null, 3).execute(games, ui, storage);

        assertEquals(List.of("Catan | 3 | 4", "Chess | 2 | 3"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void execute_minAboveExistingMax_throwsExceptionAndKeepsGame() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new EditCommand(1, null, 5, null).execute(games, ui, storage));

        assertTrue(e.getMessage().startsWith("MAX (4) cannot be less than MIN (5)."));
        assertEquals("Catan (3-4 players)", games.getGames().get(0).toString());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_maxBelowExistingMin_throwsException() {
        assertThrows(GameVaultException.class, () ->
                new EditCommand(1, null, null, 2).execute(games, ui, storage));
    }

    @Test
    void execute_numberPastEndOfList_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new EditCommand(3, "X", null, null).execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), e.getMessage());
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new EditCommand(1, "X", null, null).isExit());
    }
}
