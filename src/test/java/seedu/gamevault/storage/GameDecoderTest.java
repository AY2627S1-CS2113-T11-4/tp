package seedu.gamevault.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(GameVaultException.class, () -> GameDecoder.decodeLine("Catan | 3 | 4 | 5"));
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
}
