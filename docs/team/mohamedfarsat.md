# Mohamed Farsat - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs. It keeps track of a game
collection and quickly finds games that suit the group that turned up, by player count, play time and tags.

My main areas were **the foundation of the app**, i.e. the command loop that every other feature plugs into,
**keeping the user's data safe** when the data file is edited by hand, and the **`pick` command**, which chooses a
random game for the group.

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=MohamedFarsat&breakdown=true)

### Enhancements implemented

* **App skeleton and command loop**, v1.0 ([PR #13](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/13))
  * What it does: reads a command, runs it and repeats until `exit`. An unknown or badly formed command shows an
    error and the app keeps running instead of crashing. Includes the `help` and `exit` commands.
  * Turned the team's v1.0 class diagram into code: `GameVault`, `Ui`, `Parser`, the abstract `Command` class and
    `GameVaultException`, plus starting versions of `Game`, `GameList` and `Storage` so teammates could start on
    their features straight away.
  * Designed it to be easy to extend and test: adding a command only needs a new `Command` subclass and one case
    in `Parser`, and `Ui` can read from and write to any stream, so tests can run the whole app with fake input.

* **Handling an invalid data file**, v1.0 ([PR #14](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/14))
  * What it does: if a line in `data/games.txt` is broken (e.g. after editing it by hand), GameVault skips only
    that line, loads every other game, and tells the user which lines were skipped and why.
  * Added `GameDecoder`, which checks each part of a line and gives a specific reason when something is wrong
    (e.g. `MAX (2) is less than MIN (5)`), and the loading half of `Storage`.
  * Handled edge cases found while testing: blank lines, the invisible marker Windows Notepad adds to the start of
    a file, a missing file (a new user) and a file that cannot be read at all.

* **Picking a random game** (`pick COUNT [/time MINUTES]`), v2.0
  ([PR #29](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/29))
  * What it does: picks one random game that fits the number of players, and optionally the time available,
    so the group does not have to spend time deciding.
  * Reused the existing filtering methods so that `pick` and `filter` always agree on which games fit, and moved
    the count of games with no play time into `GameList#countWithoutPlayTime` so both commands share it.
  * Made the random choice testable by passing a `Random` object into `PickCommand`, so JUnit tests can check
    exactly which game is picked.
  * Merged the latest changes from `master` into the branch, keeping both `pick` and the new `edit` and play time
    features, and added `/time` support to match `filter`.

### Contributions to the User Guide

* Wrote the first version of the UG: the introduction, Quick Start, `help` and `exit` sections, FAQ and Command
  Summary.
* Wrote the section *Editing the data file*, which explains the file format and what happens to invalid lines.
* Wrote the section *Picking a random game*, and updated the `help` example output to match the app.

### Contributions to the Developer Guide

* Wrote the section *Picking a random game (`pick`)*, including a sequence diagram of `pick 4 /time 45`
  (`PickSequence.puml`) and design considerations on testing random results and reusing the filtering logic.
* Added the team's v1.0 class and object diagrams to `docs/diagrams`.

### Contributions to team-based tasks

* Set up the project structure from the template: replaced the sample `Duke` code with the GameVault packages,
  and updated the Gradle build (main class, jar name `gamevault.jar`, assertions enabled when running).

### Review/mentoring contributions

* Reviewed and approved [PR #15](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/15) (adding a game).
* Reviewed [PR #26](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/26) (editing a game).
