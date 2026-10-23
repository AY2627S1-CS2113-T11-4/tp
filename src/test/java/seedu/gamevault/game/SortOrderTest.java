package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

//@@author SwaggyDaddy67
/**
 * Tests for the orders used by the sort command.
 */
class SortOrderTest {

    /**
     * Returns the titles of the given games after sorting a copy of them in the given order.
     */
    private static List<String> sortedTitles(SortOrder order, Game... games) {
        List<Game> sorted = new ArrayList<>(List.of(games));
        sorted.sort(order.getComparator());
        List<String> titles = new ArrayList<>();
        for (Game game : sorted) {
            titles.add(game.getTitle());
        }
        return titles;
    }

    @Test
    void title_mixedCase_sortsAlphabeticallyIgnoringCase() {
        List<String> titles = sortedTitles(SortOrder.TITLE,
                new Game("codenames", 4, 8), new Game("Azul", 2, 4), new Game("Catan", 3, 4));

        assertEquals(List.of("Azul", "Catan", "codenames"), titles);
    }

    @Test
    void players_differentRanges_sortsByMinimumThenMaximum() {
        List<String> titles = sortedTitles(SortOrder.PLAYERS,
                new Game("Werewolf", 8, 18), new Game("Codenames", 2, 8),
                new Game("Catan", 3, 4), new Game("Chess", 2, 2));

        assertEquals(List.of("Chess", "Codenames", "Catan", "Werewolf"), titles);
    }

    @Test
    void players_sameRange_sortsTiesByTitle() {
        List<String> titles = sortedTitles(SortOrder.PLAYERS,
                new Game("Splendor", 2, 4), new Game("Azul", 2, 4));

        assertEquals(List.of("Azul", "Splendor"), titles);
    }

    @Test
    void time_someGamesUntimed_putsShortestFirstAndUntimedLast() {
        List<String> titles = sortedTitles(SortOrder.TIME,
                new Game("Codenames", 4, 8), new Game("Catan", 3, 4, 60),
                new Game("Love Letter", 2, 4, 20), new Game("Azul", 2, 4));

        assertEquals(List.of("Love Letter", "Catan", "Azul", "Codenames"), titles);
    }

    @Test
    void fromKeyword_validKeywordsInAnyCase_returnsMatchingOrder() {
        assertEquals(SortOrder.TITLE, SortOrder.fromKeyword("title").orElseThrow());
        assertEquals(SortOrder.PLAYERS, SortOrder.fromKeyword(" PLAYERS ").orElseThrow());
        assertEquals(SortOrder.TIME, SortOrder.fromKeyword("Time").orElseThrow());
    }

    @Test
    void fromKeyword_unknownKeyword_returnsEmpty() {
        assertTrue(SortOrder.fromKeyword("rating").isEmpty());
        assertTrue(SortOrder.fromKeyword("").isEmpty());
    }

    @Test
    void describeKeywords_allOrders_listsThemForMessages() {
        assertEquals("title, players or time", SortOrder.describeKeywords());
    }
}
