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
  add          Adds a game: add TITLE /min MIN /max MAX [/time MINUTES] [/diff LEVEL]
               [/tag TAG [MORE_TAGS]...]
  list         Lists all games in your collection.
  find         Finds games by title: find KEYWORD
  filter       Shows games that fit a player count: filter COUNT [/time MINUTES] [/diff LEVEL]
               [/notplayedin DAYS]
               or games with a tag: filter [COUNT] [/time MINUTES] [/diff LEVEL] /tag TAG
  pick         Picks a random game that fits a player count: pick COUNT [/time MINUTES] [/diff LEVEL]
  delete       Deletes a game: delete INDEX
  edit         Edits a game: edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]
               [/diff LEVEL|none]
  tag          Adds tags to a game: tag INDEX TAG [MORE_TAGS]...
  log          Logs that a game was played: log INDEX /players COUNT [/date YYYY-MM-DD]
  history      Shows when a game was played: history INDEX
  unplayed     Lists the games that have never been played.
  rate         Gives a game a thumbs up or down after playing: rate INDEX up|down
  unrate       Removes a thumbs up or down given by mistake: unrate INDEX up|down
  untag        Removes tags from a game: untag INDEX TAG [MORE_TAGS]...
  tags         Lists all tags in use and how many games have each.
  sort         Sorts your collection: sort title|players|time
  lend         Records that a game is lent out: lend INDEX /to NAME
  return       Records that a lent game is back: return INDEX
  lentout      Lists the games that are lent out and who has them.
  unavailable  Marks a game unavailable: unavailable INDEX /reason missing|damaged
  available    Marks a game available again: available INDEX
  borrow       Records who lent or gave you a game: borrow INDEX /from NAME [/due YYYY-MM-DD]
  giveback     Records that a borrowed game was given back: giveback INDEX
  help         Shows this list of commands.
  exit         Exits GameVault.
