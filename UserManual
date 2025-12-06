# UNO FLIP Game - User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [System Requirements](#system-requirements)
3. [Installation and Running the Game](#installation-and-running-the-game)
4. [Game Setup](#game-setup)
5. [How to Play](#how-to-play)
6. [Game Features](#game-features)
7. [UNO FLIP Rules](#uno-flip-rules)
8. [Card Types and Actions](#card-types-and-actions)
9. [Troubleshooting](#troubleshooting)

---

## Introduction

Welcome to **UNO FLIP**! This is a digital implementation of the classic UNO card game with the exciting FLIP twist. The game supports 2-4 players (human or AI) and includes features like:
- Traditional UNO gameplay with FLIP mechanics
- AI opponents with three difficulty levels (Easy, Medium, Hard)
- Save/Load game functionality
- Undo/Redo moves
- Replay functionality after game completion
- Scoring system to 500 points

**Development Team**:
- Nicky Fang (101304731) - Replay Functionality
- Bhagya Patel (101324150) - Undo/Redo Implementation
- Faris Hassan (101300683) - Testing & Documentation
- Ivan Arkhipov (101310636) - Serialization/Save-Load

**Version**: 4.0 (Milestone 4)

---

## System Requirements

- **Operating System**: Windows, macOS, or Linux
- **Java Version**: Java 8 or higher (JRE/JDK)
- **Memory**: Minimum 256 MB RAM
- **Disk Space**: 50 MB for game files
- **Display**: Minimum 1024x768 resolution recommended

---

## Installation and Running the Game

### Step 1: Verify Java Installation

Open a terminal or command prompt and type:
```bash
java -version
```

You should see output indicating Java version 8 or higher. If not, download and install Java from [https://www.java.com](https://www.java.com)

### Step 2: Run the JAR File

**Option A: Double-Click (GUI)**
1. Navigate to the folder containing `UNO.jar`
2. Double-click `UNO.jar`
3. The game should launch automatically

**Option B: Command Line**
1. Open a terminal/command prompt
2. Navigate to the directory containing `UNO.jar`:
   ```bash
   cd path/to/game/folder
   ```
3. Run the command:
   ```bash
   java -jar UNO.jar
   ```

### Troubleshooting Launch Issues

If double-clicking doesn't work:
- Ensure `.jar` files are associated with Java
- Try running from command line
- Check that you have Java installed (not just JRE)
- Verify file permissions (JAR should be executable)

---

## Game Setup

### Starting a New Game

1. **Select Number of Players**
   - When you launch the game, you'll be prompted to select 2-4 players
   - Choose the number that best suits your game session

2. **Configure Player Types**
   - For each player, select whether they are:
     - **Human Player**: You control this player
     - **AI Player**: Computer controls this player
   - By default, Player 1 is human and others are AI
   - You can have any combination (e.g., all human, all AI, mixed)

3. **Select AI Difficulty** (if AI players are enabled)
   - **Easy**: AI plays randomly with no strategy
   - **Medium**: AI uses basic strategy (prefers special cards, high-value cards)
   - **Hard**: AI uses advanced strategy (considers next player, saves wild cards, strategic play)

4. **Game Initialization**
   - Each player receives 7 cards
   - A starting card is placed in the discard pile (never a special card)
   - The game begins with Player 1's turn
   - If Player 1 is AI, they will automatically take their turn after 3 seconds

---

## How to Play

### Game Interface

The game window consists of several sections:

#### Top Card Section
- Displays the current card on top of the discard pile
- Shows a large image of the card
- This is the card you must match to play

#### Status Section
- **Current Player**: Shows whose turn it is
- **Direction**: Arrow indicates clockwise (→) or counterclockwise (←)
- **Status**: Shows special conditions (e.g., "WILD RED" if a wild color was chosen)

#### Your Hand Section
- Displays all cards in the current player's hand
- **Playable cards** have a **GREEN BORDER**
- **Non-playable cards** appear normal
- Grayed-out cards indicate it's not your turn (AI turn)
- Scroll horizontally if you have many cards

#### Scoreboard Section
- Shows all players and their current scores
- Updated after each round
- First player to 500 points wins the game

#### Game Management Section
- **Save Game** button - Save current game state
- **Load Game** button - Load a previously saved game

#### Control Buttons

**Left Side (above your hand)**:
- **Next** - End your turn and pass to the next player
- **Undo** - Undo your last action

**Right Side (above your hand)**:
- **Draw Card** - Draw a card from the deck
- **Redo** - Redo an action you undid

---

### Taking Your Turn

#### Option 1: Play a Card

1. Look at your hand for cards with a **GREEN BORDER** (these are playable)
2. Click on any green-bordered card to play it
3. **If you play a WILD card**, a dialog will appear:
   - **Light Side WILD**: Choose RED, YELLOW, GREEN, or BLUE
   - **Dark Side WILD**: Choose ORANGE, PINK, PURPLE, or TEAL
   - Click your chosen color
4. The card is added to the discard pile
5. Special card effects are applied automatically
6. Your turn typically ends automatically (exception: SKIP EVERYONE)

**Card Playability Rules**:
You can play a card if it matches the top discard card by:
- **Same Color**: RED on RED, BLUE on BLUE, etc.
- **Same Number/Symbol**: 5 on 5, SKIP on SKIP, etc.
- **WILD Cards**: Can always be played on any card

#### Option 2: Draw a Card

1. If you have **no playable cards** (no green borders), the **Draw Card** button is enabled
2. Click **Draw Card** to draw one card from the deck
3. **If the drawn card is playable**:
   - It will have a green border
   - You may play it immediately by clicking it
   - OR you can choose not to play it
4. **If the drawn card is not playable** (or you choose not to play):
   - Click the **Next** button to end your turn
   - The **Next** button is enabled after drawing

#### Important Notes

- You **cannot** draw if you have playable cards
- You **must** draw if you have no playable cards
- After drawing, you may play the drawn card only if it's playable
- Some special cards require you to choose a color (WILD cards)

---

## Game Features

### 1. Save Game (Ivan Arkhipov - Serialization)

**Purpose**: Save your current game progress to continue later

**How to Use**:
1. Click the **"Save Game"** button at any time during gameplay
2. The game serializes all data to a file named `uno_save.dat`
3. Location: Same directory as `UNO.jar`
4. A confirmation message appears: "Game saved successfully!"
5. You can safely close the game

**What Gets Saved**:
- All players' hands (every card)
- All players' scores
- Current player and turn state
- Deck state and remaining cards
- Discard pile and top card
- Game direction (clockwise/counterclockwise)
- Current side (Light/Dark)
- **Complete undo/redo history** (all previous states)
- Turn taken status

**Technical Details**:
- Uses Java serialization for object persistence
- Deep copies ensure data integrity
- Transient fields (like Random) are properly reinitialized
- File size varies based on game state and undo history

---

### 2. Load Game (Ivan Arkhipov - Deserialization)

**Purpose**: Resume a previously saved game

**How to Use**:
1. Launch the game with the same number of players as the saved game
2. Click the **"Load Game"** button
3. The game deserializes from `uno_save.dat`
4. A confirmation message appears: "Game loaded successfully!"
5. The game resumes exactly where you left off
6. If it's an AI player's turn, they automatically play after 3 seconds

**Important Requirements**:
- **Player count MUST match**: If you saved a 3-player game, you must load with 3 players
- Player types (Human/AI) are restored from the save file
- Save file must be in the same directory as the JAR
- Save file must not be corrupted

**What Happens on Load**:
1. Game removes old listeners from model
2. Reads and deserializes GameState from file
3. Restores all game components
4. Reattaches controller listeners
5. Updates UI with restored state
6. Resumes gameplay

**Error Messages**:
- "Failed to load game" - File not found or I/O error
- "Invalid save file format" - Corrupted or incompatible file
- "Number of players does not match" - Player count mismatch

---

### 3. Undo Move (Bhagya Patel - Undo Functionality)

**Purpose**: Take back your last action to try a different move

**How to Use**:
1. Click the **"Undo"** button (located above your hand on the left side)
2. The game reverts to the state before your last action
3. You can click Undo multiple times to go back several moves
4. The button is grayed out when there's nothing to undo

**What Can Be Undone**:
- Playing a card (returns card to your hand)
- Drawing a card (removes card from hand, returns to deck)
- Ending a turn (returns to previous player)
- AI moves can also be undone

**How It Works**:
- Before each action, the game creates a deep copy of the entire game state
- This copy is pushed onto the **undo stack**
- When you undo, the current state is saved to the **redo stack**
- The previous state is popped from the undo stack and restored
- All players' hands, deck, scores, and settings are restored

**Limitations**:
- Cannot undo past the start of the current session
- Undo history is cleared when you save/load (starts fresh)
- Memory limits the number of undo operations (typically hundreds)
- Button is disabled during AI turns

**Visual Feedback**:
- Button turns gray when nothing to undo
- Button is enabled (clickable) when undo is available

---

### 4. Redo Move (Bhagya Patel - Redo Functionality)

**Purpose**: Restore an action you previously undid

**How to Use**:
1. After clicking Undo, the **"Redo"** button becomes available
2. Click the **"Redo"** button (located above your hand on the right side)
3. The game restores the action you just undid
4. You can click Redo multiple times to restore a sequence of actions
5. The button is grayed out when there's nothing to redo

**How It Works**:
- When you undo, the current state is pushed onto the **redo stack**
- When you redo, the current state is pushed back onto the undo stack
- The next state is popped from the redo stack and restored

**Important Notes**:
- Redo is **only available** after using Undo
- Making a **new move clears the entire redo history**
- You cannot redo after playing a card, drawing, or ending turn
- This is standard undo/redo behavior (like in text editors)

**Example Workflow**:
1. Play a RED 5
2. Click Undo (RED 5 returns to hand)
3. Redo is now available
4. Click Redo (RED 5 is played again)
5. OR play a different card instead (redo history is cleared)

---

### 5. Replay Game (Nicky Fang - Replay Functionality)

**Purpose**: Start a new game after someone wins without restarting the program

**How to Use**:
1. Play until a player reaches 500 points
2. A dialog appears showing the winner and final scores
3. Message: "🎉 [Winner] WINS THE GAME WITH [Score] POINTS! 🎉"
4. You're asked: "Do you want to play another game?"
5. Click **"Yes"** to play again
6. Click **"No"** to exit the game

**What Happens When You Replay**:
- All player scores are reset to 0
- New deck is created and shuffled
- Each player receives 7 new cards
- A new starting card is placed
- Game starts fresh from round 1
- Same players and player types are kept
- Undo/redo history is cleared

**Benefits**:
- No need to close and restart the program
- Rematch with same players
- Scores start fresh for fair competition
- Immediate continuation for multiple games

**Technical Implementation**:
- `restartGame()` method resets all scores
- `newRound()` method sets up the new game
- All game state is reinitialized
- Players maintain their names and AI settings

---

### 6. AI Opponents

The game features three AI difficulty levels with distinct strategies:

#### Easy Difficulty
**Strategy**: Random play
- Selects a random playable card from hand
- No consideration of game state
- No preference for special cards
- Good for beginners or casual play

#### Medium Difficulty (Default)
**Strategy**: Basic tactical play
1. **Prioritize special cards**: Plays SKIP, REVERSE, DRAW cards first
2. **Color matching**: Prefers matching the current color
3. **High-value cards**: Plays highest point-value cards
4. **Wild card management**: Uses WILD cards when necessary

#### Hard Difficulty
**Strategy**: Advanced strategic play
1. **All Medium strategies** PLUS:
2. **Target weak opponents**: If next player has ≤2 cards, plays offensive cards (SKIP, DRAW cards)
3. **Save WILD cards**: Avoids playing WILD cards if other options exist
4. **Smart color selection**: Chooses colors based on hand composition
5. **Game state awareness**: Considers overall game situation

**AI Behavior**:
- AI players automatically take their turn after a **3-second delay**
- This delay makes the game feel more natural and human-like
- You'll see "AI Player X" as the current player
- All buttons are disabled during AI turns
- AI moves can be undone if needed

**AI Color Selection**:
When AI plays a WILD card:
1. Counts cards of each color in hand
2. Chooses the color with most cards
3. If no preference, chooses randomly
4. Maximizes future playability

---

## UNO FLIP Rules

### Game Objective

Be the **first player to reach 500 points** by winning multiple rounds.

### Winning a Round

- Play all cards in your hand (get rid of every card)
- When you play your last card, you win the round
- Score points based on cards remaining in opponents' hands
- A new round begins immediately

### Winning the Game

- First player to reach **500 total points** wins the entire game
- Points accumulate across multiple rounds
- Replay option appears after game completion

### Scoring System

When you win a round, you score points for **every card** left in **all opponents' hands**:

| Card Type | Point Value |
|-----------|-------------|
| Number Cards (0-9) | Face value |
| SKIP | 20 |
| REVERSE | 20 |
| DRAW ONE | 10 |
| FLIP | 20 |
| DRAW FIVE | 20 |
| SKIP EVERYONE | 30 |
| WILD | 40 |
| WILD DRAW TWO | 50 |
| WILD DRAW COLOR | 60 |

**Example**: If you win a round and opponents have:
- Player 2: WILD (40), RED 7 (7), SKIP (20) = 67 points
- Player 3: BLUE 3 (3), YELLOW 9 (9) = 12 points
- **You score**: 67 + 12 = **79 points total**

---

### The FLIP Mechanic

UNO FLIP features **double-sided cards**:
- **Light Side**: Traditional colors (RED, BLUE, GREEN, YELLOW)
- **Dark Side**: New colors (TEAL, PURPLE, PINK, ORANGE)

**When a FLIP card is played**:
1. **ALL cards** in **ALL players' hands** flip to the other side
2. **ALL cards** in the deck flip
3. **ALL cards** in the discard pile flip
4. The current side changes (Light ↔ Dark)
5. The game continues on the new side

**Card Transformations**:
- Light ZERO → Dark FIVE
- Light number (1-9) → Same number on dark side
- Light SKIP → Dark SKIP EVERYONE
- Light DRAW ONE → Dark DRAW FIVE
- Light WILD DRAW TWO → Dark WILD DRAW COLOR
- Light REVERSE → Dark REVERSE

**Strategic Implications**:
- FLIP can completely change your hand strength
- A strong light hand might become weak on dark side (or vice versa)
- Use FLIP to disrupt opponents
- Plan for both sides of your cards

---

## Card Types and Actions

### Light Side Cards

#### Number Cards (0-9)
- **Colors**: RED, BLUE, GREEN, YELLOW
- **Action**: No special effect, just number matching
- **Quantity**: 
  - ZERO: 1 per color (4 total)
  - 1-9: 2 per color each (72 total)

#### SKIP
- **Action**: Next player loses their turn completely
- **Special Case**: In 2-player game, you get another turn
- **Quantity**: 2 per color (8 total)
- **Strategy**: Use against players close to winning

#### REVERSE
- **Action**: Reverses the direction of play
- **Effect**: Clockwise → Counterclockwise (or vice versa)
- **Special Case**: In 2-player game, acts like SKIP
- **Quantity**: 2 per color (8 total)
- **Strategy**: Changes who goes next

#### DRAW ONE
- **Action**: 
  1. Next player draws 1 card from deck
  2. Next player skips their turn
  3. Play moves to the following player
- **Quantity**: 2 per color (8 total)
- **Strategy**: Slows down opponents

#### WILD
- **Action**: 
  1. Can be played on **any** card
  2. You choose the next color
  3. No other special effect
- **Color Selection**: RED, YELLOW, GREEN, or BLUE
- **Quantity**: 4 in deck
- **Strategy**: Save for when you have no other plays

#### WILD DRAW TWO
- **Action**:
  1. Can be played on any card
  2. Next player draws 2 cards
  3. Next player skips their turn
  4. You choose the next color
- **Color Selection**: RED, YELLOW, GREEN, or BLUE
- **Quantity**: 4 in deck
- **Strategy**: Powerful card, use strategically

#### FLIP
- **Action**: Flips all cards to Dark Side
- **Effect**: Complete game transformation
- **Quantity**: 2 per color (8 total)
- **Strategy**: Use when dark side favors you

---

### Dark Side Cards

#### Number Cards (1-9 and 5)
- **Colors**: TEAL, PURPLE, PINK, ORANGE
- **Action**: No special effect
- **Note**: Light ZERO becomes Dark FIVE
- **Quantity**: Multiple of each per color

#### SKIP EVERYONE
- **Action**: 
  1. ALL other players lose their turn
  2. **YOU get to play again immediately**
  3. This is a major advantage
- **Quantity**: 2 per color (8 total)
- **Strategy**: Extremely powerful, can play multiple cards in a row

#### REVERSE
- **Action**: Same as Light Side - reverses play direction
- **Quantity**: 2 per color (8 total)

#### DRAW FIVE
- **Action**:
  1. Next player draws **5 cards** from deck
  2. Next player skips their turn
  3. Very punishing
- **Quantity**: 2 per color (8 total)
- **Strategy**: Best defensive card against players close to winning

#### WILD
- **Action**: Same as Light Side WILD
- **Color Selection**: ORANGE, PINK, PURPLE, or TEAL
- **Quantity**: 4 in deck

#### WILD DRAW COLOR
- **Action**:
  1. Can be played on any card
  2. You choose a color (ORANGE, PINK, PURPLE, or TEAL)
  3. Next player draws cards **until** they draw the chosen color
  4. Next player skips their turn
  5. Maximum: 20 cards (safety limit to prevent infinite draws)
- **Color Selection**: ORANGE, PINK, PURPLE, or TEAL
- **Quantity**: 4 in deck
- **Strategy**: Most powerful card in the game, choose rare colors

#### FLIP
- **Action**: Flips all cards back to Light Side
- **Effect**: Returns to traditional UNO colors
- **Quantity**: 2 per color (8 total)

---

## Advanced Gameplay Tips

### General Strategy

1. **Play High-Value Cards Early**
   - Special cards and high numbers are worth more points
   - If opponent wins, these count against you
   - Get rid of WILD, WILD DRAW TWO, WILD DRAW COLOR first

2. **Save WILD Cards for Emergency**
   - Only use WILD cards when you have no other play
   - They give you maximum flexibility
   - Don't waste them unnecessarily

3. **Watch Opponents' Hand Sizes**
   - If an opponent has 1-2 cards, they're close to winning
   - Use action cards against them (SKIP, DRAW cards)
   - Be aggressive to prevent their victory

4. **Color Management**
   - Try to keep cards of multiple colors
   - Don't let yourself get stuck with only one color
   - This increases your playability

5. **Number Matching vs. Color Matching**
   - Both are valid plays
   - Number matching can change the active color
   - Color matching keeps current color

### FLIP Strategy

1. **Track Both Sides**
   - Remember what your cards become when flipped
   - Light SKIP becomes Dark SKIP EVERYONE (much stronger)
   - Plan ahead for potential FLIPs

2. **Flip When Advantageous**
   - Use FLIP when dark side gives you better cards
   - Use FLIP to disrupt opponent strategies
   - FLIP can turn a bad hand into a good one

3. **Post-FLIP Adaptation**
   - Immediately reassess your hand after FLIP
   - Color matching changes (RED → TEAL, etc.)
   - Number matching still works

### Undo/Redo Strategy

1. **Use Undo for Mistakes**
   - Accidentally played wrong card? Undo it
   - Drew when you had playable cards? Undo
   - Test different strategies safely

2. **Redo for Confirmation**
   - Undo to check other options
   - Redo if first play was best
   - Learn opponent patterns

3. **Don't Overuse**
   - Makes game less exciting
   - Reduces strategic decision-making
   - Use sparingly for genuine mistakes

### AI Opponent Strategy

**Against Easy AI**:
- Play normally, they're random
- Easy to predict (unpredictable is predictable)

**Against Medium AI**:
- They prioritize special cards
- Expect SKIP/REVERSE early
- They play high-value cards first

**Against Hard AI**:
- Very strategic and adaptive
- Will target you if you're close to winning
- Saves WILD cards
- Choose colors to maximize their hand
- Most challenging opponent

### WILD Card Color Selection

**Choose colors based on**:
1. **Your hand composition** - Pick color you have most of
2. **What opponents might not have** - Force them to draw
3. **Light vs. Dark side** - Different color options
4. **Next player's likely hand** - Deny them easy plays

### Common Mistakes to Avoid

1. **Forgetting to Draw**
   - If no playable cards, you MUST draw
   - Draw button will be enabled

2. **Not Ending Turn After Drawing**
   - After drawing unplayable card, click "Next"
   - Turn doesn't end automatically

3. **Wasting Action Cards**
   - Save SKIP/DRAW cards for critical moments
   - Don't use them just because you can

4. **Ignoring the Scoreboard**
   - Track who's winning overall
   - Target the leader with action cards

5. **Poor WILD Usage**
   - Playing WILD when you have regular playable cards
   - Wasting powerful cards early

6. **Not Using Undo**
   - Made a mistake? Undo it!
   - Feature exists to help you

---

## Troubleshooting

### Game Launch Issues

**Problem**: Double-clicking JAR file doesn't work

**Solutions**:
1. Verify Java installation:
   ```bash
   java -version
   ```
2. Run from command line:
   ```bash
   java -jar UNO.jar
   ```
3. Check file permissions (ensure JAR is executable)
4. Re-download the JAR file (might be corrupted)
5. Check file association (`.jar` should open with Java)

**Problem**: "Java not recognized" error

**Solutions**:
1. Install Java from https://www.java.com
2. Add Java to PATH environment variable
3. Use full path: `C:\Program Files\Java\jre1.8.0_XXX\bin\java.exe -jar UNO.jar`

---

### Save/Load Issues

**Problem**: "Failed to save game" error

**Solutions**:
1. Check available disk space
2. Ensure write permissions in game directory
3. Close other programs that might lock files
4. Try running as administrator (Windows)
5. Check antivirus isn't blocking file creation

**Problem**: "Failed to load game" error

**Solutions**:
1. Ensure `uno_save.dat` exists in same folder as JAR
2. Verify the save file isn't corrupted
3. Try loading with different player counts (2, 3, or 4)
4. Delete `uno_save.dat` and start new game
5. Re-download game if save file is corrupted

**Problem**: "Number of players does not match" error

**Solution**:
- The save file was created with X players
- You must load with exactly X players
- Try player counts: 2, then 3, then 4
- If unsure, start a new game

**Problem**: "Invalid save file format" error

**Solutions**:
1. Save file is corrupted or from different version
2. Delete `uno_save.dat` and save a new game
3. Don't manually edit save files
4. Re-download game if problem persists

---

### Display Issues

**Problem**: Cards or text are too small

**Solutions**:
1. Maximize the game window
2. Adjust screen resolution (Settings → Display)
3. Check OS display scaling settings
4. Minimum recommended: 1024x768 resolution

**Problem**: Cards not displaying / blank buttons

**Solutions**:
1. Ensure card image files are in correct directory:
   - `view/unoCards/` for light side
   - `view/DarkSideCards/` for dark side
2. Check console output for missing file errors
3. Re-download complete game package
4. Verify folder structure is intact

**Problem**: Window too large or too small

**Solutions**:
1. Resize window by dragging edges
2. Maximize window for best experience
3. Adjust before starting game

---

### Gameplay Issues

**Problem**: Can't click cards during AI turn

**Solution**: 
- This is **normal behavior**
- Wait for AI to finish (3-second delay)
- All buttons disabled during AI turn
- Be patient, AI will complete turn automatically

**Problem**: Undo/Redo buttons grayed out

**Solutions**:
- **Undo grayed**: No moves to undo yet, or at start of game
- **Redo grayed**: No undone moves to redo, or you made a new move
- **Both grayed**: Start of game, no history
- This is normal behavior

**Problem**: Can't draw card when I want to

**Solution**:
- Draw is **only enabled** when you have no playable cards
- Check for green-bordered cards in your hand
- You must play green-bordered cards first
- Only draw when stuck

**Problem**: WILD card doesn't let me choose color

**Solution**:
- Color choice dialog should appear automatically
- If it doesn't, this is a bug - try Undo and replay
- Make sure you're clicking the card itself

**Problem**: Game freezes or becomes unresponsive

**Solutions**:
1. Wait 10 seconds (AI might be processing)
2. Check if error dialog is hidden behind window
3. Close and restart game
4. Load saved game to continue
5. Start new game if problem persists

---

### Error Messages

| Error Message | Meaning | Solution |
|---------------|---------|----------|
| "Deck is empty!" | No more cards to draw | Game continues; shuffle discard if needed |
| "Invalid card index" | Programming error | use Undo |
| "Cannot play [card] on [card]" | Card not playable | Choose green-bordered card |
| "A color is required" | Canceled WILD selection | Play WILD again and choose color |

---

## Quick Reference

### Button Guide

| Button | Location | Function |
|--------|----------|----------|
| Save Game | Bottom section | Save current game |
| Load Game | Bottom section | Load saved game |
| Undo | Left side, above hand | Undo last move |
| Redo | Right side, above hand | Redo undone move |
| Draw Card | Right side, above hand | Draw from deck |
| Next | Left side, above hand | End your turn |

---

**Manual Version**: 4.0  
**Last Updated**: Milestone 4  
**Game Version**: 4.0

---
