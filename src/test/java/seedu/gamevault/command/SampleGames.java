package seedu.gamevault.command;

import seedu.gamevault.game.Difficulty;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;

//@@author yy261
/**
 * Ready-made game collections shared by the command tests, so each test does not repeat the same setup.
 * Every call returns a new collection, so a test can change it (e.g. lend a game) without affecting other tests.
 */
final class SampleGames {
    private SampleGames() {
        // Only static methods; not meant to be created.
    }

    /**
     * Returns a collection where some games have a play time and one does not:
     * 0. Catan (3-4 players, 60 min), 1. Codenames (2-8 players), 2. Love Letter (2-4 players, 20 min).
     */
    static GameList timedShelf() {
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 2, 8));
        games.add(new Game("Love Letter", 2, 4, 20));
        return games;
    }

    /**
     * Returns a collection where some games have a difficulty level and one has neither a level nor a play time:
     * 0. Catan (3-4 players, 60 min, medium), 1. Love Letter (2-4 players, 20 min, easy),
     * 2. Splendor (2-4 players, 30 min, easy), 3. Codenames (2-8 players).
     */
    static GameList shelfWithDifficulties() {
        GameList games = new GameList();
        games.add(withDifficulty(new Game("Catan", 3, 4, 60), Difficulty.MEDIUM));
        games.add(withDifficulty(new Game("Love Letter", 2, 4, 20), Difficulty.EASY));
        games.add(withDifficulty(new Game("Splendor", 2, 4, 30), Difficulty.EASY));
        games.add(new Game("Codenames", 2, 8));
        return games;
    }

    /**
     * Returns the given game after setting its difficulty, so a game can be built and added in one line.
     */
    static Game withDifficulty(Game game, Difficulty difficulty) {
        game.setDifficulty(difficulty);
        return game;
    }
}