____________________________________________________________
```
 
### Adding a game: `add`
 
Adds a game to your collection, with the number of players it supports and, optionally, how long it takes
and how hard it is.
 
Format: `add TITLE /min MIN /max MAX [/time MINUTES] [/diff LEVEL] [/tag TAG [MORE_TAGS]...]`
 
* `MIN` and `MAX` are the fewest and most players the game supports. Both must be whole numbers of at least 1,
  and `MAX` must not be less than `MIN`.
* `MINUTES` is how long one round usually takes, e.g. the time printed on the box.
  It must be a whole number of at least 1. Leave it out if you do not know it.
* `LEVEL` is how hard the game is to learn and play: `easy`, `medium` or `hard`, in any case.
  Leave it out if you are not sure. It is shown in `list`, e.g. `Catan (3-4 players, 60 min, medium)`.
* `/tag` gives the game one or more tags, separated by spaces, e.g. `/tag party word`.
  Tags follow the same rules as the [`tag` command](#tagging-a-game-tag-untag).
* `/min`, `/max`, `/time`, `/diff` and `/tag` can be given in any order after the title, but each only once.
* `TITLE` cannot contain the `|` character.

Examples of usage:
 
* `add Catan /min 3 /max 4 /time 60`
* `add Codenames /min 2 /max 8 /tag party word`

Expected output:
 
```
____________________________________________________________
Added: Catan (3-4 players, 60 min)
You now have 1 game(s) in your collection.
____________________________________________________________
```
 
<!-- @@author SwaggyDaddy67 -->
### Listing all games: `list`

Shows every game in your collection, numbered from 1 in the order they are stored.

Format: `list`

* The numbers shown are the `INDEX` values used by `delete`, `edit` and `tag`.
* The order is the order you added the games in, unless you have used `sort`.
* `list` does not take any parameters, so e.g. `list all` shows an error.

Expected output:

```
____________________________________________________________
Here are the games in your collection:
1. Catan (3-4 players, 60 min) - last played 3 days ago
2. Codenames (4-8 players) [party] - never played
3. Chess (2 players) - last played today
____________________________________________________________
```

If your collection is empty, GameVault shows `No games in your collection yet.` instead.
<!-- @@author Xandurs -->

Each game ends with when it was last played, worked out from the sessions you recorded with
[`log`](#logging-a-play-session-log):

* `last played today`, `last played 1 day ago` or `last played N days ago`
* `never played` if no session has been logged for the game yet

This helps you avoid suggesting a game the group played very recently. To hide those games when choosing what to
play, use [`filter COUNT /notplayedin DAYS`](#finding-games-for-your-group-filter).
<!-- @@author KRAZYZECTRON -->

If a game you [borrowed](#recording-who-lent-or-gave-you-a-game-borrow-giveback) should already have been given back,
`list` reminds you at the end, e.g.:

```
Overdue, please give these back:
2. Hive - give back to Bob (was due 2026-10-25)
```
<!-- @@author -->

### Finding games for your group: `filter`
 
Shows the games that can be played by the given number of players, optionally only those that fit in the time
you have.
 
Format: `filter COUNT [/time MINUTES] [/diff LEVEL] [/notplayedin DAYS]`
 
* `COUNT` is the number of players, and must be a whole number of at least 1.
* A game is shown if `COUNT` is within its minimum and maximum players (both inclusive).
* If you give `/time MINUTES`, only games that take `MINUTES` or less are shown.
  Games with no play time recorded are left out, and GameVault tells you how many were left out for this reason.
* If you give `/diff LEVEL`, only games at that level (`easy`, `medium` or `hard`) are shown, which helps you avoid
  something too complicated for new players. Games with no difficulty recorded are left out, and GameVault tells
  you how many were left out for this reason. `COUNT` is still needed.
* Games that are lent out or [marked unavailable](#marking-a-game-unavailable-unavailable-available) are left out.
* Your collection is not changed.

Examples of usage: `filter 4`, `filter 4 /time 45 /diff easy`, `filter 4 /notplayedin 14`
 
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

<!-- @@author Xandurs -->

**Hiding games played recently.** Add `/notplayedin DAYS` to leave out games the group played in the last
`DAYS` days, so you don't end up playing the same game again.

* `DAYS` must be a whole number of at least 1.
* Today counts as 0 days ago. For example, with `/notplayedin 14`, a game last played 13 days ago is hidden, but a
  game last played 14 days ago is shown.
* Games that have never been played are always shown.
* GameVault tells you how many games were left out for this reason. The notes about games with no play time
  and games that are lent out only mention games that `/notplayedin` would not have left out anyway.
* `/notplayedin` can be combined with `/time` and `/diff`, in any order, but not with `/tag` (yet).
* When a game was played comes from the sessions you recorded with [`log`](#logging-a-play-session-log).

Example: `filter 4 /notplayedin 14`, when Catan was played 3 days ago, Azul 32 days ago, and Hive never:

```
____________________________________________________________
Games that fit 4 players not played in the last 14 day(s):
  Azul (2-4 players)
  Hive (2-4 players)
(1 game(s) for 4 players were left out because they were played in the last 14 day(s).)
____________________________________________________________
```

<!-- @@author -->

<!-- @@author KRAZYZECTRON -->
#### Finding games by tag: `filter /tag`

Shows the games that have the given tag (see [Tagging a game](#tagging-a-game-tag-untag)), optionally only those
that also fit your group size and the time you have.

Format: `filter [COUNT] [/time MINUTES] [/diff LEVEL] /tag TAG`

* `TAG` is not case-sensitive, e.g. `filter /tag PARTY` finds games tagged `party`. Only one tag can be given.
* `COUNT` and `/time MINUTES` are optional and work as in `filter COUNT [/time MINUTES]` above.
  As there, games with no play time recorded are left out when you give `/time`.
* Your collection is not changed.

Examples of usage:

* `filter /tag party` shows every party game.
* `filter 6 /time 45 /tag party` shows party games for 6 players that take 45 minutes or less.

Expected output for `filter /tag party`:

```
____________________________________________________________
Games tagged 'party':
  Codenames (4-8 players) [party, word]
  Telestrations (4-8 players, 30 min) [drawing, party]
____________________________________________________________
```

Expected output for `filter 6 /time 45 /tag party`:

```
____________________________________________________________
Games tagged 'party' for 6 players in 45 minutes or less:
  Telestrations (4-8 players, 30 min) [drawing, party]
