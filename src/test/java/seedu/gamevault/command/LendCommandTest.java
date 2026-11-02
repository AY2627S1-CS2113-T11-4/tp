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

//@@author yy261
/**
 * Tests for the lend command.
 */
class LendCommandTest {
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
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));
    }

    @Test
    void lend_gameOnShelf_marksItLentAndSavesBorrower() throws GameVaultException, IOException {
        new LendCommand(1, "Alice").execute(games, ui, storage);

        assertTrue(games.get(0).isLent());
        assertEquals("Alice", games.get(0).getBorrower());
        assertTrue(output.toString().contains("Lent to Alice: Catan"));
        assertEquals(List.of("Catan | 3 | 4 | 60 |  | lent=Alice", "Codenames | 2 | 8", "Love Letter | 2 | 4 | 20"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void lend_gameAlreadyLent_throwsExceptionNamingBorrowerAndKeepsThem() {
        games.get(1).lendTo("Ben");

        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new LendCommand(2, "Alice").execute(games, ui, storage));

        assertEquals("Codenames is already lent to Ben. Use 'return 2' first if it has come back.", e.getMessage());
        assertEquals("Ben", games.get(1).getBorrower());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void lend_numberPastEndOfList_throwsExceptionAndChangesNothing() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new LendCommand(4, "Alice").execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("4"), e.getMessage());
        assertFalse(games.get(0).isLent() || games.get(1).isLent() || games.get(2).isLent());
    }
}
