package seedu.gamevault.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import seedu.gamevault.command.AddCommand;
import seedu.gamevault.command.Command;
import seedu.gamevault.command.DeleteCommand;
import seedu.gamevault.command.EditCommand;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.FilterCommand;
import seedu.gamevault.command.FilterTagCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.command.ListCommand;
import seedu.gamevault.command.TagCommand;
import seedu.gamevault.command.UntagCommand;
import seedu.gamevault.command.PickCommand;
import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.ui.Ui;

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
        assertEquals("COUNT is missing. Format: " + FilterCommand.USAGE, e.getMessage());
    }

    @Test
    void parse_filterWithInvalidCount_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter -2"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter four"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 5"));
    }

    @Test
    void parse_validPick_returnsPickCommand() throws GameVaultException {
        assertInstanceOf(PickCommand.class, Parser.parse("pick 4"));
        assertInstanceOf(PickCommand.class, Parser.parse("  PICK   2 "));
    }

    @Test
    void parse_pickWithMissingCount_throwsExceptionShowingFormat() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("pick"));
        assertEquals("COUNT is missing. Format: pick COUNT [/time MINUTES]", e.getMessage());
    }

    @Test
    void parse_pickWithInvalidCount_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("pick 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick -1"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick four"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick 4 5"));
    }

    @Test
    void parse_pickWithTime_returnsPickCommand() throws GameVaultException {
        assertInstanceOf(PickCommand.class, Parser.parse("pick 4 /time 45"));
    }

    @Test
    void parse_pickWithInvalidTime_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("pick 4 /time"));
        assertEquals("MINUTES is missing. Format: pick COUNT [/time MINUTES]", e.getMessage());
        assertThrows(GameVaultException.class, () -> Parser.parse("pick 4 /time 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick 4 /time soon"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick 4 /time 30 /time 45"));
        assertThrows(GameVaultException.class, () -> Parser.parse("pick /time 30"));
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

    @Test
    void parse_editFieldsInAnyOrder_returnsEditCommand() throws GameVaultException {
        assertInstanceOf(EditCommand.class, Parser.parse("edit 1 /max 6 /title Catan /min 2"));
    }

    @Test
    void parse_editUpperCase_returnsEditCommand() throws GameVaultException {
        assertInstanceOf(EditCommand.class, Parser.parse("EDIT 1 /min 2"));
    }

    @Test
    void parse_editNoFields_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit 1"));
    }

    @Test
    void parse_editNoIndex_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit /title Catan"));
    }

    @Test
    void parse_editZeroIndex_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit 0 /min 2"));
    }

    @Test
    void parse_editTitleWithPipe_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit 1 /title Cat|an"));
    }

    @Test
    void parse_editRepeatedField_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit 1 /min 2 /min 3"));
    }

    @Test
    void parse_editEmptyMin_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("edit 1 /min"));
    }

    @Test
    void parse_addWithTime_returnsAddCommand() throws GameVaultException {
        assertInstanceOf(AddCommand.class, Parser.parse("add Catan /min 3 /max 4 /time 60"));
        // Prefixes may come in any order after the title.
        assertInstanceOf(AddCommand.class, Parser.parse("add Catan /time 60 /max 4 /min 3"));
    }

    @Test
    void parse_addWithInvalidTime_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 3 /max 4 /time"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 3 /max 4 /time 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Catan /min 3 /max 4 /time an hour"));
    }

    @Test
    void parse_addWithRepeatedPrefix_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> Parser.parse("add Catan /min 3 /max 4 /min 5"));
        assertEquals("/min can only be given once.", e.getMessage());
    }

    @Test
    void parse_addWithPrefixInsideTitle_keepsWholeTitle() throws GameVaultException {
        // "/minimal" is part of the title, not the /min prefix, because it is not a separate word.
        assertInstanceOf(AddCommand.class, Parser.parse("add Foo/minimal /min 2 /max 3"));
    }

    @Test
    void parse_filterWithTime_returnsFilterCommand() throws GameVaultException {
        assertInstanceOf(FilterCommand.class, Parser.parse("filter 4 /time 45"));
    }

    @Test
    void parse_filterWithInvalidTime_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 /time"));
        assertEquals("MINUTES is missing. Format: " + FilterCommand.USAGE, e.getMessage());
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 /time 0"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /time 45"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 /time 45 /time 30"));
    }

    //@@author KRAZYZECTRON
    @Test
    void parse_tagAndUntag_returnTagCommands() throws GameVaultException {
        assertInstanceOf(TagCommand.class, Parser.parse("tag 2 party"));
        assertInstanceOf(UntagCommand.class, Parser.parse("UNTAG 2 co-op"));
    }

    @Test
    void parse_tagWithMissingParts_throwsExceptionSayingWhatIsMissing() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("tag"));
        assertEquals("INDEX and TAG are missing. Format: " + TagCommand.USAGE + " (e.g. tag 2 party)",
                e.getMessage());
        e = assertThrows(GameVaultException.class, () -> Parser.parse("untag 2"));
        assertEquals("TAG is missing. Format: " + UntagCommand.USAGE + " (e.g. untag 2 party)", e.getMessage());
    }

    @Test
    void parse_tagWithInvalidIndexOrTag_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("tag 0 party"));
        assertThrows(GameVaultException.class, () -> Parser.parse("tag party 2"));
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("tag 2 party game"));
        assertTrue(e.getMessage().startsWith("TAG should be one word"));
        assertTrue(e.getMessage().endsWith("but was 'party game'."));
    }

    @Test
    void parse_filterByTagInAnyCase_findsLowerCaseTag() throws GameVaultException {
        GameList games = new GameList();
        Game game = new Game("Codenames", 4, 8);
        game.addTag("party");
        games.add(game);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        Command command = Parser.parse("filter /tag PARTY");

        assertInstanceOf(FilterTagCommand.class, command);
        command.execute(games, ui, null); // Filtering does not save, so no Storage is needed.
        assertTrue(output.toString().contains("Codenames"));
    }

    @Test
    void parse_filterByTagWithCountOrTimeOrNoTag_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("filter 4 /tag party"));
        assertTrue(e.getMessage().startsWith("/tag cannot be combined with COUNT or /time."));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag party /time 30"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag party /tag word"));
    }
}
