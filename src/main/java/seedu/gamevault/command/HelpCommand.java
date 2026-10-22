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
              add     Adds a game: add TITLE /min MIN /max MAX [/time MINUTES]
              list    Lists all games in your collection.
              filter  Shows games that fit a player count: filter COUNT [/time MINUTES]
                      or games with a tag: filter /tag TAG
              pick    Picks a random game that fits a player count: pick COUNT [/time MINUTES]
              delete  Deletes a game: delete INDEX
              edit    Edits a game: edit INDEX [/title TITLE] [/min MIN] [/max MAX]
              tag     Adds a tag to a game: tag INDEX TAG
              untag   Removes a tag from a game: untag INDEX TAG
              help    Shows this list of commands.
              exit    Exits GameVault.""";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ui.showMessage(HELP_MESSAGE);
    }
}
