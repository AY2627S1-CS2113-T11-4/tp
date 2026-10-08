package seedu.gamevault.exception;

/**
 * Represents an error specific to GameVault, such as an unknown command or invalid user input.
 * The message is meant to be shown directly to the user, so it should be clear and helpful.
 */
public class GameVaultException extends Exception {

    /**
     * Creates an exception with a user-facing message.
     *
     * @param message Explanation of what went wrong, shown to the user.
     */
    public GameVaultException(String message) {
        super(message);
    }
}
