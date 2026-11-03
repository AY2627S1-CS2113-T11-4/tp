# GameVault

GameVault is a command-line app for people who host board game nights. It keeps track of your game collection
and helps you answer *what can we play tonight?*: filter or pick games by number of players, time available,
difficulty or tag, log play sessions, rate games, and keep track of games you lend out or borrow.

* Website: https://ay2627s1-cs2113-t11-4.github.io/tp/
* [User Guide](docs/UserGuide.md): how to install and use GameVault
* [Developer Guide](docs/DeveloperGuide.md): how GameVault is designed and implemented
* [About Us](docs/AboutUs.md): the team behind GameVault

## Running GameVault

1. Install Java 25.
1. Download `gamevault.jar` from the [latest release](https://github.com/AY2627S1-CS2113-T11-4/tp/releases).
1. Run `java -jar gamevault.jar` in a terminal, then type `help` to see the commands.

## Building from source

* `./gradlew run` runs the app, and `./gradlew test` runs the JUnit tests (use `gradlew.bat` on Windows).
* `./gradlew checkstyleMain checkstyleTest` checks the code against the project's Checkstyle rules.
* `./gradlew shadowJar` builds the JAR file in `build/libs/`.

This project was set up from the [CS2113 tP template](https://github.com/NUS-CS2113-AY2627-S1/tp).
