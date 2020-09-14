package jab.smartboards.commons.board.menu;

import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.board.menu.adapter.AdaptableObject;
import jab.smartboards.commons.throwables.LoopCycleException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public abstract class MenuElement extends AdaptableObject {

  public static final int LAYER_LIMIT = 50;

  private final Map<Integer, List<MenuElement>> children;
  private final Object lockSortedChildren = new Object();
  private final Object lockChildren = new Object();

  private MenuElement[] sortedChildren;
  private int x;
  private int y;
  private int width;
  private int height;
  private int lowestLayer = 1;
  private int highestLayer = 1;
  private boolean updating;
  private boolean rendering;
  private boolean sortChildrenDirty;
  private boolean childrenUpdating;
  private boolean childrenRendering;

  /**
   * Pixel units constructor.
   *
   * @param x The x coordinate position. (In pixels)
   * @param y The y coordinate position. (In pixels)
   * @param width The width of the element. (In pixels)
   * @param height The height of the element. (In pixels)
   */
  public MenuElement(int x, int y, int width, int height) {
    this();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Block units constructor.
   *
   * @param width The width of the element. (In blocks)
   * @param height The height of the element. (In blocks)
   */
  public MenuElement(int width, int height) {
    this(0, 0, width * 128, height * 128);
  }

  /** Final fields constructor. */
  private MenuElement() {
    this.children = new HashMap<>();
  }

  /**
   * Updates the element.
   *
   * <p>NOTE: If the instanceId is null, then this is the global instance for the menu.
   */
  public void update(AdaptableObject parent) {
    if (updating) {
      throw new LoopCycleException(this);
    }
    updating = true;
    update(parent);
    try {
      updateChildren();
      postUpdate(parent);
    } catch (Exception e) {
      updating = false;
      e.printStackTrace();
    }
    updating = false;
  }

  public void render(AdaptableObject parent, BoardGraphics graphics) {
    if (rendering) {
      throw new LoopCycleException(this);
    }
    try {
      rendering = true;
      preRender(parent, graphics);
      renderChildren(graphics);
      render(parent, graphics);
      postRender(parent, graphics);
    } catch (Exception e) {
      rendering = false;
      e.printStackTrace();
    }
    rendering = false;
  }

  public void updateChildren() {
    if (childrenUpdating) {
      throw new LoopCycleException(this);
    }
    childrenUpdating = true;
    try {
      if (sortChildrenDirty) {
        sortChildren();
      }
      // Update each child.
      synchronized (lockSortedChildren) {
        if (sortedChildren.length > 0) {
          for (int index = 0; index < sortedChildren.length; index++) {
            try {
              sortedChildren[index].update(this);
            } catch (Exception e) {
              System.out.println(
                  "Failed to update child: " + sortedChildren[index].getClass().getSimpleName());
            }
          }
        }
      }
    } catch (Exception e) {
      childrenUpdating = false;
      System.out.println("Failed to update children.");
      e.printStackTrace();
    }
    childrenUpdating = false;
  }

  public void renderChildren(BoardGraphics graphics) {
    if (childrenRendering) {
      throw new LoopCycleException(this);
    }
    childrenRendering = true;
    try {
      // Render each child.
      synchronized (lockSortedChildren) {
        if (sortedChildren.length > 0) {
          for (int index = 0; index < sortedChildren.length; index++) {
            try {
              sortedChildren[index].render(this, graphics);
            } catch (Exception e) {
              System.out.println(
                  "Failed to render child: " + sortedChildren[index].getClass().getSimpleName());
            }
          }
        }
      }
    } catch (Exception e) {
      childrenRendering = false;
      System.out.println("Failed to render children.");
      e.printStackTrace();
    }
    childrenRendering = false;
  }

  private void sortChildren() {
    List<MenuElement> listElements = new ArrayList<>();
    synchronized (lockChildren) {
      for (int layer = lowestLayer; layer <= highestLayer; layer++) {
        List<MenuElement> listNext = children.get(layer);
        if (listNext != null) {
          listElements.addAll(listNext);
        }
      }
    }
    synchronized (lockSortedChildren) {
      // Convert the compiled list into a raw array.
      sortedChildren = new MenuElement[listElements.size()];
      for (int index = 0; index < listElements.size(); index++) {
        sortedChildren[index] = listElements.get(index);
      }
    }
    sortChildrenDirty = false;
  }

  /**
   * Adds a element as a child on layer 1.
   *
   * @param element The element to add.
   * @throws IllegalArgumentException Thrown when the layer is outside the acceptable range of
   *     layers. (LAYER_LIMIT = 50)
   */
  public void add(@NotNull MenuElement element) {
    add(element, 1);
  }

  /**
   * Adds a element as a child.
   *
   * @param element The element to add.
   * @throws IllegalArgumentException Thrown when the layer is outside the acceptable range of
   *     layers. (LAYER_LIMIT = 50)
   */
  public void add(@NotNull MenuElement element, int layer) {
    // Make sure that the layer is within the acceptable range.
    if (layer < -LAYER_LIMIT || layer > LAYER_LIMIT) {
      throw new IllegalArgumentException(
          "The layer given is out of bounds. (min: "
              + (-LAYER_LIMIT)
              + ", max: "
              + LAYER_LIMIT
              + ")");
    }
    synchronized (lockChildren) {
      List<MenuElement> list = children.computeIfAbsent(layer, k -> new ArrayList<>());
      if (!list.contains(element)) {
        list.add(element);
        sortChildrenDirty = true;
        if (layer < lowestLayer) {
          lowestLayer = layer;
        }
        if (layer > highestLayer) {
          highestLayer = layer;
        }
      }
    }
  }

  /**
   * Removes the child element on layer 1.
   *
   * @param element The element to remove.
   */
  public void remove(@NotNull MenuElement element) {
    remove(element, 1);
  }

  /**
   * Removes the child element.
   *
   * @param element The element to remove.
   * @param layer
   */
  public void remove(@NotNull MenuElement element, int layer) {
    synchronized (lockChildren) {
      if (children.containsKey(layer)) {
        List<MenuElement> list = children.get(layer);
        list.remove(element);
        if (list.isEmpty()) {
          children.remove(layer);
        }
        sortChildrenDirty = true;
      }
    }
  }

  /**
   * Sets the offset of the element.
   *
   * @param x The x offset. (In pixels)
   * @param y The y offset. (In pixels)
   */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** @return Returns the x offset. (In pixels) */
  public int getX() {
    return this.x;
  }

  /**
   * Sets the x offset of the element.
   *
   * @param x The x offset to set. (In pixels)
   */
  public void setX(int x) {
    this.x = x;
  }

  /** @return Returns the y offset. (In pixels) */
  public int getY() {
    return this.y;
  }

  /**
   * Sets the y offset of the element.
   *
   * @param y The y offset to set. (In pixels)
   */
  public void setY(int y) {
    this.y = y;
  }

  /** @return Returns the width of the element. (In pixels) */
  public int getWidth() {
    return this.width;
  }

  /**
   * Sets the width of the element.
   *
   * @param width The width to set. (In pixels)
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /** @return Returns the height of the element. (In pixels) */
  public int getHeight() {
    return this.height;
  }

  /**
   * Sets the height of the element.
   *
   * @param height The hieght to set. (In pixels)
   */
  public void setHeight(int height) {
    this.height = height;
  }
}
