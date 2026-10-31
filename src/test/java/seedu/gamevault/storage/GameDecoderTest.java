package seedu.gamevault.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;

class GameDecoderTest {

    @Test
    void decodeLine_validLine_returnsGame() throws GameVaultException {
        Game game = GameDecoder.decodeLine("Catan | 3 | 4");
        assertEquals("Catan", game.getTitle());
        assertEquals(3, game.getMinPlayers());
        assertEquals(4, game.getMaxPlayers());
    }

    @Test
    void decodeLine_extraSpacesAndSamePlayerCount_trimmedAndAccepted() throws GameVaultException {
        Game game = GameDecoder.decodeLine("   Ticket to Ride: Europe|2|   2  ");
        assertEquals("Ticket to Ride: Europe", game.getTitle());
        assertEquals(2, game.getMinPlayers());
        assertEquals(2, game.getMaxPlayers());
    }

    @Test
    void decodeLine_byteOrderMarkAtStart_ignored() throws GameVaultException {
        assertEquals("Catan", GameDecoder.decodeLine("﻿Catan | 3 | 4").getTitle());
    }

    @Test
    void decodeLine_toFileStringOutput_decodesToSameGame() throws GameVaultException {
        Game original = new Game("Werewolf", 8, 18);
        Game decoded = GameDecoder.decodeLine(original.toFileString());
        assertEquals(original.getTitle(), decoded.getTitle());
        assertEquals(original.getMinPlayers(), decoded.getMinPlayers());
        assertEquals(original.getMaxPlayers(), decoded.getMaxPlayers());
    }

    @Test
    void decodeLine_wrongNumberOfParts_throwsException() {
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 |"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 | 4 | 60 | party | Alice | 5"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan, 3, 4"));
    }

