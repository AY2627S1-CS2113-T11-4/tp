package seedu.gamevault.command;

import java.time.LocalDate;
import java.util.List;

import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author SwaggyDaddy67
/**
 * Shows every game in the collection, numbered from 1 in the order they were added.
 * These numbers are the ones other commands, such as {@code delete}, refer to.
 * Borrowed games that should already have been given back are listed again at the end as a reminder.
 * Each game also shows when it was last played, e.g. {@code last played 3 days ago} or {@code never played}.
 */
public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";

    static final String LIST_HEADER = "Here are the games in your collection:";
    static final String EMPTY_MESSAGE = "No games in your collection yet.";
    //@@author KRAZYZECTRON
    static final String OVERDUE_HEADER = "Overdue, please give these back:";

    /** Today's date, used to find overdue borrowed games. */
    private final LocalDate today;

    /**
     * Creates a command that lists the collection, finding overdue games using today's date.
     */
    public ListCommand() {
        this(LocalDate.now());
    }

    /**
     * Creates a command that lists the collection, treating {@code today} as today's date, so that tests
     * can check overdue reminders with a fixed date.
     */
    ListCommand(LocalDate today) {
        assert today != null : "Today's date should not be null";
        this.today = today;
    }

    //@@author SwaggyDaddy67
    @Override
    public void execute(GameList games, Ui ui, Storage storage) {
        assert games != null : "Game list to show should not be null";

        List<Game> gamesToShow = games.getGames();
        if (gamesToShow.isEmpty()) {
            ui.showMessage(EMPTY_MESSAGE);
            return;
        }

        //@@author Xandurs
        ui.showMessage(LIST_HEADER + formatNumberedListWithLastPlayed(gamesToShow, today)
                + formatOverdue(gamesToShow));
        
    }

    //@@author Xandurs
    /**
     * Returns the games numbered from 1, one per line, with when each one was last played at the end,
     * e.g. {@code 1. Catan (3-4 players) - last played 3 days ago}, so the user can avoid games the group
     * played recently. Each line starts with a line separator so it can follow a header line directly.
     * Shared with {@link SortCommand}, so the collection looks the same before and after sorting.
     *
     * @param gamesToShow Games to number, in the order they should be shown.
     * @param today Today's date, used to work out how long ago each game was played.
     * @return The numbered lines, or an empty string if there are no games.
     */
    static String formatNumberedListWithLastPlayed(List<Game> gamesToShow, LocalDate today) {
        assert today != null : "Today's date should not be null";
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < gamesToShow.size(); i++) {
            int displayNumber = i + 1; // Users count from 1, while the list counts from 0.
            Game game = gamesToShow.get(i);
            lines.append(System.lineSeparator())
                    .append(displayNumber).append(". ").append(game)
                    .append(" - ").append(game.describeLastPlayed(today));
        }
        return lines.toString();
    }
    
    //@@author KRAZYZECTRON
    /**
     * Returns a reminder listing the overdue borrowed games with their numbers from {@code list},
     * e.g. {@code 2. Hive - give back to Bob (was due 2026-11-01)}, or an empty string if none are overdue.
     */
    private String formatOverdue(List<Game> gamesToShow) {
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < gamesToShow.size(); i++) {
            Game game = gamesToShow.get(i);
            if (!game.isOverdue(today)) {
                continue;
            }
            lines.append(System.lineSeparator())
                    .append(i + 1).append(". ").append(game.getTitle())
                    .append(" - give back to ").append(game.getLender())
                    .append(" (was due ").append(game.getDueBack().orElseThrow()).append(")");
        }
        return lines.length() == 0 ? "" : System.lineSeparator() + OVERDUE_HEADER + lines;
    }
}
