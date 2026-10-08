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
* Commands that take no parameters (such as `help` and `exit`) reject any extra text,
  e.g. `exit now` shows an error instead of exiting.

### Viewing help: `help`

Shows a list of all the commands you can use.

Format: `help`

Expected output:

```
____________________________________________________________
Here are the commands you can use:
  help    Shows this list of commands.
  exit    Exits GameVault.
____________________________________________________________
```

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
Advanced users can edit this file directly. Each line describes one game in the format `TITLE | MIN | MAX`:

```
Catan | 3 | 4
Codenames | 2 | 8
```

* `MIN` and `MAX` must be whole numbers of at least 1, and `MAX` must not be less than `MIN`.
* Blank lines are ignored.

If a line is not in this format, GameVault skips that line, loads the rest of your games, and tells you which
lines were skipped and why, for example:

```
Warning: 1 line(s) in your data file could not be read and were skipped:
  Line 2: "Codenames | two | 8" (MIN should be a whole number, but was 'two')
Each line should be in the format: TITLE | MIN | MAX
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
Help | `help`
Exit | `exit`