____________________________________________________________
```

If no game matches, GameVault says so, e.g. `No games tagged 'party' for 3 players.`
<!-- @@author -->

<!-- @@author SwaggyDaddy67 -->
### Finding games by title: `find`

Shows the games whose title contains the given keyword, so you can locate a game without scrolling through `list`.

Format: `find KEYWORD`

* The search is case-insensitive, e.g. `mono` finds `Monopoly`.
* Part of a title is enough, e.g. `cat` finds `Catan`.
* `KEYWORD` may contain spaces, e.g. `find ticket to` finds `Ticket to Ride`.
* Each game is shown with its number from `list`, so you can use it with `delete`, `edit` or `tag` straight away.
* Your collection is not changed.

Example of usage: `find mono`

Expected output:

```
____________________________________________________________
Games with 'mono' in the title:
2. Monopoly (2-8 players)
____________________________________________________________
```

If no title contains the keyword, GameVault shows `No games have 'mono' in the title.` instead.
<!-- @@author -->
 
### Picking a random game: `pick`

Picks one random game that can be played by the given number of players, optionally only from those that fit in
the time you have, so your group doesn't have to spend time deciding.

Format: `pick COUNT [/time MINUTES] [/diff LEVEL]`

* `COUNT` is the number of players, and must be a whole number of at least 1.
* The game is picked only from games where `COUNT` is within its minimum and maximum players (both inclusive).
* If you give `/time MINUTES`, the game is picked only from games that take `MINUTES` or less.
  Games with no play time recorded are not considered, and GameVault tells you how many were left out for this reason.
* If you give `/diff LEVEL`, the game is picked only from games at that level. Games with no difficulty recorded
  are not considered, and GameVault tells you how many for this reason.
* Games that are lent out or [marked unavailable](#marking-a-game-unavailable-unavailable-available) are not considered.
* Each use may pick a different game. Your collection is not changed.
* Games your group rated up with [`rate`](#rating-a-game-after-playing-rate) are more likely to be picked,
  and games rated down are less likely. When any of the games that fit has been rated, `pick` reminds you of this.

Examples of usage: `pick 4`, `pick 4 /time 45`, `pick 4 /diff easy`

Expected output for `pick 4` (the game shown may differ):

```
____________________________________________________________
How about: Catan (3-4 players)
(picked from 3 game(s) that fit 4 players)
____________________________________________________________
```

With a time limit, e.g. `pick 4 /time 45` when Love Letter takes 20 minutes, Catan takes 60 minutes
and Codenames has no play time recorded:

```
____________________________________________________________
How about: Love Letter (2-4 players, 20 min)
(picked from 1 game(s) that fit 4 players in 45 minutes or less)
(1 game(s) for 4 players were not considered because they have no play time recorded.)
____________________________________________________________
```

If no game fits, GameVault shows e.g. `No games fit 4 players, so there is nothing to pick from.` instead.

<!-- @@author SwaggyDaddy67 -->
### Deleting a game: `delete`

Removes a game from your collection and saves the change to the data file.

Format: `delete INDEX`

* `INDEX` is the number shown next to the game in `list`, and must be a whole number of at least 1.
* Games after the deleted one move up by one, so their numbers change. Use `list` to see the new numbers.
* There is no undo, so check the number with `list` (or `find`) first.

Example of usage: `delete 2`

Expected output:

```
____________________________________________________________
Deleted: Codenames (4-8 players) [party]
____________________________________________________________
```

If there is no game with that number, GameVault shows an error such as
`There is no game numbered 5. Use 'list' to see the current numbering.` and nothing is deleted.
<!-- @@author -->

### Editing a game: `edit`
 
Changes the title, player range, play time and/or difficulty of a game in your collection.
 
Format: `edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none] [/diff LEVEL|none]`
 
* `INDEX` is the number shown next to the game in `list`, and must be a whole number of at least 1.
* At least one of `/title`, `/min`, `/max`, `/time` or `/diff` must be given. Fields can be in any order.
* Fields you leave out keep their current values.
* The same rules as `add` apply: `MIN` must be at least 1, `MAX` must not be less than `MIN`,
  `MINUTES` must be at least 1, and `TITLE` cannot contain `|`. These are checked against the game's current
  values for any field you leave out.
* `/time MINUTES` sets or changes the play time. `/time none` removes it, and shows an error if the game has
  no play time recorded.
* `/diff LEVEL` sets or changes the difficulty to `easy`, `medium` or `hard`. `/diff none` removes it, and shows
  an error if the game has no difficulty recorded.
* If the new details are the same as the current ones, GameVault tells you that no changes were made.
* The game keeps its position in the list and its tags, and the change is saved to the data file.

Examples:
* `edit 1 /max 6` changes the 1st game's maximum to 6 players.
* `edit 2 /title Catan Deluxe /min 2` changes the 2nd game's title and minimum.
* `edit 1 /time 45` sets the 1st game's play time to 45 minutes.
* `edit 1 /time none` removes the 1st game's play time.
* `edit 2 /diff hard` sets the 2nd game's difficulty to hard.

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

Format: `tag INDEX TAG [MORE_TAGS]...` to add tags, `untag INDEX TAG [MORE_TAGS]...` to remove them.

* `INDEX` is the number shown next to the game in `list`, and must be a whole number of at least 1.
* Each `TAG` must be one word of letters and digits, optionally joined by hyphens, e.g. `party` or `co-op`.
  Separate several tags with spaces.
* Tags are not case-sensitive and are shown in lower case, e.g. `tag 1 Party` adds the tag `party`.
* A game cannot have the same tag twice. `tag` skips any tag the game already has, and `untag` skips any tag
  the game does not have, and both tell you which tags were skipped. If every tag is skipped, nothing changes
  and GameVault shows an error.
* A game can have any number of tags. They are shown in alphabetical order after the game's details,
  and the change is saved to the data file.

Examples:
* `tag 2 party drawing` adds the tags `party` and `drawing` to the 2nd game.
* `untag 1 word co-op` removes the tags `word` and `co-op` from the 1st game.

Expected output for `tag 2 party drawing`:

```
____________________________________________________________
Tagged: Telestrations (4-8 players, 30 min) [drawing, party]
____________________________________________________________
```

Expected output for `untag 1 word co-op`, when the 1st game has `word` but not `co-op`:

```
____________________________________________________________
Removed tag(s) 'word': Codenames (4-8 players) [party]
(Did not have 'co-op', so skipped.)
____________________________________________________________
```

### Listing your tags: `tags`

Shows every tag used in your collection, in alphabetical order, with how many games have it.
This helps you reuse the same tags instead of creating near-duplicates such as `party` and `parties`.

Format: `tags`

Expected output:

```
____________________________________________________________
Tags in your collection:
  drawing (1 game(s))
  party (2 game(s))
  strategy (1 game(s))
