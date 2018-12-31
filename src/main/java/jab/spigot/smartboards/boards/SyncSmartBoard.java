package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.SmartBoardThread;
import jab.spigot.smartboards.boards.graphics.BoardFrame;
import jab.spigot.smartboards.boards.graphics.BoardGraphics;
import jab.spigot.smartboards.boards.graphics.SimpleBoardGraphics;
import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.events.SmartBoardClickEvent;
import jab.spigot.smartboards.utils.BoardProfile;
import jab.spigot.smartboards.utils.MapImage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles all functions for synchronized SmartBoards.
 *
 * @author Josh
 */
public class SyncSmartBoard implements SmartBoard {

  private final int id;

  private ItemFrame[] frames;
  private Location[] frameLocations;
  private ItemStack[] stacks;
  private BoardProfile profile;
  private BoardGraphics graphics;

  private volatile boolean canClick;

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public SyncSmartBoard(@NotNull BoardProfile profile) {
    this.profile = profile;
    this.id = getNewId();
    this.graphics = new SimpleBoardGraphics(profile.getWidth(), profile.getHeight());
    int size = profile.getWidth() * profile.getHeight();
    this.stacks = new ItemStack[size];
    for (int index = 0; index < size; index++) {
      stacks[index] = new ItemStack(Material.FILLED_MAP);
    }
  }

  /** Creates the board's ItemFrames, as well as registers the board to the SmartBoardThread. */
  public void create() {
    Location location = profile.getLocation();
    // The location is on the top-left of the board. Grab the world coordinates for this point to
    // calculate the blocks from the point given.
    int lx = location.getBlockX();
    int ly = location.getBlockY();
    int lz = location.getBlockZ();
    switch (profile.getDirection()) {
      case NORTH:
        createNorth(lx, ly, lz);
        break;
      case SOUTH:
        createSouth(lx, ly, lz);
        break;
      case EAST:
        createEast(lx, ly, lz);
        break;
      case WEST:
        createWest(lx, ly, lz);
        break;
      case UP:
        break;
      case DOWN:
        break;
    }
    SmartBoardThread.instance.addBoard(this);
  }

  /**
   * Destroys all ItemFrames associated with the SmartBoard by removing them from the world. Also
   * removed the board from the SmartBoardThread.
   */
  public void destroy() {
    // GO through all frames and remove them.
    if (frames != null) {
      if (frames.length > 0) {
        for (ItemFrame itemFrame : frames) {
          if (itemFrame != null) itemFrame.remove();
        }
      }
      frames = null;
    }
    SmartBoardThread.instance.removeBoard(this);
  }

  protected void createNorth(int lx, int ly, int lz) {
    int index;
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int x = lx + getWidth() - 1; x >= lx; x--) {
        index = getIndex(x - lx, y - ly);
        frames[index] = getItemFrame(x, y, lz);
      }
    }
  }

  protected void createSouth(int lx, int ly, int lz) {
    int index;
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int x = lx; x < lx + getWidth(); x++) {
        index = getIndex(x - lx, y - ly);
        frames[index] = getItemFrame(x, y, lz);
      }
    }
  }

  protected void createEast(int lx, int ly, int lz) {
    int index;
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int z = lz + getWidth() - 1; z >= lz; z--) {
        index = getIndex(z - lz, y - ly);
        frames[index] = getItemFrame(lx, y, z);
      }
    }
  }

  protected void createWest(int lx, int ly, int lz) {
    int index;
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int z = lz; z < lz + getWidth(); z++) {
        index = getIndex(z - lz, y - ly);
        frames[index] = getItemFrame(lx, y, z);
      }
    }
  }

  @Override
  public void update() {}

  @Override
  public void render() {
    if (isDirty() || graphics.isDirty()) {
      BoardFrame frame = graphics.getFrame();
      MapImage[] frames = frame.getFrames();
      for (int y = 0; y < getHeight(); y++) {
        for (int x = 0; x < getWidth(); x++) {
          int index = getIndex(x, y);
          ItemStack stack = stacks[index];
          MapMeta mapMeta = (MapMeta) stack.getItemMeta();
          mapMeta.setMapId(frames[index].getPacketId());
          stack.setItemMeta(mapMeta);
        }
      }
      graphics.setDirty(false);
    }
  }

  @Override
  public void onClick(SmartBoardClickEvent event) {}

  @Override
  public boolean isLookingAt(Player player, int maxDistance) {
    boolean returned = false;
    World world = player.getWorld();
    // Make sure the player is in the same world.
    if (world.getName().equals(profile.getWorld().getName())) {
      return false;
    }
    Vector playerDirection = player.getLocation().getDirection();
    Vector start = player.getLocation().toVector();
    double yOffset = player.getEyeHeight();
    BlockIterator blockIterator =
        new BlockIterator(world, start, playerDirection, yOffset, maxDistance);
    Block block;
    while (blockIterator.hasNext()) {
      block = blockIterator.next();
      Location location = block.getLocation();
      int blockX = location.getBlockX();
      int blockY = location.getBlockY();
      int blockZ = location.getBlockZ();
      for (Location frameLocation : frameLocations) {
        if (blockX == frameLocation.getBlockX()
            && blockY == frameLocation.getBlockY()
            && blockZ == frameLocation.getBlockZ()) {
          returned = true;
          break;
        }
      }
    }
    return returned;
  }

  @NotNull
  public BoardGraphics getGraphics() {
    return graphics;
  }

  @Override
  public void setGraphics(@NotNull BoardGraphics graphics) {
    this.graphics = graphics;
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
  public BoardProfile getProfile() {
    return this.profile;
  }

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
  public int getId() {
    return this.id;
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
