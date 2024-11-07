# Nim Game

A Java implementation of the classic Nim game with both Player vs Player (PvP) and Player vs Machine (PvM) modes.

## Description

Nim is a mathematical game of strategy in which two players take turns removing objects from distinct heaps or piles. On each turn, a player must remove at least one object, and may remove any number of objects provided they all come from the same heap or pile. The game can be played in two variations:
- Normal Play: The player who removes the last object wins
- Misère Play: The player who removes the last object loses

## Features

- Two game modes: PvP and PvM
- Customizable game settings (number of rows, sticks per row, game rules)
- Undo moves
- Move history tracking
- Hint system
- Save/Load game state
- Give up option

## Technical Details

### Project Structure
- `models/`: Contains data models (GameModel, GameStateModel, PlayerModel)
- `controllers/`: Game logic (HomeController, GamePlayController, Machine)
- `views/`: User interface (HomeView, GamePlayView)
- `interfaces/`: Defines contracts (GamePlayInterface, HomeInterface, NavigationInterface)

### Requirements
- Java Development Kit (JDK) 8 or higher
- Java Swing (GUI)

## Installation & Running

1. Clone the repository
2. Navigate to the project directory
3. Compile the project: `javac src/main/java/nimgame/NimGame.java`
4. Run the game: `java src.main.java.nimgame.NimGame`

## How to Play

1. Start the game and choose your game mode (PvP or PvM)
2. In PvM mode, choose who goes first (Player or Machine)
3. On your turn:
   - Select a row
   - Choose number of sticks to remove
   - Click "Take" to confirm your move
4. Continue until someone wins according to the selected game rules

## Contributing

Feel free to fork the repository and submit pull requests for any improvements.

## License

This project is open source and available under the MIT License.
