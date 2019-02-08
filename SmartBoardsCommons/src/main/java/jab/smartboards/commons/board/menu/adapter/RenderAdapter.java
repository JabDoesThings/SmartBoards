package jab.smartboards.commons.board.menu.adapter;

import jab.smartboards.commons.board.graphics.BoardGraphics;

public abstract class RenderAdapter<E extends AdaptableObject, P extends AdaptableObject>
    extends LoopAdapter {

  /**
   * Main constructor.
   *
   * @param priority The higher the priority value given, the more likely the adapter will fire
   *     first.
   */
  public RenderAdapter(int priority) {
    super(priority);
  }

  public abstract void onPreRender(E element, P parent, BoardGraphics graphics);

  public abstract void onRender(E element, P parent, BoardGraphics graphics);

  public abstract void onPostRender(E element, P parent, BoardGraphics graphics);
}
