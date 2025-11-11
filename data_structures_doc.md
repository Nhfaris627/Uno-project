# Data Structure Explanation - UNO Game Project

## Overview
This document provides a detailed explanation of the data structures used in the UNO game implementation, their rationale, and the operations performed on them.

---

## 1. ArrayList<model.Card> - model.Deck and model.Player Hands

### Usage Locations:
- **model.Deck.java**: `private List<model.Card> cards`
- **model.Player.java**: `private List<model.Card> hand`
- **UnoGame.java**: `private List<model.Card> discardPile`

### Rationale:
ArrayList was chosen for card collections because:
- **Dynamic Sizing**: Cards are frequently added and removed during gameplay
- **Indexed Access**: O(1) time complexity for accessing cards by position, essential for displaying numbered card choices to players
- **Sequential Access**: Iteration through all cards is frequently needed for displaying hands and calculating scores
- **Efficient Removal**: While removal from middle is O(n), this occurs infrequently enough to not impact performance

### Key Operations:
| Operation | Method | Time Complexity | Usage |
|-----------|--------|-----------------|-------|
| Add card | `add(model.Card)` | O(1) amortized | Drawing cards, adding to discard pile |
| Remove card | `remove(int index)` | O(n) | Playing cards from hand |
| Remove specific | `remove(Object)` | O(n) | Playing drawn card immediately |
| Access by index | `get(int index)` | O(1) | Selecting cards, displaying hands |
| Size check | `size()` | O(1) | Checking hand size, deck size |
| Check empty | `isEmpty()` | O(1) | Validating deck/hand status |
| Iteration | `for-each loop` | O(n) | Displaying cards, calculating values |

### Example Usage:

```java
import model.Card;

// model.Deck - Drawing a card
public Card drawCard() {
    if (cards.isEmpty()) return null;
    return cards.remove(cards.size() - 1);  // O(1) removal from end
}

// model.Player - Calculating hand value
public int calculateHandValue() {
    int total = 0;
    for (Card card : hand) {  // O(n) iteration
        total += card.getPointValue();
    }
    return total;
}
```

---

## 2. ArrayList<model.Player> - Game Players

### Usage Location:
- **UnoGame.java**: `private List<model.Player> players`

### Rationale:
ArrayList was chosen for managing players because:
- **Fixed Size After Creation**: Number of players (2-4) is set at game start and never changes
- **Indexed Access**: O(1) access needed for current player index and winner determination
- **Order Preservation**: Maintains turn order throughout the game
- **Simple Iteration**: Easy to cycle through players for scoring and state display

### Key Operations:
| Operation | Method | Time Complexity | Usage |
|-----------|--------|-----------------|-------|
| Access player | `get(int index)` | O(1) | Getting current player, accessing by winner index |
| Get all players | `getPlayers()` | O(1) | Displaying all players, calculating scores |
| Iteration | `for-each loop` | O(n) | Dealing cards, calculating round scores |
| Size check | `size()` | O(1) | Modulo operations for turn cycling |

### Example Usage:

```java
import model.Player;

// Cycling through players
public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);  // O(1) access
}

// Round score calculation
for(
int i = 0; i <players.

size();

i++){  // O(n) iteration
        if(i !=winnerIndex){
totalPoints +=players.

get(i).

calculateHandValue();
    }
            }
```

---

## 3. ArrayList<Integer> - Playable model.Card Indices

### Usage Location:
- **UnoGame.java**: `getPlayableCards()` method returns `List<Integer>`

### Rationale:
ArrayList of integers was chosen for playable card indices because:
- **Dynamic Building**: Number of playable cards varies each turn
- **Reference Mapping**: Stores indices rather than cards themselves, maintaining connection to player's hand
- **Membership Testing**: `contains()` method validates player's card choice
- **Display Formatting**: Easy iteration for showing playable options to user

### Key Operations:
| Operation | Method | Time Complexity | Usage |
|-----------|--------|-----------------|-------|
| Add index | `add(Integer)` | O(1) amortized | Building list of playable cards |
| Check contains | `contains(Object)` | O(n) | Validating player's card choice |
| Iteration | `for-each loop` | O(n) | Displaying playable cards to user |
| Check empty | `isEmpty()` | O(1) | Determining if player must draw |

### Example Usage:

