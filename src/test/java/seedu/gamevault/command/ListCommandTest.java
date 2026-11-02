package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Tests for the list command.
 */
class ListCommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    @Test
    void execute_emptyCollection_showsEmptyMessage() {
        new ListCommand().execute(new GameList(), ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains(ListCommand.EMPTY_MESSAGE));
        assertFalse(printed.contains(ListCommand.LIST_HEADER));
    }

    @Test
    void execute_someGames_showsEachGameNumberedFromOneInOrder() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));

        new ListCommand().execute(games, ui, new Storage("unused.txt"));

        String expected = ListCommand.LIST_HEADER + System.lineSeparator()
                + "1. Catan (3-4 players) - never played" + System.lineSeparator()
                + "2. Chess (2 players) - never played";
        String printed = output.toString();
        assertTrue(printed.contains(expected));
        assertFalse(printed.contains(ListCommand.EMPTY_MESSAGE));
    }

    @Test
    void execute_someGames_leavesCollectionUnchanged() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));

        new ListCommand().execute(games, ui, new Storage("unused.txt"));

        assertEquals(1, games.size());
    }
    //@@author Xandurs
    @Test
    void execute_playedGames_showsHowLongAgoEachWasLastPlayed() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4);
        catan.addSession(new PlaySession(LocalDate.of(2026, 10, 30), 4));
        Game chess = new Game("Chess", 2, 2);
        chess.addSession(new PlaySession(LocalDate.of(2026, 11, 1), 2));
        games.add(catan);
        games.add(chess);
        games.add(new Game("Hive", 2, 2));

        new ListCommand(LocalDate.of(2026, 11, 2)).execute(games, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains("1. Catan (3-4 players) - last played 3 days ago"));
        assertTrue(printed.contains("2. Chess (2 players) - last played 1 day ago"));
        assertTrue(printed.contains("3. Hive (2 players) - never played"));
    }

    //@@author SwaggyDaddy67
    @Test
    void isExit_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
