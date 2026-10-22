package seedu.gamevault.command;

import java.util.Map;

import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Shows every tag used in the collection, in alphabetical order, with how many games have it,
 * so the user can remember which tags exist before using {@code tag} or {@code filter /tag}.
 */
public class ListTagsCommand extends Command {
    public static final String COMMAND_WORD = "tags";

    static final String NO_TAGS_MESSAGE = "None of your games have tags yet. Use 'tag INDEX TAG' to add one.";

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        Map<String, Integer> tagCounts = games.countTags();
        if (tagCounts.isEmpty()) {
            ui.showMessage(NO_TAGS_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("Tags in your collection:");
        for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
            message.append(System.lineSeparator())
                    .append("  ").append(entry.getKey())
                    .append(" (").append(entry.getValue()).append(" game(s))");
        }
        ui.showMessage(message.toString());
    }
}
