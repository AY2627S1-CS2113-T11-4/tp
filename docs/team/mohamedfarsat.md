# Mohamed Farsat - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs. It keeps track of a game
collection and quickly finds games that suit the group that turned up, by player count, play time and tags.

My main areas were **the foundation of the app**, i.e. the command loop that every other feature plugs into,
**keeping the user's data safe** when the data file is edited by hand, and the **`pick` command**, which chooses a
random game for the group and, with **ratings**, learns which games the group enjoys.

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
  * Added `/time` support to match `filter`.

* **Rating games and weighted picking** (`rate INDEX up|down`, `unrate INDEX up|down`), v2.1
  ([PR #59](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/59))
  * What it does: after playing, the group gives a game a thumbs up or a thumbs down, and `pick` then suggests
    games rated up more often and games rated down less often. `unrate` takes back a rating given by mistake.
  * Justification: a purely random pick ignores what the group enjoyed, so the host often has to pick again.
    With ratings, the suggestions get better the more the group uses GameVault.
  * Highlights: each game gets a weight of `(up + 1) / (down + 1)`, kept between 0.2 and 5, so a favourite cannot
    crowd out every other game and a disliked game is never ruled out. The weighted choice takes a `Random` like
    before, so tests check exactly which game is chosen at a given point. Ratings are saved as a new data-file
    field (`ratings=UP/DOWN`), are kept when a game is edited, and counts that are invalid or too large are
    rejected with a clear reason.
  * Built on top of a teammate's lending feature, so lent games are left out before the weighting runs.

### Contributions to the User Guide

* Wrote the first version of the UG: the introduction, Quick Start, `help` and `exit` sections, FAQ and Command
  Summary.
* Wrote the section *Editing the data file*, which explains the file format and what happens to invalid lines.
* Wrote the section *Picking a random game*, and updated the `help` example output to match the app.
* Wrote the sections *Rating a game after playing* and *Undoing a rating*, including a table showing how ratings
  change a game's chance of being picked, and added the `ratings` field to *Editing the data file*.

### Contributions to the Developer Guide

* Wrote the section *Picking a random game (`pick`)*, including a sequence diagram of `pick 4 /time 45`
  (`PickSequence.puml`) and design considerations on testing random results and reusing the filtering logic
  ([PR #60](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/60)).
* Wrote the section *Rating games and weighted picking*, including a sequence diagram of `rate 1 up`
  (`RateSequence.puml`) and design considerations on how ratings should change the chance of being picked.
* Added the team's v1.0 class and object diagrams to `docs/diagrams`.

### Contributions to team-based tasks

* Set up the project structure from the template: replaced the sample `Duke` code with the GameVault packages,
  and updated the Gradle build (main class, jar name `gamevault.jar`, assertions enabled when running).

### Review/mentoring contributions

* Reviewed and approved [PR #15](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/15) (adding a game).
* Reviewed [PR #26](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/26) (editing a game).
* Reviewed and approved [PR #58](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/58) (logging play sessions),
  checking that the new named-field data format still loads older data files.
