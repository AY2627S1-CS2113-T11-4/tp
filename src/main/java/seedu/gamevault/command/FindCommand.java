package seedu.gamevault.command;

import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Shows the games whose title contains a keyword, ignoring case, e.g. {@code mono} finds "Monopoly".
 * Each match is shown with its number from {@code list}, so it can be used directly with commands
 * such as {@code delete}, {@code edit} and {@code tag}.
 * The keyword is validated by {@link seedu.gamevault.parser.Parser} before this command is created.
 */
public class FindCommand extends Command {
    public static final String COMMAND_WORD = "find";
    public static final String USAGE = "find KEYWORD";

    private final String keyword;

    /**
     * Creates a command that finds games whose title contains the given keyword.
     *
     * @param keyword Text to look for in titles; already checked to be non-blank.
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Keyword should not be blank";
        this.keyword = keyword;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        assert games != null : "Game list to search should not be null";

        // Lower-case both sides so that the comparison ignores case.
        String lowerKeyword = keyword.toLowerCase();
        List<Game> allGames = games.getGames();
        StringBuilder message = new StringBuilder(getHeader(keyword));
        int matchCount = 0;

        // Loop by position rather than over a filtered list, so each match keeps its number from 'list'.
        for (int i = 0; i < allGames.size(); i++) {
            Game game = allGames.get(i);
            if (game.getTitle().toLowerCase().contains(lowerKeyword)) {
                int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
                message.append(System.lineSeparator())
                        .append(displayNumber).append(". ").append(game);
                matchCount++;
            }
        }

        if (matchCount == 0) {
            ui.showMessage(getNoMatchMessage(keyword));
        } else {
            ui.showMessage(message.toString());
        }
    }

    /**
     * Returns the line shown above the matching games.
     *
     * @param keyword The keyword as the user typed it.
     * @return The header line.
     */
    static String getHeader(String keyword) {
        return "Games with '" + keyword + "' in the title:";
    }

    /**
     * Returns the message shown when no title contains the keyword.
     *
     * @param keyword The keyword as the user typed it.
     * @return The no-match message.
     */
    static String getNoMatchMessage(String keyword) {
        return "No games have '" + keyword + "' in the title.";
    }
}
