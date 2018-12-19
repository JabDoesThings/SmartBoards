package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.events.SmartBoardClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SyncSmartBoard implements SmartBoard {

  private volatile boolean canClick;

  public SyncSmartBoard() {

  }

  @Override
  public void update() {}

  @Override
  public void render() {}

  @Override
  public void onClick(SmartBoardClickEvent eventToPass) {}

  @Override
  public boolean isLookingAt(Player player, int maxDistance) {
    return false;
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public void setDirty(boolean flag) {}

  @Override
  public void dispatch() {}

  @Override
  public Location getLocation() {
    return null;
  }

  @Override
  public BoardDirection getDirection() {
    return null;
  }

  @Override
  public int[] getMapIds() {
    return new int[0];
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public int getId() {
    return 0;
  }

  @Override
  public boolean isAsync() {
    return false;
  }

  @Override
  public boolean canClick() {
    return this.canClick;
  }

  @Override
  public void setCanClick(boolean flag) {
    this.canClick = flag;
  }
}
