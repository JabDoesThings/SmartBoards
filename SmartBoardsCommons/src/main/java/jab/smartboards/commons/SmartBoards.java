package jab.smartboards.commons;

import com.comphenix.protocol.ProtocolManager;
import jab.smartboards.commons.board.SyncSmartBoard;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SmartBoards {

  public static final Map<UUID, Map<Integer, Long>> mapMapPacketTimes = new HashMap<>();
  public static ProtocolManager protocolManager;
  public static int NEXT_ID = 0;
  private static int THREAD_COUNT = 1;
  public static boolean DEBUG = true;

  private static int CURRENT_MAP_ID = 300;

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
}
