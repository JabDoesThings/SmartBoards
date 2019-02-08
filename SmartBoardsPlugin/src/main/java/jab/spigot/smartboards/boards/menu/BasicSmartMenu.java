package jab.spigot.smartboards.boards.menu;

import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.board.menu.SmartMenu;
import jab.smartboards.commons.board.menu.adapter.AdaptableObject;
import jab.smartboards.commons.events.MenuClickEvent;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class BasicSmartMenu extends SmartMenu {

  /**
   * Main constructor.
   *
   * @param width The width of the menu. (In blocks)
   * @param height The height of the menu. (In blocks)
   */
  public BasicSmartMenu(int width, int height) {
    super(width, height);
  }

  @Override
  public void onClick(AdaptableObject parent, MenuClickEvent event) {}

  @Override
  public void onUpdate(AdaptableObject parent) {}

  @Override
  public void onRender(AdaptableObject parent, BoardGraphics graphics) {}
}