____________________________________________________________
```

If none of your games have tags, GameVault tells you how to add one instead.
<!-- @@author -->

<!-- @@author SwaggyDaddy67 -->
### Sorting your collection: `sort`

Reorders your whole collection by title, player count or play time, so it is easier to scan.

Format: `sort title`, `sort players` or `sort time`

* `sort title` puts games in alphabetical order, ignoring upper and lower case.
* `sort players` puts games that need the fewest players first (by minimum, then maximum players).
* `sort time` puts the shortest games first. Games with no play time recorded go last.
* Games that tie are put in alphabetical order.
* The new order is saved, so `list` shows it from now on. **The numbers next to each game change to match**,
  so check the new numbers before using `delete`, `edit` or `tag`.
* Games added later go to the end of the list. Run `sort` again to put them in place.
* Like `list`, each game ends with when it was last played.

Example of usage: `sort time`

Expected output:

```
____________________________________________________________
Sorted your collection by play time (shortest first, games with no play time last):
1. Love Letter (2-4 players, 20 min) - last played 2 days ago
2. Catan (3-4 players, 60 min) - never played
3. Codenames (4-8 players) - last played today
____________________________________________________________
```

If your collection is empty, GameVault shows `No games in your collection to sort yet.` instead.

<!-- @@author Xandurs -->
### Logging a play session: `log`

Records that your group played a game, so GameVault can keep track of when each game was last played.

Format: `log INDEX /players COUNT [/date YYYY-MM-DD]`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* `COUNT` is how many people played. It must be within the game's player range; if you now play a game
  with a different range, change it with `edit` first.
* `/date` is the day you played, in the form `YYYY-MM-DD` (e.g. `2026-10-30`). It cannot be in the future.
  Leave it out to log the session for today.
* `/players` and `/date` can be given in either order.
* You can log the same game more than once on the same day.

Examples of usage:

* `log 1 /players 4` logs that game 1 was played today by 4 people.
* `log 2 /players 3 /date 2026-10-25` logs that game 2 was played on 25 October 2026 by 3 people.

Expected output (for `log 1 /players 4 /date 2026-10-30`):

```
____________________________________________________________
Logged: Catan played on 2026-10-30 (4 players)
Catan has now been played 2 times.
____________________________________________________________
```

<!-- @@author SwaggyDaddy67 -->
### Viewing a game's play history: `history`

Shows every session logged for a game with [`log`](#logging-a-play-session-log), so you can see how often and how
recently your group has played it.

Format: `history INDEX`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* Sessions are shown oldest first, each with the date and how many people played.
* The last line gives the total number of times the game was played and the date it was last played.
* If the game has no sessions logged, GameVault says it has never been played and shows the format of `log`.
* Your collection is not changed.

Example of usage: `history 1`, after logging Catan on 1 June 2026 with 3 players and on 30 October 2026 with 4

Expected output:

```
____________________________________________________________
Play history of Catan (oldest first):
  2026-06-01 (3 players)
  2026-10-30 (4 players)
