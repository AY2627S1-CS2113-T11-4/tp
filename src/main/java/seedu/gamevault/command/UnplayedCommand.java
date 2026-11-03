package seedu.gamevault.command;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Shows the games that have never been played, i.e. have no sessions logged with {@code log},
 * so that games which keep being passed over get a fair chance.
 * Each game is shown with its number from {@code list}, so it can be used directly with commands
 * such as {@code log} and {@code history}. Games that are lent out or marked unavailable are still listed,
 * with their status, but the count line says how many of them cannot be played right now.
 */
public class UnplayedCommand extends Command {
    public static final String COMMAND_WORD = "unplayed";

    static final String HEADER = "Games you have never played:";
    static final String ALL_PLAYED_MESSAGE = "Every game in your collection has been played at least once.";

    private static final Logger logger = Logger.getLogger(UnplayedCommand.class.getName());

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        assert games != null : "Game list should not be null";

        List<Game> allGames = games.getGames();
        if (allGames.isEmpty()) {
            ui.showMessage(ListCommand.EMPTY_MESSAGE); // Same wording as 'list', so the two stay in sync.
            return;
        }

        StringBuilder message = new StringBuilder(HEADER);
        int unplayedCount = 0;
        int notAvailableCount = 0;
        // Loop by position rather than over a filtered list, so each game keeps its number from 'list'.
        for (int i = 0; i < allGames.size(); i++) {
            Game game = allGames.get(i);
            if (game.getPlayCount() == 0) {
                int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
                message.append(System.lineSeparator())
                        .append(displayNumber).append(". ").append(game);
                unplayedCount++;
                if (!game.isAvailable()) {
                    notAvailableCount++;
                }
            }
        }

        if (unplayedCount == 0) {
            ui.showMessage(ALL_PLAYED_MESSAGE);
            return;
        }
        message.append(System.lineSeparator()).append(getCountLine(unplayedCount, allGames.size()));
        if (notAvailableCount > 0) {
            message.append(System.lineSeparator()).append(getNotAvailableNote(notAvailableCount));
        }
        logger.log(Level.FINE, "Found {0} unplayed game(s)", unplayedCount);
        ui.showMessage(message.toString());
    }

    /**
     * Returns the line shown below the games, e.g. {@code 2 of your 5 game(s) have never been played.}
     */
    static String getCountLine(int unplayedCount, int totalCount) {
        assert unplayedCount >= 1 && unplayedCount <= totalCount : "Count should be within the collection size";
        String verb;
        if (unplayedCount == 1) {
            verb = " has";
        } else {
            verb = " have";
        }
        return unplayedCount + " of your " + totalCount + " game(s)" + verb + " never been played.";
    }

    /**
     * Returns a note saying how many of the unplayed games are lent out or marked unavailable,
     * e.g. {@code (1 of these cannot be played right now, as it is lent out or marked unavailable.)},
     * so the user does not plan a game night around a game that is not on the shelf.
     */
    static String getNotAvailableNote(int notAvailableCount) {
        assert notAvailableCount >= 1 : "Note is only shown when some games are not available";
        String pronoun;
        if (notAvailableCount == 1) {
            pronoun = "it is";
        } else {
            pronoun = "they are";
        }
        return "(" + notAvailableCount + " of these cannot be played right now, as " + pronoun
                + " lent out or marked unavailable.)";
    }
}
