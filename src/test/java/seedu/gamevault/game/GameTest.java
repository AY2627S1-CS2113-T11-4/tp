package seedu.gamevault.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

//@@author yy261
class GameTest {

    @Test
    void fitsPlayerCount_countWithinRange_returnsTrue() {
        Game game = new Game("Catan", 3, 5);
        // The minimum and maximum themselves are included in the range.
        assertTrue(game.fitsPlayerCount(3));
        assertTrue(game.fitsPlayerCount(4));
        assertTrue(game.fitsPlayerCount(5));
    }

    @Test
    void fitsPlayerCount_countOutsideRange_returnsFalse() {
        Game game = new Game("Catan", 3, 5);
        assertFalse(game.fitsPlayerCount(2));
        assertFalse(game.fitsPlayerCount(6));
    }

    @Test
    void fitsPlayerCount_sameMinAndMax_fitsOnlyThatCount() {
        Game game = new Game("Chess", 2, 2);
        assertTrue(game.fitsPlayerCount(2));
        assertFalse(game.fitsPlayerCount(1));
        assertFalse(game.fitsPlayerCount(3));
    }

    @Test
    void fitsTime_playTimeWithinLimit_returnsTrue() {
        Game game = new Game("Catan", 3, 4, 60);
        assertTrue(game.fitsTime(60));
        assertTrue(game.fitsTime(90));
    }

    @Test
    void fitsTime_playTimeOverLimit_returnsFalse() {
        assertFalse(new Game("Catan", 3, 4, 60).fitsTime(59));
    }

    @Test
    void fitsTime_noPlayTimeRecorded_returnsFalse() {
        Game game = new Game("Catan", 3, 4);
        assertFalse(game.hasPlayTime());
        assertFalse(game.fitsTime(1000));
    }

    @Test
    void toString_withAndWithoutPlayTime_showsTimeOnlyWhenRecorded() {
        assertEquals("Catan (3-4 players, 60 min)", new Game("Catan", 3, 4, 60).toString());
        assertEquals("Chess (2 players)", new Game("Chess", 2, 2).toString());
    }

    @Test
    void toFileString_withAndWithoutPlayTime_addsTimeFieldOnlyWhenRecorded() {
        assertEquals("Catan | 3 | 4 | 60", new Game("Catan", 3, 4, 60).toFileString());
        assertEquals("Chess | 2 | 2", new Game("Chess", 2, 2).toFileString());
    }

    //@@author KRAZYZECTRON
    @Test
    void addTagAndRemoveTag_changeTagsKeptInAlphabeticalOrder() {
        Game game = new Game("Catan", 3, 4);
        game.addTag("trading");
        game.addTag("strategy");
        assertTrue(game.hasTag("strategy"));
        assertEquals(List.of("strategy", "trading"), List.copyOf(game.getTags()));

        game.removeTag("trading");
        assertFalse(game.hasTag("trading"));
        assertEquals(List.of("strategy"), List.copyOf(game.getTags()));
    }

    @Test
    void toString_withTags_listsTagsAfterDetails() {
        Game game = new Game("Catan", 3, 4, 60);
        game.addTag("strategy");
        game.addTag("family");
        assertEquals("Catan (3-4 players, 60 min) [family, strategy]", game.toString());
    }

    @Test
    void toFileString_withTags_putsTagsInFifthFieldWithTimeBlankIfNotRecorded() {
        Game timed = new Game("Catan", 3, 4, 60);
        timed.addTag("strategy");
        Game untimed = new Game("Codenames", 4, 8);
        untimed.addTag("word");
        untimed.addTag("party");

        assertEquals("Catan | 3 | 4 | 60 | strategy", timed.toFileString());
        assertEquals("Codenames | 4 | 8 |  | party,word", untimed.toFileString());
    }

