package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.Difficulty;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Tests for the help, exit and filter commands.
 */
class CommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    @Test
    void helpCommand_execute_showsEveryCommand() {
        new HelpCommand().execute(new GameList(), ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains(HelpCommand.HELP_MESSAGE));
        assertTrue(printed.contains("help"));
        assertTrue(printed.contains("list"));
        assertTrue(printed.contains("filter"));
        assertTrue(printed.contains("delete"));
        assertTrue(printed.contains("exit"));
    }

    @Test
    void helpCommand_isExit_returnsFalse() {
        assertFalse(new HelpCommand().isExit());
    }

    @Test
    void exitCommand_execute_showsGoodbye() {
        new ExitCommand().execute(new GameList(), ui, new Storage("unused.txt"));
        assertTrue(output.toString().contains(ExitCommand.GOODBYE_MESSAGE));
    }

    //@@author yy261
    @Test
    void filterCommand_someGamesFit_showsOnlyMatchingGames() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4).execute(games, ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players:"));
        assertTrue(printed.contains("Catan (3-4 players)"));
        assertTrue(printed.contains("Codenames (2-8 players)"));
        assertFalse(printed.contains("Chess"));
    }

    @Test
    void filterCommand_noGamesFit_showsNoGamesMessage() {
        GameList games = new GameList();
        games.add(new Game("Chess", 2, 2));

        new FilterCommand(5).execute(games, ui, new Storage("unused.txt"));
        assertTrue(output.toString().contains("No games fit 5 players."));
    }

    //@@author
    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    //@@author yy261
    @Test
    void filterCommand_withTimeLimit_showsOnlyGamesThatFitInTime() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Love Letter", 2, 4, 20));

        new FilterCommand(4, 30).execute(games, ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players in 30 minutes or less:"));
        assertTrue(printed.contains("Love Letter (2-4 players, 20 min)"));
        assertFalse(printed.contains("Catan"));
        assertFalse(printed.contains("no play time recorded"));
    }

    @Test
    void filterCommand_withTimeLimitAndUntimedGames_mentionsHowManyWereLeftOut() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Werewolf", 8, 18));

        new FilterCommand(4, 45).execute(games, ui, new Storage("unused.txt"));
        String printed = output.toString();
        assertTrue(printed.contains("No games fit 4 players in 45 minutes or less."));
        // Only Codenames fits 4 players; Werewolf is not counted because it needs at least 8.
        assertTrue(printed.contains("(1 game(s) for 4 players were left out"));
    }

    @Test
    void filterCommand_lentGameFits_leavesItOutAndSaysWhoHasIt() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));
        games.get(1).lendTo("Ben");

        new FilterCommand(4).execute(games, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players:"));
        assertTrue(printed.contains("  Catan (3-4 players, 60 min)"));
        assertFalse(printed.contains("  Codenames"));
        assertTrue(printed.contains("(Not shown because they are lent out: Codenames (to Ben))"));
    }

    @Test
    void filterCommand_lentGamesAndTimeLimit_mentionsOnlyThoseThatFitInTime() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));
        games.get(0).lendTo("Alice");
        games.get(2).lendTo("Ben");

        new FilterCommand(4, 30).execute(games, ui, new Storage("unused.txt"));

        String printed = output.toString();
        // Catan takes 60 minutes, so it would not have been shown anyway and is not mentioned.
        assertTrue(printed.contains("(Not shown because they are lent out: Love Letter (to Ben))"));
        assertFalse(printed.contains("Catan (to Alice)"));
        assertTrue(printed.contains("(1 game(s) for 4 players were left out because they have no play time"));
    }

    @Test
    void filterCommand_noLentGames_addsNoNote() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));

        new FilterCommand(4).execute(games, ui, new Storage("unused.txt"));

        assertFalse(output.toString().contains("lent out"));
    }

    @Test
    void filterCommand_withDifficulty_showsOnlyGamesAtThatLevel() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4, 60);
        catan.setDifficulty(Difficulty.MEDIUM);
        games.add(catan);
        Game loveLetter = new Game("Love Letter", 2, 4, 20);
        loveLetter.setDifficulty(Difficulty.EASY);
        games.add(loveLetter);
        Game splendor = new Game("Splendor", 2, 4, 30);
        splendor.setDifficulty(Difficulty.EASY);
        games.add(splendor);
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4, 0, Difficulty.EASY).execute(games, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players at easy difficulty:"));
        assertTrue(printed.contains("  Love Letter (2-4 players, 20 min, easy)"));
        assertTrue(printed.contains("  Splendor (2-4 players, 30 min, easy)"));
        assertFalse(printed.contains("  Catan"));
        assertTrue(printed.contains(
                "(1 game(s) for 4 players were left out because they have no difficulty recorded.)"));
    }

    @Test
    void filterCommand_withTimeAndDifficulty_appliesBothConditions() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4, 60);
        catan.setDifficulty(Difficulty.MEDIUM);
        games.add(catan);
        Game loveLetter = new Game("Love Letter", 2, 4, 20);
        loveLetter.setDifficulty(Difficulty.EASY);
        games.add(loveLetter);
        Game splendor = new Game("Splendor", 2, 4, 30);
        splendor.setDifficulty(Difficulty.EASY);
        games.add(splendor);
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4, 25, Difficulty.EASY).execute(games, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains("Games that fit 4 players in 25 minutes or less at easy difficulty:"));
        assertTrue(printed.contains("  Love Letter"));
        assertFalse(printed.contains("  Splendor"));
    }

    @Test
    void filterCommand_noGamesAtDifficulty_showsNoGamesMessage() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4, 60);
        catan.setDifficulty(Difficulty.MEDIUM);
        games.add(catan);
        Game loveLetter = new Game("Love Letter", 2, 4, 20);
        loveLetter.setDifficulty(Difficulty.EASY);
        games.add(loveLetter);
        Game splendor = new Game("Splendor", 2, 4, 30);
        splendor.setDifficulty(Difficulty.EASY);
        games.add(splendor);
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4, 0, Difficulty.HARD).execute(games, ui, new Storage("unused.txt"));

        assertTrue(output.toString().contains("No games fit 4 players at hard difficulty."));
    }

    @Test
    void filterCommand_withoutDifficulty_addsNoDifficultyNote() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4, 60);
        catan.setDifficulty(Difficulty.MEDIUM);
        games.add(catan);
        Game loveLetter = new Game("Love Letter", 2, 4, 20);
        loveLetter.setDifficulty(Difficulty.EASY);
        games.add(loveLetter);
        Game splendor = new Game("Splendor", 2, 4, 30);
        splendor.setDifficulty(Difficulty.EASY);
        games.add(splendor);
        games.add(new Game("Codenames", 2, 8));

        new FilterCommand(4).execute(games, ui, new Storage("unused.txt"));

        assertFalse(output.toString().contains("no difficulty recorded"));
    }

    @Test
    void filterCommand_lentGameAtOtherLevel_notMentioned() {
        GameList games = new GameList();
        Game catan = new Game("Catan", 3, 4, 60);
        catan.setDifficulty(Difficulty.MEDIUM);
        games.add(catan);
        Game loveLetter = new Game("Love Letter", 2, 4, 20);
        loveLetter.setDifficulty(Difficulty.EASY);
        games.add(loveLetter);
        Game splendor = new Game("Splendor", 2, 4, 30);
        splendor.setDifficulty(Difficulty.EASY);
        games.add(splendor);
        games.add(new Game("Codenames", 2, 8));
        catan.lendTo("Alice");
        loveLetter.lendTo("Ben");

        new FilterCommand(4, 0, Difficulty.EASY).execute(games, ui, new Storage("unused.txt"));

        // Catan is medium, so it would not have been shown anyway.
        assertTrue(output.toString().contains("(Not shown because they are lent out: Love Letter (to Ben))"));
        assertFalse(output.toString().contains("Catan (to Alice)"));
    }

    @Test
    void filterCommand_untimedGameAtOtherLevel_notCounted() {
        GameList games = new GameList();
        Game codenames = new Game("Codenames", 2, 8);
        codenames.setDifficulty(Difficulty.HARD);
        games.add(codenames);

        new FilterCommand(4, 30, Difficulty.EASY).execute(games, ui, new Storage("unused.txt"));

        assertFalse(output.toString().contains("no play time recorded"));
    }
}
