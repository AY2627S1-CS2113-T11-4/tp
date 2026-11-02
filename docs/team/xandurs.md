# Xander Lim - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.

My main areas were **getting games into the collection and keeping them correct** (`add` and `edit`), and
**tracking what the group has played** (`log`, "last played" and hiding recently played games), so a host does not
keep suggesting the game the group played last week.

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Xandurs&breakdown=true)

### Enhancements implemented

* **Adding a game** (`add TITLE /min MIN /max MAX`), v1.0 ([PR #15](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/15))
  * The starting point of the product: every other feature works on games added this way.
  * The parser reports exactly what is wrong (missing field, non-numeric count, `MIN` below 1, `MAX` below `MIN`).
    Titles containing `|` are rejected, because the data file uses `|` to separate fields.

* **Editing a game** (`edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]`), v2.0
  ([#26](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/26), [#41](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/41))
  * Changes any mix of fields in place, so the game keeps its number in `list`, its tags and its history.
  * Harder than `add` because the result mixes new and current values: `MAX >= MIN` is checked in the parser when
    both are given, and in `EditCommand` against the game's current values otherwise (e.g. `edit 1 /min 9` must fail
    if the current maximum is 6).
  * `/time none` removes a play time (with an error if there is none, most likely the wrong `INDEX`), and an edit
    that changes nothing says "No changes made" instead of rewriting the data file.

* **Logging play sessions** (`log INDEX /players COUNT [/date YYYY-MM-DD]`), v2.1
  ([#58](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/58))
  * Records when a game was played and by how many people. The foundation for "last played" and `/notplayedin`.
  * Sessions are kept in date order, so the last one is always the latest. Dates in the future are rejected, and
    impossible dates such as `2026-02-30` are caught rather than adjusted.
  * **Designed the named-field data format** (`KEY=VALUE` fields after `TAGS`, e.g. `sessions=2026-10-30@4`). Fields
    can come in any order and are only written when there is data, so older data files still load. The team then used
    it for every v2.1 feature that saves data: `lent`, `ratings`, `unavailable`, `from`/`due` and `difficulty`.

* **Last played, and hiding recently played games** (`filter COUNT /notplayedin DAYS`), v2.1
  ([#63](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/63), [#66](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/66))
  * `list` and `sort` end each game with `last played N day(s) ago` or `never played`.
  * `filter 4 /notplayedin 14` hides games played in the last 14 days and says how many were left out. It works
    together with `/time` and `/diff`, and the notes about untimed and lent games skip games it would hide anyway,
    so every note stays accurate when several conditions are combined.
  * Worked out from the logged sessions rather than stored, so it can never disagree with the play history.
    Today's date is passed in, so tests use a fixed date and do not start failing as time passes.

### Contributions to the User Guide

* Sections for `edit` (including `/time`) ([#28](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/28)) and `log`,
  and the "last played" parts of `list`, `sort` and `filter /notplayedin`.
* The named-field format (`sessions=...`) in **Editing the data file**, so users can fix a hand-edited data file.
* The matching rows of the Command Summary, and fixes to the `help` output shown in the UG.

### Contributions to the Developer Guide

* Implementation and design considerations for the edit feature
  ([#27](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/27)), **Logging a play session** (with a sequence
  diagram, `LogSequenceDiagram`), and **Showing when games were last played**.
* In the Storage section: the named-field data format, how it is checked when loading, and its design consideration.
* The "last played" step of the Sort feature, and the v2.1 user story for last played.

### Contributions to team-based tasks

* Set up the issue tracker for the v1.0 and v2.1 features (e.g. [#6](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/6)-[#12](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/12),
  [#51](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/51)-[#57](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/57)), with labels and milestones.
* Adapted a teammate's lending feature ([#50](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/50)) to the new
  named-field format (`lent=NAME`) when it clashed with my `log` PR, so both could be merged.
* Merged teammates' PRs after review ([#25](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/25),
  [#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62), [#65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65)),
  and resolved the merge between my `/notplayedin` and the new `/diff` option.
* Kept `@@author` tags accurate across shared files.

### Review/mentoring contributions

* [#25](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/25) (play time): keeping the `Storage` Javadoc in sync with the
  new file format, sharing one `NO_PLAY_TIME` constant, and making `splitByPrefixes` safe with `Pattern.quote`.
* [#65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65) (difficulty): found games with no play time and no
  difficulty being counted in two notes at once, and suggested building the keyword list from the enum values.
* [#59](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/59) (rate and weighted pick): suggested capping the pick
  weight so a favourite cannot crowd out every other game, and a way to undo a mistaken rating. Both were adopted
  (the 0.2-5 weight limits and `unrate`).
* [#60](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/60) (DG for `pick`): spotted that the PR accidentally deleted
  the "Loading the data file" DG section, and that the sequence diagram did not match the code.
* [#61](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/61) (unavailable games): suggested a clearer error when
  `/reason` is left out, aligned `help` output and logging, which were added.
* [#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62) (borrowed games): pointed out that `giveback` on a gift
  made no sense and that given-back games were still suggested; `giveback` now refuses gifts and removes loans.
* [#67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67) (play history): suggested showing "N days ago" in the
  summary to match `list`, and handling lent or unavailable games in `unplayed`.