//@@author Xandurs
package seedu.gamevault.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Difficulty;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.game.PlaySession;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;
//@@author Xandurs
/**
 * Changes the title, player range and/or play time of a game, then saves the updated collection to the data file.
  * Fields that are not given keep the game's existing values, and the game's tags, play sessions
 * and unavailable reason are always kept.
 * The game keeps its position in the list.
 */
public class EditCommand extends Command {
    public static final String COMMAND_WORD = "edit";
    public static final String USAGE =
            "edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none] [/diff LEVEL|none]";

    /**
     * Value of {@code newPlayTime} that means "remove the game's recorded play time",
     * as typed by the user with {@code /time none}.
     */
    public static final int CLEAR_PLAY_TIME = 0;

    private static final Logger logger = Logger.getLogger(EditCommand.class.getName());

    private final int displayNumber;
    private final String newTitle;
    private final Integer newMinPlayers;
    private final Integer newMaxPlayers;
    /** New play time in minutes, {@link #CLEAR_PLAY_TIME} to remove it, or null to keep the current one. */
    private final Integer newPlayTime;
    //@@author yy261
    /** New difficulty, or null to keep the current one (unless {@link #isClearingDifficulty} is set). */
    private final Difficulty newDifficulty;
    /** Whether to remove the game's recorded difficulty, as typed by the user with {@code /diff none}. */
    private final boolean isClearingDifficulty;
    //@@author Xandurs

    /**
     * Creates a command that edits the game shown with the given number in {@code list}.
     * A null field means "keep the existing value". Whether the game exists, and whether
     * MAX is still at least MIN, is only checked when the command runs, because both
     * depend on the game's current values.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param newTitle New title, or null to keep the current one.
     * @param newMinPlayers New minimum players, or null to keep the current value.
     * @param newMaxPlayers New maximum players, or null to keep the current value.
     */
    public EditCommand(int displayNumber, String newTitle, Integer newMinPlayers, Integer newMaxPlayers) {
        this(displayNumber, newTitle, newMinPlayers, newMaxPlayers, null);
    }

    /**
     * Creates a command that edits the game shown with the given number in {@code list},
     * including its play time.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param newTitle New title, or null to keep the current one.
     * @param newMinPlayers New minimum players, or null to keep the current value.
     * @param newMaxPlayers New maximum players, or null to keep the current value.
     * @param newPlayTime New play time in minutes (at least 1), {@link #CLEAR_PLAY_TIME} to remove it,
     *     or null to keep the current one.
     */
    public EditCommand(int displayNumber, String newTitle, Integer newMinPlayers, Integer newMaxPlayers,
            Integer newPlayTime) {
        this(displayNumber, newTitle, newMinPlayers, newMaxPlayers, newPlayTime, null, false);
    }

    //@@author yy261
    /**
     * Creates a command that edits the game shown with the given number in {@code list},
     * including its play time and difficulty.
     *
     * @param displayNumber Number of the game as shown in {@code list} (at least 1).
     * @param newTitle New title, or null to keep the current one.
     * @param newMinPlayers New minimum players, or null to keep the current value.
     * @param newMaxPlayers New maximum players, or null to keep the current value.
     * @param newPlayTime New play time in minutes (at least 1), {@link #CLEAR_PLAY_TIME} to remove it,
     *     or null to keep the current one.
     * @param newDifficulty New difficulty, or null to keep the current one.
     * @param isClearingDifficulty Whether to remove the game's difficulty instead.
     */
    public EditCommand(int displayNumber, String newTitle, Integer newMinPlayers, Integer newMaxPlayers,
            Integer newPlayTime, Difficulty newDifficulty, boolean isClearingDifficulty) {
        //@@author Xandurs
        assert displayNumber >= 1 : "Display number should be at least 1";
        assert newTitle != null || newMinPlayers != null || newMaxPlayers != null || newPlayTime != null
                || newDifficulty != null || isClearingDifficulty : "At least one field should be edited";
        assert newDifficulty == null || !isClearingDifficulty : "Cannot both set and remove the difficulty";
        assert newPlayTime == null || newPlayTime >= CLEAR_PLAY_TIME : "Play time should not be negative";
        this.displayNumber = displayNumber;
        this.newTitle = newTitle;
        this.newMinPlayers = newMinPlayers;
        this.newMaxPlayers = newMaxPlayers;
        this.newPlayTime = newPlayTime;
        this.newDifficulty = newDifficulty;
        this.isClearingDifficulty = isClearingDifficulty;
    }

