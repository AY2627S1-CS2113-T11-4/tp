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

//@@author SwaggyDaddy67
/**
 * Tests for the delete command.
 */
class DeleteCommandTest {
    /** JUnit creates a fresh empty folder for each test and deletes it afterwards. */
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
    void execute_validNumber_deletesThatGameAndShowsIt() throws GameVaultException {
        new DeleteCommand(1).execute(games, ui, storage);

        assertEquals(1, games.size());
        assertEquals("Chess", games.getGames().get(0).getTitle());
        String printed = output.toString();
        assertTrue(printed.contains("Deleted: Catan (3-4 players)"));
        assertTrue(printed.contains("You now have 1 game(s) in your collection."));
    }

    @Test
    void execute_validNumber_savesRemainingGames() throws GameVaultException, IOException {
        new DeleteCommand(2).execute(games, ui, storage);

        assertEquals(List.of("Catan | 3 | 4"), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void execute_numberPastEndOfList_throwsExceptionAndKeepsEveryGame() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new DeleteCommand(3).execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), e.getMessage());
        assertEquals(2, games.size());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_emptyCollection_throwsException() {
        assertThrows(GameVaultException.class, () ->
                new DeleteCommand(1).execute(new GameList(), ui, storage));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new DeleteCommand(1).isExit());
    }
}
