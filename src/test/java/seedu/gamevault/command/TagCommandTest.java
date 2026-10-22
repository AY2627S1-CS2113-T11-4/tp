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
 * Tests for the tag, untag and filter-by-tag commands, which share the same setup.
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
    void tag_newTag_addsTagAndSavesToDataFile() throws GameVaultException, IOException {
        new TagCommand(2, "party").execute(games, ui, storage);

        assertTrue(games.get(1).hasTag("party"));
        assertTrue(output.toString().contains("Tagged: Codenames (4-8 players) [party]"));
        assertEquals(List.of("Catan | 3 | 4 | 60", "Codenames | 4 | 8 |  | party"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void tag_tagAlreadyThere_throwsExceptionAndDoesNotSave() throws GameVaultException {
        games.get(0).addTag("strategy");

        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new TagCommand(1, "strategy").execute(games, ui, storage));

        assertEquals("Catan is already tagged 'strategy'.", e.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void tagAndUntag_numberPastEndOfList_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new TagCommand(3, "party").execute(games, ui, storage));
        assertEquals(DeleteCommand.getNoSuchGameMessage("3"), e.getMessage());

        assertThrows(GameVaultException.class, () -> new UntagCommand(3, "party").execute(games, ui, storage));
    }

    @Test
    void untag_existingTag_removesTagAndSavesToDataFile() throws GameVaultException, IOException {
        games.get(0).addTag("strategy");
        games.get(0).addTag("trading");

        new UntagCommand(1, "trading").execute(games, ui, storage);

        assertEquals("Catan (3-4 players, 60 min) [strategy]", games.get(0).toString());
        assertTrue(output.toString().contains("Removed tag 'trading'"));
        assertEquals("Catan | 3 | 4 | 60 | strategy", Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0));
    }

    @Test
    void untag_tagNotThere_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () ->
                new UntagCommand(2, "party").execute(games, ui, storage));

        assertEquals("Codenames does not have the tag 'party'.", e.getMessage());
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
    void filterByTag_noGameHasTag_showsNoGamesMessage() {
        new FilterTagCommand("co-op").execute(games, ui, storage);

        assertTrue(output.toString().contains("No games are tagged 'co-op'."));
    }

    @Test
    void isExit_allTagCommands_returnFalse() {
        assertFalse(new TagCommand(1, "party").isExit());
        assertFalse(new UntagCommand(1, "party").isExit());
        assertFalse(new FilterTagCommand("party").isExit());
    }
}
