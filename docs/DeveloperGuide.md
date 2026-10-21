# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}


### Loading the data file

When the app starts, `GameVault` calls `Storage#load`, which reads `data/games.txt` line by line. Each non-blank line
is decoded by `GameDecoder#decodeLine`, which expects the format `TITLE | MIN | MAX` (e.g. `Catan | 3 | 4`).

**Handling invalid lines.** If a line is invalid (e.g. a missing field, a non-numeric count, or `MAX < MIN`),
`GameDecoder` throws a `GameVaultException` explaining why. `Storage#load` catches it, records the line number and
reason, and moves on to the next line. After loading, `GameVault` shows these via `Ui#showSkippedLines`.
A missing data file is not an error; the app simply starts with an empty collection.

**Design considerations.**
* *Skip the line vs. reject the whole file:* the data file is meant to be human-editable, so one typo should not
  lose the user's entire collection. Skipping only the bad lines keeps every valid game.

## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
