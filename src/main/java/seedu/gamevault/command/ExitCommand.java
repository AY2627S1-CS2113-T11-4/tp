package seedu.gamevault.command;

import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Ends the program after saying goodbye.
 */
public class ExitCommand extends Command {
    public static final String COMMAND_WORD = "exit";

    static final String GOODBYE_MESSAGE = "Bye! Have a great game night.";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ui.showMessage(GOODBYE_MESSAGE);
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
