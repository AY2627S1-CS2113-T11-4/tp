package seedu.gamevault.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.exception.GameVaultException;

class ParserTest {

    @Test
    void parse_help_returnsHelpCommand() throws GameVaultException {
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    @Test
    void parse_exit_returnsExitCommand() throws GameVaultException {
        assertInstanceOf(ExitCommand.class, Parser.parse("exit"));
    }

    @Test
    void parse_differentCaseAndSurroundingSpaces_stillRecognised() throws GameVaultException {
        assertInstanceOf(HelpCommand.class, Parser.parse("  HeLp  "));
        assertInstanceOf(ExitCommand.class, Parser.parse("\tEXIT "));
    }

    @Test
    void parse_unknownCommand_throwsExceptionNamingTheCommand() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("dance now"));
        assertEquals("Unknown command 'dance'. Type 'help' to see all commands.", e.getMessage());
    }

    @Test
    void parse_emptyOrBlankInput_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse(""));
        assertThrows(GameVaultException.class, () -> Parser.parse("   "));
    }

    @Test
    void parse_extraArgumentsForNoArgumentCommand_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("exit now"));
        assertTrue(e.getMessage().contains("does not take any arguments"));
        assertThrows(GameVaultException.class, () -> Parser.parse("help me"));
    }
}
