package seedu.gamevault.command;

import java.util.ArrayList;
import java.util.List;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.Tag;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Removes one or more tags from a game, then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * Tags the game does not have are skipped and reported.
 */
public class UntagCommand extends Command {
    public static final String COMMAND_WORD = "untag";
    public static final String USAGE = "untag INDEX TAG [MORE_TAGS]...";

    private final int displayNumber;
    private final List<String> tags;

    /**
     * Creates a command that removes tags from the game shown with the given number in {@code list}.
     * Whether that game exists, and which tags it has, is only checked when the command runs.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param tags Tags to remove (at least one, no repeats); already normalised and checked by the parser.
     */
    public UntagCommand(int displayNumber, List<String> tags) {
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert !tags.isEmpty() : "At least one tag should be given";
        assert tags.stream().allMatch(Tag::isValid) : "Tags should be valid and normalised";
        this.displayNumber = displayNumber;
        this.tags = List.copyOf(tags);
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        Game game = games.get(displayNumber - 1); // The list shows games from 1, while GameList counts from 0.
        List<String> removed = new ArrayList<>();
        List<String> notThere = new ArrayList<>();
        for (String tag : tags) {
            if (game.hasTag(tag)) {
                game.removeTag(tag);
                removed.add(tag);
            } else {
                notThere.add(tag);
            }
        }
        if (removed.isEmpty()) {
            throw new GameVaultException(game.getTitle() + " does not have the tag(s) " + TagCommand.quote(tags)
                    + ".");
        }

        storage.save(games);
        String message = "Removed tag(s) " + TagCommand.quote(removed) + ": " + game;
        if (!notThere.isEmpty()) {
            message += System.lineSeparator() + "(Did not have " + TagCommand.quote(notThere) + ", so skipped.)";
        }
        ui.showMessage(message);
    }
}
