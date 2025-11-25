# UNO Game - Milestone 3
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
- [UML Diagrams](#uml-diagrams)
- [Data Structure Changes](#data-structure-changes)

---

## Project Overview

This project implements a fully functional UNO card game with a graphical user interface, AI players, and UNO Flip expansion support. The game supports 2-4 players (human or AI) and includes all standard UNO rules plus UNO Flip special cards. The project demonstrates proper implementation of the MVC pattern, Java Event Model, inheritance, polymorphism, and separation of concerns.

---

## Features

### Gameplay Features
- **2-4 model.Player Support**: Select number of players at game start
- **AI Players**: Configure any player as AI with three difficulty levels (EASY, MEDIUM, HARD)
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
- **Dark Side Colors**: TEAL, PURPLE, PINK, ORANGE (when flipped)

### AI Features (NEW in Milestone 3)
- **Three Difficulty Levels**:
    - **EASY**: Random valid moves
    - **MEDIUM**: Basic strategy (special cards, color matching, high-value plays)
    - **HARD**: Advanced strategy (threat detection, wild card preservation, opponent analysis)
- **Smart Wild Color Selection**: AI chooses colors based on hand composition
- **Automatic Turn Processing**: AI plays seamlessly without user intervention
- **Legal Moves Only**: AI always selects from validated playable cards

### Technical Features
- **MVC Architecture**: Clean separation between Model, View, and Controller
- **Observer Pattern**: Event-driven state updates
- **GUI Interface**: User-friendly Swing-based interface
- **Mouse Interaction**: All actions via mouse clicks
- **Error Handling**: Graceful handling of invalid moves

---
## System Requirements

- **Java Version**: JDK 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: 512 MB RAM minimum
- **Display**: 1024x768 resolution minimum

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
3. Configure each player as Human or AI
4. Select AI difficulty level for AI players
5. Click "Start Game"

### During Your Turn (Human Players)
1. **View Your Cards**: Your hand is displayed at the bottom
2. **Playable Cards**: Green border indicates cards you can play
3. **Play a Card**: Click on any playable card
4. **Wild Cards**:
    - If LIGHT side: Choose RED, BLUE, GREEN, or YELLOW
    - If DARK side: Choose TEAL, PURPLE, PINK, or ORANGE
5. **Draw Card**: If no cards are playable, click "Draw Card"
6. **Next Turn**: After playing or drawing, click "Next" to advance

### AI Player Turns
- AI players automatically select and play cards
- A message shows "AI is thinking..." or "AI is playing..."
- AI turns process seamlessly without user input
- Watch the AI's decisions and learn strategies!

### FLIP Card Effect
When a FLIP card is played:
1. All cards in the game flip to their opposite side
2. LIGHT side → DARK side (or vice versa)
3. Colors change (e.g., RED → TEAL, BLUE → PURPLE)
4. Special cards change (e.g., DRAW_ONE → DRAW_FIVE)
5. All player hands, discard pile, and deck are flipped
6. Game continues on the new side

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

**UNO Flip Cards:**
- FLIP: 20 points
- DRAW_FIVE: 20 points
- SKIP_EVERYONE: 30 points
- WILD_DRAW_COLOR: 60 points

---

## AI Player Strategy

### How AI Selects a Legal Move

The AI player uses a **rule-based heuristic approach** to make decisions. AI always selects from the `playableIndices` list provided by GameModel, ensuring **100% legal moves**.

### Decision Process:
1. Get list of playable cards from GameState
2. If no playable cards → Draw card
3. If playable cards available → Apply difficulty-specific strategy
4. Return selected card index
5. If wild card → Choose color based on hand composition

### EASY Difficulty
**Strategy**: Random selection from playable cards

**Characteristics**:
- Completely random valid moves
- No strategic thinking
- Fast decisions (instant)
- Good for beginners learning the game

**Example**:
```
Playable: [RED 5, BLUE SKIP, WILD]
AI picks: RED 5 (random, missed strategic SKIP)
```

### MEDIUM Difficulty
**Strategy**: Priority-based decision making

**Priority Order**:
1. **Play special cards first** (Skip, Reverse, Draw cards)
2. **Match by color** to maintain color control
3. **Play high-value cards** to minimize hand value
4. **Play any valid card** as fallback

**Characteristics**:
- Makes sensible strategic decisions
- Competes effectively against casual players
- O(n) complexity where n = hand size (~7-15)
- Uses special cards tactically

**Example**:
```
Top Card: RED 7
Playable: [BLUE 7, RED 3, RED SKIP, WILD]
AI Decision:
1. Check special cards: RED SKIP found
2. PLAY RED SKIP (disrupts opponent)
```

### HARD Difficulty
**Strategy**: Advanced game state analysis

**Enhanced Features**:
1. **Threat Detection**: Identifies opponents close to winning (≤2 cards)
2. **Defensive Play**: Prioritizes disruptive cards when threatened
3. **Wild Card Preservation**: Saves wilds for critical moments
4. **Hand Optimization**: Analyzes color distribution

**Decision Logic**:
```
IF next player has ≤ 2 cards:
    → Play Draw Five, Skip Everyone, or Wild Draw Color
ELSE IF have non-wild playable cards:
    → Save wild cards, use basic strategy on non-wilds
ELSE:
    → Use wild cards strategically
```

**Characteristics**:
- Highly competitive against experienced players
- Looks ahead to next player's position
- Adapts tactics based on game state
- Makes defensive plays to prevent opponent wins

**Example**:
```
Game State:
- AI: 5 cards
- Next Player: 2 cards (THREAT!)
- Playable: [BLUE 7, RED 3, DRAW_FIVE, WILD]

AI Decision:
1. Detect threat: Next player close to winning
2. Find disruptive card: DRAW_FIVE
3. PLAY DRAW_FIVE → Opponent draws 5, loses turn
   (Prevents potential win!)
```

### Wild Card Color Selection

When AI plays a wild card, it intelligently chooses color:

**Algorithm**:
1. Count each color in remaining hand
2. Find most common color
3. Choose that color (or random if tie/none)

**Example**:
```
After playing wild, AI hand: [RED 5, RED 3, BLUE 7, GREEN 2]
Color counts: RED=2, BLUE=1, GREEN=1, YELLOW=0
AI chooses: RED (can play those cards next turn!)
```

**For Dark Side Wilds**: Same logic with TEAL, PURPLE, PINK, ORANGE

### AI Turn Flow
```
1. Turn advances to AI
2. GameController detects: currentPlayer.isAI()
3. GameModel.checkAndProcessAITurn()
4. AI.selectCardToPlay(gameState)
5. IF cardIndex == -1: Draw card
   ELSE: Play selected card
6. If wild: AI.chooseWildColor()
7. Update view, advance turn
```

**Performance**: All AI decisions complete in < 1ms

---

## Project Structure

```
uno-project/
├── src/
│   ├── model.Card.java                  # model.Card entity (Model)
│   ├── model.Deck.java                  # model.Deck of cards (Model)
│   ├── model.Player.java                # model.Player entity (Model)
│   ├── model.GameModel.java             # Core game logic (Model)
|   ├── model.AIPlayer                   # Core AI logic (Model)
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
- `GameModel.java`: Core game engine, AI turn processing
- `Card.java`: Dual-sided card with flip() method
- `Deck.java`: 112-card deck with light/dark sides
- `Player.java`: Base player class
- `AIPlayer.java`: AI player with difficulty strategies
**(NEW)**
- `GameState.java`: Immutable state snapshots

**Key Features**:
- No UI dependencies: Pure logic, fully testable
- AI decision-making encapsulated in AIPlayer
- Polymorphic player handling (Human and AI)


### View Layer
**Purpose**: Displays game state to user
**Classes**:
- `GameView.java`: Main GUI coordinator

**Key Features**:
- Dual color prompts (light and dark side wilds)
- Dynamic card image loading (light/dark sides)
- No game logic: Only rendering

### Controller Layer
**Purpose**: Mediates between Model and View
- `GameController.java`: Handles all events, AI detection

**Key Features**:
- Detects AI players via `isAI()` check
- Triggers AI turn processing automatically
- Validates actions before model changes


### Communication Flow
```
User Click → View → Controller → Model
                                   ↓
                              (updates state)
                                   ↓
                              (AI turn check)
                                   ↓
User sees ← View ← Controller ← Model (fires event)
```

---

## Testing

### Test Coverage
- **Card.java**: 8 tests (including flip tests)
- **Deck.java**: 7 tests (including flip deck tests)
- **Player.java**: 11 tests (including isAI tests)
- **AIPlayer.java**: 12 tests **(NEW)**
- **GameModel.java**: 25 tests (including AI integration)
- **UNO Flip Cards**: 8 tests **(NEW)**
- **Total**: 71 unit tests

### Running Tests
```bash
cd "uno project/tests"
javac -cp .:junit-5.jar:../src *.java
java -jar junit-platform-console-standalone.jar --class-path .:../src --scan-class-path
```

### Test Categories
1. **Game Setup**: Player counts, AI configuration, initial dealing
2. **Card Playing**: Valid/invalid plays, hand updates
3. **Turn Management**: Turn advancement, AI detection
4. **Special Cards**: SKIP, REVERSE, DRAW effects
5. **UNO Flip**: FLIP card cascade, side changes
6. **AI Strategy**: Easy/Medium/Hard decision making
7. **Winning Conditions**: Round wins, scoring, game wins
8. **Card Playability**: Color/value matching rules

---

## Known Issues

### Current Issues
1. **Deck Exhaustion**: If deck runs out before round ends, game may stall
    - *Planned Fix*: Implement discard pile reshuffling

### Resolved Issues (from Milestone 2)
- ✅ Multiple Rounds: Fixed with `newRound()` method
- ✅ REVERSE in 2-Player: Properly implemented as skip

### Future Enhancements
- Undo/Redo functionality
- Save/Load game state
- Network multiplayer support
- Card animation effects
- Sound effects and music
- AI learning from player behavior
- Tournament mode
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

### Milestone 3 (AI Players + UNO Flip) - **(NEW)**

#### **Faris Hassan** (101300683) - AI Integration & Game Model Enhancement
- Extended GameModel with AI turn processing
- Implemented `processAITurn()` and `checkAndProcessAITurn()` methods
- AI-specific card play handling with wild color selection
- GameModel constructor overload for AI configuration
- UNO Flip special card handlers (FLIP, DRAW_FIVE, SKIP_EVERYONE, WILD_DRAW_COLOR)
- **Deliverables**:
    - Enhanced `GameModel.java` with AI support
    - `handleFlipCard()` cascade logic
    - AI turn processing integration
    - Updated `GameState` with currentSide
    - Comprehensive JavaDocs for new methods
    - AI integration unit tests

#### **Nicky Fang** (101304731) - AI Player Implementation
- Created `AIPlayer` class extending Player
- Implemented three difficulty levels (EASY, MEDIUM, HARD)
- `selectCardToPlay()` method with strategy pattern
- Smart wild color selection algorithms
- Next player threat detection (HARD mode)
- Polymorphic AI detection with `isAI()` override
- **Deliverables**:
    - `AIPlayer.java` with complete strategies
    - `chooseWildColor()` and `chooseWildDrawColor()` methods
    - Easy/Medium/Hard strategy implementations
    - DifficultyLevel enum
    - AI player unit tests
    - Strategy validation tests

#### **Ivan Arkhipov** (101310636) - GUI Updates & UNO Flip Support
- Updated GameView with dark side wild color prompt
- Dynamic card image loading for light/dark sides
- Card image path logic based on currentSide
- UI support for displaying flipped cards
- Visual feedback for AI turns
- Resource organization for dual-sided cards
- **Deliverables**:
    - `promptDarkWildColor()` dialog
    - `getPath(Card)` method for image loading
    - Card image folders: `/unoCards/` and `/DarkSideCards/`
    - Updated `render()` method for flip support
    - AI turn status messages
    - Visual polish and UX improvements

#### **Bhagya Patel** (101324150) - Documentation, UML & Testing
- Updated UML class diagram with AIPlayer and flip support
- Created sequence diagrams for Flip Card and AI Turn
- Wrote comprehensive AI strategy explanation
- Documented data structure changes (M2 → M3)
- Updated README with AI features and flip mechanics
- Coordinated testing coverage across new features
- **Deliverables**:
    - Updated PlantUML class diagram
    - `SequenceDiagram-FlipCard.puml` **(NEW)**
    - `SequenceDiagram-AITurn.puml` **(NEW)**
    - `DataStructureChanges-M3.md` **(NEW)**
    - AI Strategy section in README **(NEW)**
    - Updated `README.md` (this file)
    - AIPlayer test cases
    - Documentation review and proofreading

---

## Additional Resources

### UML Diagrams
- See `docs/UML-ClassDiagram.puml` for full class structure
- See `docs/SequenceDiagram-*.puml` for interaction flows
- See '`docs/M3SequenceHuman-*.puml` for interaction flows for HumanPlayers
- See `docs/M3SequenceAI-*.puml` for interaction flows for AI
### Data Structure Changes
- See `docs/DataStructureChanges.md` for detailed M1→M2 evolution
- See `docs/M3DataStructureChanges.md` for detailed M2→M3 evolution

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
**Version**: 3.0 (Milestone 3 - AI Players + UNO Flip)