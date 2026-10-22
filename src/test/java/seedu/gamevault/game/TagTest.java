package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

//@@author KRAZYZECTRON
class TagTest {

    @Test
    void normalise_mixedCaseAndSpaces_returnsTrimmedLowerCase() {
        assertEquals("co-op", Tag.normalise("  Co-Op "));
    }

    @Test
    void isValid_oneWordOfLettersDigitsAndInnerHyphens_returnsTrue() {
        assertTrue(Tag.isValid("party"));
        assertTrue(Tag.isValid("co-op"));
        assertTrue(Tag.isValid("2p"));
    }

    @Test
    void isValid_spacesSeparatorsOrStrayHyphens_returnsFalse() {
        assertFalse(Tag.isValid("party game"));
        assertFalse(Tag.isValid("party,word"));
        assertFalse(Tag.isValid("a|b"));
        assertFalse(Tag.isValid("-"));
        assertFalse(Tag.isValid("co-"));
        assertFalse(Tag.isValid(""));
    }

    @Test
    void isValid_upperCase_returnsFalseUntilNormalised() {
        assertFalse(Tag.isValid("Party"));
        assertTrue(Tag.isValid(Tag.normalise("Party")));
    }
}
