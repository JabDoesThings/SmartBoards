package jab.smartboards.commons.board;

import jab.smartboards.commons.SmartBoardCluster;
import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.board.graphics.BoardFrame;
import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.board.graphics.SimpleBoardGraphics;
import jab.smartboards.commons.events.SmartBoardClickEvent;
import jab.smartboards.commons.utils.MapImage;
import jab.smartboards.commons.utils.PacketUtils;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.bukkit.Material.FILLED_MAP;
import static org.bukkit.Material.MAP;

/**
 * This class handles all functions for synchronized SmartBoards.
 *
 * @author Josh
 */
public class SyncSmartBoard implements SmartBoard {

  private final int id;
  private final DataWatcherObject<net.minecraft.server.v1_16_R2.ItemStack> e;
  private final DataWatcherObject<Integer> f;

  private ItemFrame[] frames;
  private Location[] frameLocations;
  private ItemStack[] stacks;
  private BoardProfile profile;
  private BoardGraphics graphics;

  private volatile boolean canClick;
  private volatile boolean dirty;
  private volatile boolean canUpdate;
  private List<Player> players;
  private DataWatcher[] datawatchers;
  private PacketPlayOutEntityMetadata[] metadata;
  private SmartBoardCluster cluster;
  private boolean created;

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public SyncSmartBoard(@NotNull BoardProfile profile) {
    this.profile = profile;
    this.id = getNewId();
    this.players = new ArrayList<>();
    this.graphics = new SimpleBoardGraphics(profile.getBlockWidth(), profile.getBlockHeight());
    int size = profile.getBlockWidth() * profile.getBlockHeight();
    e = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.g);
    f = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.b);
  }

  /** Creates the board's ItemFrames, as well as registers the board to the SmartBoardThread. */
  public void create() {
    Location location = profile.getLocation();
    // The location is on the top-left of the board. Grab the world coordinates for this point to
    // calculate the blocks from the point given.
    int lx = location.getBlockX();
    int ly = location.getBlockY();
    int lz = location.getBlockZ();
    int size = getBlockWidth() * getBlockHeight();
    this.frames = new ItemFrame[size];
    this.datawatchers = new DataWatcher[size];
    this.metadata = new PacketPlayOutEntityMetadata[size];
    this.frameLocations = new Location[size];
    stacks = new ItemStack[size];
    for (int index = 0; index < size; index++) {
      stacks[index] = new ItemStack(FILLED_MAP);
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
    SmartBoards.addBoard(this);
    setDirty(true);
    this.created = true;
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
    for (ItemFrame frame : frames) {
      SmartBoards.removePacket(frame.getEntityId());
    }
    SmartBoards.removeBoard(this);
    this.created = false;
  }

  protected void createNorth(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getBlockHeight(); y++) {
      for (int x = lx + getBlockWidth() - 1; x >= lx; x--) {
        index = getIndex(x - lx, y - ly);
        frames[index] = createItemFrame(itemFrames, world, x, y, lz, BoardDirection.NORTH);
        frameLocations[index] = new Location(world, x, y, lz);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        metadata[index] = new PacketPlayOutEntityMetadata();
        SmartBoards.addPacket(metadata[index], frames[index].getEntityId());
      }
    }
  }

  protected void createSouth(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getBlockHeight(); y++) {
      for (int x = lx; x < lx + getBlockWidth(); x++) {
        index = getIndex(x - lx, y - ly);
        frames[index] = createItemFrame(itemFrames, world, x, y, lz, BoardDirection.SOUTH);
        frameLocations[index] = new Location(world, x, y, lz);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        int entityId = frames[index].getEntityId();
        metadata[index] = new PacketPlayOutEntityMetadata();
        SmartBoards.addPacket(metadata[index], frames[index].getEntityId());
      }
    }
  }

  protected void createEast(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getBlockHeight(); y++) {
      for (int z = lz + getBlockWidth() - 1; z >= lz; z--) {
        index = getIndex(z - lz, y - ly);
        frames[index] = createItemFrame(itemFrames, world, lx, y, z, BoardDirection.EAST);
        frameLocations[index] = new Location(world, lx, y, z);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        metadata[index] = new PacketPlayOutEntityMetadata();
        SmartBoards.addPacket(metadata[index], frames[index].getEntityId());
      }
    }
  }

  protected void createWest(int lx, int ly, int lz, Collection<ItemFrame> itemFrames) {
    int index;
    World world = profile.getWorld();
    for (int y = ly; y < ly + getBlockHeight(); y++) {
      for (int z = lz; z < lz + getBlockWidth(); z++) {
        index = getIndex(z - lz, y - ly);
        frames[index] = createItemFrame(itemFrames, world, lx, y, z, BoardDirection.WEST);
        frameLocations[index] = new Location(world, lx, y, z);
        //        EntityItemFrame entityItemFrame = ((CraftItemFrame) frames[index]).getHandle();
        //        datawatchers[index] = entityItemFrame.getDataWatcher();
        //        datawatchers[index].set(DataWatcherRegistry.b.a(7), 0);
        metadata[index] = new PacketPlayOutEntityMetadata();
        SmartBoards.addPacket(metadata[index], frames[index].getEntityId());
      }
    }
  }

  @Override
  public void update() {
  }

  private Material[] mats = {FILLED_MAP, MAP};

  int mIndex = 0;

  @Override
  public void render() {
    BoardFrame frame = graphics.getFrame();
    MapImage[] frames = frame.getFrames();
    for (int y = 0; y < getBlockHeight(); y++) {
      for (int x = 0; x < getBlockWidth(); x++) {
        int index = getIndex(x, y);

        ItemStack itemStack = new ItemStack(FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapId(frames[index].getPacketId());
        itemStack.setItemMeta(mapMeta);

        EntityItemFrame entityItemFrame = ((CraftItemFrame) this.frames[index]).getHandle();
        entityItemFrame.setItem(CraftItemStack.asNMSCopy(itemStack));
        DataWatcher dataWatcher = entityItemFrame.getDataWatcher();

        PacketUtils.setMapId(this.frames[index].getEntityId(), metadata[index], dataWatcher);
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
  public boolean isLookingAt(org.bukkit.entity.LivingEntity player, int maxDistance) {
    boolean returned = false;
    World world = player.getWorld();
    // Make sure the player is in the same world.
    if (!world.getName().equals(profile.getWorld().getName())) {
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
    BoardFrame boardFrame = graphics.getFrame();
    MapImage[] frames = boardFrame.getFrames();
    for (MapImage frame : frames) {
      if (!frame.isSent(player)) {
        frame.send(player);
      }
    }
    for (int index = 0; index < metadata.length; index++) {
      sendMetadata(player, index);
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
    return getProfile().getLocation();
  }

  @Override
  public BoardDirection getDirection() {
    return getProfile().getDirection();
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

  @Override
  public SmartBoardCluster getCluster() {
    return this.cluster;
  }

  @Override
  public void setCluster(SmartBoardCluster cluster) {
    checkCluster(cluster);
    this.cluster = cluster;
  }

  @Override
  public boolean keepChunksLoaded() {
    return this.profile.keepChunksLoaded();
  }

  public boolean isCreated() {
    return this.created;
  }
}
