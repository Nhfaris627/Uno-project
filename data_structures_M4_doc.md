# Data Structure Changes Documentation - Milestone 3 to Milestone 4

## Document Information

**Author**: Faris Hassan (101300683)  
**Purpose**: Document UML and data structure changes for Milestone 4  
**Date**: Milestone 4 Submission  
**Version**: 4.0

---

## Executive Summary

Milestone 4 introduced significant architectural enhancements to support game persistence, state management, and replay functionality. The primary additions include:

1. **Serialization/Deserialization** (Ivan Arkhipov) - Complete save/load game functionality
2. **Undo/Redo System** (Bhagya Patel) - Multi-level state management with Stack data structures
3. **Replay Functionality** (Nicky Fang) - Game restart capability after completion

These changes required modifications to the UML class diagrams, addition of new methods, and careful consideration of data structure design.

---

## Table of Contents

1. [Overview of Changes](#1-overview-of-changes)
2. [Undo/Redo Data Structures](#2-undoredo-data-structures)
3. [New Methods in GameModel](#3-new-methods-in-gamemodel)
4. [New Methods in GameController](#4-new-methods-in-gamecontroller)
5. [New Methods in GameView](#5-new-methods-in-gameview)
6. [GameState Deep Copy Strategy](#6-gamestate-deep-copy-strategy)
7. [Replay Functionality](#7-replay-functionality)
8. [Summary of Changes](#8-summary-of-changes)

---

## 1. Overview of Changes

### 1.1 Serialization Support (Ivan Arkhipov)

All model classes now implement `Serializable` to enable game state persistence:

```java
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
}

public class AIPlayer extends Player {
    private static final long serialVersionUID = 1L;
    private transient Random random; // Reinitialized after deserialization
}

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
}

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
}

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

**Key Points**:
- `serialVersionUID = 1L` ensures version compatibility during deserialization
- `transient Random random` in AIPlayer is excluded from serialization and reinitialized via `readObject()` method
- `transient List<GameModelListener> listeners` in GameModel are not serialized (UI components)

### 1.2 New UI Components

**GameView additions**:
```java
private final JButton saveBtn = new JButton("Save Game");
private final JButton loadBtn = new JButton("Load Game");
private final JButton undoBtn = new JButton("Undo");
private final JButton redoBtn = new JButton("Redo");
```

---

## 2. Undo/Redo Data Structures

### 2.1 Stack Data Structures (Bhagya Patel)

**Added to GameModel**:
```java
private Stack<GameState> undoStack = new Stack<>();
private Stack<GameState> redoStack = new Stack<>();
```

**Added to GameState**:
```java
public Stack<GameState> undoStack;
public Stack<GameState> redoStack;
```

### 2.2 Why Stacks?

**Stack Characteristics**:
- **LIFO** (Last In, First Out) ordering
- **O(1)** push and pop operations
- Perfect for undo/redo operations

**Rationale**:
1. Most recent action should be undone first (LIFO behavior)
2. Efficient operations with no searching needed
3. Natural semantics for undo/redo
4. Built-in Java class with proper serialization support

### 2.3 Stack Contents

Each stack stores **complete GameState snapshots** including:
- All players and their hands (deep copies)
- Current player and index
- Top discard card
- Deck size
- Game settings (clockwise, turnTaken, currentSide)
- Nested undo/redo stacks (for save/load preservation)

**Why complete snapshots**:
- Ensures perfect restoration of previous state
- Simpler implementation than computing reverse operations
- Prevents state corruption
- Trade-off: Higher memory usage vs. reliability

---

## 3. New Methods in GameModel

### 3.1 Save/Load Methods (Ivan Arkhipov)

#### saveGame(String filename)

```java
public void saveGame(String filename) throws IOException {
    try (ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(filename))) {
        out.writeObject(this.getState());
    }
}
```

**Purpose**: Serialize current game state to file

**Process**:
1. Create ObjectOutputStream wrapping FileOutputStream
2. Call `getState()` to get complete game snapshot
3. Serialize GameState object (automatic)
4. Close stream (automatic via try-with-resources)

#### loadGame(String filename) - Static Method

```java
public static GameState loadGame(String filename) 
        throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(
            new FileInputStream(filename))) {
        GameState state = (GameState) in.readObject();
        return state;
    }
}
```

**Purpose**: Deserialize game state from file

**Why static**: Can be called without existing GameModel instance, returns GameState that can be applied to any GameModel

**Exception Handling**:
- **IOException**: File not found, read error, disk error
- **ClassNotFoundException**: Save file format incompatible, corrupted data

#### restoreState(GameState state)

```java
public void restoreState(GameState state) {
    this.currentPlayerIndex = state.currentPlayerIndex;
    this.isClockwise = state.clockwise;
    this.currentTurnTaken = state.turnTaken;
    this.currentSide = state.currentSide;
    this.undoStack = state.undoStack;
    this.redoStack = state.redoStack;
    
    // Deep copy players' hands
    for (int i = 0; i < players.size(); i++) {
        Player player = players.get(i);
        player.getHand().clear();
        Player snapshotPlayer = state.players.get(i);
        for (Card snapshotCard : snapshotPlayer.getHand()) {
            Card restoredCard = new Card(
                snapshotCard.getLightColor(),
                snapshotCard.getLightValue(),
                snapshotCard.getDarkColor(),
                snapshotCard.getDarkValue(),
                snapshotCard.getCurrentSide()
            );
            player.drawCard(restoredCard);
        }
        player.setScore(snapshotPlayer.getScore());
    }
    
    // Deep copy discard pile
    discardPile.clear();
    if (state.topDiscard != null) {
        Card topCopy = new Card(/* ... */);
        discardPile.add(topCopy);
    }
}
```

**Purpose**: Apply a loaded GameState to the current GameModel

**Key Operations**:
1. Restore scalar values (indices, booleans, enums)
2. Deep copy player hands (each card reconstructed)
3. Restore scores
4. Deep copy discard pile
5. Restore undo/redo stacks

**Why deep copies**: Prevents reference sharing between GameState and GameModel

---

### 3.2 Undo/Redo Methods (Bhagya Patel)

#### saveStateOnMove()

```java
private void saveStateOnMove() {
    undoStack.push(getState());
    redoStack.clear();
}
```

**Purpose**: Capture current state before any game-changing action

**When called**:
- Before `playCard()`
- Before `drawCard()`
- Before `endTurn()`
- Before `handleAICardPlay()`

**Why clear redoStack**: Standard undo/redo pattern - new action invalidates redo history

#### undo()

```java
public void undo() {
    if (!undoStack.isEmpty()) {
        redoStack.push(getState());        // Save current for redo
        GameState prev = undoStack.pop();  // Get previous state
        restoreState(prev);                // Apply it
        fireStateUpdated();                // Notify UI
    }
}
```

**Algorithm**:
1. Check if undo stack is not empty
2. Save current state to redo stack
3. Pop previous state from undo stack
4. Restore that previous state
5. Notify UI via listeners

**Visual representation**:
```
Before undo:
  undoStack: [state1, state2, state3]
  current: state4
  redoStack: []

After undo:
  undoStack: [state1, state2]
  current: state3
  redoStack: [state4]
```

#### redo()

```java
public void redo() {
    if (!redoStack.isEmpty()) {
        undoStack.push(getState());        // Save current for undo
        GameState next = redoStack.pop();  // Get next state
        restoreState(next);                // Apply it
        fireStateUpdated();                // Notify UI
    }
}
```

**Visual representation**:
```
Before redo:
  undoStack: [state1, state2]
  current: state3
  redoStack: [state4]

After redo:
  undoStack: [state1, state2, state3]
  current: state4
  redoStack: []
```

#### canUndo() and canRedo()

```java
public boolean canUndo() {
    return !undoStack.isEmpty();
}

public boolean canRedo() {
    return !redoStack.isEmpty();
}
```

**Purpose**: Enable/disable undo/redo buttons in UI based on availability

---

## 4. New Methods in GameController

### 4.1 Save/Load Action Handlers (Ivan Arkhipov)

#### onSaveGame()

```java
private void onSaveGame() {
    try {
        model.saveGame(SAVE_FILE);
        view.showMessage("Game saved successfully!");
    } catch (IOException ex) {
        view.showMessage("Failed to save game: " + ex.getMessage());
        ex.printStackTrace();
    }
}
```

**Error Handling**: Catches IOException and displays user-friendly message

#### onLoadGame()

```java
private void onLoadGame() {
    try {
        model.removeListener(this);                      // Step 1
        model.restoreState(model.loadGame(SAVE_FILE));  // Step 2
        model.addListener(this);                         // Step 3
        view.render(model.getState());                   // Step 4
        view.showMessage("Game loaded successfully!");
        
        if (model.getState().currentPlayer.isAI()) {     // Step 5
            SwingUtilities.invokeLater(() -> {
                model.checkAndProcessAITurn();
            });
        }
    } catch (IOException ex) {
        view.showMessage("Failed to load game: " + ex.getMessage());
    } catch (ClassNotFoundException ex) {
        view.showMessage("Invalid save file format");
    }
}
```

**Complex sequence**:
1. Remove listener to prevent duplicate notifications
2. Load and restore state
3. Re-add listener to resume observation
4. Render updated UI
5. If AI turn, trigger AI processing

### 4.2 Undo/Redo Action Handlers (Bhagya Patel)

```java
private void onUndo() {
    model.undo();
}

private void onRedo() {
    model.redo();
}
```

**Note**: Simple delegation to model; state update notifications handled automatically by model

---

## 5. New Methods in GameView

### 5.1 Button State Methods

```java
public void setUndoEnabled(boolean enabled) {
    undoBtn.setEnabled(enabled);
}

public void setRedoEnabled(boolean enabled) {
    redoBtn.setEnabled(enabled);
}
```

**Purpose**: Provide visual feedback by enabling/disabling buttons based on availability

**Called from**: `GameController.onStateUpdated()` after every state change

### 5.2 Replay Prompt Method (Nicky Fang)

```java
public boolean promptPlayAgain(Player winner) {
    int choice = JOptionPane.showConfirmDialog(
        root,
        winner.getName() + " WINS THE GAME WITH " + 
        winner.getScore() + " POINTS! \n\nDo you want to play another game?",
        "Game Over",
        JOptionPane.YES_NO_OPTION
    );
    return choice == JOptionPane.YES_OPTION;
}
```

**Returns**: `true` if player wants to replay, `false` to exit

---

## 6. GameState Deep Copy Strategy

### 6.1 The Problem with Shallow Copies

**WRONG approach**:
```java
GameState state = new GameState();
state.players = this.players; // Same reference!
```

**Problem**: All states in undo stack would point to same objects. Changes to current game would modify "saved" states. Undo wouldn't work.

### 6.2 Deep Copy Solution in getState()

```java
public GameState getState() {
    GameState state = new GameState();
    
    // Deep copy players
    state.players = new ArrayList<>();
    for (Player original : players) {
        Player copy;
        if (original instanceof AIPlayer) {
            AIPlayer aiOriginal = (AIPlayer) original;
            copy = new AIPlayer(aiOriginal.getName(), 
                              aiOriginal.getDifficultyLevel());
        } else {
            copy = new Player(original.getName());
        }
        copy.setScore(original.getScore());
        
        // Deep copy each card in hand
        for (Card originalCard : original.getHand()) {
            Card cardCopy = new Card(
                originalCard.getLightColor(),
                originalCard.getLightValue(),
                originalCard.getDarkColor(),
                originalCard.getDarkValue(),
                originalCard.getCurrentSide()
            );
            copy.drawCard(cardCopy);
        }
        state.players.add(copy);
    }
    
    // Deep copy top discard card
    Card top = getTopDiscardCard();
    state.topDiscard = (top != null) ? new Card(
        top.getLightColor(), top.getLightValue(),
        top.getDarkColor(), top.getDarkValue(),
        top.getCurrentSide()
    ) : null;
    
    // Copy other fields...
    state.currentPlayerIndex = currentPlayerIndex;
    state.deckSize = deck.size();
    state.playableIndices = getPlayableIndices();
    state.clockwise = isClockwise;
    state.turnTaken = currentTurnTaken;
    state.currentSide = currentSide;
    state.undoStack = undoStack;
    state.redoStack = redoStack;
    
    return state;
}
```

**Key Points**:
1. Creates new ArrayList (not reference)
2. Reconstructs each Player object
3. Reconstructs each Card object
4. Preserves player types (AIPlayer stays AIPlayer)
5. Complete independence - no shared references

**Memory diagram**:
```
Current Game:
  player1 -> hand -> [card1, card2, card3]
  
Deep Copy (CORRECT):
  undoStack.top.player1 -> NEW hand -> [NEW card1, NEW card2, NEW card3]
```

**Result**: Changes to current game don't affect saved states in undo/redo stacks

---

## 7. Replay Functionality

### 7.1 New Method in GameModel (Nicky Fang)

```java
public void restartGame() {
    for (Player p : players) {
        p.setScore(0);
    }
    newRound();
}
```

**Purpose**: Reset game for replay after game completion

**Operations**:
1. Reset all player scores to 0
2. Call `newRound()` to set up fresh round (deals cards, creates new deck, etc.)

**Why separate from newRound()**:
- `newRound()` is called after each round win (scores persist)
- `restartGame()` is called only after game win (scores reset)
- Clear separation of concerns

### 7.2 Integration in GameController

```java
@Override
public void onGameWon(Player winner, GameState state) {
    view.render(state);
    
    boolean playAgain = view.promptPlayAgain(winner);
    if (playAgain) {
        model.restartGame();
    } else {
        System.exit(0);
    }
    
    view.showMessage("  " + winner + 
        "  WINS THE GAME WITH " + winner.getScore() + " POINTS!  ");
}
```

**Flow**:
1. Game won event fired
2. Render final state
3. Prompt player for replay
4. If yes: restart game (scores reset, new round begins)
5. If no: exit program
6. Show victory message

---

## 8. Summary of Changes

### 8.1 Classes Modified

| Class | Changes |
|-------|---------|
| **Player** | Added `implements Serializable`, `serialVersionUID` |
| **AIPlayer** | Added `transient Random`, `readObject()` method for reinitialization |
| **Card** | Added `implements Serializable`, `serialVersionUID` |
| **Deck** | Added `implements Serializable`, `serialVersionUID` |
| **GameState** | Added `undoStack` and `redoStack` fields |
| **GameModel** | Added save/load methods, undo/redo methods, replay method, Stack fields |
| **GameController** | Added action handlers for save, load, undo, redo |
| **GameView** | Added save/load/undo/redo buttons, button state methods, replay prompt |

### 8.2 New Methods Summary

**GameModel**:
- `saveGame(String)` - Serialize to file (Ivan)
- `loadGame(String)` - Deserialize from file, static (Ivan)
- `restoreState(GameState)` - Apply state to model (Ivan)
- `saveStateOnMove()` - Capture state before action (Bhagya)
- `undo()` - Revert to previous state (Bhagya)
- `redo()` - Restore undone state (Bhagya)
- `canUndo()` - Check undo availability (Bhagya)
- `canRedo()` - Check redo availability (Bhagya)
- `restartGame()` - Reset for replay (Nicky)

**GameController**:
- `onSaveGame()` - Handle save button
- `onLoadGame()` - Handle load button
- `onUndo()` - Handle undo button
- `onRedo()` - Handle redo button

**GameView**:
- `setUndoEnabled(boolean)` - Enable/disable undo button
- `setRedoEnabled(boolean)` - Enable/disable redo button
- `promptPlayAgain(Player)` - Ask for replay

### 8.3 New Fields Summary

**GameModel**:
```java
private Stack<GameState> undoStack = new Stack<>();
private Stack<GameState> redoStack = new Stack<>();
```

**GameState**:
```java
public Stack<GameState> undoStack;
public Stack<GameState> redoStack;
```

**GameView**:
```java
private final JButton saveBtn = new JButton("Save Game");
private final JButton loadBtn = new JButton("Load Game");
private final JButton undoBtn = new JButton("Undo");
private final JButton redoBtn = new JButton("Redo");
```

### 8.4 Key Design Decisions

1. **Stack for Undo/Redo**: Natural LIFO semantics, O(1) operations, standard pattern
2. **Deep Copies**: Prevents reference corruption, ensures state independence
3. **Complete Snapshots**: Simpler than computing reverse operations
4. **Serialization**: Java's built-in mechanism, no external dependencies
5. **Transient Fields**: Random and listeners excluded from serialization for proper reinitialization
6. **Static loadGame()**: Allows loading without existing model instance

---

## Conclusion

Milestone 4 significantly enhanced the UNO FLIP game with professional-grade features:

- **Persistence** through serialization (Ivan Arkhipov)
- **State management** through undo/redo (Bhagya Patel)  
- **Replay capability** for continuous gameplay (Nicky Fang)
- **Comprehensive testing** for validation (Faris Hassan)

These additions required careful consideration of data structures, particularly:
- Stack-based undo/redo for efficient state management
- Deep copy strategy to prevent reference corruption
- Serialization support for all game objects
- Proper error handling and user feedback

The result is a robust, feature-complete implementation of UNO FLIP with save/load, undo/redo, and replay functionality.
