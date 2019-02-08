package jab.smartboards.commons;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import jab.smartboards.commons.board.SmartBoard;
import jab.smartboards.commons.board.SmartBoardSearch;
import jab.smartboards.commons.utils.PacketUtils;
import jab.smartboards.commons.utils.UVUtil;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class SmartBoards {

  private static final Map<Integer, PacketPlayOutMap> mapMapPackets = new HashMap<>();
  private static final Map<Integer, PacketPlayOutEntityMetadata> mapMetaPackets = new HashMap<>();
  public static final Map<UUID, Map<Integer, Long>> mapMapPacketTimes = new HashMap<>();
  /** This is a list of pre-approved packets registered by smartboards. */
  private static final List<PacketPlayOutMap> listApprovedPackets = new ArrayList<>();

  private static final List<SmartBoardCluster> listRegisteredClusters = new ArrayList<>();
  private static ProtocolManager protocolManager;
  static final Object lockPackets = new Object();
  public static int NEXT_ID = 0;
  private static int CURRENT_MAP_ID = 300;
  public static boolean DEBUG = true;

  private static BukkitSmartBoardCluster syncCluster;
  private static ThreadSmartBoardCluster asyncCluster;
  private static SmartBoardsMapAdapter smartSmartBoardsMapAdapter;
  private static SmartBoardsClickAdapter smartBoardsClickAdapter;

  public static void start(@Nullable Plugin plugin) {
    if (plugin != null) {
      if (syncCluster == null) {
        syncCluster = new BukkitSmartBoardCluster(plugin);
      }
      if (!syncCluster.isRunning()) {
        syncCluster.start();
      }
      registerCluster(syncCluster);
    }
    if (asyncCluster == null) {
      asyncCluster = new ThreadSmartBoardCluster();
    }
    if (!asyncCluster.isRunning()) {
      asyncCluster.start();
      registerCluster(asyncCluster);
    }
  }

  public static void stop() {
    if (syncCluster != null && syncCluster.isRunning()) {
      syncCluster.stop();
      unregisterCluster(syncCluster);
    }
    if (asyncCluster != null && asyncCluster.isRunning()) {
      asyncCluster.stop();
      unregisterCluster(asyncCluster);
    }
  }

  public static void registerPacketAdapters(Plugin plugin) {
    smartSmartBoardsMapAdapter = new SmartBoardsMapAdapter(plugin);
    smartBoardsClickAdapter = new SmartBoardsClickAdapter(plugin);
    SmartBoards.protocolManager = ProtocolLibrary.getProtocolManager();
    ProtocolManager protocolManager = SmartBoards.protocolManager;
    protocolManager.addPacketListener(smartSmartBoardsMapAdapter);
    protocolManager.addPacketListener(smartBoardsClickAdapter);
  }

  public static void unregisterPacketAdapters() {
    ProtocolManager protocolManager = SmartBoards.protocolManager;
    protocolManager.removePacketListener(smartSmartBoardsMapAdapter);
    protocolManager.removePacketListener(smartBoardsClickAdapter);
  }

  public static void registerCluster(@NotNull SmartBoardCluster cluster) {
    listRegisteredClusters.add(cluster);
  }

  public static void unregisterCluster(@NotNull SmartBoardCluster cluster) {
    listRegisteredClusters.remove(cluster);
  }

  public static void addBoard(@NotNull SmartBoard board) {
    if (board.isAsync()) {
      asyncCluster.addBoard(board);
    } else {
      // If the synchronized cluster is active, add the board to the cluster.
      if (syncCluster != null) {
        syncCluster.addBoard(board);
      }
      // If the synchronized cluster is not active, then add the board to the a-synchronous cluster.
      else {
        asyncCluster.addBoard(board);
      }
    }
  }

  public static void removeBoard(@NotNull SmartBoard board) {
    if (board.isAsync()) {
      asyncCluster.removeBoard(board);
    } else {
      // If the synchronized cluster is active, remove the board from the cluster.
      if (syncCluster != null) {
        syncCluster.removeBoard(board);
      }
      // If the synchronized cluster is not active, then remove the board from the a-synchronous
      // cluster.
      else {
        asyncCluster.removeBoard(board);
      }
    }
  }

  public static int generateMapIndex() {
    return generateMapIndex(1);
  }

  public static int generateMapIndex(int count) {
    int ret = CURRENT_MAP_ID;
    CURRENT_MAP_ID += count;
    return ret;
  }

  public static void createCache(@NotNull UUID playerId) {
    mapMapPacketTimes.put(playerId, new HashMap<>());
  }

  public static void removeCache(@NotNull UUID playerId) {
    mapMapPacketTimes.remove(playerId);
  }

  /**
   * @param player The Player to test.
   * @param maxDistance The maximum distance in blocks from the player to search for Boards.
   * @return Returns a BoardSearch result if the Player is looking at a Board within the maximum
   *     distance given. If the player is not looking at a board, null is returned.
   */
  @Nullable
  public static SmartBoardSearch getBoardAndUVLookedAt(
      @NotNull LivingEntity player, int maxDistance) {
    // Attempt to grab any boards that the player is looking at within 12 blocks.
    List<SmartBoard> boards = getBoardsLookedAt(player, maxDistance);
    // No boards were looked at to interact with.
    if (boards == null) {
      System.out.println("No boards looked at: " + player.getName());
      return null;
    }
    // Attempt to grab the first board that is directly clicked on.
    double[] uv = null;
    SmartBoard boardClicked = null;
    for (SmartBoard board : boards) {
      if (!board.canClick()) continue;
      // If the player clicked on a board.
      uv = UVUtil.calculateUV(board, player);
      if (uv != null) {
        boardClicked = board;
        break;
      }
    }
    // No boards were actually looked at directly.
    if (uv == null) return null;
    return new SmartBoardSearch(boardClicked, uv);
  }

  @Nullable
  public static List<SmartBoard> getBoardsLookedAt(@NotNull LivingEntity player, int maxDistance) {
    List<SmartBoard> boards = new ArrayList<>();
    for (SmartBoardCluster cluster : listRegisteredClusters) {
      boards.addAll(cluster.getBoardsLookedAt(player, maxDistance));
    }
    return boards;
  }

  public static void addPacket(PacketPlayOutEntityMetadata packet, int entityId) {
    synchronized (lockPackets) {
      mapMetaPackets.put(entityId, packet);
    }
  }

  public static void removePacket(PacketPlayOutEntityMetadata packet) {
    synchronized (lockPackets) {
      int entityId = PacketUtils.getEntityId(packet);
      mapMetaPackets.remove(entityId);
    }
  }

  public static void removePacket(int entityId) {
    synchronized (lockPackets) {
      mapMetaPackets.remove(entityId);
    }
  }

  public static void addPacket(PacketPlayOutMap packet) {
    synchronized (lockPackets) {
      int id = PacketUtils.getMapId(packet);
      mapMapPackets.put(id, packet);
    }
  }

  public static void removePacket(PacketPlayOutMap packet) {
    synchronized (lockPackets) {
      mapMapPackets.remove(PacketUtils.getMapId(packet));
    }
  }

  @NotNull
  public static Map<Integer, PacketPlayOutMap> getRegisteredMapPackets() {
    return mapMapPackets;
  }

  @NotNull
  public static Map<Integer, PacketPlayOutEntityMetadata> getRegisteredMetaPackets() {
    return mapMetaPackets;
  }
}
