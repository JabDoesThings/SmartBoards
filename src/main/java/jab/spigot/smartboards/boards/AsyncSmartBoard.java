package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.events.SmartBoardClickEvent;
import jab.spigot.smartboards.utils.BoardProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AsyncSmartBoard implements SmartBoard {

  private BoardProfile profile;

  private volatile boolean canClick;

  public AsyncSmartBoard(@NotNull BoardProfile profile) {
    this.profile = profile;
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
    return true;
  }

  @Override
  public boolean canClick() {
    return canClick;
  }

  @Override
  public void setCanClick(boolean flag) {
    this.canClick = flag;
  }
}
