package jab.smartboards.commons.board.menu.adapter;

import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.throwables.LoopCycleException;
import jab.smartboards.commons.board.menu.MenuElement;
import jab.smartboards.commons.events.MenuClickEvent;
import net.jodah.typetools.TypeResolver;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AdaptableObject {

  public static final int PRIORITY_LIMIT = 10;

  private final Map<Integer, List<ClickAdapter>> clickAdapters;
  private final Map<Integer, List<UpdateAdapter>> updateAdapters;
  private final Map<Integer, List<RenderAdapter>> renderAdapters;
  private final Object lockClick = new Object();
  private final Object lockSortedClick = new Object();
  private final Object lockUpdate = new Object();
  private final Object lockSortedUpdate = new Object();
  private final Object lockRender = new Object();
  private final Object lockSortedRender = new Object();

  private ClickAdapter[] sortedClickAdapters;
  private UpdateAdapter[] sortedUpdateAdapters;
  private RenderAdapter[] sortedRenderAdapters;

  private int lowestPriorityClick = 1;
  private int highestPriorityClick = 1;
  private int lowestPriorityUpdate = 1;
  private int highestPriorityUpdate = 1;
  private int lowestPriorityRender = 1;
  private int highestPriorityRender = 1;
  private boolean sortChildrenDirty;
  private boolean sortClickAdaptersDirty;
  private boolean sortUpdateAdaptersDirty;
  private boolean sortRenderAdaptersDirty;
  private boolean updating;
  private boolean postUpdating;
  private boolean preRendering;
  private boolean rendering;
  private boolean postRendering;
  private boolean clickable;

  public AdaptableObject() {
    this.updateAdapters = new HashMap<>();
    this.renderAdapters = new HashMap<>();
    this.clickAdapters = new HashMap<>();
  }

  public void click(AdaptableObject parent, MenuClickEvent event) {
    synchronized (lockClick) {
      if (!clickAdapters.isEmpty()) {
        boolean cancelled = false;
        for (int priority = highestPriorityClick; priority >= lowestPriorityClick; priority--) {
          List<ClickAdapter> adapters = clickAdapters.get(priority);
          if (adapters != null) {
            for (ClickAdapter clickAdapter : adapters) {
              if (cancelled && clickAdapter.ignoreCancelled()) {
                cancelled = clickAdapter.onClick(this, parent, event);
              }
            }
          }
        }
      }
    }
  }

  public void update(@NotNull AdaptableObject parent) {
    if (updating) {
      throw new LoopCycleException(this);
    }
    try {
      updating = true;
      synchronized (lockUpdate) {
        if (!updateAdapters.isEmpty()) {
          for (int priority = highestPriorityUpdate; priority >= lowestPriorityUpdate; priority--) {
            List<UpdateAdapter> adapters = updateAdapters.get(priority);
            if (adapters != null) {
              for (UpdateAdapter updateAdapter : adapters) {
                try {
                  updateAdapter.onUpdate(this, parent);
                } catch (Exception e) {
                  System.out.println(
                      "Failed to execute "
                          + updateAdapter.getClass().getSimpleName()
                          + " for "
                          + getClass().getSimpleName()
                          + ".");
                  e.printStackTrace(System.out);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      updating = false;
      e.printStackTrace();
    }
    updating = false;
  }

  public void postUpdate(@NotNull AdaptableObject parent) {
    if (postUpdating) {
      throw new LoopCycleException(this);
    }
    try {
      postUpdating = true;
      synchronized (lockUpdate) {
        if (!updateAdapters.isEmpty()) {
          for (int priority = highestPriorityUpdate; priority >= lowestPriorityUpdate; priority--) {
            List<UpdateAdapter> adapters = updateAdapters.get(priority);
            if (adapters != null) {
              for (UpdateAdapter updateAdapter : adapters) {
                try {
                  updateAdapter.onPostUpdate(this, parent);
                } catch (Exception e) {
                  System.out.println(
                      "Failed to execute "
                          + updateAdapter.getClass().getSimpleName()
                          + " for "
                          + getClass().getSimpleName()
                          + ".");
                  e.printStackTrace(System.out);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      postUpdating = false;
      e.printStackTrace();
    }
    postUpdating = false;
  }

  public void preRender(AdaptableObject parent, BoardGraphics graphics) {
    if (preRendering) {
      throw new LoopCycleException(this);
    }
    try {
      preRendering = true;
      boolean hasAdapters = !renderAdapters.isEmpty();
      synchronized (lockRender) {
        if (hasAdapters) {
          for (int priority = highestPriorityRender; priority >= lowestPriorityRender; priority--) {
            List<RenderAdapter> adapters = renderAdapters.get(priority);
            if (adapters != null) {
              for (RenderAdapter renderAdapter : adapters) {
                try {
                  renderAdapter.onPreRender(this, parent, graphics);
                } catch (Exception e) {
                  System.out.println(
                      "Failed to execute "
                          + renderAdapter.getClass().getSimpleName()
                          + " for "
                          + getClass().getSimpleName()
                          + ".");
                  e.printStackTrace(System.out);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      preRendering = false;
      e.printStackTrace();
    }
    preRendering = false;
  }

  public void render(AdaptableObject parent, BoardGraphics graphics) {
    if (rendering) {
      throw new LoopCycleException(this);
    }
    try {
      rendering = true;
      boolean hasAdapters = !renderAdapters.isEmpty();
      synchronized (lockRender) {
        if (hasAdapters) {
          for (int priority = highestPriorityRender; priority >= lowestPriorityRender; priority--) {
            List<RenderAdapter> adapters = renderAdapters.get(priority);
            if (adapters != null) {
              for (RenderAdapter renderAdapter : adapters) {
                try {
                  renderAdapter.onRender(this, parent, graphics);
                } catch (Exception e) {
                  System.out.println(
                      "Failed to execute "
                          + renderAdapter.getClass().getSimpleName()
                          + " for "
                          + getClass().getSimpleName()
                          + ".");
                  e.printStackTrace(System.out);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      rendering = false;
      e.printStackTrace();
    }
    rendering = false;
  }

  public void postRender(AdaptableObject parent, BoardGraphics graphics) {
    if (postRendering) {
      throw new LoopCycleException(this);
    }
    postRendering = true;
    synchronized (lockRender) {
      try {
        if (!renderAdapters.isEmpty()) {
          for (int priority = highestPriorityRender; priority >= lowestPriorityRender; priority--) {
            List<RenderAdapter> adapters = renderAdapters.get(priority);
            if (adapters != null) {
              for (RenderAdapter renderAdapter : adapters) {
                try {
                  renderAdapter.onPostRender(this, parent, graphics);
                } catch (Exception e) {
                  System.out.println(
                      "Failed to execute "
                          + renderAdapter.getClass().getSimpleName()
                          + " for "
                          + getClass().getSimpleName()
                          + ".");
                  e.printStackTrace(System.out);
                }
              }
            }
          }
        }
      } catch (Exception e) {
        postRendering = false;
        e.printStackTrace();
      }
    }
    postRendering = false;
  }

  private void sortClickAdapters() {
    List<ClickAdapter> listAdapters = new ArrayList<>();
    synchronized (lockClick) {
      for (int priority = lowestPriorityClick; priority <= highestPriorityClick; priority++) {
        List<ClickAdapter> listNext = clickAdapters.get(priority);
        if (listNext != null) {
          listAdapters.addAll(listNext);
        }
      }
    }
    synchronized (lockSortedClick) {
      // Convert the compiled list into a raw array.
      sortedClickAdapters = new ClickAdapter[listAdapters.size()];
      for (int index = 0; index < listAdapters.size(); index++) {
        sortedClickAdapters[index] = listAdapters.get(index);
      }
    }
    sortClickAdaptersDirty = false;
  }

  private void sortUpdateAdapters() {
    List<UpdateAdapter> listAdapters = new ArrayList<>();
    synchronized (lockUpdate) {
      for (int priority = lowestPriorityUpdate; priority <= highestPriorityUpdate; priority++) {
        List<UpdateAdapter> listNext = updateAdapters.get(priority);
        if (listNext != null) {
          listAdapters.addAll(listNext);
        }
      }
    }
    synchronized (lockSortedUpdate) {
      // Convert the compiled list into a raw array.
      sortedUpdateAdapters = new UpdateAdapter[listAdapters.size()];
      for (int index = 0; index < listAdapters.size(); index++) {
        sortedUpdateAdapters[index] = listAdapters.get(index);
      }
    }
    sortUpdateAdaptersDirty = false;
  }

  private void sortRenderAdapters() {
    List<RenderAdapter> listAdapters = new ArrayList<>();
    synchronized (lockRender) {
      for (int priority = lowestPriorityRender; priority <= highestPriorityRender; priority++) {
        List<RenderAdapter> listNext = renderAdapters.get(priority);
        if (listNext != null) {
          listAdapters.addAll(listNext);
        }
      }
    }
    synchronized (lockSortedRender) {
      // Convert the compiled list into a raw array.
      sortedRenderAdapters = new RenderAdapter[listAdapters.size()];
      for (int index = 0; index < listAdapters.size(); index++) {
        sortedRenderAdapters[index] = listAdapters.get(index);
      }
    }
    sortRenderAdaptersDirty = false;
  }

  /**
   * Adds a ClickAdapter to the element.
   *
   * @param adapter The adapter to add.
   * @param priority The priority for the adapter. The higher the priority, the more likely the
   *     adapter will be executed first over others.
   * @throws IllegalArgumentException Thrown when the priority is outside the acceptable range of
   *     priorities. <code>PRIORITY_LIMIT</code>. Thrown when the adapter's type is not compatible
   *     with the element adding the adapter.
   */
  public void add(@NotNull ClickAdapter adapter, int priority) {
    // Make sure that the priority is within the acceptable range.
    if (priority < 1 || priority > PRIORITY_LIMIT) {
      throw new IllegalArgumentException(
          "The priority given is out of bounds. (min: 1, max: " + PRIORITY_LIMIT + ")");
    }
    // Check to make sure the adapter is compatible with the element type.
    Class elementClass = TypeResolver.resolveRawArgument(ClickAdapter.class, adapter.getClass());
    if (elementClass != MenuElement.class && !getClass().isAssignableFrom(elementClass)) {
      System.out.println(
          "Invalid Type for ClickAdapter: "
              + elementClass.getSimpleName()
              + " ("
              + MenuElement.class.getSimpleName()
              + " or "
              + getClass().getSimpleName()
              + " required)");
    }
    // Try to add the ClickAdapter.
    synchronized (lockClick) {
      List<ClickAdapter> list = clickAdapters.computeIfAbsent(priority, k -> new ArrayList<>());
      if (!list.contains(adapter)) {
        list.add(adapter);
        // Adjust the range if necessary.
        if (priority > highestPriorityClick) {
          highestPriorityClick = priority;
        }
        sortClickAdaptersDirty = true;
      }
    }
  }

  /**
   * Adds a UpdateAdapter to the element.
   *
   * @param adapter The adapter to add.
   * @param priority The priority for the adapter. The higher the priority, the more likely the
   *     adapter will be executed first over others.
   * @throws IllegalArgumentException Thrown when the priority is outside the acceptable range of
   *     priorities. <code>PRIORITY_LIMIT</code>. Thrown when the adapter's type is not compatible
   *     with the element adding the adapter.
   */
  public void add(@NotNull UpdateAdapter adapter, int priority) {
    // Make sure that the priority is within the acceptable range.
    if (priority < 1 || priority > PRIORITY_LIMIT) {
      throw new IllegalArgumentException(
          "The priority given is out of bounds. (min: 1, max: " + PRIORITY_LIMIT + ")");
    }
    // Check to make sure the adapter is compatible with the element type.
    Class elementClass = TypeResolver.resolveRawArgument(ClickAdapter.class, adapter.getClass());
    if (elementClass != MenuElement.class && !getClass().isAssignableFrom(elementClass)) {
      System.out.println(
          "Invalid Type for UpdateAdapter: "
              + elementClass.getSimpleName()
              + " ("
              + MenuElement.class.getSimpleName()
              + " or "
              + getClass().getSimpleName()
              + " required)");
    }
    // Try to add the UpdateAdapter.
    synchronized (lockUpdate) {
      List<UpdateAdapter> list = updateAdapters.computeIfAbsent(priority, k -> new ArrayList<>());
      if (!list.contains(adapter)) {
        list.add(adapter);
        // Adjust the range if necessary.
        if (priority > highestPriorityUpdate) {
          highestPriorityUpdate = priority;
        }
        sortUpdateAdaptersDirty = true;
      }
    }
  }

  /**
   * Adds a RenderAdapter to the element.
   *
   * @param adapter The adapter to add.
   * @param priority The priority for the adapter. The higher the priority, the more likely the
   *     adapter will be executed first over others.
   * @throws IllegalArgumentException Thrown when the priority is outside the acceptable range of
   *     priorities. <code>PRIORITY_LIMIT</code>. Thrown when the adapter's type is not compatible
   *     with the element adding the adapter.
   */
  public void add(@NotNull RenderAdapter adapter, int priority) {
    verifyPriority(priority);
    verifyAdapter(adapter);
    // Try to add the RenderAdapter.
    synchronized (lockRender) {
      List<RenderAdapter> list = renderAdapters.computeIfAbsent(priority, k -> new ArrayList<>());
      if (!list.contains(adapter)) {
        list.add(adapter);
        // Adjust the range if necessary.
        if (priority > highestPriorityRender) {
          highestPriorityRender = priority;
        }
        sortRenderAdaptersDirty = true;
      }
    }
  }

  /**
   * Removes the adapter from the menu element.
   *
   * @param adapter The adapter to remove.
   * @param layer
   */
  public void remove(@NotNull ClickAdapter adapter, int layer) {
    synchronized (lockClick) {
      if (clickAdapters.containsKey(layer)) {
        List<ClickAdapter> list = clickAdapters.get(layer);
        list.remove(adapter);
        if (list.isEmpty()) {
          clickAdapters.remove(layer);
        }
        sortClickAdaptersDirty = true;
      }
    }
  }

  /**
   * Removes the adapter from the menu element.
   *
   * @param adapter The adapter to remove.
   * @param layer
   */
  public void remove(@NotNull UpdateAdapter adapter, int layer) {
    synchronized (lockUpdate) {
      if (updateAdapters.containsKey(layer)) {
        List<UpdateAdapter> list = updateAdapters.get(layer);
        list.remove(adapter);
        if (list.isEmpty()) {
          updateAdapters.remove(layer);
        }
        sortUpdateAdaptersDirty = true;
      }
    }
  }

  /**
   * Removes the adapter from the menu element.
   *
   * @param adapter The adapter to remove.
   * @param layer
   */
  public void remove(@NotNull RenderAdapter adapter, int layer) {
    synchronized (lockRender) {
      if (renderAdapters.containsKey(layer)) {
        List<RenderAdapter> list = renderAdapters.get(layer);
        list.remove(adapter);
        if (list.isEmpty()) {
          renderAdapters.remove(layer);
        }
        sortRenderAdaptersDirty = true;
      }
    }
  }

  /**
   * Make sure that the priority is within the acceptable range.
   *
   * @param priority The priority value to test.
   */
  void verifyPriority(int priority) {
    if (priority < 1 || priority > PRIORITY_LIMIT) {
      throw new IllegalArgumentException(
          "The priority given is out of bounds. (min: 1, max: " + PRIORITY_LIMIT + ")");
    }
  }

  /**
   * Check to make sure the adapter is compatible with the element type.
   *
   * @param adapter The adapter to test.
   */
  void verifyAdapter(@NotNull LoopAdapter adapter) {
    // Check to make sure the adapter is compatible with the element type.
    Class elementClass = TypeResolver.resolveRawArgument(ClickAdapter.class, adapter.getClass());
    if (elementClass != MenuElement.class && !getClass().isAssignableFrom(elementClass)) {
      throw new IllegalArgumentException(
          "Invalid Type for "
              + adapter.getClass().getSimpleName()
              + ": "
              + elementClass.getSimpleName()
              + " ("
              + MenuElement.class.getSimpleName()
              + " or "
              + getClass().getSimpleName()
              + " required)");
    }
  }

  public boolean isClickable() {
    return this.clickable;
  }

  public void setClickable(boolean clickable) {
    this.clickable = clickable;
  }

  public abstract void onClick(AdaptableObject parent, MenuClickEvent event);

  public abstract void onUpdate(AdaptableObject parent);

  public abstract void onRender(AdaptableObject parent, BoardGraphics graphics);
}
