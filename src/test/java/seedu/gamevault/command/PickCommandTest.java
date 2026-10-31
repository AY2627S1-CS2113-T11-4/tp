package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Tests for the pick command.
 */
class PickCommandTest {
    private static final int NO_LIMIT = 0;

    private ByteArrayOutputStream output;
    private Ui ui;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Werewolf", 8, 18));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Splendor", 2, 4));
    }

    /**
     * Returns a Random whose {@code nextDouble()} always gives {@code fraction}, so the test knows where
     * the weighted pick lands, e.g. 0.5 is halfway along the total weight of the matching games.
     */
    private static Random alwaysLandingAt(double fraction) {
        assert fraction >= 0 && fraction < 1 : "nextDouble() only returns values from 0 (inclusive) to 1";
        return new Random() {
            @Override
            public double nextDouble() {
                return fraction;
            }
        };
    }

    /**
     * Returns a Random that makes pick choose the game at {@code index} among {@code matchCount} matching games,
     * when none of them is rated (so every game has the same weight).
     */
    private static Random alwaysReturning(int index, int matchCount) {
        assert index < matchCount : "Index should be within the matching games";
        return alwaysLandingAt((index + 0.5) / matchCount);
    }

    private void executePick(int playerCount, Random random) {
        new PickCommand(playerCount, NO_LIMIT, random).execute(games, ui, new Storage("unused.txt"));
    }

    @Test
    void execute_severalGamesFit_picksGameAtRandomPositionAmongMatches() {
        // Games that fit 4 players, in order: Catan, Codenames, Splendor. Position 2 is Splendor.
        executePick(4, alwaysReturning(2, 3));
        String printed = output.toString();
        assertTrue(printed.contains("How about: Splendor (2-4 players)"));
        assertTrue(printed.contains("(picked from 3 game(s) that fit 4 players)"));
    }

    @Test
    void execute_anyRandomValue_neverPicksGameThatDoesNotFit() {
        Random seededRandom = new Random(42);
        for (int i = 0; i < 50; i++) {
            executePick(4, seededRandom);
        }
        assertFalse(output.toString().contains("Werewolf"));
    }

    @Test
    void execute_onlyOneGameFits_picksThatGame() {
        executePick(10, new Random());
        assertTrue(output.toString().contains("How about: Werewolf (8-18 players)"));
        assertTrue(output.toString().contains("(picked from 1 game(s) that fit 10 players)"));
    }

    @Test
    void execute_noGameFits_showsNoMatchMessage() {
        executePick(1, new Random());
        String printed = output.toString();
        assertTrue(printed.contains(PickCommand.getNoMatchMessage("1 players")));
        assertFalse(printed.contains("How about"));
    }

    @Test
    void execute_emptyCollection_showsNoMatchMessage() {
        new PickCommand(4, NO_LIMIT, new Random()).execute(new GameList(), ui, new Storage("unused.txt"));
        assertTrue(output.toString().contains(PickCommand.getNoMatchMessage("4 players")));
    }

    @Test
    void execute_gamePicked_collectionUnchanged() {
        executePick(4, alwaysReturning(0, 3));
        assertEquals(4, games.size());
    }

    @Test
    void execute_timeLimit_picksOnlyFromTimedGamesWithinLimit() {
        GameList timedGames = new GameList();
        timedGames.add(new Game("Catan", 3, 4, 60));
        timedGames.add(new Game("Love Letter", 2, 4, 20));
        timedGames.add(new Game("Splendor", 2, 4, 30));
        timedGames.add(new Game("Codenames", 2, 8));

        // Games that fit 4 players in 30 minutes, in order: Love Letter, Splendor. Position 1 is Splendor.
        new PickCommand(4, 30, alwaysReturning(1, 2)).execute(timedGames, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains("How about: Splendor (2-4 players, 30 min)"));
        assertTrue(printed.contains("(picked from 2 game(s) that fit 4 players in 30 minutes or less)"));
        assertTrue(printed.contains("(1 game(s) for 4 players were not considered because they have no play "
                + "time recorded.)"));
    }

    @Test
    void execute_timeLimitTooShort_showsNoMatchMessageWithTime() {
        GameList timedGames = new GameList();
        timedGames.add(new Game("Catan", 3, 4, 60));

        new PickCommand(4, 45, new Random()).execute(timedGames, ui, new Storage("unused.txt"));

        String printed = output.toString();
        assertTrue(printed.contains(PickCommand.getNoMatchMessage("4 players in 45 minutes or less")));
        assertFalse(printed.contains("How about"));
        assertFalse(printed.contains("not considered"));
    }

    @Test
    void execute_noTimeLimit_doesNotMentionUntimedGames() {
        executePick(4, alwaysReturning(0, 3));
        assertFalse(output.toString().contains("not considered"));
    }

    //@@author MohamedFarsat
    @Test
    void execute_ratedUpGame_takesUpMoreOfTheTotalWeight() {
        // Games that fit 4 players: Catan (weight 4 after +3/-0), Codenames (1), Splendor (1); total 6.
        games.get(0).setRatings(3, 0);

        executePick(4, alwaysLandingAt(0.5)); // 0.5 * 6 = 3, inside Catan's 0-4.
        assertTrue(output.toString().contains("How about: Catan (3-4 players) +3/-0"));

        output.reset();
        executePick(4, alwaysLandingAt(0.7)); // 0.7 * 6 = 4.2, inside Codenames' 4-5.
        assertTrue(output.toString().contains("How about: Codenames (2-8 players)"));

        output.reset();
        executePick(4, alwaysLandingAt(0.99)); // 5.94, inside Splendor's 5-6.
        assertTrue(output.toString().contains("How about: Splendor (2-4 players)"));
    }

    @Test
    void execute_ratedDownGame_takesUpLessOfTheTotalWeight() {
        // Catan rated +0/-3 has weight 0.25, so the total is 2.25 and Catan only covers 0-0.25.
        games.get(0).setRatings(0, 3);

        executePick(4, alwaysLandingAt(0.1)); // 0.225
        assertTrue(output.toString().contains("How about: Catan"));

        output.reset();
        executePick(4, alwaysLandingAt(0.12)); // 0.27, just past Catan.
        assertTrue(output.toString().contains("How about: Codenames"));
    }

    @Test
    void execute_manyPicks_ratedUpGamePickedAboutAsOftenAsItsWeightSays() {
        games.get(0).setRatings(3, 0); // Catan should be picked about 4/6 of the time.
        Random seededRandom = new Random(42);
        int catanCount = 0;
        for (int i = 0; i < 3000; i++) {
            output.reset();
            executePick(4, seededRandom);
            if (output.toString().contains("How about: Catan")) {
                catanCount++;
            }
        }
        assertTrue(catanCount > 1800 && catanCount < 2200, "Catan was picked " + catanCount + " times");
    }

    @Test
    void execute_someMatchRated_mentionsRatings() {
        games.get(2).setRatings(1, 0);
        executePick(4, alwaysLandingAt(0));
        assertTrue(output.toString().contains("(Games rated up were more likely to be picked"));
    }

    @Test
    void execute_noMatchRated_doesNotMentionRatings() {
        games.get(1).setRatings(5, 0); // Werewolf does not fit 4 players, so it is not one of the matches.
        executePick(4, alwaysLandingAt(0));
        assertFalse(output.toString().contains("rated up"));
    }

    //@@author
    @Test
    void describeCriteria_withAndWithoutLimit_describesBoth() {
        assertEquals("4 players", PickCommand.describeCriteria(4, NO_LIMIT));
        assertEquals("4 players in 45 minutes or less", PickCommand.describeCriteria(4, 45));
    }

    @Test
    void isExit_returnsFalse() {
        assertFalse(new PickCommand(4).isExit());
    }
}
