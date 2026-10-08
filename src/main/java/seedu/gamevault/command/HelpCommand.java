package seedu.gamevault.command;

import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Shows a summary of every command GameVault supports.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_WORD = "help";

    /**
     * One line per command. When you add a new command, add its usage here too
     * so that {@code help} and the User Guide stay in sync.
     */
    static final String HELP_MESSAGE = """
            Here are the commands you can use:
              help    Shows this list of commands.
              exit    Exits GameVault.""";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ui.showMessage(HELP_MESSAGE);
    }
}
