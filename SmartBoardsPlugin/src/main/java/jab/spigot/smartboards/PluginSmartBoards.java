package jab.spigot.smartboards;

import jab.smartboards.commons.SmartBoards;
import jab.smartboards.commons.board.SyncSmartBoard;
import jab.spigot.smartboards.boards.examples.GalleryStaticBoard;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
import jab.spigot.smartboards.boards.menu.MenuAssets;
import jab.compiler.CompilerManager;
import jab.smartboards.commons.board.BoardDirection;
import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.SmartBoardsClickAdapter;
import jab.smartboards.commons.SmartBoardsMapAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    // Create and start the main thread for processing smartboards.
    //    SmartBoardThread.instance = new SmartBoardThread(this);
    //    CompilerManager.instance = new CompilerManager(this, 1);
    //    SmartBoardThread.instance.start();
    //    CompilerManager.instance.start();

    SmartBoards.start(this);

    SmartBoards.registerPacketAdapters(this);
    World world = Bukkit.getWorlds().get(0);
    Location location = new Location(world, 180, 70, 61);
    board = new GalleryStaticBoard(new BoardProfile(location, BoardDirection.SOUTH, 4, 3));
    board.create();
  }

  @Override
  public void onDisable() {
    SmartBoards.stop();
    SmartBoards.unregisterPacketAdapters();
    TransitionEffects.disableJavaFX();
    CompilerManager.instance.stop();
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    SmartBoards.createCache(playerId);
    new BukkitRunnable() {
      @Override
      public void run() {
        board.addPlayer(player);
      }
    }.runTaskLater(this, 30L);
  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    SmartBoards.removeCache(playerId);
    board.removePlayer(player);
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
