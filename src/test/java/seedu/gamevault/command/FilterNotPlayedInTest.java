package seedu.gamevault.command;

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

//@@author Xandurs
/**
 * Tests for {@code filter COUNT /notplayedin DAYS}, which hides games played recently.
 */
class FilterNotPlayedInTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);

    private ByteArrayOutputStream output;
    private Ui ui;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        games = new GameList();
        games.add(gamePlayedOn("Catan", LocalDate.of(2026, 10, 30))); // 3 days ago
        games.add(gamePlayedOn("Azul", LocalDate.of(2026, 10, 1))); // 32 days ago
        games.add(new Game("Hive", 2, 4)); // never played
    }

    private static Game gamePlayedOn(String title, LocalDate date) {
        Game game = new Game(title, 2, 4);
        game.addSession(new PlaySession(date, 4));
        return game;
    }

    private String execute(int notPlayedInDays) {
        new FilterCommand(4, 0, notPlayedInDays, TODAY).execute(games, ui, new Storage("unused.txt"));
        return output.toString();
    }

    @Test
    void execute_notPlayedIn_hidesRecentlyPlayedGamesAndSaysHowMany() {
        String printed = execute(14);
        assertFalse(printed.contains("Catan"));
        assertTrue(printed.contains("Azul"));
        assertTrue(printed.contains("Hive"));
        assertTrue(printed.contains("1 game(s) were hidden because they were played in the last 14 day(s)."));
    }

    @Test
    void execute_noDaysLimit_showsEveryFittingGame() {
        String printed = execute(FilterCommand.NO_DAYS_LIMIT);
        assertTrue(printed.contains("Catan"));
        assertTrue(printed.contains("Azul"));
        assertTrue(printed.contains("Hive"));
        assertFalse(printed.contains("were hidden"));
    }

    @Test
    void execute_everyGamePlayedRecently_showsOnlyNeverPlayedGames() {
        String printed = execute(60);
        assertFalse(printed.contains("Catan"));
        assertFalse(printed.contains("Azul"));
        assertTrue(printed.contains("Hive"));
        assertTrue(printed.contains("2 game(s) were hidden"));
    }

    @Test
    void execute_notPlayedIn_criteriaMentionDays() {
        assertTrue(execute(14).contains("not played in the last 14 day(s)"));
    }
}
