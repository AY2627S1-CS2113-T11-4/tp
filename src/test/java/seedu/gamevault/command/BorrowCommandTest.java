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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Tests for the borrow and giveback commands, and the overdue reminder in list, which share the same setup.
 */
class BorrowCommandTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);
    private static final LocalDate DUE = LocalDate.of(2026, 11, 10);

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
        games.add(new Game("Hive", 2, 2));
    }

    @Test
    void borrow_withDueDate_recordsLenderAndSavesToDataFile() throws GameVaultException, IOException {
        new BorrowCommand(2, "Bob", DUE).execute(games, ui, storage);

        assertEquals("Bob", games.get(1).getLender());
        assertEquals(Optional.of(DUE), games.get(1).getDueBack());
        assertTrue(output.toString().contains("Recorded: Hive (2 players) (borrowed from Bob, due 2026-11-10)"));
        assertTrue(output.toString().contains("Use 'giveback 2' once you have given it back."));
        assertEquals(List.of("Catan | 3 | 4 | 60", "Hive | 2 | 2 |  |  | from=Bob | due=2026-11-10"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void borrow_withoutDueDate_recordsGift() throws GameVaultException {
        new BorrowCommand(1, "Mary Tan", null).execute(games, ui, storage);

        assertEquals(Optional.empty(), games.get(0).getDueBack());
        assertTrue(output.toString().contains("Recorded: Catan (3-4 players, 60 min) (from Mary Tan)"));
        assertFalse(output.toString().contains("giveback"));
    }

    @Test
    void borrow_newDueDate_replacesRecordAndReportsOldOne() throws GameVaultException {
        games.get(1).recordLender("Bob", DUE);

        new BorrowCommand(2, "Bob", DUE.plusDays(7)).execute(games, ui, storage);

        assertEquals(Optional.of(DUE.plusDays(7)), games.get(1).getDueBack());
        assertTrue(output.toString().contains("(Was: borrowed from Bob, due 2026-11-10.)"));
    }

    @Test
    void borrow_sameRecord_throwsExceptionAndDoesNotSave() {
        games.get(1).recordLender("Bob", DUE);

        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new BorrowCommand(2, "Bob", DUE).execute(games, ui, storage));

        assertEquals("Hive is already recorded as borrowed from Bob, due 2026-11-10.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void borrow_indexBeyondList_throwsExceptionAndChangesNothing() {
        assertThrows(GameVaultException.class, () -> new BorrowCommand(3, "Bob", null).execute(games, ui, storage));
        assertFalse(games.get(0).hasLender());
        assertFalse(games.get(1).hasLender());
    }

    @Test
    void giveBack_borrowedGame_clearsLenderButKeepsGame() throws GameVaultException, IOException {
        games.get(1).recordLender("Bob", DUE);

        new GiveBackCommand(2).execute(games, ui, storage);

        assertFalse(games.get(1).hasLender());
        assertEquals(Optional.empty(), games.get(1).getDueBack());
        assertEquals(2, games.size());
        assertTrue(output.toString().contains("Given back to Bob: Hive"));
        assertEquals(List.of("Catan | 3 | 4 | 60", "Hive | 2 | 2"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void giveBack_gameNotFromAnyone_throwsExceptionAndDoesNotSave() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new GiveBackCommand(1).execute(games, ui, storage));

        assertEquals("Catan is not recorded as being from anyone.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void list_overdueGame_remindsUserAtEnd() {
        games.get(1).recordLender("Bob", LocalDate.of(2026, 11, 1));
        games.get(0).recordLender("Alice", TODAY);

        new ListCommand(TODAY).execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("2. Hive (2 players) (borrowed from Bob, due 2026-11-01)"));
        assertTrue(printed.contains(ListCommand.OVERDUE_HEADER + System.lineSeparator()
                + "2. Hive - give back to Bob (was due 2026-11-01)"));
        // A game due back today is not overdue yet.
        assertFalse(printed.contains("Catan - give back"));
    }

    @Test
    void list_nothingOverdue_noReminder() {
        games.get(1).recordLender("Bob", DUE);

        new ListCommand(TODAY).execute(games, ui, storage);

        assertFalse(output.toString().contains(ListCommand.OVERDUE_HEADER));
    }

    @Test
    void edit_borrowedGame_keepsLenderAndDueDate() throws GameVaultException {
        games.get(1).recordLender("Bob", DUE);

        new EditCommand(2, "Hive Pocket", null, null).execute(games, ui, storage);

        assertEquals("Bob", games.get(1).getLender());
        assertEquals(Optional.of(DUE), games.get(1).getDueBack());
    }

    @Test
    void pick_borrowedGame_stillConsidered() {
        games.get(1).recordLender("Bob", DUE);

        new PickCommand(2).execute(games, ui, storage);

        // A borrowed game is on the user's shelf, so unlike a lent-out game it can still be suggested.
        assertTrue(output.toString().contains("How about: Hive (2 players) (borrowed from Bob, due 2026-11-10)"));
    }
}
