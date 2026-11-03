# Bhao Tanush - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs. It keeps track of a game
collection and quickly finds games that suit the group that turned up, by player count, play time and tags.

My main area was **working with the collection once games are in it**: viewing it (`list`), removing games
(`delete`), finding a game by title (`find`), reordering the collection (`sort`), and looking back at what the group
has played (`history`, `unplayed`).

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=SwaggyDaddy67&breakdown=true)

### Enhancements implemented

* **Listing all games** (`list`), v1.0 ([PR #17](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/17)): shows every
  game numbered from 1. These numbers are the `INDEX` that other commands use. Extra text such as `list all` is an
  error. Moved the numbering into a helper shared with `sort`, which a teammate later extended with "last played".
* **Deleting a game** (`delete INDEX`), v1.0 ([PR #19](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/19)): gives a
  specific error for each kind of bad `INDEX` (missing, not a whole number, below 1, past the end, too large for an
  `int`). These checks were later extracted into `Parser#parseIndex`, now reused by `edit`, `tag` and `untag`.
* **Finding games by title** (`find KEYWORD`), v2.0 ([PR #34](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/34)):
  case-insensitive, matches a keyword with spaces as one phrase, and ignores extra spaces. Each result keeps its number
  from `list`, so it can go straight to `delete` or `edit`. Renumbering results from 1 was rejected, because
  `delete 1` after a search could then delete the wrong game.
* **Sorting the collection** (`sort title|players|time`), v2.0
  ([PR #37](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/37)): built around a `SortOrder` enumeration that pairs
  each keyword with its `Comparator`, so a new order needs only one new enum value. Games with no play time go last,
  ties are broken by title so the result is repeatable, and the new order is saved.
* **Play history and never-played games** (`history INDEX`, `unplayed`), v2.1
  ([PR #67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67)): `history` lists a game's sessions, oldest first,
  then the total and how long ago it was last played (e.g. `last played 3 days ago (2026-10-30)`). `unplayed` lists
  games never played, keeping their `list` numbers, with a note on how many are lent out or unavailable. Neither
  needed a change to the data file.
* **Testing**: wrote 68 JUnit tests for these features, including checks that `delete` and `sort` save the right
  content and that `history` saves nothing.

### Contributions to the User Guide

* Wrote *Listing all games*, *Deleting a game*, *Finding games by title*, *Sorting your collection*, *Viewing a game's
  play history* and *Listing games you have never played*, with expected output taken from the running app, plus
  their Command Summary rows and `help` lines.

### Contributions to the Developer Guide

* Wrote the *Find feature*, *Sort feature* and *Play history and unplayed games* sections
  ([PR #46](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/46), [PR #67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67)),
  with design alternatives for each, and four UML diagrams: sequence diagrams of `find mono`, `sort time` and
  `history 1`, and a class diagram of the sort feature.
* Added the `find`, `sort`, `history` and `unplayed` user stories.

### Contributions to team-based tasks

* Added `@@author` tags to the `list` and `delete` code and tests
  ([PR #35](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/35)).
* Merged [#29](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/29),
  [#31](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/31),
  [#32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32) and
  [#33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33) after checking each one against the latest `master`.
* Before v2.1, checked the whole product against the UG and the team's review comments, and shared a checklist of
  release tasks and remaining bugs with the team.

### Review/mentoring contributions

* [PR #29](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/29) (`pick`): suggested sharing the `COUNT [/time MINUTES]`
  parsing with `filter`, and pointed out different wording for the same note in the two commands.
* [PR #32](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/32) (tagging): asked whether changing a `Game` in place was
  intended, since `edit` replaces it, and suggested a shared list-number check.
* [PR #33](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/33) (tag extras): found that `filter ... /tag` left out
  games with no play time without a note.
* [PR #65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65) (difficulty): found the same missing note for
  difficulty, and a UG bullet missing `/diff`.
* [PR #69](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/69) (DG appendix): ran every manual testing step against
  the JAR, and asked for the missing user stories, test cases and Acknowledgements, which were added.
