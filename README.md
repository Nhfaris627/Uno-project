# Milestone 5: Custom Card Images Implementation

## Feature Selected: Custom Images for Cards (UNO)

Our team implemented **Feature 1 – Images** by adding custom card images for all UNO cards in our game. This enhancement significantly improved the visual experience and user interface, making the game more engaging and true to the physical UNO card game.

---

## Implementation Overview

The custom card image system was integrated into the `GameView` class, which handles all visual rendering of the game. The implementation involved:

1. **Asset Organization**: Creating a comprehensive directory structure for all card images
2. **Dynamic Image Loading**: Implementing a resource loading system using Java's ClassLoader
3. **Image Rendering**: Scaling and displaying cards appropriately in the GUI
4. **Visual Feedback**: Creating disabled/grayed-out states for unplayable cards
5. **UNO Flip Support**: Managing both Light and Dark side card images

All card images are loaded dynamically at runtime from the `view/unoCards/` and `view/DarkSideCards/` directories, organized by card value and color.

---

## Team Contributions

### **Faris Hassan (101300683) - Asset Organization & Resource Structure**

**Contribution**: Faris was responsible for organizing the complete card image asset structure and ensuring all images were properly named and accessible.

**Implementation Details**:
- Created the hierarchical directory structure for card images:
  ```
  view/
  ├── unoCards/          (Light side cards)
  │   ├── ZERO/
  │   ├── ONE/
  │   ├── TWO/
  │   ├── ...
  │   ├── SKIP/
  │   ├── REVERSE/
  │   ├── DRAW_ONE/
  │   ├── FLIP/
  │   ├── WILD/
  │   └── WILD_DRAW_TWO/
  └── DarkSideCards/     (Dark side cards)
      ├── FIVE/
      ├── DRAW_FIVE/
      ├── SKIP_EVERYONE/
      ├── FLIP/
      └── WILD_DRAW_COLOR/
  ```

- Ensured proper naming conventions for all image files (e.g., `RED.png`, `BLUE.png`, `GREEN.png`, `YELLOW.png` for numbered cards, and `WILD.png` for wild cards)
- Organized 112+ card images into appropriate subdirectories based on card values
- Validated that all card types (numbers 0-9, special cards, wild cards, and UNO Flip cards) had corresponding images for all colors
- Coordinated with the team to ensure file paths matched the code implementation

---

### **Ivan Arkhipov (101310636) - Dynamic Image Loading System**

**Contribution**: Ivan implemented the core image loading functionality that dynamically retrieves card images from the resource directory at runtime.

**Implementation Details**:

Developed the `getPath()` method in `GameView.java` (lines 202-221):
```java
private String getPath(Card c) {
    String resourcePath = "";
    Card.Value value = c.getValue();
    Card.Color color = c.getColor();
    Card.Side side = c.getCurrentSide();

    if (side == Card.Side.LIGHT) {
        resourcePath = "view/unoCards/";
    } else {
        resourcePath = "view/DarkSideCards/";
    }

    if (value == Card.Value.WILD || value == Card.Value.WILD_DRAW_TWO || 
        value == Card.Value.WILD_DRAW_COLOR) {
        resourcePath += value + "/" + value + ".png";
    } else {
        resourcePath += value + "/" + color + ".png";
    }

    return resourcePath;
}
```

