package jab.spigot.smartboards.boards.examples;

import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.spigot.smartboards.boards.menu.FrameType;
import jab.smartboards.commons.board.menu.adapter.AdaptableObject;
import jab.spigot.smartboards.boards.menu.MenuFrame;
import jab.smartboards.commons.board.menu.SmartMenu;
import jab.smartboards.commons.events.MenuClickEvent;

public class ExampleSmartMenu extends SmartMenu {

  /**
   * Main constructor.
   *
   * @param width The width of the menu. (In blocks)
   * @param height The height of the menu. (In blocks)
   */
  public ExampleSmartMenu(int width, int height) {
    super(width, height);
    MenuFrame frame = new MenuFrame(FrameType.FRAME_1, width, height);
    add(frame, 0);
  }

  @Override
  public void onClick(AdaptableObject parent, MenuClickEvent event) {}

  @Override
  public void onUpdate(AdaptableObject parent) {}

  @Override
  public void onRender(AdaptableObject parent, BoardGraphics graphics) {}
}
