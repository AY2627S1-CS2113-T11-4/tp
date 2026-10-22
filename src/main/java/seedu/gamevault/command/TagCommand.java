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
 * Adds one or more tags (e.g. {@code party}) to a game, then saves the updated collection to the data file.
 * The game is chosen by the number shown next to it in {@code list}, which starts from 1.
 * Tags the game already has are skipped and reported, so {@code tag 2 party word} still adds {@code word}
 * when the game is already tagged {@code party}.
 */
public class TagCommand extends Command {
    public static final String COMMAND_WORD = "tag";
    public static final String USAGE = "tag INDEX TAG [MORE_TAGS]...";

    private final int displayNumber;
    private final List<String> tags;

    /**
     * Creates a command that adds tags to the game shown with the given number in {@code list}.
     * Whether that game exists, and which tags it already has, is only checked when the command runs,
     * because the collection may change before then.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param tags Tags to add (at least one, no repeats); already normalised and checked by the parser.
     */
    public TagCommand(int displayNumber, List<String> tags) {
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
        List<String> alreadyThere = new ArrayList<>();
        for (String tag : tags) {
            if (game.hasTag(tag)) {
                alreadyThere.add(tag);
            } else {
                game.addTag(tag);
            }
        }
        if (alreadyThere.size() == tags.size()) {
            throw new GameVaultException(game.getTitle() + " is already tagged " + quote(tags) + ".");
        }

        storage.save(games);
        String message = "Tagged: " + game;
        if (!alreadyThere.isEmpty()) {
            message += System.lineSeparator() + "(Already tagged " + quote(alreadyThere) + ", so skipped.)";
        }
        ui.showMessage(message);
    }

    /**
     * Returns the tags quoted and separated by commas, e.g. {@code 'party', 'word'}, for messages.
     */
    static String quote(List<String> tags) {
        return "'" + String.join("', '", tags) + "'";
    }
}
