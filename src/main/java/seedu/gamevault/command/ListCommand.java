package seedu.gamevault.command;

import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Shows every game in the collection, numbered from 1 in the order they were added.
 * These numbers are the ones other commands, such as {@code delete}, refer to.
 */
public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";

    static final String LIST_HEADER = "Here are the games in your collection:";
    static final String EMPTY_MESSAGE = "No games in your collection yet.";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        assert games != null : "Game list to show should not be null";

        List<Game> gamesToShow = games.getGames();
        if (gamesToShow.isEmpty()) {
            ui.showMessage(EMPTY_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder(LIST_HEADER);
        for (int i = 0; i < gamesToShow.size(); i++) {
            int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
            message.append(System.lineSeparator())
                    .append(displayNumber).append(". ").append(gamesToShow.get(i));
        }
        ui.showMessage(message.toString());
    }
}