**Key Features**:
- Automatically determines the correct directory based on the card's current side (Light vs. Dark)
- Handles special cases for wild cards (which don't have color-specific images)
- Constructs paths dynamically based on card properties
- Supports both standard UNO cards and UNO Flip expansion cards

---

### **Nicky Fang (101304731) - Image Rendering & Scaling**

**Contribution**: Nicky implemented the image loading and rendering system that converts resource files into displayable ImageIcons with proper scaling.

**Implementation Details**:

Developed the `getIcon()` method in `GameView.java` (lines 223-245):
```java
private ImageIcon getIcon(Card c, int x, int y) {
    ClassLoader classLoader = getClass().getClassLoader();
    String resourcePath = getPath(c);

    System.out.println("Trying to load: " + resourcePath);

    InputStream is = classLoader.getResourceAsStream(resourcePath);

    if (is != null) {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image image = icon.getImage();
        return new ImageIcon(image.getScaledInstance(x, y, Image.SCALE_SMOOTH));
    }

    return null;
}
```

**Key Features**:
- Uses ClassLoader to access resources from the compiled JAR or project structure
- Reads image files as InputStreams for flexible resource loading
- Scales images to specified dimensions (80x120 for hand cards, 160x240 for top discard card)
- Uses `Image.SCALE_SMOOTH` for high-quality image scaling
- Includes error handling and debugging output for troubleshooting

---

### **Bhagya Patel (101324150) - Visual Feedback & UI Integration**

**Contribution**: Bhagya implemented the visual feedback system that creates disabled card states and integrated the card images into the game's render loop.

**Implementation Details**:

1. **Disabled Icon Generation** - Developed the `getDisabledIcon()` method in `GameView.java` (lines 247-265):
```java
private ImageIcon getDisabledIcon(ImageIcon icon) {
    Image image = icon.getImage();

    BufferedImage buffered = new BufferedImage(
            image.getWidth(null),
            image.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
    );

    Graphics2D g = buffered.createGraphics();

    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
    g.drawImage(image, 0, 0, null);

    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
    g.setColor(new Color(200, 200, 200));
    g.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
    g.dispose();

    return new ImageIcon(buffered);
}
```

**Key Features**:
- Creates a semi-transparent gray overlay for unplayable cards
- Uses alpha compositing to maintain card visibility while indicating unavailability
- Applies a 70% opacity to the original image and a 30% gray overlay

2. **UI Integration** - Enhanced the `render()` method (lines 267-332) to incorporate card images:
```java
// Set card icons for buttons
cardBtn.setIcon(getIcon(c, 80, 120));
cardBtn.setDisabledIcon(getDisabledIcon(Objects.requireNonNull(getIcon(c, 80, 120))));

// Add green border for playable cards
if (playable) cardBtn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

// Display top discard card
topCardText.setIcon(getIcon(s.topDiscard, 160, 240));
```

**Key Features**:
- Dynamically renders all cards in player's hand with appropriate images
- Highlights playable cards with a green border
- Shows the top discard card at a larger scale (160x240)
- Automatically disables card buttons for AI players or after turn is taken
- Ensures visual consistency across different game states

---

## Technical Highlights

1. **Resource Management**: All images are loaded using Java's ClassLoader, making the application portable and compatible with JAR packaging

2. **Scalability**: The system supports all UNO card types:
   - 76 numbered cards (0-9 in 4 colors, with duplicates)
   - 24 action cards (Skip, Reverse, Draw One in 4 colors)
   - 8 wild cards (Wild, Wild Draw Two)
   - UNO Flip cards (both Light and Dark sides)

3. **Performance**: Images are loaded on-demand during rendering, with efficient scaling algorithms

4. **User Experience**: Visual feedback clearly indicates which cards are playable, improving game usability

---

## Testing & Validation

The team collectively tested the image system by:
- Verifying all 112+ cards display correctly
- Testing both Light and Dark side rendering during FLIP card plays
- Ensuring proper scaling at different sizes
- Validating disabled states for unplayable cards
- Confirming resource loading works in both IDE and JAR execution

---

## Conclusion

This implementation demonstrates our team's ability to enhance the user experience through visual design while maintaining clean, modular code. Each team member contributed a critical component that, when integrated, created a polished and professional-looking card game interface.

---

## Authors

- **Faris Hassan** - 101300683
- **Ivan Arkhipov** - 101310636
- **Nicky Fang** - 101304731
- **Bhagya Patel** - 101324150

**Course**: SYSC 3110 - Software Development Project  
**Milestone**: 5 (Bonus Feature)  
**Date**: December 2024