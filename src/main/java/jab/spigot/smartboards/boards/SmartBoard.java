package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.boards.graphics.BoardGraphics;
import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.events.SmartBoardClickEvent;
import jab.spigot.smartboards.utils.BoardProfile;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** @author Josh */
public interface SmartBoard {

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
    Entity[] entities = chunk.getEntities();
    if (entities.length > 0) {
      for (Entity entity : entities) {
        if (entity instanceof ItemFrame && entity.getLocation().equals(location)) {
          itemFrame = (ItemFrame) entity;
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

  /** @return Returns the width of the smartboard in blocks. */
  default int getWidth() {
    return getProfile().getWidth();
  }

  /** @return Returns the height of the smartboard in blocks. */
  default int getHeight() {
    return getProfile().getHeight();
  }

  /** @return Returns the next unique ID for new smartboards. */
  default int getNewId() {
    return PluginSmartBoards.NEXT_ID++;
  }

  /** NOTE: This method is only fired for async smartboards. */
  void update();

  /** NOTE: This method is only fired for async smartboards. */
  void render();

  /** This is fired when a player clicks on the smartboard. */
  void onClick(SmartBoardClickEvent eventToPass);

  /**
   * @param player The player to test.
   * @param maxDistance The maximum distance (in blocks) a ray can be cast.
   * @return Returns true if the smartboard is looked at by the player.
   */
  boolean isLookingAt(Player player, int maxDistance);

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
    return getIndex(x, y, getWidth());
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
