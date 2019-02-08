package jab.smartboards.commons.board.menu.adapter;

/**
 * MenuAdapters are used to handle the operations of menus and their respective elements in a way
 * that can be managed flexibly. Java AWT uses the same philosophy when implementing listeners.
 *
 * <p>MenuAdapters require a priority value as an integer. This value will be used to sort
 * registered adaptors for each element. If two or more adapters use the same priority level, the
 * most prioritized adapter will be the oldest instance, using <code>getTimeCreated()</code>.
 *
 * <p>The methods to implement will be fired before any internal code for the adapted element
 * executes.
 *
 * @author Josh
 */
public abstract class LoopAdapter {

  private int priority;
  private long timeCreated;

  /**
   * Main constructor.
   *
   * @param priority The higher the priority value given, the more likely the adapter will fire
   *     first.
   */
  public LoopAdapter(int priority) {
    timeCreated = System.currentTimeMillis();
    this.priority = priority;
  }

  /** @return Returns the UNIX timestamp that the adapter was created. */
  public long getTimeCreated() {
    return this.timeCreated;
  }

  /**
   * @return Returns the priority level assigned to the adapter when created.
   *     <p>NOTE: The higher the priority value assigned, the more likely the adapter will fire
   *     first.
   */
  public int getPriority() {
    return this.priority;
  }
}