Played 2 times in total, most recently on 2026-10-30.
____________________________________________________________
```

If there is no game with that number, GameVault shows an error such as
`There is no game numbered 9. Use 'list' to see the current numbering.`

### Listing games you have never played: `unplayed`

Shows the games that have no sessions logged, so games that keep being passed over get a fair chance.

Format: `unplayed`

* Each game is shown with its number from `list`, so you can use it with `log` or `history` straight away.
* The last line says how many of your games have never been played.
* If every game has been played at least once, GameVault says so instead.
* Anything typed after `unplayed` (e.g. `unplayed all`) is reported as an error.
* Your collection is not changed.

Example of usage: `unplayed`

Expected output, when only Catan has been played:

```
____________________________________________________________
Games you have never played:
2. Codenames (4-8 players)
3. Hive (2 players)
2 of your 3 game(s) have never been played.
____________________________________________________________
```

<!-- @@author MohamedFarsat -->
### Rating a game after playing: `rate`

Gives a game a thumbs up or a thumbs down after your group has played it, so that `pick` suggests the games your
group enjoyed more often.

Format: `rate INDEX up|down`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* Use `up` if the group enjoyed the game, or `down` if they did not. `UP` and `Down` work too.
* You can rate the same game as many times as you like, e.g. once after every game night. Each rating is added
  to the game's count of thumbs up or thumbs down.
* You do not need to [`log`](#logging-a-play-session-log) a game before rating it, since not every group logs
  every game they play.
* Rated the wrong game? Use [`unrate`](#undoing-a-rating-unrate) to take the rating back.
* Once a game has been rated, `list` (and other commands that show the game) show its counts after it,
  e.g. `Catan (3-4 players) +5/-1` for 5 thumbs up and 1 thumbs down.

Examples of usage: `rate 1 up`, `rate 3 down`

Expected output (for `rate 1 up`, when Catan already had 4 thumbs up and 1 thumbs down):

```
____________________________________________________________
Thumbs up for Catan!
Catan is now rated +5/-1 (thumbs up/thumbs down).
____________________________________________________________
```

**How ratings affect `pick`:** each game that fits is given a weight of
(thumbs up + 1) ÷ (thumbs down + 1), kept between 0.2 and 5, and `pick` chooses games in proportion to their weights:

Rating | Weight | Compared with an unrated game
-------|--------|------------------------------
Not rated (`+0/-0`) | 1 | Equally likely
`+3/-0` | 4 | 4 times as likely
`+2/-2` | 1 | Equally likely
`+0/-3` | 0.25 | A quarter as likely
`+30/-0` | 5 (the most) | 5 times as likely
`+0/-30` | 0.2 (the least) | A fifth as likely

The limits mean a long-time favourite is picked more often, but cannot crowd out every other game, and a game with
many thumbs down is picked less often, but is never ruled out, so your group still gets the occasional surprise.
Ratings only affect `pick`: `filter` shows the same games whether or not they are rated.

### Undoing a rating: `unrate`

Removes one thumbs up or thumbs down from a game, e.g. if you typed `rate 2 down` but meant `rate 3 down`.

Format: `unrate INDEX up|down`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* Use `up` to remove one thumbs up, or `down` to remove one thumbs down. The game's other ratings are kept.
* If the game has no rating of that kind, GameVault tells you and changes nothing.

Example of usage: `unrate 2 down`

Expected output (when Codenames had 1 thumbs up and 1 thumbs down):

```
____________________________________________________________
Removed a thumbs down from Codenames.
Codenames is now rated +1/-0 (thumbs up/thumbs down).
____________________________________________________________
```

<!-- @@author KRAZYZECTRON -->
### Marking a game unavailable: `unavailable`, `available`

Marks a game you cannot play at the moment, for example because pieces are missing or it is damaged.
`filter` and `pick` then leave it out, so you are never offered a game you cannot play, and `list` shows why.
Mark it available again once it is back in play.

Format: `unavailable INDEX /reason REASON` and `available INDEX`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* `REASON` is `missing` (missing pieces) or `damaged`, in any case.
* Marking a game that is already unavailable with a different reason replaces the old reason.
* An unavailable game stays in your collection: `list`, `find` and `sort` still show it, with its reason after it.
  `edit`, `tag`, `untag`, `log` and `rate` work on it as usual, and `edit` keeps its reason.
* For games you have lent to someone, use [`lend`](#lending-a-game-to-a-friend-lend) instead,
  which also records who has the game.

Examples of usage:

* `unavailable 1 /reason missing` marks game 1 as having missing pieces.
* `available 1` marks game 1 as available again.

Expected output (for `unavailable 1 /reason damaged`, when game 1 was already marked as having missing pieces):

```
____________________________________________________________
Marked unavailable: Catan (3-4 players, 60 min) (unavailable: damaged)
(Was: missing pieces.)
Use 'available 1' once it can be played again.
____________________________________________________________
```

`list` then shows the reason after the game, e.g. `1. Catan (3-4 players, 60 min) (unavailable: damaged)`.

<!-- @@author -->

<!-- @@author yy261 -->
### Lending a game to a friend: `lend`

Records that a game has been lent to someone. While a game is lent out, `filter` and `pick` leave it out,
so you are never offered a game that is not on your shelf.

Format: `lend INDEX /to NAME`

* `INDEX` is the number shown next to the game in `list`.
* `NAME` is the person borrowing the game, e.g. `Alice` or `Mary Tan`. It must be 1 to 30 characters long and
  cannot contain `|`. Extra spaces are removed, so `/to   Mary   Tan` is saved as `Mary Tan`.
* A game can only be lent to one person at a time. To lend it to someone else, `return` it first.
* The game stays in your collection: `list` and `find` still show it, with `(lent to NAME)` after it.

Example of usage: `lend 2 /to Alice`

Expected output:

```
____________________________________________________________
Lent to Alice: Codenames
It will be left out of 'filter' and 'pick' until you 'return 2'.
____________________________________________________________
```

If the game is already lent out, GameVault tells you who has it and changes nothing, e.g.
`Codenames is already lent to Alice. Use 'return 2' first if it has come back.`

**How lent games affect other commands:** `filter` and `pick` leave out lent games, and say which games were
left out and who has them, so you know why a game you expected is missing:

```
____________________________________________________________
Games that fit 4 players:
  Love Letter (2-4 players, 20 min)
