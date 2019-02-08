package jab.smartboards.commons.board;

import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.events.SmartBoardClickEvent;
import jab.smartboards.commons.utils.BoardSizable;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/** @author Josh */
public interface SmartBoard extends BoardSizable {

  /**
   * Attempts to locate a pre-existing ItemFrame in the world that the SmartBoard exists. If one
   * does not exist in the location, one is created.
   *
   * @param x The x global coordinate.
   * @param y The y global coordinate.
   * @param z The z global coordinate.
   * @return Returns either a pre-existing ItemFrame in the location, or a new ItemFrame created in
   *     the location.
   */
  default ItemFrame getItemFrame(int x, int y, int z) {
    World world = getProfile().getWorld();
    ItemFrame itemFrame = null;
    Location location = new Location(world, x, y, z);
    // If the chunk is not loaded, load it.
    Chunk chunk = world.getChunkAt(location);
    if (!chunk.isLoaded()) {
      chunk.load(true);
    }
    // Attempt to find the ItemFrame. (If it already exists)
    Collection<Entity> entities = world.getNearbyEntities(location, 0.5, 0.5, 0.5);
    if (entities.size() > 0) {
      for (Entity entity : entities) {
        if (entity instanceof ItemFrame && entity.getLocation().equals(location)) {
          ItemFrame itemFrameToRemove = (ItemFrame) entity;
          itemFrameToRemove.remove();
          break;
        }
      }
    }
    // If the ItemFrame does not exist, create on in the location given.
    if (itemFrame == null) {
      itemFrame = (ItemFrame) world.spawnEntity(location, EntityType.ITEM_FRAME);
    }
    return itemFrame;
  }

  @NotNull
  default ItemFrame createItemFrame(
      Collection<ItemFrame> itemFrames,
      World world,
      int x,
      int y,
      int z,
      BoardDirection direction) {
    Location loc = new Location(world, x, y, z);
    Chunk chunk = loc.getChunk();
    if (!chunk.isLoaded()) {
      chunk.load();
    }
    ItemFrame itemFrame = null;
    for (ItemFrame frame : itemFrames) {
      Location location = frame.getLocation();
      if (location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z) {
        itemFrame = frame;
        break;
      }
    }
    if (itemFrame == null) {
      itemFrame = world.spawn(new Location(world, x, y, z), ItemFrame.class);
      itemFrame.setFacingDirection(direction.getFacingDirection());
    }
    return itemFrame;
  }

  /** @return Returns the width of the smartboard. (In blocks) */
  default int getBlockWidth() {
    return getProfile().getBlockWidth();
  }

  /** @return Returns the height of the smartboard (In blocks) */
  default int getBlockHeight() {
    return getProfile().getBlockHeight();
  }

  /** @return Returns the next unique ID for new SmartBoards. */
  default int getNewId() {
    return SmartBoards.NEXT_ID++;
  }

  /** NOTE: This method is only fired for async SmartBoards. */
  void update();

  /** NOTE: This method is only fired for async SmartBoards. */
  void render();

  /** This is fired when a player clicks on the SmartBoard. */
  void onClick(SmartBoardClickEvent eventToPass);

  /**
   * @param player The player to test.
   * @param maxDistance The maximum distance a ray can be cast. (In blocks)
   * @return Returns true if the smartboard is looked at by the player.
   */
  boolean isLookingAt(LivingEntity player, int maxDistance);

  @NotNull
  BoardGraphics getGraphics();

  void setGraphics(@NotNull BoardGraphics graphics);

  /** NOTE: This method is only fired for async smartboards. */
  boolean canUpdate();

  /** NOTE: This method is only fired for async smartboards. */
  boolean isDirty();

  /** NOTE: This method is only fired for async smartboards. */
  void setDirty(boolean flag);

  /** NOTE: This method is only fired for async smartboards. */
  void dispatch();

  /** @return Returns the profile used to define attributes for the smartboard. */
  BoardProfile getProfile();

  /**
   * @return Returns the bottom-left location of the smartboard. (Relative to the direction the
   *     board faces)
   */
  Location getLocation();

  /** @return Returns the direction that the smartboard is facing. */
  BoardDirection getDirection();

  int[] getMapIds();

  /** @return Returns the ID assigned to the smartboard. */
  int getId();

  /** @return Returns true if the smartboard should be registered to the smartboard thread. */
  boolean isAsync();

  /** @return Returns true if the smartboard can be clicked. */
  boolean canClick();

  /**
   * Sets the ability for the board to be clickable.
   *
   * @param flag The flag to set.
   */
  void setCanClick(boolean flag);

  /**
   * This is the formula for one-dimensional arrays composed of unique indexes of 2-dimensional
   * coordinates: <br>
   * index = (y + width) + (width - 1) + x
   *
   * @param x The x coordinate relative to the board's left side.
   * @param y The y coordinate relative to the board's top side.
   * @return Returns the array index for the given coordinates.
   */
  default int getIndex(int x, int y) {
    return getIndex(x, y, getBlockWidth());
  }

  /**
   * This is the formula for one-dimensional arrays composed of unique indexes of 2-dimensional
   * coordinates: <br>
   * index = (y + width) + (width - 1) + x
   *
   * @param x The x coordinate relative to the board's left side.
   * @param y The y coordinate relative to the board's top side.
   * @param width The width of the board.
   * @return Returns the array index for the given coordinates.
   */
  static int getIndex(int x, int y, int width) {
    return (y * width) + (width - 1) - x;
  }
}
