# User Guide
 
## Introduction
 
GameVault is a command-line app for people who host game nights for friends or CCAs.
It helps you keep track of your game collection and quickly find games that suit the group you have tonight.
If you can type fast, GameVault gets you there faster than flipping through boxes on a shelf.
 
## Quick Start
 
1. Ensure that you have Java 25 installed.
1. Download the latest `gamevault.jar` from the team repository's Releases page.
1. Copy the file to the folder you want to use as the home folder for GameVault.
1. Open a terminal in that folder and run `java -jar gamevault.jar`.
1. Type a command and press Enter. For example, type `help` to see all commands.

## Features
 
Notes about the command format:
 
* Command words are not case-sensitive, e.g. `HELP` works the same as `help`.
* Words in `UPPER_CASE` are parameters you supply, e.g. in `filter COUNT`, `COUNT` can be `4`.
* Parameters in square brackets are optional, e.g. `filter COUNT [/time MINUTES]` can be used as `filter 4`
  or as `filter 4 /time 45`.
* Commands that take no parameters (such as `help` and `exit`) reject any extra text,
  e.g. `exit now` shows an error instead of exiting.

### Viewing help: `help`
 
Shows a list of all the commands you can use.
 
Format: `help`
 
Expected output:
 
```
____________________________________________________________
Here are the commands you can use:
  add     Adds a game: add TITLE /min MIN /max MAX [/time MINUTES]
  list    Lists all games in your collection.
  filter  Shows games that fit a player count: filter COUNT [/time MINUTES]
          or games with a tag: filter /tag TAG
  delete  Deletes a game: delete INDEX
  edit    Edits a game: edit INDEX [/title TITLE] [/min MIN] [/max MAX]
  tag     Adds a tag to a game: tag INDEX TAG
  untag   Removes a tag from a game: untag INDEX TAG
  help    Shows this list of commands.
  exit    Exits GameVault.
____________________________________________________________
```
 
### Adding a game: `add`
 
Adds a game to your collection, with the number of players it supports and, optionally, how long it takes.
 
Format: `add TITLE /min MIN /max MAX [/time MINUTES]`
 
* `MIN` and `MAX` are the fewest and most players the game supports. Both must be whole numbers of at least 1,
  and `MAX` must not be less than `MIN`.
* `MINUTES` is how long one round usually takes, e.g. the time printed on the box.
  It must be a whole number of at least 1. Leave it out if you do not know it.
* `/min`, `/max` and `/time` can be given in any order after the title, but each only once.
* `TITLE` cannot contain the `|` character.

Examples of usage:
 
* `add Catan /min 3 /max 4 /time 60`
* `add Codenames /min 2 /max 8`

Expected output:
 
```
____________________________________________________________
Added: Catan (3-4 players, 60 min)
You now have 1 game(s) in your collection.
____________________________________________________________
```
 
### Finding games for your group: `filter`
 
Shows the games that can be played by the given number of players, optionally only those that fit in the time
you have.
 
Format: `filter COUNT [/time MINUTES]`
 
* `COUNT` is the number of players, and must be a whole number of at least 1.
* A game is shown if `COUNT` is within its minimum and maximum players (both inclusive).
* If you give `/time MINUTES`, only games that take `MINUTES` or less are shown.
  Games with no play time recorded are left out, and GameVault tells you how many were left out for this reason.
* Your collection is not changed.

Examples of usage: `filter 4`, `filter 4 /time 45`
 
Expected output:
 
```
____________________________________________________________
Games that fit 4 players:
  Catan (3-4 players)
  Codenames (2-8 players)
____________________________________________________________
```
 
If no game fits, GameVault shows `No games fit 4 players.` instead.
 
With a time limit, e.g. `filter 4 /time 45` when Catan takes 60 minutes and Codenames has no play time recorded:
 
```
____________________________________________________________
No games fit 4 players in 45 minutes or less.
(1 game(s) for 4 players were left out because they have no play time recorded.)
____________________________________________________________
```

<!-- @@author KRAZYZECTRON -->
#### Finding games by tag: `filter /tag`

