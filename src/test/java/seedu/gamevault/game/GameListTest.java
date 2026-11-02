package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//@@author yy261
class GameListTest {
    private GameList games;

    @BeforeEach
    void setUp() {
        games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));
        games.add(new Game("Codenames", 2, 8));
    }

    @Test
    void filterByPlayers_someGamesFit_returnsMatchesInOriginalOrder() {
        ArrayList<Game> matches = games.filterByPlayers(4);
        assertEquals(2, matches.size());
        assertEquals("Catan", matches.get(0).getTitle());
        assertEquals("Codenames", matches.get(1).getTitle());
    }

    @Test
    void filterByPlayers_noGamesFit_returnsEmptyList() {
        assertTrue(games.filterByPlayers(9).isEmpty());
    }

    @Test
    void filterByPlayers_anyCount_leavesCollectionUnchanged() {
        games.filterByPlayers(4);
        assertEquals(3, games.size());
    }

    //@@author SwaggyDaddy67
    @Test
    void delete_middleGame_returnsItAndShiftsLaterGamesUp() {
        Game deleted = games.delete(1);

        assertEquals("Chess", deleted.getTitle());
        assertEquals(2, games.size());
        assertEquals("Catan", games.getGames().get(0).getTitle());
        assertEquals("Codenames", games.getGames().get(1).getTitle());
    }

    @Test
    void delete_firstAndLastGames_removesThem() {
        assertEquals("Codenames", games.delete(2).getTitle());
        assertEquals("Catan", games.delete(0).getTitle());

        assertEquals(1, games.size());
        assertEquals("Chess", games.getGames().get(0).getTitle());
    }

    //@@author yy261
    @Test
    void filterByPlayersAndTime_mixOfTimes_returnsOnlyTimedGamesWithinLimit() {
        GameList timedGames = new GameList();
        timedGames.add(new Game("Catan", 3, 4, 60));
        timedGames.add(new Game("Love Letter", 2, 4, 20));
        timedGames.add(new Game("Codenames", 2, 8));
        timedGames.add(new Game("Werewolf", 8, 18, 30));

        ArrayList<Game> matches = timedGames.filterByPlayersAndTime(4, 60);
        assertEquals(2, matches.size());
        assertEquals("Catan", matches.get(0).getTitle());
        assertEquals("Love Letter", matches.get(1).getTitle());

        assertEquals(1, timedGames.filterByPlayersAndTime(4, 59).size());
        assertTrue(timedGames.filterByPlayersAndTime(4, 10).isEmpty());
    }

    //@@author
    @Test
    void countWithoutPlayTime_mixOfTimes_countsOnlyUntimedGamesThatFit() {
        GameList timedGames = new GameList();
        timedGames.add(new Game("Catan", 3, 4, 60));
        timedGames.add(new Game("Codenames", 2, 8));
        timedGames.add(new Game("Chess", 2, 2));
        timedGames.add(new Game("Werewolf", 8, 18));

        assertEquals(1, timedGames.countWithoutPlayTime(4)); // Codenames only; Werewolf does not fit 4.
        assertEquals(2, timedGames.countWithoutPlayTime(2)); // Codenames and Chess.
        assertEquals(0, new GameList().countWithoutPlayTime(4));
    }

    //@@author KRAZYZECTRON
    @Test
    void filterByTag_someGamesHaveTag_returnsMatchesInOriginalOrder() {
        games.get(2).addTag("party");
        games.get(0).addTag("party");
        games.get(1).addTag("abstract");

        ArrayList<Game> matches = games.filterByTag("party");
        assertEquals(2, matches.size());
        assertEquals("Catan", matches.get(0).getTitle());
        assertEquals("Codenames", matches.get(1).getTitle());
        assertTrue(games.filterByTag("co-op").isEmpty());
    }

    @Test
    void countTags_gamesWithTags_countsEachTagInAlphabeticalOrder() {
        assertTrue(games.countTags().isEmpty());
        games.get(0).addTag("strategy");
        games.get(0).addTag("family");
        games.get(2).addTag("family");

        assertEquals(Map.of("family", 2, "strategy", 1), games.countTags());
        assertEquals("family", games.countTags().firstKey());
    }

    //@@author yy261
    @Test
    void filterAvailableByPlayersAndFindLentFitting_lentGames_splitBetweenTheTwo() {
        GameList shelf = new GameList();
        shelf.add(new Game("Catan", 3, 4, 60));
        shelf.add(new Game("Love Letter", 2, 4, 20));
        shelf.add(new Game("Codenames", 2, 8));
        shelf.get(0).lendTo("Alice");
        shelf.get(1).lendTo("Ben");

        // The plain filter still includes every game, for features that need all games for a group size.
        assertEquals(3, shelf.filterByPlayers(4).size());

        ArrayList<Game> available = shelf.filterAvailableByPlayers(4);
        assertEquals(1, available.size());
        assertEquals("Codenames", available.get(0).getTitle());

        assertEquals(2, shelf.findLentFitting(4).size());
        // Only Love Letter is short enough; Catan takes 60 minutes.
        ArrayList<Game> lentInTime = shelf.findLentFitting(4, 30);
        assertEquals(1, lentInTime.size());
        assertEquals("Love Letter", lentInTime.get(0).getTitle());
        assertTrue(shelf.findLentFitting(9).isEmpty());
    }

    @Test
    void countWithoutPlayTime_lentUntimedGame_notCounted() {
        GameList shelf = new GameList();
        shelf.add(new Game("Codenames", 2, 8));
        shelf.add(new Game("Werewolf", 4, 18));
        shelf.get(1).lendTo("Alice");

        assertEquals(1, shelf.countWithoutPlayTime(4));
    }

    @Test
    void filterAvailableByPlayersAndTime_lentGameInTime_leftOut() {
        GameList shelf = new GameList();
        shelf.add(new Game("Love Letter", 2, 4, 20));
        shelf.add(new Game("Splendor", 2, 4, 30));
        shelf.add(new Game("Catan", 3, 4, 60));
        shelf.get(0).lendTo("Alice");

        // The plain time filter still includes the lent game.
        assertEquals(2, shelf.filterByPlayersAndTime(4, 45).size());
        ArrayList<Game> available = shelf.filterAvailableByPlayersAndTime(4, 45);
        assertEquals(1, available.size());
        assertEquals("Splendor", available.get(0).getTitle());
    }

    @Test
    void describeLeftOut_noMatchingLentGames_returnsEmptyString() {
        assertEquals("", games.describeLeftOut(4, GameList.NO_TIME_LIMIT, "shown"));
        assertEquals("", GameList.describeLeftOut(new ArrayList<>(), "shown"));
    }

    @Test
    void describeLeftOut_lentGames_namesThemOnANewLineWithBorrowers() {
        GameList shelf = new GameList();
        shelf.add(new Game("Catan", 3, 4, 60));
        shelf.add(new Game("Love Letter", 2, 4, 20));
        shelf.get(0).lendTo("Alice");
        shelf.get(1).lendTo("Ben");

        assertEquals(System.lineSeparator()
                + "(Not considered because they are lent out: Catan (to Alice), Love Letter (to Ben))",
                shelf.describeLeftOut(4, GameList.NO_TIME_LIMIT, "considered"));
        // With a time limit, a game that takes too long would not have matched, so it is not named.
        assertEquals(System.lineSeparator() + "(Not shown because they are lent out: Love Letter (to Ben))",
                shelf.describeLeftOut(4, 30, "shown"));
    }

    @Test
    void filterAvailable_mixOfConditions_appliesAllGiven() {
        GameList shelf = new GameList();
        Game easyShort = new Game("Love Letter", 2, 4, 20);
        easyShort.setDifficulty(Difficulty.EASY);
        Game easyLong = new Game("Splendor", 2, 4, 50);
        easyLong.setDifficulty(Difficulty.EASY);
        Game easyLent = new Game("Hive", 2, 4, 20);
        easyLent.setDifficulty(Difficulty.EASY);
        easyLent.lendTo("Alice");
        shelf.add(easyShort);
        shelf.add(easyLong);
        shelf.add(easyLent);
        shelf.add(new Game("Codenames", 2, 8));

        assertEquals(3, shelf.filterAvailable(4, GameList.NO_TIME_LIMIT, null).size());
        assertEquals(2, shelf.filterAvailable(4, GameList.NO_TIME_LIMIT, Difficulty.EASY).size());
        ArrayList<Game> matches = shelf.filterAvailable(4, 30, Difficulty.EASY);
        assertEquals(1, matches.size());
        assertEquals("Love Letter", matches.get(0).getTitle());
    }

    @Test
    void countWithoutPlayTime_withDifficulty_skipsGamesKnownToBeAtOtherLevels() {
        GameList shelf = new GameList();
        Game hard = new Game("Chess", 2, 4);
        hard.setDifficulty(Difficulty.HARD);
        Game easy = new Game("Uno", 2, 4);
        easy.setDifficulty(Difficulty.EASY);
        shelf.add(hard);
        shelf.add(easy);
        shelf.add(new Game("Codenames", 2, 8));

        assertEquals(3, shelf.countWithoutPlayTime(4));
        // Codenames has no difficulty, so it might have fitted; Chess is known not to.
        assertEquals(2, shelf.countWithoutPlayTime(4, Difficulty.EASY));
    }

    @Test
    void describeNoDifficulty_countsOnlyGamesThatCouldHaveFittedTheTime() {
        GameList shelf = new GameList();
        shelf.add(new Game("Catan", 3, 4, 60));
        shelf.add(new Game("Love Letter", 2, 4, 20));
        Game rated = new Game("Splendor", 2, 4, 30);
        rated.setDifficulty(Difficulty.EASY);
        shelf.add(rated);

        assertEquals(System.lineSeparator()
                + "(2 game(s) for 4 players were left out because they have no difficulty recorded.)",
                shelf.describeNoDifficulty(4, GameList.NO_TIME_LIMIT, "left out"));
        assertEquals(System.lineSeparator()
                + "(1 game(s) for 4 players were not considered because they have no difficulty recorded.)",
                shelf.describeNoDifficulty(4, 30, "not considered"));
        assertEquals("", shelf.describeNoDifficulty(9, GameList.NO_TIME_LIMIT, "left out"));
    }
}
