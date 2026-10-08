package seedu.gamevault.command;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

/**
 * Represents a single action that the user asked GameVault to perform.
 * Each command word (e.g. {@code help}, {@code exit}) has its own subclass,
 * created by {@link seedu.gamevault.parser.Parser} from the user's input.
 *
 * <p>To add a new command: create a subclass, implement {@link #execute}, add a case for it
 * in {@code Parser}, and add a line for it in {@link HelpCommand}.</p>
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param games The user's game collection, which the command may read or change.
     * @param ui Used to show results or messages to the user.
     * @param storage Used to save the collection if the command changes it.
     * @throws GameVaultException If the command cannot be completed (e.g. an invalid index).
     */
    public abstract void execute(GameList games, Ui ui, Storage storage) throws GameVaultException;

    /**
     * Returns whether the program should stop after this command.
     * Only {@link ExitCommand} overrides this to return true.
     *
     * @return True if this command ends the program.
     */
    public boolean isExit() {
        return false;
    }
}