(Not shown because they are lent out: Catan (to Mary Tan), Codenames (to Alice))
____________________________________________________________
```

A lent game is only mentioned if it would otherwise have been shown, e.g. a 60-minute game is not mentioned
for `filter 4 /time 30`. `edit`, `tag`, `untag`, `sort` and `delete` work on lent games as usual,
and an edited game stays lent to the same person.

### Getting a game back: `return`

Records that a lent game is back on your shelf, so `filter` and `pick` can suggest it again.

Format: `return INDEX`

* `INDEX` is the number shown next to the game in `list` or `lentout`.

Example of usage: `return 2`

Expected output:

```
____________________________________________________________
Returned by Alice: Codenames
It is back on your shelf and can be suggested by 'filter' and 'pick' again.
____________________________________________________________
```

If the game is not lent out, GameVault shows
`Love Letter is not lent to anyone. Use 'lentout' to see which games are lent out.` and changes nothing.

### Seeing which games are lent out: `lentout`

Shows every game that is lent out and who has it, so you know which games to ask for back.

Format: `lentout`

* The numbers are the same as in `list`, so you can use them with `return` straight away.

Expected output:

```
____________________________________________________________
2 game(s) are lent out:
1. Catan - lent to Mary Tan
2. Codenames - lent to Alice
____________________________________________________________
```

If no game is lent out, GameVault shows `None of your games are lent out. Everything is on your shelf.` instead.
<!-- @@author KRAZYZECTRON -->
### Recording who lent or gave you a game: `borrow`, `giveback`

Records who a game came from, so you know who to give a borrowed game back to, and who to thank for a gift.
For a borrowed game you can also record when it is due back, and `list` reminds you once it is overdue.
This is the opposite of [`lend`](#lending-a-game-to-a-friend-lend), which is for games you lend out.

Format: `borrow INDEX /from NAME [/due YYYY-MM-DD]` and `giveback INDEX`

* `INDEX` is the game's number in `list`, and must be a whole number of at least 1.
* `NAME` is the person the game is from, e.g. `Bob` or `Mary Tan`. It follows the same rules as for `lend`:
  1 to 30 characters long and cannot contain `|`. Extra spaces are removed.
* `/due` is the day the game must be given back, in the form `YYYY-MM-DD` (e.g. `2026-11-10`). It cannot be in
  the past. Leave it out for a game that does not have to be given back, such as a gift.
* `/from` and `/due` can be given in either order.
* Using `borrow` again on the same game replaces the old record, e.g. to extend a loan.
* `giveback` is for a borrowed game you have given back. Since it is no longer on your shelf, it is **removed from
  your collection**, along with its play history and ratings, so `filter` and `pick` stop suggesting it.
* `giveback` only works on games borrowed with `/due`. A game recorded without `/due` is a gift, so there is
  nothing to give back; use `delete` if you no longer have it.
* While you have them, borrowed games are on your shelf, so `filter` and `pick` still suggest them.
  `edit` keeps who the game is from.

Examples of usage:

* `borrow 2 /from Bob /due 2026-11-10` records that game 2 is borrowed from Bob until 10 November 2026.
* `borrow 3 /from Mary Tan` records that game 3 is from Mary Tan, e.g. a gift.
* `giveback 2` records that game 2 has been given back, and removes it from your collection.

Expected output (for `borrow 2 /from Bob /due 2026-11-10`):

```
____________________________________________________________
Recorded: Hive (2 players) (borrowed from Bob, due 2026-11-10)
'list' will remind you if it is not given back by 2026-11-10. Use 'giveback 2' once you have given it back.
____________________________________________________________
```

Expected output (for `giveback 2`, with 3 games in your collection before):

```
____________________________________________________________
Given back to Bob: Hive
It has been removed from your collection. You now have 2 game(s).
____________________________________________________________
```

For a gift, `giveback` changes nothing and shows e.g.
`Azul was a gift from Mary Tan, so there is nothing to give back. Use 'delete 3' if you no longer have it.`

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
optionally followed by `| TIME`, the play time in minutes, `| TAGS`, the game's tags separated by commas,
and `| BORROWER`, the person the game is lent to:
 
```
Catan | 3 | 4 | 60
Codenames | 2 | 8
Azul | 2 | 4 | 45 | family,strategy
Werewolf | 8 | 18 |  | party
Hive | 2 | 2 |  |  | lent=Alice
Love Letter | 2 | 4 | 20 |  | sessions=2026-10-25@3;2026-10-30@4
Splendor | 2 | 4 | 30 |  | ratings=5/1
```
 
* `MIN` and `MAX` must be whole numbers of at least 1, and `MAX` must not be less than `MIN`.
* `TIME`, if given, must be a whole number of at least 1. A line with no `TIME` means no play time is recorded.
* `TAGS`, if given, must be the fifth field. For a game with tags but no play time, leave `TIME` blank,
  as in the `Werewolf` line above. Each tag must follow the rules in [Tagging a game](#tagging-a-game-tag-untag).
* Any further data comes after `TAGS` as named `KEY=VALUE` fields. For these lines, write `TIME` and `TAGS`
  even if blank, as in the `Hive` and `Love Letter` lines above. The named fields are:
  * `lent`, the person the game is lent to (see [`lend`](#lending-a-game-to-a-friend-lend)).
  * `difficulty`, how hard the game is: `easy`, `medium` or `hard` (e.g. `difficulty=easy`).
  * `sessions`, which lists the logged play sessions as `DATE@PLAYERS` (e.g. `2026-10-30@4`), separated by `;`.
  * `ratings`, which holds the thumbs-up and thumbs-down counts from [`rate`](#rating-a-game-after-playing-rate)
    as `UP/DOWN` (e.g. `5/1`). Both counts must be whole numbers of at least 0.
  * `unavailable`, which says why the game cannot be played at the moment: `missing` or `damaged`
    (e.g. `unavailable=missing`).
  * `from`, the person who lent or gave you the game (see [`borrow`](#recording-who-lent-or-gave-you-a-game-borrow-giveback)),
    and `due`, the day it must be given back in the form `YYYY-MM-DD` (e.g. `from=Bob | due=2026-11-10`).
    `due` can only be given together with `from`.
* Blank lines are ignored.

If a line is not in this format, GameVault skips that line, loads the rest of your games, and tells you which
lines were skipped and why, for example:
 
```
Warning: 1 line(s) in your data file could not be read and were skipped:
  Line 2: "Codenames | two | 8" (MIN should be a whole number, but was 'two')
