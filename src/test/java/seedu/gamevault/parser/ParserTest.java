package seedu.gamevault.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.gamevault.command.AddCommand;
import seedu.gamevault.command.DeleteCommand;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.FilterCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.command.ListCommand;
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
    void parse_list_returnsListCommand() throws GameVaultException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ListCommand.class, Parser.parse("  LIST "));
    }

    @Test
    void parse_listWithArguments_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("list 5"));
        assertTrue(e.getMessage().contains("does not take any arguments"));
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

    @Test
    void parse_validFilter_returnsFilterCommand() throws GameVaultException {
        assertInstanceOf(FilterCommand.class, Parser.parse("filter 4"));
        assertInstanceOf(FilterCommand.class, Parser.parse("FILTER   1 "));
    }

    @Test
    void parse_filterWithMissingCount_throwsExceptionShowingFormat() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("filter"));
        assertEquals("COUNT is missing. Format: filter COUNT", e.getMessage());
    }

    @Test
    void parse_filterWithInvalidCount_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter -2"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter four"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 5"));
    }

    @Test
    void parse_validDelete_returnsDeleteCommand() throws GameVaultException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("DELETE   2 "));
    }

    @Test
    void parse_deleteWithoutIndex_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("delete"));
        assertTrue(e.getMessage().contains("INDEX is missing"));
    }

    @Test
    void parse_deleteWithNonNumberIndex_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("delete two"));
        assertThrows(GameVaultException.class, () -> Parser.parse("delete 1.5"));
        assertThrows(GameVaultException.class, () -> Parser.parse("delete 1 2"));
    }

    @Test
    void parse_deleteWithIndexBelowOne_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("delete 0"));
        assertEquals("INDEX should be at least 1, but was 0.", e.getMessage());
        assertThrows(GameVaultException.class, () -> Parser.parse("delete -3"));
        assertThrows(GameVaultException.class, () -> Parser.parse("delete -99999999999"));
    }

    @Test
    void parse_deleteWithIndexTooBigForInt_reportsNoSuchGame() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("delete 99999999999"));
        assertEquals(DeleteCommand.getNoSuchGameMessage("99999999999"), e.getMessage());
    }
}
