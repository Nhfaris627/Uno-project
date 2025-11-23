# UNO Game - Milestone 2
**SYSC 3110 - Software Development Project**

A graphical implementation of the classic UNO card game using the Model-View-Controller (MVC) architectural pattern with Java Swing.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [How to Run](#how-to-run)
- [How to Play](#how-to-play)
- [Project Structure](#project-structure)
- [MVC Architecture](#mvc-architecture)
- [Testing](#testing)
- [Known Issues](#known-issues)
- [Team Contributions](#team-contributions)
- [Rubric Compliance](#rubric-compliance)

---

## Project Overview

This project implements a fully functional UNO card game with a graphical user interface. The game supports 2-4 players and includes all standard UNO rules including special cards (SKIP, REVERSE, DRAW_ONE, WILD, WILD_DRAW_TWO). The project demonstrates proper implementation of the MVC pattern, Java Event Model, and separation of concerns.

---

## Features

### Gameplay Features
- **2-4 model.Player Support**: Select number of players at game start
- **Full UNO Rules**: All standard cards and special card effects
- **Score Tracking**: Points accumulate across rounds until 500
- **Multiple Rounds**: Automatic round reset when player wins
- **Turn Management**: Clockwise/counter-clockwise turn rotation
- **Visual Feedback**: Clear indication of playable cards

### Special Cards Implemented
- **SKIP**: Next player loses their turn
- **REVERSE**: Reverses turn order direction
- **DRAW_ONE**: Next player draws 1 card and loses turn
- **WILD**: model.Player chooses new color
- **WILD_DRAW_TWO**: model.Player chooses color, next player draws 2 and loses turn

### Technical Features
- **MVC Architecture**: Clean separation between Model, View, and Controller
- **Observer Pattern**: Event-driven state updates
- **GUI Interface**: User-friendly Swing-based interface
- **Mouse Interaction**: All actions via mouse clicks
- **Error Handling**: Graceful handling of invalid moves

---

## Installation & Setup

### Clone the Repository
```bash
git clone https://github.com/your-repo/uno-game.git
cd uno-game
```

### Compile the Project
```bash
# Navigate to source directory
cd "uno project/src"

# Compile all Java files
javac *.java
```

### Run Tests (Optional)
```bash
# Navigate to tests directory
cd "uno project/tests"

# Compile and run tests
javac -cp .:junit-platform-console-standalone.jar *.java
java -jar junit-platform-console-standalone.jar --class-path . --scan-class-path
```

---

## How to Run

### GUI Version (Recommended)
```bash
# From the src directory
java Main
```

### Console Version (Milestone 1 - Legacy)
```bash
# From the src directory
java Main
```

---

## How to Play

### Game Start
1. Run `Main`
2. Select number of players (2-4)
3. Game automatically deals 7 cards to each player

### During Your Turn
1. **View Your Cards**: Your hand is displayed at the bottom
2. **Playable Cards**: Green border indicates cards you can play
3. **Play a model.Card**: Click on any playable card
4. **Wild Cards**: If you play a WILD card, select a color from the dialog
5. **Draw model.Card**: If no cards are playable, click "Draw model.Card"
6. **Next Turn**: After playing or drawing, click "Next" to advance

### Winning
- **Round Win**: First player to empty their hand wins the round
- **Points**: Winner receives points equal to sum of all opponents' cards
- **Game Win**: First player to reach 500 points wins the game

### model.Card Values
- Number Cards (0-9): Face value
- SKIP/REVERSE: 20 points
- DRAW_ONE: 10 points
- WILD: 40 points
- WILD_DRAW_TWO: 50 points

---

## Project Structure

```
uno-project/
├── src/
│   ├── model.Card.java                  # model.Card entity (Model)
│   ├── model.Deck.java                  # model.Deck of cards (Model)
│   ├── model.Player.java                # model.Player entity (Model)
│   ├── model.GameModel.java             # Core game logic (Model)
│   ├── controller.GameState.java             # Immutable state snapshot (Model)
│   ├── controller.GameModelListener.java    # Observer interface
│   ├── view.GameView.java              # GUI components (View)
│   ├── controller.GameController.java        # Event handler (Controller)
│   ├── Main.java               # Application entry point
│   ├── Main.java                  # Console version (legacy)
│   └── UnoGame.java               # Console game logic (legacy)
├── tests/
│   ├── CardTest.java              # model.Card class tests
│   ├── DeckTest.java              # model.Deck class tests
│   ├── PlayerTest.java            # model.Player class tests
│   ├── GameModelCoreTest.java    # Core game logic tests
│   └── UnoGameTest.java           # Legacy tests
├── docs/
│   ├── UML-ClassDiagram.puml     # PlantUML class diagram
│   ├── SequenceDiagram-DrawCard.puml
│   ├── SequenceDiagram-PlayWild.puml
│   └── DataStructureChanges.md   # M1 to M2 changes
└── README.md
```

---

## MVC Architecture

### Model Layer
**Purpose**: Manages game state and business logic
- `model.GameModel.java`: Core game engine, enforces rules
- `model.Card.java`, `model.Deck.java`, `model.Player.java`: Game entities
- `controller.GameState.java`: Immutable state snapshots
- **No UI dependencies**: Pure logic, fully testable

### View Layer
**Purpose**: Displays game state to user
- `view.GameView.java`: Main GUI coordinator
- Panels for hand, discard pile, controls, scoreboard
- `promptWildColor()`: Color selection dialog
- **No game logic**: Only rendering and user input capture

### Controller Layer
**Purpose**: Mediates between Model and View
- `controller.GameController.java`: Handles all events
- Implements `ActionListener` for button clicks
- Implements `controller.GameModelListener` for model updates
- **Validates actions**: Ensures valid moves before model changes

### Communication Flow
```
User Click → View → Controller → Model
                                   ↓
                              (updates state)
                                   ↓
User sees ← View ← Controller ← Model (fires event)
```

---

## Testing

### Test Coverage
- **model.Card.java**: 6 tests 
- **model.Deck.java**: 6 tests 
- **model.Player.java**: 10 tests 
- **model.GameModel.java**: 20 tests 
- **Total**: 42 unit tests

### Running Tests
```bash
cd "uno project/tests"
javac -cp .:junit-5.jar:../src *.java
java -jar junit-platform-console-standalone.jar --class-path .:../src --scan-class-path
```

### Test Categories
1. **Game Setup**: model.Player counts, initial dealing, discard pile
2. **model.Card Playing**: Valid/invalid plays, hand updates
3. **Turn Management**: Turn advancement, direction changes
4. **Special Cards**: SKIP, REVERSE, DRAW effects
5. **Winning Conditions**: Round wins, scoring, game wins
6. **model.Card Playability**: Color/value matching rules

---

## Known Issues

### Current Issues
1. **model.Deck Exhaustion**: If deck runs out before round ends, game may stall
   - *Planned Fix*: Implement discard pile reshuffling
   
2. **REVERSE in 2-model.Player**: Acts as SKIP but message could be clearer
   - *Status*: Working as intended per UNO rules, UI feedback could improve

3. **Multiple Rounds**: Game ends after first round win
   - *Status*: Fixed in current version with `newRound()` method

### Future Enhancements
- AI players for single-player mode
- Network multiplayer support
- model.Card animation effects
- Sound effects and music
- Customizable house rules

---

## Team Contributions

### Milestone 1 (Console Version)
**All Team Members**: Collaborative development of console-based UNO
- Game logic implementation
- model.Card, model.Deck, model.Player classes
- Basic turn management
- Special card effects

### Milestone 2 (MVC + GUI)

#### **Bhagya Patel** (101324150) - Model & Event System
- Refactored `UnoGame` into `model.GameModel` (pure logic, no I/O)
- Implemented observer pattern (`controller.GameModelListener`, `controller.GameState`)
- Event firing system for all state changes
- Data consistency and synchronization
- **Deliverables**:
  - `model.GameModel.java` with full JavaDocs
  - `controller.GameModelListener.java` interface
  - `controller.GameState.java` immutable snapshots
  - Sequence diagrams: Draw model.Card, Play Wild
  - Model unit tests

#### **Nicky Fang** (101304731) - Controller & Event Handling
- Implemented `controller.GameController` mediating Model ↔ View
- `ActionListener` implementation for button events
- `controller.GameModelListener` implementation for model updates
- Command routing (PLAY, DRAW, NEXT, CHOOSE_COLOR)
- Error handling and validation
- **Deliverables**:
  - `controller.GameController.java` with JavaDocs
  - Controller wiring and registration
  - Sequence diagram: Button Click → Render flow
  - Integration testing

#### **Ivan Arkhipov** (101310636) - GUI/View Layer
- Built complete Swing UI in JFrame
- model.Player count selection (2-4 dialog)
- Hand panel with clickable cards
- Top card and deck display
- Control buttons (Draw, Next)
- Color picker dialog for Wild cards
- **Deliverables**:
  - `view.GameView.java` with all panels
  - UI component layout and styling
  - `render(controller.GameState)` method
  - GUI screenshots/demo
  - Responsive mouse interaction

#### **Faris Hassan** (101300683) - Testing, UML & Documentation
- UML class diagram (complete signatures)
- Sequence diagrams coordination
- Data structure changes documentation
- README and project documentation
- JUnit test coverage for Model
- GitHub workflow management
- **Deliverables**:
  - PlantUML diagrams (class + sequence)
  - `README.md` (this file)
  - `DataStructureChanges.md`
  - `GameModelCoreTest.java`
  - JavaDoc completeness review
  - Git commit/branch strategy

## Additional Resources

### UML Diagrams
- See `docs/UML-ClassDiagram.puml` for full class structure
- See `docs/SequenceDiagram-*.puml` for interaction flows

### Data Structure Changes
- See `docs/DataStructureChanges.md` for detailed M1→M2 evolution

### API Documentation
- Generate JavaDocs: `javadoc -d docs/javadoc src/*.java`
- Open `docs/javadoc/index.html` in browser

---

### Team Members
- **Bhagya Patel**: bhagyapatel@cmail.carleton.ca
- **Faris Hassan**: farishassan@cmail.carleton.ca  
- **Ivan Arkhipov**: ivanarkhipov@cmail.carleton.ca
- **Nicky Fang**: nickyfang@cmail.carleton.ca

### Course Information
- **Course**: SYSC 3110 - Software Development Project
- **Instructor**: Safaa Bedawi
- **Term**: Fall 2025

---

## License

This project is developed for educational purposes as part of SYSC 3110 at Carleton University.

---

## Acknowledgments

- UNO game rules and mechanics © Mattel
- JUnit testing framework
- Java Swing documentation

---

**Last Updated**: November 2025  
**Version**: 2.0 (Milestone 2 - MVC + GUI)
