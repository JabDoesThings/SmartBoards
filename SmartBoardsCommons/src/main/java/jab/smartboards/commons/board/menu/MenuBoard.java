package jab.smartboards.commons.board.menu;

import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.board.SyncSmartBoard;
import org.jetbrains.annotations.NotNull;

/** @author Josh */
public abstract class MenuBoard extends SyncSmartBoard {

  private SmartMenu menu;

  /**
   * Basic constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public MenuBoard(@NotNull BoardProfile profile) {
    super(profile);
  }

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   * @param menu The menu to apply to the board.
   */
  public MenuBoard(@NotNull BoardProfile profile, SmartMenu menu) {
    this(profile);
    this.menu = menu;
  }

  @Override
  public void create() {
    super.create();
    onCreate();
  }

  @Override
  public void update() {
    super.update();
  }

  @Override
  public void render() {
    if (menu != null && !getGraphics().equals(menu.getGraphics())) {
      setGraphics(menu.getGraphics());
    }
    super.render();
  }

  @Override
  public boolean canUpdate() {
    return true;
  }

  @Override
  public void setCanUpdate(boolean flag) {
    throw new RuntimeException("MenuBoards update.");
  }

  public SmartMenu getMenu() {
    return this.menu;
  }

  public void setMenu(@NotNull SmartMenu menu) {
    this.menu = menu;
  }

  protected abstract void onCreate();
}
