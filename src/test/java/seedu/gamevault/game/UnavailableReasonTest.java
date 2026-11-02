package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

//@@author KRAZYZECTRON
class UnavailableReasonTest {
    @Test
    void fromKeyword_anyCaseAndSpaces_returnsReason() {
        assertEquals(Optional.of(UnavailableReason.MISSING), UnavailableReason.fromKeyword("missing"));
        assertEquals(Optional.of(UnavailableReason.DAMAGED), UnavailableReason.fromKeyword("  DAMAGED "));
    }

    @Test
    void fromKeyword_unknownOrEmpty_returnsEmpty() {
        assertEquals(Optional.empty(), UnavailableReason.fromKeyword("lent"));
        assertEquals(Optional.empty(), UnavailableReason.fromKeyword(""));
    }

    @Test
    void keywords_everyReasonInOrder() {
        assertEquals(List.of("missing", "damaged"), UnavailableReason.keywords());
    }

    @Test
    void describeKeywords_listsEveryKeyword() {
        assertEquals("missing or damaged", UnavailableReason.describeKeywords());
    }
}
