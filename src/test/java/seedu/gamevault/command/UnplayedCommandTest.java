package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.game.UnavailableReason;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Tests for the unplayed command.
 */
class UnplayedCommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.add(new Game("Codenames", 4, 8));
    }

    private void executeUnplayed(GameList gamesToCheck) {
        new UnplayedCommand().execute(gamesToCheck, ui, new Storage("unused.txt"));
    }

    private void logOnce(int index) {
        games.get(index).addSession(new PlaySession(LocalDate.of(2026, 10, 30), 4));
    }

    @Test
    void findUnplayedNumbers_somePlayed_keepsNumbersFromList() {
        logOnce(1);

        assertEquals(List.of(1, 3), UnplayedCommand.findUnplayedNumbers(games.getGames()));
    }

    @Test
    void execute_somePlayed_showsOnlyUnplayedWithTheirListNumbers() {
        logOnce(0);

        executeUnplayed(games);

        String expected = UnplayedCommand.HEADER + System.lineSeparator()
                + "2. Chess (2 players)" + System.lineSeparator()
                + "3. Codenames (4-8 players)" + System.lineSeparator()
                + "2 of your 3 game(s) have never been played.";
        String printed = output.toString();
        assertTrue(printed.contains(expected));
        assertFalse(printed.contains("Catan"));
        assertFalse(printed.contains("cannot be played right now"));
    }

    @Test
    void execute_unplayedGamesLentOrUnavailable_listsThemWithNote() {
        games.get(1).lendTo("Alice");
        games.get(2).markUnavailable(UnavailableReason.MISSING);

        executeUnplayed(games);

        String printed = output.toString();
        assertTrue(printed.contains("2. Chess (2 players) (lent to Alice)"));
        assertTrue(printed.contains("3. Codenames (4-8 players) (unavailable: missing pieces)"));
        assertTrue(printed.contains(UnplayedCommand.getNotAvailableNote(2)));
    }

    @Test
    void getNotAvailableNote_oneOrMoreGames_usesMatchingWording() {
        assertEquals("(1 of these cannot be played right now, as it is lent out or marked unavailable.)",
                UnplayedCommand.getNotAvailableNote(1));
        assertEquals("(2 of these cannot be played right now, as they are lent out or marked unavailable.)",
                UnplayedCommand.getNotAvailableNote(2));
    }

    @Test
    void execute_nonePlayed_showsEveryGame() {
        executeUnplayed(games);

        assertTrue(output.toString().contains("3 of your 3 game(s) have never been played."));
    }

    @Test
    void execute_allPlayed_showsAllPlayedMessage() {
        logOnce(0);
        logOnce(1);
        logOnce(2);

        executeUnplayed(games);

        String printed = output.toString();
        assertTrue(printed.contains(UnplayedCommand.ALL_PLAYED_MESSAGE));
        assertFalse(printed.contains(UnplayedCommand.HEADER));
    }

    @Test
    void execute_emptyCollection_showsEmptyMessage() {
        executeUnplayed(new GameList());

        assertTrue(output.toString().contains(ListCommand.EMPTY_MESSAGE));
    }

    @Test
    void getCountLine_oneGame_usesSingularVerb() {
        assertEquals("1 of your 3 game(s) has never been played.", UnplayedCommand.getCountLine(1, 3));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new UnplayedCommand().isExit());
    }
}
