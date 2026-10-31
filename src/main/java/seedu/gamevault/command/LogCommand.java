package seedu.gamevault.command;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author Xandurs
/**
 * Records that a game was played (a play session), then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * The logged sessions are what "last played" and play history are worked out from.
 */
public class LogCommand extends Command {
    public static final String COMMAND_WORD = "log";
    public static final String USAGE = "log INDEX /players COUNT [/date YYYY-MM-DD]";

    private static final Logger logger = Logger.getLogger(LogCommand.class.getName());

    private final int displayNumber;
    private final PlaySession session;

    /**
     * Creates a command that logs a session for the game shown with the given number in {@code list}.
     * Whether that game exists, and whether COUNT fits its player range, is only checked when the command
     * runs, because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param date Date the game was played; already checked by the parser not to be in the future.
     * @param playerCount Number of people who played (at least 1).
     */
    public LogCommand(int displayNumber, LocalDate date, int playerCount) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
        this.session = new PlaySession(date, playerCount);
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        // A count outside the game's range is most likely a typo (e.g. 44 instead of 4), and logging it would
        // make the history disagree with the player range that filter and pick rely on, so it is rejected.
        if (!game.fitsPlayerCount(session.getPlayerCount())) {
            throw new GameVaultException(game.getTitle() + " is for " + describePlayerRange(game)
                    + ", so it cannot be logged with " + session.getPlayerCount() + ". "
                    + "If you now play it with a different range, use 'edit' to change MIN or MAX first.");
        }

        game.addSession(session);
        storage.save(games);
        logger.log(Level.FINE, "Logged session " + session + " for " + game.getTitle());
        ui.showMessage("Logged: " + game.getTitle() + " played on " + session + System.lineSeparator()
                + game.getTitle() + " has now been played " + describeTimes(game.getPlayCount()) + ".");
    }

    /**
     * Returns the game's player range in words, e.g. {@code 3-4 players} or {@code 2 players}.
     */
    private static String describePlayerRange(Game game) {
        if (game.getMinPlayers() == game.getMaxPlayers()) {
            return game.getMinPlayers() + (game.getMinPlayers() == 1 ? " player" : " players");
        }
        return game.getMinPlayers() + "-" + game.getMaxPlayers() + " players";
    }

    /**
     * Returns a play count in words, e.g. {@code once} or {@code 3 times}.
     */
    private static String describeTimes(int count) {
        return count == 1 ? "once" : count + " times";
    }
}
