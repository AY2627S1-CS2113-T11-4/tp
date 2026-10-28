package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import seedu.gamevault.game.SortOrder;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Tests for the sort command.
 */
class SortCommandTest {
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
        games.add(new Game("Codenames", 4, 8));
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Love Letter", 2, 4, 20));
    }

    private List<String> titlesInOrder() {
        return games.getGames().stream().map(Game::getTitle).toList();
    }

    @Test
    void execute_byTitle_reordersCollection() throws GameVaultException {
        new SortCommand(SortOrder.TITLE).execute(games, ui, storage);

        assertEquals(List.of("Catan", "Codenames", "Love Letter"), titlesInOrder());
    }

    @Test
    void execute_byTime_showsGamesNumberedInNewOrder() throws GameVaultException {
        new SortCommand(SortOrder.TIME).execute(games, ui, storage);

        String expected = SortCommand.getHeader(SortOrder.TIME) + System.lineSeparator()
                + "1. Love Letter (2-4 players, 20 min)" + System.lineSeparator()
                + "2. Catan (3-4 players, 60 min)" + System.lineSeparator()
                + "3. Codenames (4-8 players)";
        assertTrue(output.toString().contains(expected));
    }

    @Test
    void execute_byPlayers_savesNewOrderToDataFile() throws GameVaultException, IOException {
        new SortCommand(SortOrder.PLAYERS).execute(games, ui, storage);

        assertEquals(List.of("Love Letter | 2 | 4 | 20", "Catan | 3 | 4 | 60", "Codenames | 4 | 8"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void execute_emptyCollection_showsEmptyMessageAndDoesNotSave() throws GameVaultException {
        new SortCommand(SortOrder.TITLE).execute(new GameList(), ui, storage);

        assertTrue(output.toString().contains(SortCommand.EMPTY_MESSAGE));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_sameOrderTwice_givesSameResult() throws GameVaultException {
        new SortCommand(SortOrder.TIME).execute(games, ui, storage);
        List<String> firstResult = titlesInOrder();
        new SortCommand(SortOrder.TIME).execute(games, ui, storage);

        assertEquals(firstResult, titlesInOrder());
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new SortCommand(SortOrder.TITLE).isExit());
    }
}
