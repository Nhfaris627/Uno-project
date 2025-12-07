# UNO FLIP Game - Milestone 4

## Project Overview

This is a Java-based implementation of the UNO FLIP card game, developed using the Model-View-Controller (MVC) architectural pattern. The game supports 2-4 players (human or AI) and includes advanced features such as save/load functionality, undo/redo capabilities, replay functionality, and AI opponents with varying difficulty levels.

**Version**: 4.0  
**Course**: SYSC3110 - Software Engineering  
**Institution**: Carleton University

---

## Team Members

| Name | Student ID |
|------|------------|
| **Nicky Fang** | 101304731 |
| **Bhagya Patel** | 101324150 |
| **Faris Hassan** | 101300683 |
| **Ivan Arkhipov** | 101310636 |

---

## Team Contributions

### Milestone 4 Contributions

| Team Member | Milestone 4 Contributions |
|-------------|--------------------------|
| **Nicky Fang (101304731)** | • Replay functionality implementation<br>• Game restart after win<br>• Score reset mechanism<br>• Testing replay features |
| **Bhagya Patel (101324150)** | • Undo functionality implementation<br>• Redo functionality implementation<br>• Stack-based state management<br>• State restoration logic |
| **Faris Hassan (101300683)** | • JUnit tests for serialization<br>• JUnit tests for undo/redo<br>• UML class diagrams creation<br>• Project documentation<br>• Sequence diagrams |
| **Ivan Arkhipov (101310636)** | • Serialization implementation<br>• Deserialization implementation<br>• Save/Load functionality<br>• Transient field handling<br>• File I/O operations |

### Previous Milestones Summary

#### Milestone 3 Contributions
| Team Member | Contributions |
|-------------|---------------|
| **Nicky Fang** | AI player implementation, AI difficulty levels, AI strategy patterns |
| **Bhagya Patel** | GameModel core logic, Observer pattern implementation, Game rules |
| **Faris Hassan** | UNO FLIP mechanics, Dark side cards, FLIP card functionality |
| **Ivan Arkhipov** | GameView GUI, Card rendering, UI components and layout |

#### Milestone 2 Contributions
| Team Member | Contributions |
|-------------|---------------|
| **Nicky Fang** | Controller implementation, ActionListener handling |
| **Bhagya Patel** | Model Class, Implementing functionality for game |
| **Faris Hassan** | Junit Testing, UMl diagrams, Documentation |
| **Ivan Arkhipov** | Initial GUI framework, Implementation of View |

#### Milestone 1 Contributions
| Team Member | Contributions |
|-------------|---------------|
| **Nicky Fang** | Project setup, Initial architecture design |
| **Bhagya Patel** | MVC pattern design, GameModel structure |
| **Faris Hassan** | UNO rules research, Game flow design |
| **Ivan Arkhipov** | UI mockups, Visual design planning |

---

## Milestone 4 Features

### New Features Implemented

1. **Serialization/Deserialization (Ivan Arkhipov)**
   - Save current game state to file (`uno_save.dat`)
   - Load previously saved games
   - Preserves all game data including undo/redo history
   - Proper handling of transient fields
   - Deep copy mechanisms for state preservation

2. **Undo/Redo Functionality (Bhagya Patel)**
   - Undo any game action (play card, draw card, end turn)
   - Redo previously undone actions
   - Multiple levels of undo/redo using Stack data structures
   - Visual feedback with enabled/disabled buttons
   - State management and restoration

3. **Replay Functionality (Nicky Fang)**
   - Play again after game completion
   - Reset all player scores to 0
   - Start new round with same players
   - Prompt dialog after game win
   - Seamless transition to new game

4. **Comprehensive Testing (Faris Hassan)**
   - JUnit test suite for serialization
   - JUnit test suite for undo/redo functionality
   - Edge case testing
   - Validation of save/load integrity

5. **Documentation (Faris Hassan)**
   - UML class diagrams
   - Sequence diagrams
   - Technical documentation
   - Architecture documentation

---

## Project Structure

```
uno-project/
│
├── src/
│   ├── controller/
│   │   ├── GameController.java       # Main controller, handles user input
│   │   ├── GameModelListener.java    # Observer interface
│   │   └── GameState.java            # Immutable game state snapshot
│   │
│   ├── model/
│   │   ├── GameModel.java            # Core game logic
│   │   ├── Player.java               # Player class
│   │   ├── AIPlayer.java             # AI player with strategy
│   │   ├── Card.java                 # Card representation
│   │   └── Deck.java                 # Deck management
│   │
│   ├── view/
│   │   ├── GameView.java             # GUI implementation
│   │   ├── unoCards/                 # Light side card images
│   │   └── DarkSideCards/            # Dark side card images
│   │
│   └── test/
│       ├── SerializationTest.java    # Serialization JUnit tests
│       └── UndoRedoTest.java         # Undo/Redo JUnit tests
│
├── docs/
│   ├── UserManual.md                 # Comprehensive user guide
│   ├── DataStructureChanges.md       # Documentation of UML and data structure changes
│   ├── ClassDiagram.puml             # PlantUML class diagram
│   ├── UndoSequence.puml             # Undo sequence diagram
│   └── LoadSequence.puml             # Load game sequence diagram
│
├── UNO.jar                           # Runnable JAR file
├── README.md                         # This file
└── uno_save.dat                      # Save file (generated during gameplay)
```

---

## Deliverables Explanation

### 1. UML Class Diagrams (Faris Hassan)
- **Location**: `docs/ClassDiagram.puml`
- **Description**: Complete class diagram showing all classes, interfaces, relationships, and method signatures
- **Format**: PlantUML code (paste into PlantUML editor to generate diagram)
- **Included**: All packages (controller, model, view), enums, and complete method signatures

