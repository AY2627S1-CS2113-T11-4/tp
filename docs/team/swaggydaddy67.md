# Bhao Tanush - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs. It keeps track of a game
collection and quickly finds games that suit the group that turned up, by player count, play time and tags.

My main area was **working with the collection once games are in it**: viewing it (`list`), removing games
(`delete`), locating a game by its title (`find`), reordering the whole collection (`sort`), and looking back at
what the group has played (`history`, `unplayed`).

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=SwaggyDaddy67&breakdown=true)

### Enhancements implemented

* **Listing all games** (`list`), v1.0 ([PR #17](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/17))
  * What it does: shows every game numbered from 1. These numbers are the `INDEX` that `delete`, `edit` and `tag`
    use, so `list` is how users refer to a game.
  * Extra text after the command (e.g. `list all`) is reported as an error instead of being ignored.
  * Moved the numbering into a shared helper in `ListCommand`, so `list` and `sort` print the collection the same
    way without duplicating the loop. A teammate later extended this helper to show when each game was last played.

* **Deleting a game** (`delete INDEX`), v1.0 ([PR #19](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/19))
  * What it does: removes a game by its number in `list` and saves the change to the data file.
  * Gives a specific error for each kind of bad `INDEX`: missing, not a whole number, below 1, past the end of the
    list, and a number too large for an `int`. For example, `delete 99999999999` reports that there is no game with
    that number, instead of claiming it is not a whole number. Nothing is deleted when the input is invalid.
  * The `INDEX` checks were later extracted into `Parser#parseIndex` and are now reused by `edit`, `tag` and `untag`.

* **Finding games by title** (`find KEYWORD`), v2.0 ([PR #34](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/34))
  * What it does: shows every game whose title contains the keyword, ignoring case (`mono` finds "Monopoly").
    A keyword with spaces is matched as one phrase (`find ticket to` finds "Ticket to Ride").
  * Each result keeps its number from `list`, so it can be passed straight to `delete`, `edit` or `tag`.
    `FindCommand` goes through the collection by position instead of filtering it first, which is what keeps the
    original numbers. Renumbering the results from 1 was rejected because `delete 1` after a search could then
    delete the wrong game.

* **Sorting the collection** (`sort title|players|time`), v2.0
  ([PR #37](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/37))
  * What it does: reorders the whole collection by title, player count or play time, saves the new order, and
    shows the renumbered list.
  * Built around a `SortOrder` enumeration that pairs each keyword with its description and `Comparator`.
    Adding a new order only needs one new enum value; the parser and the error messages pick it up automatically.
  * Handled the edge cases: games with no play time go last when sorting by time, ties are broken by title so
    sorting twice always gives the same result, and an empty collection is neither sorted nor saved.

* **Viewing play history and never-played games** (`history INDEX`, `unplayed`), v2.1
  ([PR #67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67))
  * What it does: `history` lists every session logged for a game, oldest first, followed by how many times it was
    played and when it was last played. `unplayed` lists the games with no sessions, so games that keep being passed
    over get a fair chance.
  * Built on the play sessions recorded by `log`, so it needed no change to the data file. `history` reuses
    `Parser#parseIndex`, so its errors match `delete` and `edit`.
  * `unplayed` keeps each game's number from `list`, as `find` does, so the number can go straight to `log` or
    `history`. It has separate messages for an empty collection and for when every game has been played.

* **Testing**: wrote 61 JUnit tests for these features (`ListCommandTest`, `DeleteCommandTest`, `FindCommandTest`,
  `SortCommandTest`, `SortOrderTest`, `HistoryCommandTest`, `UnplayedCommandTest`, and the matching `ParserTest` and
  `GameListTest` cases), including checks that `delete` and `sort` save the right content to the data file and that
  `history` does not.

### Contributions to the User Guide

* Wrote the sections *Listing all games*, *Deleting a game*, *Finding games by title*, *Sorting your collection*,
  *Viewing a game's play history* and *Listing games you have never played*, each with the format, examples,
  expected output and error cases, taken from the running app.
* Added the `list`, `delete`, `find`, `sort`, `history` and `unplayed` rows to the Command Summary, and the
  matching lines to the `help` output.

### Contributions to the Developer Guide

* Wrote the *Find feature* and *Sort feature* sections
  ([PR #46](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/46)) and the *Play history and unplayed games* section
  ([PR #67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67)), including four UML diagrams:
  * a sequence diagram of `find mono` (`FindSequenceDiagram.puml`)
  * a class diagram of the sort feature (`SortClassDiagram.puml`)
  * a sequence diagram of `sort time` (`SortSequenceDiagram.puml`)
  * a sequence diagram of `history 1` (`HistorySequenceDiagram.puml`)
* Described the design alternatives for each feature: how search results are numbered, where the search happens,
  the matching rule, reordering the collection versus showing a sorted copy, an enum versus a `switch` for the
  sort orders, how unplayed games are numbered, and the order of sessions in `history`.
* Added the `find`, `sort`, `history` and `unplayed` user stories, replacing the template's placeholder row.

### Contributions to team-based tasks

* Added `@@author` tags to the existing `list` and `delete` code and tests, so RepoSense keeps crediting them
  correctly after teammates edit the same files ([PR #35](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/35)).
* Reviewed and merged teammates' PRs
  [#29](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/29),
  [#31](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/31),
  [#32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32) and
  [#33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33), after checking that each one builds and works with the
  latest `master`.

### Review/mentoring contributions

* Reviewed and approved [PR #29](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/29) (`pick`): suggested one shared
  helper for parsing `COUNT [/time MINUTES]` in both `pick` and `filter`, and pointed out that the two commands used
  different wording for the same note about games with no play time.
* Reviewed and approved [PR #32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32) (tagging): asked whether
  changing a `Game` in place with `addTag` was intended, since `edit` replaces the game instead, and suggested a shared
  helper for the list-number check that `delete`, `edit`, `tag` and `untag` each repeat.
* Reviewed and approved [PR #33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33) (tag extras): found by running
  the app that `filter COUNT /time MINUTES /tag TAG` left out games with no play time without saying so, while `filter`
  and `pick` both report them, and suggested folding `FilterTagCommand` into `FilterCommand` to remove the duplication.
* Reviewed [PR #65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65) (difficulty levels) after building and
  running it: found that the `/tag` form of `filter` left out games with no difficulty without a note, unlike the
  other form, and pointed out a UG bullet that did not list the new `/diff` option.
