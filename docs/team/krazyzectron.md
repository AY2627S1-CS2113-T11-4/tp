# Vishwak Nattamai - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.

My main parts of the project were saving the collection so that it is still there the next time the app starts,
tags, which let hosts group games by mood or style (e.g. `party`, `co-op`) and find the right ones quickly,
and keeping track of games that are not on the shelf or not the host's own: games that cannot be played right now,
and games borrowed from or given by someone else.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=KRAZYZECTRON&breakdown=true)

* **New Feature**: Saved the game collection to a data file ([#16](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/16)).
  * What it does: every command that changes the collection saves it to `data/games.txt` straight away,
    and the data folder and file are created when the app starts if they do not exist yet.
  * Justification: without saving, the user would have to type in their whole collection every time they open the app.
  * Highlights: the line format is produced in one place (`Game#toFileString()`), so `Storage` does not need to know
    the fields of a game, and new fields could later be added without changing how saving works.
    Added JUnit tests for saving, loading saved games back, and creating a missing file.

* **New Feature**: Added tags ([#32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32),
  [#33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33)).
  * What it does: `tag` and `untag` add and remove tags such as `party` or `co-op` on a game, `add` accepts tags
    with `/tag`, `tags` lists every tag with how many games have it, and `filter /tag` shows the games with a tag,
    optionally combined with a player count and a time limit (e.g. `filter 6 /time 45 /tag party`).
  * Justification: player count and play time alone cannot tell a host whether a game suits the mood of the night.
    Tags let hosts sort their collection in their own way, and the combined filter answers questions like
    "a party game for 6 people in under 45 minutes" in one command.
  * Highlights:
    * The tag rules are kept in a single `Tag` class, so tags typed by the user and tags read from the data file
      are checked the same way. Tags may only contain letters, digits and hyphens, so they can never contain the
      `|` or `,` used in the data file, and no escaping is needed.
    * Tags are not case-sensitive and kept unique and sorted. `tag` and `untag` accept several tags at once and
      say which ones were skipped. `tags` helps hosts reuse tags instead of creating near-duplicates.
    * Added JUnit tests for `Tag`, the tag commands, tag parsing and decoding tags from the data file.

* **New Feature**: Marked games as unavailable ([#61](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/61)).
  * What it does: `unavailable INDEX /reason missing|damaged` records why a game cannot be played right now, and
    `available INDEX` clears it. `filter` and `pick` leave unavailable games out, and `list` shows the reason.
  * Justification: a host should never be offered a game with missing pieces when the group is waiting to play.
  * Highlights: the reasons are an enum (`UnavailableReason`), so the parser, the data file and the error messages
    share one list, and adding a reason needs only one new value. Games are left out through the same
    `Game#isAvailable()` check as lent games, rather than a second check.

* **New Feature**: Recorded who lent or gave a game, and when it is due back
  ([#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62)).
  * What it does: `borrow INDEX /from NAME [/due YYYY-MM-DD]` records who a game came from and, optionally, when it
    must be given back. `giveback INDEX` removes a borrowed game once it has been returned, and refuses for gifts.
    `list` shows who each game is from and ends with a reminder of any overdue games.
  * Justification: hosts often play games borrowed from friends, and need to know who to give each one back to,
    and by when.
  * Highlights: past due dates are rejected, and today's date is passed into the parser so this can be tested with
    a fixed date. Lender names reuse the `Borrower` rules from `lend`, and `edit` keeps the record.
  * Both features save to named fields in the data file, so older files still load, and have JUnit tests for the
    commands, `Parser`, `GameDecoder` and `Game`.

* **Documentation**:
  * User Guide:
    * Added the sections on tagging (`tag`, `untag`), listing tags (`tags`) and finding games by tag (`filter /tag`),
      and updated `add`, `help`, the data file format and the Command Summary for tags.
    * Added the sections on marking games unavailable (`unavailable`, `available`) and on borrowed games
      (`borrow`, `giveback`), including the overdue reminder in `list`.
    * Updated the `edit` section for `/time` ([#47](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/47)).
  * Developer Guide:
    * Added the Storage section: how games are loaded at startup and saved after each change, and the design
      considerations (file format, handling invalid lines, how to save)
      ([#31](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/31)).
    * Added three UML diagrams for it: a class diagram of the Storage component, and sequence diagrams for loading
      and for saving ([#38](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/38)), and later updated them for tags
      ([#45](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/45)).
    * Added the sections "Marking a game unavailable" and "Recording who lent or gave a game", each with a
      sequence diagram.
    * Wrote the appendices: product scope, user stories, non-functional requirements, glossary and instructions
      for manual testing, plus the Acknowledgements
      ([#69](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/69)).

* **Contributions to team-based tasks**:
  * Wrote the product website's home page and replaced the template README
    ([#69](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/69)).
  * Updated the project name in the README, and the About Us page
    ([#4](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/4),
    [#44](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/44),
    [#49](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/49)).
  * Added `@@author` tags to my code and tests.

* **Review/mentoring contributions**:
  * Reviewed and merged [#34](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/34) (find),
    [#35](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/35) (`@@author` tags) and
    [#37](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/37) (sort).
  * Left review comments on [#39](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/39),
    [#41](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/41),
    [#46](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/46),
    [#48](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/48),
    [#50](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/50) and
    [#59](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/59).
