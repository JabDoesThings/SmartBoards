package jab.spigot.smartboards;

import jab.compiler.CompilerManager;
import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.SmartBoardsClickAdapter;
import jab.smartboards.commons.SmartBoardsMapAdapter;
import jab.smartboards.commons.board.BoardDirection;
import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.board.SyncSmartBoard;
import jab.spigot.smartboards.boards.examples.GalleryStaticBoard;
import jab.spigot.smartboards.boards.examples.PaintBoard;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
import jab.spigot.smartboards.boards.menu.MenuAssets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings("unused")
public class PluginSmartBoards extends JavaPlugin implements Listener {

  public static PluginSmartBoards instance;
  private SyncSmartBoard board;
  private PaintBoard paintBoard;
  private SmartBoardsMapAdapter smartSmartBoardsMapAdapter;
  private SmartBoardsClickAdapter smartBoardsClickAdapter;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    TransitionEffects.enableJavaFX();
    MenuAssets.load(false);
    getServer().getPluginManager().registerEvents(this, this);
    SmartBoards.start(this);
    SmartBoards.registerPacketAdapters(this);

    CompilerManager.instance = new CompilerManager(this, 2);
    CompilerManager.instance.start();

    int width = 8;
    int height = 6;
    World world = Objects.requireNonNull(Bukkit.getWorld("world"));
    Location locationA = new Location(world, 180, 90, 61);
    Location locationB = new Location(world, locationA.getBlockX() + width + 1, 90, 61);
    cleanSpot(locationB, width, height);

    board =
        new GalleryStaticBoard(
            new BoardProfile(locationA, BoardDirection.SOUTH, width, height, true));
    board.create();

    for (Player player : Bukkit.getOnlinePlayers()) {
      board.addPlayer(player);
    }

    this.paintBoard =
        new PaintBoard(new BoardProfile(locationB, BoardDirection.SOUTH, width, height, true));
    this.paintBoard.create();

    for (Player player : Bukkit.getOnlinePlayers()) {
      this.paintBoard.addPlayer(player);
    }
  }

  void cleanSpot(Location location, int width, int height) {
    World world = Objects.requireNonNull(location.getWorld());
    int z = location.getBlockZ();
    for (int y = location.getBlockY(); y < location.getBlockY() + height; y++) {
      for (int x = location.getBlockX(); x < location.getBlockX() + width; x++) {
        world.getBlockAt(x, y, z - 1).setType(Material.STONE);
        world.getBlockAt(x, y, z).setType(Material.AIR);
      }
    }
  }

  @Override
  public void onDisable() {
    SmartBoards.stop();
    SmartBoards.unregisterPacketAdapters();
    CompilerManager.instance.stop();
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    SmartBoards.createCache(playerId);
    // NOTE: Add after the player A little bit after enters the game.
    //   If you don't, the map packets will be ignored when sent.
    new BukkitRunnable() {
      @Override
      public void run() {
        if (board != null) {
          board.addPlayer(player);
        }
        if (paintBoard != null) {
          paintBoard.addPlayer(player);
        }
      }
    }.runTaskLater(this, 30L);
  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    SmartBoards.removeCache(playerId);
    if (board != null) {
      board.removePlayer(player);
    }
    if (paintBoard != null) {
      paintBoard.removePlayer(player);
    }
  }

  @EventHandler
  public void on(PlayerInteractEntityEvent event) {
    Entity entity = event.getRightClicked();
    Player player = event.getPlayer();
    if (entity instanceof ItemFrame) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void on(HangingBreakEvent event) {
    event.setCancelled(true);
  }
}
