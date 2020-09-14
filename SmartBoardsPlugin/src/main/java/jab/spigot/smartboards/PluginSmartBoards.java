package jab.spigot.smartboards;

import jab.compiler.CompilerManager;
import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.SmartBoardsClickAdapter;
import jab.smartboards.commons.SmartBoardsMapAdapter;
import jab.smartboards.commons.board.BoardDirection;
import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.board.SyncSmartBoard;
import jab.spigot.smartboards.boards.examples.GalleryStaticBoard;
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

    World world = Objects.requireNonNull(Bukkit.getWorld("world"));
    Location location = new Location(world, 180, 90, 61);

    int width = 8;
    int height = 6;
    int z = 61;
    for (int y = location.getBlockY(); y < location.getBlockY() + height; y++) {
      for (int x = location.getBlockX(); x < location.getBlockX() + width; x++) {
        world.getBlockAt(x, y, z - 1).setType(Material.STONE);
        world.getBlockAt(x, y, z).setType(Material.AIR);
      }
    }

    board =
        new GalleryStaticBoard(
            new BoardProfile(location, BoardDirection.SOUTH, width, height, true));
    board.create();

    for (Player player : Bukkit.getOnlinePlayers()) {
      board.addPlayer(player);
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
      }
    }.runTaskLater(this, 30L);
  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    if (board != null) {
      Player player = event.getPlayer();
      UUID playerId = player.getUniqueId();
      SmartBoards.removeCache(playerId);
      board.removePlayer(player);
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
