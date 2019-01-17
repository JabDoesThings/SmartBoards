package jab.spigot.smartboards.boards;

import jab.spigot.smartboards.SmartBoardThread;
import jab.spigot.smartboards.boards.graphics.BoardFrame;
import jab.spigot.smartboards.boards.graphics.BoardGraphics;
import jab.spigot.smartboards.boards.graphics.SimpleBoardGraphics;
import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.events.SmartBoardClickEvent;
import jab.spigot.smartboards.utils.BoardProfile;
import jab.spigot.smartboards.utils.MapImage;
import jab.spigot.smartboards.utils.NMSUtils;
import jab.spigot.smartboards.utils.PacketUtils;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.bukkit.Material.*;

/**
 * This class handles all functions for synchronized SmartBoards.
 *
 * @author Josh
 */
public class SyncSmartBoard implements SmartBoard {

  private final int id;

  private ItemFrame[] frames;
  private Location[] frameLocations;
  private net.minecraft.server.v1_13_R2.ItemStack[] itemStacks;
  private BoardProfile profile;
  private BoardGraphics graphics;

  private volatile boolean canClick;
  private volatile boolean dirty;
  private volatile boolean canUpdate;
  private List<Player> players;
  private DataWatcher[] datawatchers;
  private PacketPlayOutEntityMetadata[] metadata;

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public SyncSmartBoard(@NotNull BoardProfile profile) {
    this.profile = profile;
    this.id = getNewId();
    this.players = new ArrayList<>();
    this.graphics = new SimpleBoardGraphics(profile.getWidth(), profile.getHeight());
    int size = profile.getWidth() * profile.getHeight();
  }

  /** Creates the board's ItemFrames, as well as registers the board to the SmartBoardThread. */
  public void create() {
    Location location = profile.getLocation();
    // The location is on the top-left of the board. Grab the world coordinates for this point to
    // calculate the blocks from the point given.
    int lx = location.getBlockX();
    int ly = location.getBlockY();
    int lz = location.getBlockZ();
    int size = getWidth() * getHeight();
    this.frames = new ItemFrame[size];
    this.datawatchers = new DataWatcher[size];
    this.metadata = new PacketPlayOutEntityMetadata[size];
    itemStacks = new net.minecraft.server.v1_13_R2.ItemStack[size];
    for (int index = 0; index < size; index++) {
      itemStacks[index] = CraftItemStack.asNMSCopy(new ItemStack(FILLED_MAP));
    }

    Collection<ItemFrame> itemFrames = profile.getWorld().getEntitiesByClass(ItemFrame.class);
    switch (profile.getDirection()) {
      case NORTH:
        createNorth(lx, ly, lz, itemFrames);
        break;
      case SOUTH:
        createSouth(lx, ly, lz, itemFrames);
        break;
      case EAST:
        createEast(lx, ly, lz, itemFrames);
        break;
      case WEST:
        createWest(lx, ly, lz, itemFrames);
        break;
      case UP:
        break;
      case DOWN:
        break;
    }
    //    for (int index = 0; index < size; index++) {
    //      PacketUtils.setMapId(
    //          frames[index].getEntityId(), metadata[index], datawatchers[index],
    // itemStacks[index]);
    //    }
    SmartBoardThread.instance.addBoard(this);
    setDirty(true);
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

  protected void createNorth(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int x = lx + getWidth() - 1; x >= lx; x--) {
        index = getIndex(x - lx, y - ly);
        frames[index] = createItemFrame(itemFrames, world, x, y, lz, BoardDirection.NORTH);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);

        metadata[index] = new PacketPlayOutEntityMetadata();
      }
    }
  }

  protected void createSouth(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int x = lx; x < lx + getWidth(); x++) {
        index = getIndex(x - lx, y - ly);
        frames[index] = createItemFrame(itemFrames, world, x, y, lz, BoardDirection.SOUTH);
        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        entityItemFrame.getDataWatcher().set(DataWatcherRegistry.b.a(7), 0);

        metadata[index] = new PacketPlayOutEntityMetadata();
      }
    }
  }

  protected void createEast(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int z = lz + getWidth() - 1; z >= lz; z--) {
        index = getIndex(z - lz, y - ly);
        frames[index] = createItemFrame(itemFrames, world, lx, y, z, BoardDirection.EAST);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        metadata[index] = new PacketPlayOutEntityMetadata();
      }
    }
  }

  protected void createWest(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getHeight(); y++) {
      for (int z = lz; z < lz + getWidth(); z++) {
        index = getIndex(z - lz, y - ly);
        frames[index] = createItemFrame(itemFrames, world, lx, y, z, BoardDirection.WEST);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        metadata[index] = new PacketPlayOutEntityMetadata();
      }
    }
  }

  @Override
  public void update() {}

  private Material[] mats = {FILLED_MAP, MAP};

  int mIndex = 0;

  @Override
  public void render() {
    BoardFrame frame = graphics.getFrame();
    MapImage[] frames = frame.getFrames();
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        int index = getIndex(x, y);
        ItemStack itemStack = new ItemStack(FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapId(frames[index].getPacketId());
        itemStack.setItemMeta(mapMeta);
        //        NMSUtils.setMapId(itemStacks[index], frames[index].getPacketId());
        DataWatcher dataWatcher =
            ((CraftItemFrame) this.frames[index]).getHandle().getDataWatcher();
        PacketUtils.setMapId(
            this.frames[index].getEntityId(), metadata[index], dataWatcher, itemStack);
        //        this.frames[index].setItem(new ItemStack(mats[mIndex]));
      }
    }
    mIndex++;
    if (mIndex >= mats.length) {
      mIndex = 0;
    }
    graphics.setDirty(false);
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

  public void addPlayer(Player player) {
    System.out.println("Adding player: " + player.getName());
    players.add(player);
    sendToPlayer(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public void sendToPlayer(Player player) {
    //    System.out.println("Sending to player: " + player.getName());
    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    for (int index = 0; index < metadata.length; index++) {
      sendMetadata(player, index);
    }
    BoardFrame boardFrame = graphics.getFrame();
    MapImage[] frames = boardFrame.getFrames();
    for (MapImage frame : frames) {
      if (!frame.isSent(player)) {
        frame.send(player);
      }
    }
  }

  private void sendMetadata(@NotNull Player player, int index) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata[index]);
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
    return this.canUpdate;
  }

  public void setCanUpdate(boolean flag) {
    this.canUpdate = flag;
  }

  @Override
  public boolean isDirty() {
    return dirty || graphics.isDirty();
  }

  @Override
  public void setDirty(boolean flag) {
    this.dirty = flag;
  }

  @Override
  public void dispatch() {
    for (Player player : players) {
      sendToPlayer(player);
    }
  }

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
