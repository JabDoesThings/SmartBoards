package jab.spigot.smartboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import jab.spigot.smartboards.boards.examples.GalleryStaticBoard;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
import jab.spigot.smartboards.enums.BoardDirection;
import jab.spigot.smartboards.utils.BoardProfile;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class PluginSmartBoards extends JavaPlugin implements Listener {

  public static final Map<UUID, Map<Integer, Long>> mapMapPacketTimes = new HashMap<>();

  public static boolean DEBUG = true;

  public static PluginSmartBoards instance;
  public static ProtocolManager protocolManager;
  public static int NEXT_ID = 0;
  private static int THREAD_COUNT = 1;
  private GalleryStaticBoard board;

  private static int CURRENT_MAP_ID = 15000;

  public static int generateMapIndex() {
    return generateMapIndex(1);
  }

  public static int generateMapIndex(int count) {
    int ret = CURRENT_MAP_ID;
    CURRENT_MAP_ID += count;
    return ret;
  }

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    TransitionEffects.enableJavaFX();

    getServer().getPluginManager().registerEvents(this, this);

    protocolManager = ProtocolLibrary.getProtocolManager();
    // Create and start the main thread for processing smartboards.
    SmartBoardThread.instance = new SmartBoardThread();
    CompilerManager.instance = new CompilerManager(THREAD_COUNT);
    SmartBoardThread.instance.start();
    CompilerManager.instance.start();

    World world = Bukkit.getWorlds().get(0);
    Location location = new Location(world, 180, 70, 61);
    board = new GalleryStaticBoard(new BoardProfile(location, BoardDirection.SOUTH, 16, 9));
    board.create();
  }

  @Override
  public void onDisable() {
    TransitionEffects.disableJavaFX();
    SmartBoardThread.instance.stop();
    CompilerManager.instance.stop();
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    //    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    new BukkitRunnable() {
      @Override
      public void run() {
        System.out.println("ADD");
        //        for (MapImage image : MapImageUtils.getStaticImages()) {
        //          connection.sendPacket(image.getPacket());
        //        }
        board.addPlayer(player);
      }
    }.runTaskLater(this, 30L);
  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    mapMapPacketTimes.remove(playerId);
    //    mapMetaPacketTimes.remove(playerId);
    board.removePlayer(player);
  }
}
