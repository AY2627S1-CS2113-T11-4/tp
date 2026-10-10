package seedu.gamevault.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.GameList;

class StorageTest {
    /** JUnit creates a fresh empty folder for each test and deletes it afterwards. */
    @TempDir
    Path tempDir;

    private Storage createStorageWithLines(String... lines) throws IOException {
        Path file = tempDir.resolve("games.txt");
        Files.write(file, List.of(lines), StandardCharsets.UTF_8);
        return new Storage(file.toString());
    }

    @Test
    void load_missingFile_returnsEmptyListWithNoSkippedLines() throws GameVaultException {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    void load_allLinesValid_loadsEveryGame() throws Exception {
        Storage storage = createStorageWithLines("Catan | 3 | 4", "Codenames | 2 | 8", "Werewolf | 8 | 18");
        ArrayList<Game> games = storage.load();
        assertEquals(3, games.size());
        assertEquals("Codenames", games.get(1).getTitle());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    void load_someLinesInvalid_skipsThemAndKeepsTheRest() throws Exception {
        Storage storage = createStorageWithLines(
                "Catan | 3 | 4",
                "this line is corrupted",
                "Codenames | two | 8",
                "Werewolf | 8 | 18");
        ArrayList<Game> games = storage.load();

        assertEquals(2, games.size());
        assertEquals("Catan", games.get(0).getTitle());
        assertEquals("Werewolf", games.get(1).getTitle());

        List<String> skipped = storage.getSkippedLines();
        assertEquals(2, skipped.size());
        assertTrue(skipped.get(0).startsWith("Line 2: \"this line is corrupted\""));
        assertTrue(skipped.get(1).startsWith("Line 3: \"Codenames | two | 8\""));
        assertTrue(skipped.get(1).contains("MIN should be a whole number"));
    }

    @Test
    void load_blankLines_ignoredButLineNumbersStayAccurate() throws Exception {
        Storage storage = createStorageWithLines("", "Catan | 3 | 4", "   ", "bad line");
        assertEquals(1, storage.load().size());
        assertEquals(1, storage.getSkippedLines().size());
        assertTrue(storage.getSkippedLines().get(0).startsWith("Line 4:"));
    }

    @Test
    void load_calledAgain_forgetsPreviouslySkippedLines() throws Exception {
        Path file = tempDir.resolve("games.txt");
        Files.writeString(file, "bad line\n");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertEquals(1, storage.getSkippedLines().size());

        Files.writeString(file, "Catan | 3 | 4\n");
        storage.load();
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    void save_games_writesOneLinePerGameInFileFormat() throws Exception {
        Path file = tempDir.resolve("games.txt");
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Chess", 2, 2));

        new Storage(file.toString()).save(games);

        assertEquals(List.of("Catan | 3 | 4", "Chess | 2 | 2"), Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    void save_thenLoad_returnsSameGames() throws Exception {
        Storage storage = new Storage(tempDir.resolve("games.txt").toString());
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        games.add(new Game("Werewolf", 8, 18));

        storage.save(games);
        ArrayList<Game> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("Catan", loaded.get(0).getTitle());
        assertEquals(8, loaded.get(1).getMinPlayers());
        assertEquals(18, loaded.get(1).getMaxPlayers());
        assertTrue(storage.getSkippedLines().isEmpty());
    }

    @Test
    void save_missingFolders_createsThem() throws Exception {
        Path file = tempDir.resolve("data").resolve("nested").resolve("games.txt");
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));

        new Storage(file.toString()).save(games);

        assertTrue(Files.exists(file));
    }

    @Test
    void save_calledAgain_replacesOldContents() throws Exception {
        Path file = tempDir.resolve("games.txt");
        Storage storage = new Storage(file.toString());
        GameList games = new GameList();
        games.add(new Game("Catan", 3, 4));
        storage.save(games);

        storage.save(new GameList());

        assertTrue(Files.readAllLines(file, StandardCharsets.UTF_8).isEmpty());
    }

    @Test
    void createFileIfMissing_missingFileAndFolder_createsEmptyFile() throws Exception {
        Path file = tempDir.resolve("data").resolve("games.txt");

        new Storage(file.toString()).createFileIfMissing();

        assertTrue(Files.exists(file));
        assertEquals(0, Files.size(file));
    }

    @Test
    void createFileIfMissing_existingFile_leavesContentsUntouched() throws Exception {
        Path file = tempDir.resolve("games.txt");
        Files.writeString(file, "Catan | 3 | 4\n");

        new Storage(file.toString()).createFileIfMissing();

        assertEquals("Catan | 3 | 4\n", Files.readString(file));
    }
}
