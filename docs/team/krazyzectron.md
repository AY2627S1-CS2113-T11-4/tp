# Vishwak Nattamai - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.

My main parts were saving the collection, tags, and keeping track of games that cannot be played right now or
that were borrowed from or given by someone else.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=KRAZYZECTRON&breakdown=true)

* **New Feature**: Saved the game collection to a data file ([#16](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/16)).
  * What it does: every change is saved to `data/games.txt` straight away, and the file is created if missing.
  * Justification: without saving, the user would have to type in their whole collection every time.
  * Highlights: the line format is produced in one place (`Game#toFileString()`), so `Storage` does not need to
    know the fields of a game, and new fields can be added without changing how saving works.

* **New Feature**: Added tags ([#32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32),
  [#33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33)).
  * What it does: `tag`/`untag` add and remove tags such as `party`, `add` accepts `/tag`, `tags` lists every tag,
    and `filter /tag` can be combined with a player count and time, e.g. `filter 6 /time 45 /tag party`.
  * Justification: player count and play time alone cannot tell a host whether a game suits the mood of the night.
  * Highlights: one `Tag` class checks tags from the user and from the data file the same way. Tags only allow
    letters, digits and hyphens, so they never clash with the `|` and `,` separators and need no escaping.

* **New Feature**: Marked games as unavailable ([#61](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/61)).
  * What it does: `unavailable INDEX /reason missing|damaged` and `available INDEX`. `filter` and `pick` leave
    unavailable games out, and `list` shows the reason.
  * Highlights: the reasons are an enum, so the parser, data file and error messages share one list. Games are
    left out through the same `Game#isAvailable()` check as lent games, rather than a second check.

* **New Feature**: Recorded who lent or gave a game, and when it is due back
  ([#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62)).
  * What it does: `borrow INDEX /from NAME [/due YYYY-MM-DD]` and `giveback INDEX`. `list` shows who each game
    is from and reminds the user of overdue games.
  * Highlights: past due dates are rejected, and today's date is passed into the parser so this can be tested
    with a fixed date. Lender names reuse the `Borrower` rules from `lend`.

* All four features have JUnit tests, and the last three save to optional fields, so older data files still load.

* **Documentation**:
  * User Guide: the sections on `tag`, `untag`, `tags`, `filter /tag`, `unavailable`, `available`, `borrow` and
    `giveback`, the overdue reminder in `list`, and `edit /time`
    ([#47](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/47)).
  * Developer Guide: the Storage section with a class diagram and two sequence diagrams
    ([#31](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/31),
    [#38](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/38),
    [#45](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/45)), the "Marking a game unavailable" and
    "Recording who lent or gave a game" sections with a sequence diagram each, and the Acknowledgements and
    appendices: product scope, user stories, NFRs, glossary and manual testing
    ([#69](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/69)).

* **Contributions to team-based tasks**: wrote the website home page and the README
  ([#69](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/69)), updated About Us
  ([#4](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/4),
  [#44](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/44),
  [#49](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/49)), and added `@@author` tags to my code and tests.

* **Review/mentoring contributions**: reviewed and merged
  [#34](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/34),
  [#35](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/35) and
  [#37](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/37), and left review comments on
  [#39](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/39),
  [#41](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/41),
  [#46](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/46),
  [#48](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/48),
  [#50](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/50) and
  [#59](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/59).
