package seedu.gamevault.command;

import java.util.ArrayList;
import java.util.Random;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Picks one random game that can be played by a given number of players,
 * so the group does not have to spend time deciding what to play.
 * The count is validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class PickCommand extends Command {
    public static final String COMMAND_WORD = "pick";
    public static final String USAGE = "pick COUNT";

    private final int playerCount;
    /** Source of randomness; passed in so tests can control which game is picked. */
    private final Random random;

    /**
     * Creates a command that picks a random game fitting the given number of players.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     */
    public PickCommand(int playerCount) {
        this(playerCount, new Random());
    }

    /**
     * Creates a command that uses the given {@link Random}, so tests can make the pick predictable.
     *
     * @param playerCount Number of players tonight; already checked to be at least 1.
     * @param random Source of randomness used to choose the game.
     */
    PickCommand(int playerCount, Random random) {
        assert playerCount >= 1 : "Player count should be at least 1";
        assert random != null : "Random should not be null";
        this.playerCount = playerCount;
        this.random = random;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ArrayList<Game> matches = games.filterByPlayers(playerCount);
        if (matches.isEmpty()) {
            ui.showMessage(getNoMatchMessage(playerCount));
            return;
        }

        Game pickedGame = matches.get(random.nextInt(matches.size()));
        ui.showMessage("How about: " + pickedGame + System.lineSeparator()
                + "(picked from " + matches.size() + " game(s) that fit " + playerCount + " players)");
    }

    static String getNoMatchMessage(int playerCount) {
        return "No games fit " + playerCount + " players, so there is nothing to pick from.";
    }
}