    @Test
    void decodeLine_emptyTitle_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("  | 3 | 4"));
        assertEquals("TITLE is empty", e.getMessage());
    }

    @Test
    void decodeLine_nonNumericPlayerCount_throwsExceptionNamingField() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | three | 4"));
        assertEquals("MIN should be a whole number, but was 'three'", e.getMessage());
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 | 4.5"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 | 99999999999"));
    }

    @Test
    void decodeLine_playerCountBelowOne_throwsException() {
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 0 | 4"));
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | -2 | 4"));
    }

    @Test
    void decodeLine_maxLessThanMin_throwsException() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | 5 | 2"));
        assertEquals("MAX (2) is less than MIN (5)", e.getMessage());
    }

    //@@author yy261
    @Test
    void decodeLine_withPlayTime_returnsGameWithPlayTime() throws GameVaultException {
        Game game = GameDecoder.decodeLine("Catan | 3 | 4 | 60");
        assertTrue(game.hasPlayTime());
        assertEquals(60, game.getPlayTime());
    }

    @Test
    void decodeLine_oldFormatOrEmptyTime_returnsGameWithoutPlayTime() throws GameVaultException {
        assertFalse(GameDecoder.decodeLine("Catan | 3 | 4").hasPlayTime());
        assertFalse(GameDecoder.decodeLine("Catan | 3 | 4 |  ").hasPlayTime());
    }

    @Test
    void decodeLine_invalidPlayTime_throwsExceptionNamingField() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | 3 | 4 | an hour"));
        assertEquals("TIME should be a whole number, but was 'an hour'", e.getMessage());
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 | 4 | 0"));
    }

    @Test
    void decodeLine_toFileStringWithPlayTime_decodesToSameGame() throws GameVaultException {
        Game original = new Game("Catan", 3, 4, 60);
        assertEquals(original.toString(), GameDecoder.decodeLine(original.toFileString()).toString());
    }

    //@@author KRAZYZECTRON
    @Test
    void decodeLine_withTagsAndBlankTime_returnsTaggedGameWithoutPlayTime() throws GameVaultException {
        Game game = GameDecoder.decodeLine("Codenames | 4 | 8 |  | Party, word ,");
        assertFalse(game.hasPlayTime());
        assertTrue(game.hasTag("party"));
        assertTrue(game.hasTag("word"));
        assertEquals(2, game.getTags().size());
    }

    @Test
    void decodeLine_repeatedOrEmptyTags_ignored() throws GameVaultException {
        assertEquals(1, GameDecoder.decodeLine("Catan | 3 | 4 | 60 | strategy,STRATEGY").getTags().size());
        assertTrue(GameDecoder.decodeLine("Catan | 3 | 4 | 60 |  ").getTags().isEmpty());
    }

    @Test
    void decodeLine_invalidTag_throwsExceptionNamingTag() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | 3 | 4 | 60 | party game"));
        assertTrue(e.getMessage().startsWith("TAGS contains 'party game'"));
    }

    @Test
    void decodeLine_toFileStringWithTags_decodesToSameGame() throws GameVaultException {
        Game original = new Game("Codenames", 4, 8);
        original.addTag("party");
        original.addTag("co-op");
        assertEquals(original.toString(), GameDecoder.decodeLine(original.toFileString()).toString());
    }

    //@@author yy261
    @Test
    void decodeLine_withBorrower_returnsLentGame() throws GameVaultException {
        Game game = GameDecoder.decodeLine("Hive | 2 | 2 |  |  | lent=  Mary   Tan ");
        assertTrue(game.isLent());
        // The name is normalised in the same way as when it is typed with 'lend'.
        assertEquals("Mary Tan", game.getBorrower());
        assertFalse(game.hasPlayTime());
        assertTrue(game.getTags().isEmpty());
    }

    @Test
    void decodeLine_noLentField_returnsGameOnShelf() throws GameVaultException {
        assertFalse(GameDecoder.decodeLine("Catan | 3 | 4 | 60 | strategy").isLent());
    }

    @Test
    void decodeLine_blankLentField_throwsException() {
        assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | 3 | 4 | 60 | strategy | lent="));
    }

    @Test
    void decodeLine_borrowerTooLong_throwsExceptionNamingField() {
        String longName = "a".repeat(31);
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Hive | 2 | 2 |  |  | lent=" + longName));
        assertTrue(e.getMessage().startsWith("lent is '" + longName + "'"));
    }

    @Test
    void decodeLine_toFileStringOfLentGame_decodesToSameGame() throws GameVaultException {
        Game original = new Game("Catan", 3, 4, 60);
        original.addTag("strategy");
        original.lendTo("Alice");
        assertEquals(original.toString(), GameDecoder.decodeLine(original.toFileString()).toString());
    }

    //@@author MohamedFarsat
    @Test
    void decodeLine_withRatings_returnsRatedGame() throws GameVaultException {
        Game game = GameDecoder.decodeLine("Catan | 3 | 4 |  |  | ratings=5/1");
        assertEquals(5, game.getThumbsUp());
        assertEquals(1, game.getThumbsDown());
        assertEquals("+0/-2", GameDecoder.decodeLine("Hive | 2 | 2 |  |  | RATINGS = 0 / 2").describeRatings());
    }

    @Test
    void decodeLine_noRatingsField_returnsUnratedGame() throws GameVaultException {
        assertFalse(GameDecoder.decodeLine("Catan | 3 | 4 | 60 | strategy").isRated());
    }

    @Test
    void decodeLine_invalidRatings_throwsException() {
        String[] invalidValues = {"", "5", "5/1/2", "/1", "five/1", "5/-1", "-1/0", "1.5/0", "99999999999/0"};
        for (String value : invalidValues) {
            assertThrows(GameVaultException.class,
                    () -> GameDecoder.decodeLine("Catan | 3 | 4 |  |  | ratings=" + value), value);
        }
    }

    @Test
    void decodeLine_negativeRating_throwsExceptionNamingField() {
        GameVaultException e = assertThrows(GameVaultException.class,
                () -> GameDecoder.decodeLine("Catan | 3 | 4 |  |  | ratings=2/-1"));
        assertEquals("DOWN in ratings should not be negative, but was -1", e.getMessage());
    }

    @Test
    void decodeLine_toFileStringOfRatedGameWithEverything_decodesToSameGame() throws GameVaultException {
        Game original = new Game("Catan", 3, 4, 60);
        original.addTag("strategy");
        original.lendTo("Alice");
        original.setRatings(3, 2);
        Game decoded = GameDecoder.decodeLine(original.toFileString());
        assertEquals(original.toString(), decoded.toString());
        assertEquals(original.toFileString(), decoded.toFileString());
    }
}
