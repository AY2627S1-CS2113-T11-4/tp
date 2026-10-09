package seedu.gamevault.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.gamevault.command.AddCommand;
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

    @Test
    void parse_validAdd_returnsAddCommand() throws GameVaultException {
        assertInstanceOf(AddCommand.class, Parser.parse("add Catan /min 3 /max 4"));
        assertInstanceOf(AddCommand.class, Parser.parse("add Chess /min 2 /max 2"));
    }

    @Test
    void parse_addWithMissingField_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 3"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add /min 3 /max 4"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min /max 4"));
    }

    @Test
    void parse_addWithInvalidCounts_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 0 /max 4"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 5 /max 4"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min three /max 4"));
    }
}
