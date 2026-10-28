# Xander Lim - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.

My main part of the project was getting games into the collection and keeping them correct afterwards:
the `add` command and the `edit` command, including their input validation.

### Summary of Contributions

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Xandurs&breakdown=true)

* **New Feature**: Added the ability to add a game (`add TITLE /min MIN /max MAX`).
  * What it does: lets the user add a game to their collection with the range of player counts it supports.
  * Justification: this is the starting point of the product; every other feature (list, filter, pick, sort) works on games added this way.
  * Highlights: the parser reports exactly what is wrong (missing field, non-numeric count, `MIN` below 1, `MAX` below `MIN`)
    instead of a generic error. Titles containing `|` are rejected, because the data file uses `|` to separate fields
    and such a title could not be loaded back.

* **New Feature**: Added the ability to edit a game (`edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]`).
  * What it does: lets the user change a game's title, player range and/or play time without deleting and re-adding it.
    Fields can be given in any order, and fields that are left out keep their current values.
  * Justification: without `edit`, fixing a typo or a wrong player count meant deleting the game and adding it again,
    which also changed its number in `list` and lost its tags.
  * Highlights: this was harder than `add` because the new game is a mix of new and existing values.
    * `MAX >= MIN` cannot always be checked in the parser, since the parser does not know the game's current values
      (e.g. `edit 1 /min 9` must fail if the current maximum is 6). The check is done in the parser when both are given,
      and in `EditCommand` against the current values otherwise.
    * The game keeps its position in the list, its tags are always kept, and its play time is kept unless the user changes it.
    * Extended it so that play time can be set, changed or removed (`/time none`), with an error when removing a play time
      the game does not have (most likely the wrong `INDEX`).
    * An edit that changes nothing tells the user "No changes made" instead of rewriting the data file.
    * Added logging and assertions, and JUnit tests for `EditCommand` and the `add`/`edit` parsing.

* **Documentation**:
  * User Guide:
    * Added the `edit` section, and the `edit` row of the Command Summary.
    * Added the missing `delete` command to the `help` output shown in the UG.
    * Fixed the formatting of the `edit` section and the Command Summary.
  * Developer Guide:
    * Added the implementation of the edit feature: how it is parsed, how it is executed, and the design considerations
      (where to check `MIN`/`MAX`, and replacing in place vs. deleting and re-adding).

* **Contributions to team-based tasks**:
  * After a teammate's play time feature was merged, updated `edit` and its UG section to work with play time ([#26](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/26),
    [#28](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/28)).
  * Added `@@author` tags to my code and tests.

* **Review/mentoring contributions**:
  * Reviewed and merged the play time PR [#25](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/25), with suggestions
    on keeping the `Storage` Javadoc in sync with the new file format, sharing one `NO_PLAY_TIME` constant between
    `Game` and `GameDecoder`, making `splitByPrefixes` safe for future prefixes with `Pattern.quote`,
    and flagging that prefixes are case-sensitive while command words are not.