    //@@author MohamedFarsat
    @Test
    void newGame_notRated_hasWeightOneAndNoRatingShown() {
        Game game = new Game("Catan", 3, 4);
        assertFalse(game.isRated());
        assertEquals(1.0, game.getPickWeight());
        assertEquals("Catan (3-4 players)", game.toString());
        assertEquals("Catan | 3 | 4", game.toFileString());
    }

    @Test
    void addRating_upAndDown_countsAddUpAndAreShown() {
        Game game = new Game("Catan", 3, 4, 60);
        game.addRating(Thumb.UP);
        game.addRating(Thumb.UP);
        game.addRating(Thumb.DOWN);
        assertTrue(game.isRated());
        assertEquals("+2/-1", game.describeRatings());
        assertEquals("Catan (3-4 players, 60 min) +2/-1", game.toString());
    }

    @Test
    void getPickWeight_variousRatings_isUpPlusOneOverDownPlusOne() {
        Game game = new Game("Catan", 3, 4);
        game.setRatings(3, 0);
        assertEquals(4.0, game.getPickWeight());
        game.setRatings(0, 3);
        assertEquals(0.25, game.getPickWeight());
        game.setRatings(2, 2);
        assertEquals(1.0, game.getPickWeight());
    }

    @Test
    void toFileString_ratedGame_writesRatingsField() {
        Game game = new Game("Catan", 3, 4, 60);
        game.addTag("strategy");
        game.setRatings(5, 1);
        assertEquals("Catan | 3 | 4 | 60 | strategy | ratings=5/1", game.toFileString());
    }

    @Test
    void toString_ratedAndLentGame_showsRatingBeforeBorrower() {
        Game game = new Game("Catan", 3, 4);
        game.addTag("strategy");
        game.setRatings(1, 0);
        game.lendTo("Alice");
        assertEquals("Catan (3-4 players) [strategy] +1/-0 (lent to Alice)", game.toString());
    }

    @Test
    void getPickWeight_extremeRatings_keptBetweenLimits() {
        Game game = new Game("Catan", 3, 4);
        game.setRatings(30, 0); // (30 + 1) / 1 = 31 without the limit.
        assertEquals(Game.MAX_PICK_WEIGHT, game.getPickWeight());
        game.setRatings(4, 0); // Exactly 5, so not changed.
        assertEquals(5.0, game.getPickWeight());
        game.setRatings(0, 30);
        assertEquals(Game.MIN_PICK_WEIGHT, game.getPickWeight());
    }

    @Test
    void removeRating_ratedGame_removesOneOfThatKindOnly() {
        Game game = new Game("Catan", 3, 4);
        game.setRatings(2, 1);
        assertTrue(game.canRemoveRating(Thumb.DOWN));
        game.removeRating(Thumb.DOWN);
        assertEquals("+2/-0", game.describeRatings());
        assertFalse(game.canRemoveRating(Thumb.DOWN));
        assertTrue(game.canRemoveRating(Thumb.UP));
    }

    @Test
    void canAddRating_countAtLimit_returnsFalseOnlyForThatKind() {
        Game game = new Game("Catan", 3, 4);
        assertTrue(game.canAddRating(Thumb.UP));
        game.setRatings(Integer.MAX_VALUE, 0);
        assertFalse(game.canAddRating(Thumb.UP));
        assertTrue(game.canAddRating(Thumb.DOWN));
        assertEquals(Integer.MAX_VALUE, game.getRatingCount(Thumb.UP));
        assertEquals(0, game.getRatingCount(Thumb.DOWN));
    }

    @Test
    void thumbFromKeyword_anyCaseAndSpaces_findsThumb() {
        assertEquals(Thumb.UP, Thumb.fromKeyword(" UP ").orElseThrow());
        assertEquals(Thumb.DOWN, Thumb.fromKeyword("Down").orElseThrow());
        assertTrue(Thumb.fromKeyword("sideways").isEmpty());
        assertTrue(Thumb.fromKeyword("").isEmpty());
    }
}
