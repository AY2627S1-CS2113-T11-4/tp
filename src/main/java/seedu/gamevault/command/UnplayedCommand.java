package seedu.gamevault.command;

import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Shows the games that have never been played, i.e. have no sessions logged with {@code log},
 * so that games which keep being passed over get a fair chance.
 * Each game is shown with its number from {@code list}, so it can be used directly with commands
 * such as {@code log} and {@code history}.
 */
public class UnplayedCommand extends Command {
    public static final String COMMAND_WORD = "unplayed";

    static final String HEADER = "Games you have never played:";
    static final String EMPTY_MESSAGE = "No games in your collection yet.";
    static final String ALL_PLAYED_MESSAGE = "Every game in your collection has been played at least once.";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        assert games != null : "Game list should not be null";

        List<Game> allGames = games.getGames();
        if (allGames.isEmpty()) {
            ui.showMessage(EMPTY_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder(HEADER);
        int unplayedCount = 0;
        // Loop by position rather than over a filtered list, so each game keeps its number from 'list'.
        for (int i = 0; i < allGames.size(); i++) {
            Game game = allGames.get(i);
            if (game.getPlayCount() == 0) {
                int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
                message.append(System.lineSeparator())
                        .append(displayNumber).append(". ").append(game);
                unplayedCount++;
            }
        }

        if (unplayedCount == 0) {
            ui.showMessage(ALL_PLAYED_MESSAGE);
            return;
        }
        message.append(System.lineSeparator()).append(getCountLine(unplayedCount, allGames.size()));
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
}
