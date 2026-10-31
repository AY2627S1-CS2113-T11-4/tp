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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author Xandurs
/**
 * Tests for the log command.
 */
class LogCommandTest {
    private static final LocalDate PLAYED_ON = LocalDate.of(2026, 10, 30);

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
    void execute_validSession_addsSessionAndReportsPlayCount() throws GameVaultException {
        new LogCommand(1, PLAYED_ON, 4).execute(games, ui, storage);

        assertEquals(List.of(new PlaySession(PLAYED_ON, 4)), games.get(0).getSessions());
        assertTrue(output.toString().contains("Logged: Catan played on 2026-10-30 (4 players)"));
        assertTrue(output.toString().contains("Catan has now been played once."));
    }

    @Test
    void execute_secondSession_reportsTimesInPlural() throws GameVaultException {
        new LogCommand(1, PLAYED_ON.minusDays(7), 3).execute(games, ui, storage);
        new LogCommand(1, PLAYED_ON, 4).execute(games, ui, storage);

        assertEquals(2, games.get(0).getPlayCount());
        assertEquals(PLAYED_ON, games.get(0).getLastPlayed().get());
        assertTrue(output.toString().contains("Catan has now been played 2 times."));
    }

    @Test
    void execute_validSession_savesSessionToDataFile() throws GameVaultException, IOException {
        new LogCommand(2, PLAYED_ON, 2).execute(games, ui, storage);

        List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        assertEquals(List.of("Catan | 3 | 4", "Chess | 2 | 2 |  |  | sessions=2026-10-30@2"), lines);
    }

    @Test
    void execute_numberPastEndOfList_throwsExceptionAndSavesNothing() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new LogCommand(3, PLAYED_ON, 2).execute(games, ui, storage));
        assertTrue(e.getMessage().contains("There is no game numbered 3"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void execute_countOutsidePlayerRange_throwsExceptionAndKeepsHistory() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new LogCommand(1, PLAYED_ON, 5).execute(games, ui, storage));
        assertTrue(e.getMessage().startsWith("Catan is for 3-4 players, so it cannot be logged with 5."));
        assertEquals(0, games.get(0).getPlayCount());

        e = assertThrows(GameVaultException.class,
                () -> new LogCommand(2, PLAYED_ON, 3).execute(games, ui, storage));
        assertTrue(e.getMessage().startsWith("Chess is for 2 players,"));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new LogCommand(1, PLAYED_ON, 4).isExit());
    }
}