```java
import model.Player;

// Building playable cards list
private List<Integer> getPlayableCards(Player player) {
    List<Integer> playableCards = new ArrayList<>();
    for (int i = 0; i < player.getHandSize(); i++) {
        if (isCardPlayable(player.getHand().get(i))) {
            playableCards.add(i);  // O(1) add
        }
    }
    return playableCards;
}

// Validating choice
if(playableCards.

contains(choice)){  // O(n) search
        return choice;
}
```

---

## 4. Primitive Data Types

### Integer Variables:
- **currentPlayerIndex** (UnoGame): Tracks whose turn it is
- **score** (model.Player): Accumulates points across rounds
- **Time Complexity**: O(1) for all operations (increment, comparison, modulo)

### Boolean Variables:
- **isClockwise** (UnoGame): Tracks direction of play for REVERSE cards
- **Time Complexity**: O(1) for all operations (toggle, check)

### Rationale:
Primitives were chosen over wrapper classes (Integer, Boolean) because:
- **Performance**: No object creation overhead
- **Memory Efficiency**: Stored directly on stack
- **Simplicity**: Direct value semantics, no null checks needed

---

## 5. Enumerations

### model.Card.Color and model.Card.Value:
```java
public enum Color { RED, BLUE, GREEN, YELLOW, WILD }
public enum Value { ZERO, ONE, ..., NINE, SKIP, REVERSE, DRAW_ONE, WILD, WILD_DRAW_TWO }
```

### Rationale:
Enums were chosen over String constants because:
- **Type Safety**: Compile-time checking prevents invalid values
- **Performance**: Comparison is O(1) using reference equality
- **Maintainability**: Adding new colors/values requires updating enum only
- **Memory Efficiency**: Single instance per value (flyweight pattern)

### Key Operations:
| Operation | Time Complexity | Usage |
|-----------|-----------------|-------|
| Comparison | O(1) | Checking card color/value matches |
| Switch statement | O(1) | Calculating point values, handling special cards |
| valueOf() | O(n) where n=enum size | Parsing user color input for Wild cards |

---

## 6. Design Trade-offs and Alternatives Considered

### Why Not LinkedList?
- **Pro**: O(1) removal from arbitrary positions
- **Con**: O(n) indexed access, which is frequently needed for card selection
- **Decision**: ArrayList's O(1) indexed access outweighs occasional O(n) removal cost

### Why Not HashMap for Players?
- **Pro**: O(1) access by name
- **Con**: Doesn't preserve turn order, requires additional index tracking
- **Decision**: ArrayList maintains natural turn order and index-based access

### Why Not Stack for model.Deck/Discard?
- **Pro**: Models real-world card stacks
- **Con**: Limited to top-only access, no iteration support
- **Decision**: ArrayList provides flexibility for shuffle, iteration, and access while still supporting stack-like operations

### Why Not Array for Cards?
- **Pro**: Slightly better performance, lower memory overhead
- **Con**: Fixed size, requires manual resizing logic
- **Decision**: ArrayList's dynamic sizing simplifies code with negligible performance impact

---

## 7. Performance Analysis

### Overall Time Complexity by Operation:
| Game Operation | Dominant Data Structure Operation | Time Complexity |
|----------------|-----------------------------------|-----------------|
| Playing a card | Remove from ArrayList by index | O(n) |
| Drawing a card | Remove from ArrayList end | O(1) |
| Displaying hand | Iterate through ArrayList | O(n) |
| Checking playable cards | Iterate through hand, check each card | O(n × m)* |
| Calculating score | Iterate through all players and their cards | O(p × c)** |
| Getting current player | Access ArrayList by index | O(1) |
| Checking card validity | Enum comparisons | O(1) |

*where n = hand size, m = validation operations per card
**where p = number of players, c = average cards per player

### Space Complexity:
- **model.Deck**: O(108) = O(1) - fixed deck size
- **model.Player Hands**: O(p × c) where p = players (2-4), c = cards per player (varies)
- **Discard Pile**: O(n) where n = cards played (grows throughout game)
- **Total**: O(n) where n is total number of cards in play

---

## 8. Conclusion

The choice of ArrayList as the primary data structure throughout the UNO game provides an optimal balance of:
- **Simplicity**: Easy to understand and maintain
- **Performance**: O(1) indexed access for frequent operations
- **Flexibility**: Dynamic sizing handles variable game states
- **Java Integration**: Works seamlessly with enhanced for-loops and Collections utilities

While other data structures could optimize specific operations, ArrayList's versatility makes it the ideal choice for this implementation, where indexed access and iteration are the dominant operations.