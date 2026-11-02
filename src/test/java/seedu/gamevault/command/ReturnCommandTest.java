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
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Tests for the return command.
 */
class ReturnCommandTest {
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
    void return_lentGame_putsItBackAndSaves() throws GameVaultException, IOException {
        games.get(0).lendTo("Alice");

        new ReturnCommand(1).execute(games, ui, storage);

        assertFalse(games.get(0).isLent());
        assertTrue(output.toString().contains("Returned by Alice: Catan"));
        assertEquals("Catan | 3 | 4 | 60", Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void return_gameNotLent_throwsExceptionPointingToLentout() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new ReturnCommand(2).execute(games, ui, storage));

        assertEquals("Codenames is not lent to anyone. Use 'lentout' to see which games are lent out.",
                e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void return_numberPastEndOfList_throwsException() {
        assertThrows(GameVaultException.class, () -> new ReturnCommand(9).execute(games, ui, storage));
    }

    @Test
    void lendThenReturn_gameIsSuggestedAgain() throws GameVaultException {
        new LendCommand(2, "Ben").execute(games, ui, storage);
        new ReturnCommand(2).execute(games, ui, storage);
        output.reset();

        new FilterCommand(8).execute(games, ui, storage);

        assertTrue(output.toString().contains("  Codenames (2-8 players)"));
        assertFalse(output.toString().contains("lent out"));
    }
}
