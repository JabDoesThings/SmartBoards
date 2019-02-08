package jab.smartboards.commons.board.menu.adapter;

import jab.smartboards.commons.events.MenuClickEvent;

public abstract class ClickAdapter<E extends AdaptableObject, P extends AdaptableObject>
    extends LoopAdapter {

  /**
   * Main constructor.
   *
   * @param priority The higher the priority value given, the more likely the adapter will fire
   *     first.
   */
  public ClickAdapter(int priority) {
    super(priority);
  }

  /**
   * This method fires from menu elements that have the adapter registered.
   *
   * <p>NOTE: This method fires before menu elements in the profile are updated.
   *
   * @param element
   * @param parent
   * @param event
   */
  public abstract boolean onClick(E element, P parent, MenuClickEvent event);

  public abstract boolean ignoreCancelled();
}
