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
              add     Adds a game: add TITLE /min MIN /max MAX [/time MINUTES] [/tag TAG [MORE_TAGS]...]
              list    Lists all games in your collection.
              find    Finds games by title: find KEYWORD
              filter  Shows games that fit a player count: filter COUNT [/time MINUTES]
                      or games with a tag: filter [COUNT] [/time MINUTES] /tag TAG
              pick    Picks a random game that fits a player count: pick COUNT [/time MINUTES]
              delete  Deletes a game: delete INDEX
              edit    Edits a game: edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]
              tag     Adds tags to a game: tag INDEX TAG [MORE_TAGS]...
              log     Logs that a game was played: log INDEX /players COUNT [/date YYYY-MM-DD]
              untag   Removes tags from a game: untag INDEX TAG [MORE_TAGS]...
              tags    Lists all tags in use and how many games have each.
              sort    Sorts your collection: sort title|players|time
              help    Shows this list of commands.
              exit    Exits GameVault.""";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        ui.showMessage(HELP_MESSAGE);
    }
}
