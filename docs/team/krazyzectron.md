# Vishwak Nattamai - Project Portfolio Page

## Overview

GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.

My main parts of the project were saving the collection so that it is still there the next time the app starts,
and tags, which let hosts group games by mood or style (e.g. `party`, `co-op`) and find the right ones quickly.

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
      `|` or `,` used to separate fields in the data file, and no escaping is needed.
    * Tags are saved as an optional fifth field, so data files saved by older versions still load.
    * Tags are not case-sensitive and each game keeps its tags unique and in alphabetical order.
      `tag` and `untag` accept several tags at once, skip the ones that do not apply and tell the user which ones
      were skipped.
    * `tags` was added so hosts reuse existing tags instead of creating near-duplicates like `party` and `parties`.
    * Updated `edit` so that editing a game keeps its tags.
    * Added JUnit tests for `Tag`, the tag commands, tag parsing and decoding tags from the data file.

* **Documentation**:
  * User Guide:
    * Added the sections on tagging (`tag`, `untag`), listing tags (`tags`) and finding games by tag (`filter /tag`),
      and updated `add`, `help`, the data file format and the Command Summary for tags.
  * Developer Guide:
    * Added the Storage section: how games are loaded at startup and saved after each change, and the design
      considerations (file format, handling invalid lines, how to save)
      ([#31](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/31)).
    * Added three UML diagrams for it: a class diagram of the Storage component, and sequence diagrams for loading
      and for saving ([#38](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/38)).
    * Updated the Storage section and its diagrams for the tags field.

* **Contributions to team-based tasks**:
  * Updated the project name in the README, and my entry in the About Us page
    ([#4](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/4)).
  * Added `@@author` tags to my code and tests.

* **Review/mentoring contributions**:
  * Reviewed and merged [#34](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/34) (find),
    [#35](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/35) (`@@author` tags) and
    [#37](https://github.com/AY2627S1-CS2113-T11-4/tp/pull/37) (sort).

* **Contributions beyond the project team**:
  * TODO: add any forum posts, or bugs reported in other teams' products, with links.
