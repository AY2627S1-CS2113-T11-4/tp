package seedu.gamevault.command;

import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author yy261
/**
 * Shows every game that is lent out and who has it, so the host knows which games to ask for back.
 * Games are numbered as in {@code list}, so the number can be used straight away with {@code return}.
 */
public class ListLentCommand extends Command {
    public static final String COMMAND_WORD = "lentout";

    static final String NONE_LENT_MESSAGE = "None of your games are lent out. Everything is on your shelf.";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        List<Game> allGames = games.getGames();
        StringBuilder message = new StringBuilder();
        int lentCount = 0;
        for (int i = 0; i < allGames.size(); i++) {
            Game game = allGames.get(i);
            if (game.isLent()) {
                int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
                message.append(System.lineSeparator())
                        .append(displayNumber).append(". ").append(game.getTitle())
                        .append(" - lent to ").append(game.getBorrower());
                lentCount++;
            }
        }

        if (lentCount == 0) {
            ui.showMessage(NONE_LENT_MESSAGE);
            return;
        }
        ui.showMessage(lentCount + " game(s) are lent out:" + message);
    }
}