    @Override
    public void execute(GameList games, Ui ui, Storage storage) throws GameVaultException {
        if (displayNumber > games.size()) {
            logger.log(Level.FINE, "Edit rejected: no game numbered " + displayNumber);
            throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(String.valueOf(displayNumber)));
        }

        int index = displayNumber - 1; // The list shows games from 1, while GameList counts from 0.
        Game oldGame = games.get(index);
        String title = newTitle != null ? newTitle : oldGame.getTitle();
        int minPlayers = newMinPlayers != null ? newMinPlayers : oldGame.getMinPlayers();
        int maxPlayers = newMaxPlayers != null ? newMaxPlayers : oldGame.getMaxPlayers();

        if (maxPlayers < minPlayers) {
            logger.log(Level.FINE, "Edit rejected: MAX " + maxPlayers + " is less than MIN " + minPlayers);
            throw new GameVaultException("MAX (" + maxPlayers + ") cannot be less than MIN (" + minPlayers
                    + "). The game is currently: " + oldGame);
        }
        if (isClearingMissingPlayTime(oldGame)) {
            throw new GameVaultException("This game has no play time to remove. The game is currently: " + oldGame);
        }

        if (isClearingDifficulty && !oldGame.hasDifficulty()) {
            throw new GameVaultException("This game has no difficulty to remove. The game is currently: " + oldGame);
        }

        int playTime = choosePlayTime(oldGame);
        Game editedGame = playTime == CLEAR_PLAY_TIME
                ? new Game(title, minPlayers, maxPlayers)
                : new Game(title, minPlayers, maxPlayers, playTime);
        // Tags cannot be changed with edit (use tag/untag), so carry them over unchanged.
        for (String tag : oldGame.getTags()) {
            editedGame.addTag(tag);
        }
        //@@author yy261
        // Lending cannot be changed with edit either (use lend/return), so keep the game lent if it was.
        if (oldGame.isLent()) {
            editedGame.lendTo(oldGame.getBorrower());
        }
        if (newDifficulty != null) {
            editedGame.setDifficulty(newDifficulty);
        } else if (oldGame.hasDifficulty() && !isClearingDifficulty) {
            editedGame.setDifficulty(oldGame.getDifficulty());
        }
        //@@author Xandurs
        // Play history is not something edit changes either, so keep every logged session.
        for (PlaySession session : oldGame.getSessions()) {
            editedGame.addSession(session);
        }
        //@@author KRAZYZECTRON
        // Availability is changed with unavailable/available, so keep the game's current reason, if any.
        oldGame.getUnavailableReason().ifPresent(editedGame::markUnavailable);
        // Who the game is from is changed with borrow/giveback, so keep that too.
        if (oldGame.hasLender()) {
            editedGame.recordLender(oldGame.getLender(), oldGame.getDueBack().orElse(null));
        }
        //@@author MohamedFarsat
        // Ratings are changed with rate, not edit, so keep them too.
        editedGame.setRatings(oldGame.getThumbsUp(), oldGame.getThumbsDown());
        //@@author Xandurs

        if (editedGame.toFileString().equals(oldGame.toFileString())) {
            // Nothing would change, so tell the user instead of rewriting the data file.
            ui.showMessage("No changes made: the game is already " + oldGame);
            return;
        }

        games.set(index, editedGame);
        storage.save(games);
        logger.log(Level.FINE, "Edited game " + displayNumber + ": " + oldGame + " -> " + editedGame);
        ui.showMessage("Edited: " + oldGame + System.lineSeparator() + "Now: " + editedGame);
    }

    /**
     * Returns the play time the edited game should have: the new one if given,
     * otherwise the old game's play time, or {@link #CLEAR_PLAY_TIME} if it should have none.
     */
    private int choosePlayTime(Game oldGame) {
        if (newPlayTime != null) {
            return newPlayTime;
        }
        return oldGame.hasPlayTime() ? oldGame.getPlayTime() : CLEAR_PLAY_TIME;
    }

    /**
     * Returns whether the user asked to remove the play time of a game that has none,
     * which is most likely a mistake (e.g. the wrong INDEX), so it is reported rather than ignored.
     */
    private boolean isClearingMissingPlayTime(Game oldGame) {
        return newPlayTime != null && newPlayTime == CLEAR_PLAY_TIME && !oldGame.hasPlayTime();
    }
}