Each line should be in the format: TITLE | MIN | MAX | TIME | TAGS | KEY=VALUE... (everything after MAX is optional)
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
Add | `add TITLE /min MIN /max MAX [/time MINUTES] [/diff LEVEL] [/tag TAG [MORE_TAGS]...]`, e.g. `add Catan /min 3 /max 4 /time 60 /tag strategy`
List | `list`
Filter | `filter COUNT [/time MINUTES] [/diff LEVEL] [/notplayedin DAYS]`, e.g. `filter 4 /time 45 /diff easy`, `filter 4 /notplayedin 14`<br>`filter [COUNT] [/time MINUTES] [/diff LEVEL] /tag TAG`, e.g. `filter 6 /tag party`
Find | `find KEYWORD`, e.g. `find mono`
Pick | `pick COUNT [/time MINUTES] [/diff LEVEL]`, e.g. `pick 4 /time 45 /diff easy`
Delete | `delete INDEX`, e.g. `delete 2`
Edit | `edit INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES\|none] [/diff LEVEL\|none]`, e.g. `edit 1 /max 6`, `edit 1 /time none`, `edit 1 /diff hard`
Tag | `tag INDEX TAG [MORE_TAGS]...`, e.g. `tag 2 party drawing`
Untag | `untag INDEX TAG [MORE_TAGS]...`, e.g. `untag 2 co-op`
List tags | `tags`
Sort | `sort title`, `sort players` or `sort time`, e.g. `sort time`
Lend | `lend INDEX /to NAME`, e.g. `lend 2 /to Alice`
Return | `return INDEX`, e.g. `return 2`
List lent games | `lentout`
Log | `log INDEX /players COUNT [/date YYYY-MM-DD]`, e.g. `log 1 /players 4 /date 2026-10-30`
History | `history INDEX`, e.g. `history 1`
Unplayed | `unplayed`
Rate | `rate INDEX up\|down`, e.g. `rate 1 up`
Unrate | `unrate INDEX up\|down`, e.g. `unrate 2 down`
Mark unavailable | `unavailable INDEX /reason missing\|damaged`, e.g. `unavailable 1 /reason missing`
Mark available | `available INDEX`, e.g. `available 1`
Borrow | `borrow INDEX /from NAME [/due YYYY-MM-DD]`, e.g. `borrow 2 /from Bob /due 2026-11-10`
Give back | `giveback INDEX`, e.g. `giveback 2`
Help | `help`
Exit | `exit`