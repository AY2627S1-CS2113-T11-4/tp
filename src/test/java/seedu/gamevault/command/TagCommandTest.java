package seedu.gamevault.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;
import seedu.gamevault.storage.Storage;
import seedu.gamevault.ui.Ui;

//@@author KRAZYZECTRON
/**
 * Tests for the tag, untag, tags and filter-by-tag commands, which share the same setup.
 */
class TagCommandTest {
    @TempDir
    Path tempDir;

    private ByteArrayOutputStream output;
    private Ui ui;
    private Path dataFile;
    private Storage storage;
    private GameList games;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
        dataFile = tempDir.resolve("games.txt");
        storage = new Storage(dataFile.toString());
        games = new GameList();
        games.add(new Game("Catan", 3, 4, 60));
        games.add(new Game("Codenames", 4, 8));
    }

    @Test
    void tag_newTags_addsAllAndSavesToDataFile() throws GameVaultException, IOException {
        new TagCommand(2, List.of("word", "party")).execute(games, ui, storage);

        assertTrue(output.toString().contains("Tagged: Codenames (4-8 players) [party, word]"));
        assertEquals(List.of("Catan | 3 | 4 | 60", "Codenames | 4 | 8 |  | party,word"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void tag_someTagsAlreadyThere_addsOthersAndReportsSkipped() throws GameVaultException {
        games.get(1).addTag("party");

        new TagCommand(2, List.of("party", "word")).execute(games, ui, storage);

        assertEquals("Codenames (4-8 players) [party, word]", games.get(1).toString());
        assertTrue(output.toString().contains("(Already tagged 'party', so skipped.)"));
    }

    @Test
    void tag_allTagsAlreadyThere_throwsExceptionAndDoesNotSave() {
        games.get(0).addTag("strategy");

        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new TagCommand(1, List.of("strategy")).execute(games, ui, storage));

        assertEquals("Catan is already tagged 'strategy'.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void tagAndUntag_numberPastEndOfList_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new TagCommand(3, List.of("party")).execute(games, ui, storage));
        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), e.getMessage());

        assertThrows(GameVaultException.class, () ->
                new UntagCommand(3, List.of("party")).execute(games, ui, storage));
    }

    @Test
    void untag_someTagsThere_removesThoseReportsOthersAndSaves() throws GameVaultException, IOException {
        games.get(0).addTag("strategy");
        games.get(0).addTag("trading");

        new UntagCommand(1, List.of("trading", "party")).execute(games, ui, storage);

        assertEquals("Catan (3-4 players, 60 min) [strategy]", games.get(0).toString());
        String printed = output.toString();
        assertTrue(printed.contains("Removed tag(s) 'trading'"));
        assertTrue(printed.contains("(Did not have 'party', so skipped.)"));
        assertEquals("Catan | 3 | 4 | 60 | strategy", Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void untag_noTagsThere_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new UntagCommand(2, List.of("party", "word")).execute(games, ui, storage));

        assertEquals("Codenames does not have the tag(s) 'party', 'word'.", e.getMessage());
    }

    @Test
    void listTags_gamesWithTags_showsEachTagWithCountInAlphabeticalOrder() {
        games.get(0).addTag("strategy");
        games.get(0).addTag("family");
        games.get(1).addTag("family");

        new ListTagsCommand().execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("Tags in your collection:"));
        assertTrue(printed.indexOf("family (2 game(s))") < printed.indexOf("strategy (1 game(s))"));
    }

    @Test
    void listTags_noTags_showsHowToAddOne() {
        new ListTagsCommand().execute(games, ui, storage);

        assertTrue(output.toString().contains(ListTagsCommand.NO_TAGS_MESSAGE));
    }

    @Test
    void filterByTag_someGamesHaveTag_showsOnlyThoseGames() {
        games.get(1).addTag("party");

        new FilterTagCommand("party").execute(games, ui, storage);

        String printed = output.toString();
        assertTrue(printed.contains("Games tagged 'party':"));
        assertTrue(printed.contains("Codenames"));
        assertFalse(printed.contains("Catan"));
    }

    @Test
    void filterByTag_withPlayerCountAndTime_showsOnlyGamesFittingAll() {
        games.add(new Game("Werewolf", 8, 18, 30));
        games.add(new Game("Telestrations", 4, 8, 30));
        for (int i = 1; i < 4; i++) {
            games.get(i).addTag("party");
        }

        new FilterTagCommand("party", 6, 45).execute(games, ui, storage);

        // Codenames has no play time and Werewolf needs 8+ players, so only Telestrations fits.
        String printed = output.toString();
        assertTrue(printed.contains("Games tagged 'party' for 6 players in 45 minutes or less:"));
        assertTrue(printed.contains("Telestrations"));
        assertFalse(printed.contains("Codenames"));
        assertFalse(printed.contains("Werewolf"));
    }

    @Test
    void filterByTag_noGameMatches_showsNoGamesMessageWithConditions() {
        new FilterTagCommand("co-op", 2, 0).execute(games, ui, storage);

        assertTrue(output.toString().contains("No games tagged 'co-op' for 2 players."));
    }

    @Test
    void isExit_allTagCommands_returnFalse() {
        assertFalse(new TagCommand(1, List.of("party")).isExit());
        assertFalse(new UntagCommand(1, List.of("party")).isExit());
        assertFalse(new ListTagsCommand().isExit());
        assertFalse(new FilterTagCommand("party").isExit());
    }
}
