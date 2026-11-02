package seedu.gamevault.command;

import java.util.List;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Shows every play session logged for one game, oldest first, followed by how many times it has been played.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * Sessions are recorded with {@code log}; this command only reads them and does not change the collection.
 */
public class HistoryCommand extends Command {
    public static final String COMMAND_WORD = "history";
    public static final String USAGE = "history INDEX";

    private final int displayNumber;

    /**
     * Creates a command that shows the play history of the game shown with the given number in {@code list}.
     * Whether that game exists is only checked when the command runs, because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     */
    public HistoryCommand(int displayNumber) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        this.displayNumber = displayNumber;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        assert games != null : "Game list should not be null";
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        List<PlaySession> sessions = game.getSessions();
        if (sessions.isEmpty()) {
            ui.showMessage(getNeverPlayedMessage(game));
            return;
        }

        StringBuilder message = new StringBuilder(getHeader(game));
        for (PlaySession session : sessions) {
            message.append(System.lineSeparator()).append("  ").append(session);
        }
        message.append(System.lineSeparator()).append(getSummary(game));
        ui.showMessage(message.toString());
    }

    /**
     * Returns the line shown above the sessions, e.g. {@code Play history of Catan (oldest first):}.
     */
    static String getHeader(Game game) {
        return "Play history of " + game.getTitle() + " (oldest first):";
    }

    /**
     * Returns the line shown below the sessions, e.g. {@code Played 2 times in total, most recently on 2026-10-30.}
     * Only called for a game with at least one session.
     */
    static String getSummary(Game game) {
        assert game.getPlayCount() >= 1 : "Summary is only shown for games that have been played";
        String times;
        if (game.getPlayCount() == 1) {
            times = "once";
        } else {
            times = game.getPlayCount() + " times";
        }
        return "Played " + times + " in total, most recently on " + game.getLastPlayed().orElseThrow() + ".";
    }

    /**
     * Returns the message shown for a game with no sessions, which also says how to record one.
     */
    static String getNeverPlayedMessage(Game game) {
        return game.getTitle() + " has never been played. Record a session with: " + LogCommand.USAGE;
    }
}