Shows the games that have the given tag (see [Tagging a game](#tagging-a-game-tag-untag)).

Format: `filter /tag TAG`

* `TAG` is not case-sensitive, e.g. `filter /tag PARTY` finds games tagged `party`.
* `/tag` cannot be combined with `COUNT` or `/time`.
* Your collection is not changed.

Example of usage: `filter /tag party`

Expected output:

```
____________________________________________________________
Games tagged 'party':
  Codenames (4-8 players) [co-op, party]
____________________________________________________________
```

If no game has the tag, GameVault shows `No games are tagged 'party'.` instead.
<!-- @@author -->
 
### Editing a game: `edit`
 
Changes the title and/or player range of a game in your collection.
 
Format: `edit INDEX [/title TITLE] [/min MIN] [/max MAX]`
 
* `INDEX` is the number shown next to the game in `list`, and must be a whole number of at least 1.
* At least one of `/title`, `/min` or `/max` must be given. Fields can be in any order.
* Fields you leave out keep their current values.
* The same rules as `add` apply: `MIN` must be at least 1, `MAX` must not be less than `MIN`,
  and `TITLE` cannot contain `|`. These are checked against the game's current values
  for any field you leave out.
* The game keeps its position in the list, its play time (if any) and its tags, and the change is saved to the
  data file.

Examples:
* `edit 1 /max 6` changes the 1st game's maximum to 6 players.
* `edit 2 /title Catan Deluxe /min 2` changes the 2nd game's title and minimum.

Expected output for `edit 1 /max 6`:
 
```
____________________________________________________________
Edited: Catan (3-4 players)
Now: Catan (3-6 players)
____________________________________________________________
```

<!-- @@author KRAZYZECTRON -->
### Tagging a game: `tag`, `untag`

Tags let you group games by mood or style (e.g. `party`, `strategy`, `co-op`), so you can pick games that suit
your group. Use `filter /tag TAG` to [find games by tag](#finding-games-by-tag-filter-tag).

Format: `tag INDEX TAG` to add a tag, `untag INDEX TAG` to remove one.

* `INDEX` is the number shown next to the game in `list`, and must be a whole number of at least 1.
* `TAG` must be one word of letters and digits, optionally joined by hyphens, e.g. `party` or `co-op`.
* Tags are not case-sensitive and are shown in lower case, e.g. `tag 1 Party` adds the tag `party`.
* A game cannot have the same tag twice, and you can only remove a tag the game has.
* A game can have any number of tags. They are shown in alphabetical order after the game's details,
  and the change is saved to the data file.

Examples:
* `tag 2 party` adds the tag `party` to the 2nd game.
* `untag 2 co-op` removes the tag `co-op` from the 2nd game.

Expected output for `tag 2 party`:

```
____________________________________________________________
Tagged: Codenames (4-8 players) [co-op, party]
____________________________________________________________
```

Expected output for `untag 2 co-op`:

```
____________________________________________________________
Removed tag 'co-op': Codenames (4-8 players) [party]
____________________________________________________________
```
<!-- @@author -->
 
### Exiting the program: `exit`
 
Exits GameVault.
 
Format: `exit`
 
Expected output:

```
____________________________________________________________
Bye! Have a great game night.
____________________________________________________________
```
 
### Editing the data file
 
GameVault stores your games in `data/games.txt` (relative to the folder you run GameVault from).
Advanced users can edit this file directly. Each line describes one game in the format `TITLE | MIN | MAX`,
optionally followed by `| TIME`, the play time in minutes, and `| TAGS`, the game's tags separated by commas:
 
```
Catan | 3 | 4 | 60
Codenames | 2 | 8
Azul | 2 | 4 | 45 | family,strategy
Werewolf | 8 | 18 |  | party
```
 
* `MIN` and `MAX` must be whole numbers of at least 1, and `MAX` must not be less than `MIN`.
* `TIME`, if given, must be a whole number of at least 1. A line with no `TIME` means no play time is recorded.
* `TAGS`, if given, must be the fifth field. For a game with tags but no play time, leave `TIME` blank,
  as in the `Werewolf` line above. Each tag must follow the rules in [Tagging a game](#tagging-a-game-tag-untag).
* Blank lines are ignored.

If a line is not in this format, GameVault skips that line, loads the rest of your games, and tells you which
lines were skipped and why, for example:
 
```
Warning: 1 line(s) in your data file could not be read and were skipped:
  Line 2: "Codenames | two | 8" (MIN should be a whole number, but was 'two')
Each line should be in the format: TITLE | MIN | MAX | TIME | TAGS (TIME and TAGS are optional)
```
 
**Caution:** Skipped lines are not saved back to the file. To keep those games, exit GameVault and fix the lines
before making any changes.
 
## FAQ
 
**Q**: What happens if I type a command GameVault does not recognise?
 
**A**: GameVault shows an error message such as `Error: Unknown command 'foo'. Type 'help' to see all commands.`
and waits for your next command. Nothing is changed.
 
## Command Summary
 
Action | Format
-------|-------
Add | `add TITLE /min MIN /max MAX [/time MINUTES]`, e.g. `add Catan /min 3 /max 4 /time 60`
Filter | `filter COUNT [/time MINUTES]`, e.g. `filter 4 /time 45`<br>`filter /tag TAG`, e.g. `filter /tag party`
Edit | `edit INDEX [/title TITLE] [/min MIN] [/max MAX]`, e.g. `edit 1 /max 6`
Tag | `tag INDEX TAG`, e.g. `tag 2 party`
Untag | `untag INDEX TAG`, e.g. `untag 2 co-op`
Help | `help`
Exit | `exit`
 