### 2. Sequence Diagrams (Faris Hassan)

#### a) Undo Move Sequence Diagram
- **Location**: `docs/UndoSequence.puml`
- **Description**: Shows the complete flow when a player undoes a move
- **Key Interactions**: User → View → Controller → Model → Stacks
- **Highlights**: State saving to redo stack, state restoration, UI updates

#### b) Load Saved Game Sequence Diagram
- **Location**: `docs/LoadSequence.puml`
- **Description**: Shows the deserialization and restoration process
- **Key Interactions**: File system interaction, object deserialization, error handling
- **Highlights**: Listener management, state restoration, AI turn processing

### 3. Data Structure Changes Documentation (Faris Hassan)
- **Location**: `docs/DataStructureChanges.md`
- **Description**: Detailed explanation of all changes from Milestone 3 to Milestone 4
- **Includes**:
  - Serialization additions and implementation details
  - New methods for save/load
  - Undo/redo data structures (Stack implementation)
  - Deep copy strategies
  - Replay functionality additions
  - Rationale for all design decisions

### 4. User Manual
- **Location**: `docs/UserManual.md`
- **Description**: Complete guide for end users
- **Sections**:
  - Installation and setup
  - How to play
  - Feature explanations (Save/Load, Undo/Redo, Replay)
  - Card types and rules
  - Strategy tips

### 5. README File
- **Location**: This file (`README.md`)
- **Contents**: Project overview, team contributions (all milestones), deliverables guide

---

### Running the JAR File

**Method 1: Double-Click**
```
Navigate to the folder and double-click UNO.jar
```

**Method 2: Command Line**
```bash
java -jar UNO.jar
```

### Game Setup
1. Select number of players (2-4)
2. Choose player types (Human or AI)
3. Select AI difficulty (Easy/Medium/Hard)
4. Click OK to start the game

---

## How to Use Key Features

### Save Game (Ivan Arkhipov)
1. Click the **"Save Game"** button at any time during gameplay
2. Game state is serialized to `uno_save.dat` in the same directory as the JAR
3. Confirmation message appears: "Game saved successfully!"
4. All game data is preserved including undo/redo history

### Load Game (Ivan Arkhipov)
1. Click the **"Load Game"** button
2. Game deserializes from `uno_save.dat`
3. Continue playing from where you left off
4. **Note**: Must have same number of players as saved game

### Undo Move (Bhagya Patel)
1. Click the **"Undo"** button (located above your hand, left side)
2. Game reverts to previous state
3. Current state is pushed to redo stack
4. Can undo multiple times
5. Button is disabled when undo stack is empty

### Redo Move (Bhagya Patel)
1. After undoing, click the **"Redo"** button (located above your hand, right side)
2. Game restores the undone action
3. Can redo multiple times
4. Making a new move clears redo history
5. Button is disabled when redo stack is empty

### Replay Game (Nicky Fang)
1. After a player reaches 500 points, a dialog appears
2. Shows the winner and their final score
3. Choose "Yes" to play again with same players
4. All scores reset to 0
5. New round begins automatically
6. Choose "No" to exit the game

---

## Technical Details

### Design Patterns Used

1. **Model-View-Controller (MVC)**
   - Model: Game logic and state (Bhagya Patel)
   - View: GUI components (Ivan Arkhipov)
   - Controller: Mediates between Model and View (Nicky Fang)

2. **Observer Pattern**
   - `GameModelListener` interface
   - Model notifies listeners of state changes
   - Controller observes model events

3. **Memento Pattern**
   - `GameState` acts as memento
   - Stores complete game state
   - Used for undo/redo functionality (Bhagya Patel)

4. **Serialization Pattern**
   - Save/Load implementation (Ivan Arkhipov)
   - Object persistence
   - File I/O operations

### Serialization Strategy (Ivan Arkhipov)

**Serializable Classes**:
- `Player`, `AIPlayer`
- `Card`, `Deck`
- `GameState`

**Transient Fields**:
- `Random random` in AIPlayer (reinitialized after deserialization)
- `List<GameModelListener> listeners` in GameModel (not serialized)

**Deep Copy Approach**:
- `getState()` creates complete deep copies
- Prevents reference sharing between states
- Ensures undo/redo integrity

### Undo/Redo Implementation (Bhagya Patel)

**Data Structures**:
- `Stack<GameState> undoStack` - stores previous states
- `Stack<GameState> redoStack` - stores undone states

**Algorithm**:
- `saveStateOnMove()` - called before any game action
- `undo()` - pops from undoStack, pushes current to redoStack
- `redo()` - pops from redoStack, pushes current to undoStack
- `restoreState()` - applies saved state to current game

### Replay Implementation (Nicky Fang)

**Components**:
- `restartGame()` - resets scores and starts new round
- `promptPlayAgain()` - dialog to continue or exit
- `onGameWon()` - triggers replay prompt

## Documentation Files

1. **UserManual.md** - Complete guide for end users
2. **DataStructureChanges.md** - Technical documentation of changes
3. **README.md** - This file

---

## Version History

- **v4.0 (Milestone 4)** - Save/Load, Undo/Redo, Replay, Testing
- **v3.0 (Milestone 3)** - AI Players, UNO FLIP mechanics
- **v2.0 (Milestone 2)** - Full MVC implementation, GUI
- **v1.0 (Milestone 1)** - Basic game logic, console version

---

## License

This project is submitted as academic coursework for SYSC 3110. All rights reserved by the team members.

---
