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
import java.util.Random;

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
 * Tests for the lend, return and lent commands, and for how lent games are left out of filter and pick.
 */
class LendCommandTest {
    /** Value passed to commands for "no time limit". */
    private static final int NO_LIMIT = 0;

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

    /**
     * Returns a Random that always gives {@code index}, so the test knows which game will be picked.
     */
    private static Random alwaysReturning(int index) {
        return new Random() {
            @Override
            public int nextInt(int bound) {
                return index;
            }
        };
    }

    @Test
    void lend_gameOnShelf_marksItLentAndSavesBorrower() throws GameVaultException, IOException {
        new LendCommand(1, "Alice").execute(games, ui, storage);

        assertTrue(games.get(0).isLent());
        assertEquals("Alice", games.get(0).getBorrower());
        assertTrue(output.toString().contains("Lent to Alice: Catan"));
        assertEquals(List.of("Catan | 3 | 4 | 60 |  | Alice", "Codenames | 2 | 8", "Love Letter | 2 | 4 | 20"),
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

    @Test
    void return_lentGame_putsItBackAndSaves() throws GameVaultException, IOException {
        games.get(0).lendTo("Alice");

        new ReturnCommand(1).execute(games, ui, storage);

        assertFalse(games.get(0).isLent());
        assertTrue(output.toString().contains("Returned by Alice: Catan"));
        assertEquals("Catan | 3 | 4 | 60", Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void return_gameNotLent_throwsExceptionPointingToLent() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> new ReturnCommand(2).execute(games, ui, storage));

        assertEquals("Codenames is not lent to anyone. Use 'lent' to see which games are lent out.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void return_numberPastEndOfList_throwsException() {
        assertThrows(GameVaultException.class, () -> new ReturnCommand(9).execute(games, ui, storage));
    }

    @Test
    void lent_someGamesLent_listsThemWithTheirListNumbers() {
        games.get(0).lendTo("Alice");
        games.get(2).lendTo("Mary Tan");

        new LentCommand().execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("2 game(s) are lent out:"));
        // Numbers match 'list', so they can be used with 'return' straight away.
        assertTrue(printed.contains("1. Catan - lent to Alice"));
        assertTrue(printed.contains("3. Love Letter - lent to Mary Tan"));
        assertFalse(printed.contains("Codenames"));
    }

    @Test
    void lent_noGamesLent_saysEverythingIsOnShelf() {
        new LentCommand().execute(games, ui, storage);

        assertTrue(output.toString().contains(LentCommand.NONE_LENT_MESSAGE));
    }

    @Test
    void filter_lentGameFits_leavesItOutAndSaysWhoHasIt() {
        games.get(1).lendTo("Ben");

        new FilterCommand(4).execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players:"));
        assertTrue(printed.contains("  Catan (3-4 players, 60 min)"));
        assertFalse(printed.contains("  Codenames"));
        assertTrue(printed.contains("(Not shown because they are lent out: Codenames (to Ben))"));
    }

    @Test
    void filter_withTimeLimit_mentionsOnlyLentGamesThatFitInTime() {
        games.get(0).lendTo("Alice");
        games.get(2).lendTo("Ben");

        new FilterCommand(4, 30).execute(games, ui, storage);

        String printed = output.toString();
        // Catan takes 60 minutes, so it would not have been shown anyway and is not mentioned.
        assertTrue(printed.contains("(Not shown because they are lent out: Love Letter (to Ben))"));
        assertFalse(printed.contains("Catan (to Alice)"));
        assertTrue(printed.contains("(1 game(s) for 4 players were left out because they have no play time"));
    }

    @Test
    void filter_noLentGames_addsNoNote() {
        new FilterCommand(4).execute(games, ui, storage);

        assertFalse(output.toString().contains("lent out"));
    }

    @Test
    void pick_onlyAvailableGamesArePicked() {
        games.get(0).lendTo("Alice");
        games.get(1).lendTo("Ben");

        new PickCommand(4, NO_LIMIT, alwaysReturning(0)).execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("How about: Love Letter (2-4 players, 20 min)"));
        assertTrue(printed.contains("(picked from 1 game(s) that fit 4 players)"));
        assertTrue(printed.contains(
                "(Not considered because they are lent out: Catan (to Alice), Codenames (to Ben))"));
    }

    @Test
    void pick_everyFittingGameLent_saysNothingToPickAndWhy() {
        games.get(0).lendTo("Alice");
        games.get(1).lendTo("Ben");
        games.get(2).lendTo("Cara");

        new PickCommand(3, NO_LIMIT, alwaysReturning(0)).execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("No games fit 3 players, so there is nothing to pick from."));
        assertTrue(printed.contains("Catan (to Alice), Codenames (to Ben), Love Letter (to Cara)"));
    }

    @Test
    void filterByTag_lentGameWithTag_leavesItOutAndSaysWhoHasIt() {
        games.get(0).addTag("strategy");
        games.get(0).lendTo("Alice");

        new FilterTagCommand("strategy").execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("No games tagged 'strategy'."));
        assertTrue(printed.contains("(Not shown because they are lent out: Catan (to Alice))"));
    }

    @Test
    void edit_lentGame_staysLentToSamePerson() throws GameVaultException {
        games.get(0).lendTo("Alice");

        new EditCommand(1, "Catan: Seafarers", null, null).execute(games, ui, storage);

        assertTrue(games.get(0).isLent());
        assertEquals("Alice", games.get(0).getBorrower());
        assertEquals("Catan: Seafarers (3-4 players, 60 min) (lent to Alice)", games.get(0).toString());
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

    @Test
    void describeLeftOut_noGames_returnsEmptyString() {
        assertEquals("", LentCommand.describeLeftOut(List.of(), "shown"));
    }
}
