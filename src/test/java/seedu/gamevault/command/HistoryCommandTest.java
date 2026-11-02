package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Tests for the history command.
 */
class HistoryCommandTest {
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
    void execute_severalSessions_showsThemOldestFirstWithSummary() throws GameVaultException {
        Game catan = games.get(0);
        catan.addSession(new PlaySession(LocalDate.of(2026, 10, 30), 4));
        catan.addSession(new PlaySession(LocalDate.of(2026, 6, 1), 3));

        new HistoryCommand(1).execute(games, ui, storage);

        String expected = HistoryCommand.getHeader(catan) + System.lineSeparator()
                + "  2026-06-01 (3 players)" + System.lineSeparator()
                + "  2026-10-30 (4 players)" + System.lineSeparator()
                + "Played 2 times in total, most recently on 2026-10-30.";
        assertTrue(output.toString().contains(expected));
    }

    @Test
    void execute_oneSession_saysPlayedOnce() throws GameVaultException {
        games.get(1).addSession(new PlaySession(LocalDate.of(2026, 9, 15), 2));

        new HistoryCommand(2).execute(games, ui, storage);

        assertTrue(output.toString().contains("Played once in total, most recently on 2026-09-15."));
    }

    @Test
    void execute_neverPlayed_saysSoAndHowToLog() throws GameVaultException {
        new HistoryCommand(1).execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("Catan has never been played."));
        assertTrue(printed.contains(LogCommand.USAGE));
    }

    @Test
    void execute_indexPastEndOfList_throwsNoSuchGame() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new HistoryCommand(3).execute(games, ui, storage));

        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), e.getMessage());
    }

    @Test
    void execute_anyGame_doesNotChangeOrSaveCollection() throws GameVaultException {
        games.get(0).addSession(new PlaySession(LocalDate.of(2026, 10, 30), 4));

        new HistoryCommand(1).execute(games, ui, storage);

        assertEquals(1, games.get(0).getPlayCount());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new HistoryCommand(1).isExit());
    }
}
