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
  * The parser reports exactly what is wrong (missing field, non-numeric count, `MIN` below 1, `MAX` below `MIN`),
    and rejects titles containing `|`, which the data file uses to separate fields.

* **Editing a game** (`edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]`), v2.0
  ([#26](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/26), [#41](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/41))
  * Changes any mix of fields in place, keeping the game's number in `list`, its tags and its history.
  * The result mixes new and current values, so `MAX >= MIN` is checked in the parser when both are given, and
    against the game's current values otherwise (e.g. `edit 1 /min 9` must fail if the current maximum is 6).
  * `/time none` removes a play time, and an edit that changes nothing says so instead of rewriting the data file.

* **Logging play sessions** (`log INDEX /players COUNT [/date YYYY-MM-DD]`), v2.1
  ([#58](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/58))
  * Records when a game was played and by how many. Future and impossible dates (e.g. `2026-02-30`) are rejected.
  * **Designed the named-field data format** (`KEY=VALUE` fields after `TAGS`, e.g. `sessions=2026-10-30@4`). Fields
    can come in any order and are only written when there is data, so older data files still load. The team then used
    it for every v2.1 feature that saves data: `lent`, `ratings`, `unavailable`, `from`/`due` and `difficulty`.

* **Last played, and hiding recently played games** (`filter COUNT /notplayedin DAYS`), v2.1
  ([#63](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/63), [#66](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/66))
  * `list` and `sort` show `last played N day(s) ago` or `never played` for each game.
  * `filter 4 /notplayedin 14` hides games played in the last 14 days. It combines with `/time` and `/diff`, and the
    notes about untimed and lent games skip games it hides, so every note stays accurate.
  * Worked out from the logged sessions, so it can never disagree with the play history, with today's date passed in
    so tests stay correct as time passes.

### Contributions to the User Guide

Sections for `edit` ([#28](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/28)) and `log`; the "last played" parts of
`list`, `sort` and `filter`; the `sessions` field in **Editing the data file**; and the matching Command Summary rows.

### Contributions to the Developer Guide

Implementation and design considerations for **Edit** ([#27](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/27)),
**Logging a play session** (with the `LogSequenceDiagram` sequence diagram) and **Showing when games were last
played**; the named-field data format in the Storage section; and the v2.1 "last played" user story.

### Contributions to team-based tasks

* Set up the issue tracker for the v1.0 and v2.1 features (e.g. [#6](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/6)-[#12](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/12),
  [#51](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/51)-[#57](https://github.com/AY2627S1-CS2113-T11-4/tp/issues/57)), with labels and milestones.
* Adapted a teammate's lending feature ([#50](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/50)) to the new
  named-field format (`lent=NAME`) when it clashed with my `log` PR, so both could be merged.
* Merged teammates' PRs after review ([#25](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/25),
  [#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62), [#65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65)),
  and resolved the merge between my `/notplayedin` and the new `/diff` option.

### Review/mentoring contributions

Reviewed teammates' PRs with suggestions that were adopted:
[#59](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/59) (capping the pick weight, and `unrate` for mistaken ratings),
[#61](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/61) (a clearer error when `/reason` is missing),
[#62](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/62) (`giveback` refusing gifts and removing loans).
Also reviewed [#25](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/25),
[#60](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/60) (found an accidentally deleted DG section),
[#65](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/65) (found a game counted in two notes at once) and
[#67](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/67).