package jab.smartboards.commons.board.menu.adapter;

public abstract class UpdateAdapter<E extends AdaptableObject, P extends AdaptableObject>
    extends LoopAdapter {

  /**
   * Main constructor.
   *
   * @param priority The higher the priority value given, the more likely the adapter will fire
   *     first.
   */
  public UpdateAdapter(int priority) {
    super(priority);
  }

  /**
   * This method fires from menu elements that have the adapter registered.
   *
   * <p>NOTE: This method fires before the menu element is internally updated.
   *
   * @param element
   * @param parent
   */
  public abstract void onUpdate(E element, P parent);

  public abstract void onPostUpdate(E element, P parent);
}
