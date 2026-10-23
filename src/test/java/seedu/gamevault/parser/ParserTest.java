package seedu.gamevault.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.command.AddCommand;
import seedu.gamevault.command.DeleteCommand;
import seedu.gamevault.command.EditCommand;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.FilterCommand;
import seedu.gamevault.command.FilterTagCommand;
import seedu.gamevault.command.FindCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.command.ListCommand;
import seedu.gamevault.command.ListTagsCommand;
import seedu.gamevault.command.TagCommand;
import seedu.gamevault.command.UntagCommand;
import seedu.gamevault.command.PickCommand;
import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
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
    void parse_tagUntagAndTags_returnTagCommands() throws GameVaultException {
        assertInstanceOf(TagCommand.class, Parser.parse("tag 2 party"));
        assertInstanceOf(UntagCommand.class, Parser.parse("UNTAG 2 co-op"));
        assertInstanceOf(ListTagsCommand.class, Parser.parse("tags"));
        assertInstanceOf(FilterTagCommand.class, Parser.parse("filter 4 /tag party"));
        assertThrows(GameVaultException.class, () -> Parser.parse("tags party"));
    }

    @Test
    void parse_tagWithMissingParts_throwsExceptionSayingWhatIsMissing() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("tag"));
        assertEquals("INDEX and TAG are missing. Format: " + TagCommand.USAGE + " (e.g. tag 2 party word)",
                e.getMessage());
        e = assertThrows(GameVaultException.class, () -> Parser.parse("untag 2"));
        assertEquals("TAG is missing. Format: " + UntagCommand.USAGE + " (e.g. untag 2 party)", e.getMessage());
    }

    @Test
    void parse_tagWithInvalidIndexOrTag_throwsException() {
        assertThrows(GameVaultException.class, () -> Parser.parse("tag 0 party"));
        assertThrows(GameVaultException.class, () -> Parser.parse("tag party 2"));
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("tag 2 party b|d"));
        assertTrue(e.getMessage().startsWith("TAG should be one word"));
        assertTrue(e.getMessage().endsWith("but was 'b|d'."));
    }

    @Test
    void parse_tagWithSeveralTags_addsEachOnceInLowerCase(@TempDir Path tempDir) throws GameVaultException {
        GameList games = new GameList();
        games.add(new Game("Codenames", 4, 8));

        run("tag 1 Party word PARTY", games, new Storage(tempDir.resolve("games.txt").toString()));

        assertEquals(List.of("party", "word"), List.copyOf(games.get(0).getTags()));
    }

    @Test
    void parse_addWithTags_addsTaggedGame(@TempDir Path tempDir) throws GameVaultException {
        GameList games = new GameList();

        Storage storage = new Storage(tempDir.resolve("games.txt").toString());
        run("add Codenames /tag party word /min 4 /max 8", games, storage);

        assertEquals("Codenames (4-8 players) [party, word]", games.get(0).toString());
        assertThrows(GameVaultException.class, () -> Parser.parse("add Codenames /min 4 /max 8 /tag"));
        assertThrows(GameVaultException.class, () -> Parser.parse("add Codenames /min 4 /max 8 /tag a /tag b"));
    }

    @Test
    void parse_filterByTagInAnyCase_findsLowerCaseTag() throws GameVaultException {
        String printed = run("filter /tag PARTY", createGamesTaggedParty(), null);

        assertTrue(printed.contains("Games tagged 'party':"));
        assertTrue(printed.contains("Codenames"));
    }

    @Test
    void parse_filterByTagWithCountAndTime_appliesAllConditions() throws GameVaultException {
        // Filtering does not save, so no Storage is needed.
        String printed = run("filter 6 /time 30 /tag party", createGamesTaggedParty(), null);

        assertTrue(printed.contains("Games tagged 'party' for 6 players in 30 minutes or less:"));
        assertTrue(printed.contains("Telestrations"));
        assertFalse(printed.contains("Codenames"));
    }

    @Test
    void parse_filterByTagWithInvalidParts_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("filter 0 /tag party"));
        assertEquals("COUNT should be at least 1, but was 0.", e.getMessage());
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag party /time soon"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag party word"));
        assertThrows(GameVaultException.class, () -> Parser.parse("filter /tag party /tag word"));
    }

    /**
     * Returns games where Codenames (no play time) and Telestrations (30 min) are both tagged party.
     */
    private static GameList createGamesTaggedParty() {
        GameList games = new GameList();
        games.add(new Game("Codenames", 4, 8));
        games.add(new Game("Telestrations", 4, 8, 30));
        games.get(0).addTag("party");
        games.get(1).addTag("party");
        return games;
    }

    /**
     * Parses and runs the input on the given games, and returns what it printed.
     */
    private static String run(String input, GameList games, Storage storage) throws GameVaultException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        Parser.parse(input).execute(games, ui, storage);
        return output.toString();
    }

    //@@author SwaggyDaddy67
    @Test
    void parse_validFind_returnsFindCommand() throws GameVaultException {
        assertInstanceOf(FindCommand.class, Parser.parse("find mono"));
        assertInstanceOf(FindCommand.class, Parser.parse("FIND   ticket to "));
    }

    @Test
    void parse_findWithoutKeyword_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> Parser.parse("find   "));
        assertEquals("KEYWORD is missing. Format: " + FindCommand.USAGE + " (e.g. find cat)", e.getMessage());
    }
}
