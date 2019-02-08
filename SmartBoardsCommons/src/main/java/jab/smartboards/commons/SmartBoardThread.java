//package jab.smartboards.commons;
//
//import jab.smartboards.commons.board.SmartBoard;
//import jab.smartboards.commons.board.SmartBoardSearch;
//import jab.smartboards.commons.board.graphics.BoardGraphics;
//import jab.smartboards.commons.utils.PacketUtils;
//import jab.smartboards.commons.utils.UVUtil;
//import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
//import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
///**
// * TODO: Document.
// *
// * @author Josh
// */
//public class SmartBoardThread implements Runnable {
//
//  public static final String THREAD_NAME = "SmartBoard Thread";
//  private static volatile long SLEEP_TIME = 25L;
//  public static int NEXT_ID = 0;
//
//  public static SmartBoardThread instance;
//  private final JavaPlugin plugin;
//
//  private SmartBoardSyncRunnable syncRunnable;
//
//  /** Store all maps to the thread. */
//  private final Map<Integer, SmartBoard> mapBoards;
//
//  /** Key: Mini-Map ID, Value: Board using the ID. */
//  private final Map<Integer, SmartBoard> mapBoardIds;
//
//  private static final Map<Integer, PacketPlayOutMap> mapMapPackets = new HashMap<>();
//
//  /** This is a list of pre-approved packets registered by smartboards. */
//  private final List<PacketPlayOutMap> listApprovedPackets;
//
//  private static final Map<Integer, PacketPlayOutEntityMetadata> mapMetaPackets = new HashMap<>();
//
//  public void addPacket(PacketPlayOutEntityMetadata packet, int entityId) {
//    synchronized (lockPackets) {
//      mapMetaPackets.put(entityId, packet);
//    }
//  }
//
//  public void removePacket(PacketPlayOutEntityMetadata packet) {
//    synchronized (lockPackets) {
//      int entityId = PacketUtils.getEntityId(packet);
//      mapMetaPackets.remove(entityId);
//    }
//  }
//
//  public void removePacket(int entityId) {
//    synchronized (lockPackets) {
//      mapMetaPackets.remove(entityId);
//    }
//  }
//
//  public static void addPacket(PacketPlayOutMap packet) {
//    synchronized (lockPackets) {
//      int id = PacketUtils.getMapId(packet);
//      mapMapPackets.put(id, packet);
//    }
//  }
//
//  public static void removePacket(PacketPlayOutMap packet) {
//    synchronized (lockPackets) {
//      mapMapPackets.remove(PacketUtils.getMapId(packet));
//    }
//  }
//
//  private final List<SmartBoard> listFlaggedBoards;
//
//  private SmartBoard[] boardsToLoop;
//
//  private SmartBoard[] boardsToLoopSync;
//  /** Flag to onStop the thread on the next tick. */
//  private volatile boolean stopped;
//
//  private volatile boolean started;
//
//  public static final Object lockPackets = new Object();
//
//  public final Object lockBoards = new Object();
//  public final Object lockBoardsSync = new Object();
//  private Thread thread;
//
//  /** Main constructor. */
//  public SmartBoardThread(JavaPlugin plugin) {
//    this.plugin = plugin;
//    this.mapBoards = new HashMap<>();
//    this.mapBoardIds = new HashMap<>();
//    this.listApprovedPackets = new ArrayList<>();
//    this.listFlaggedBoards = new ArrayList<>();
//    this.boardsToLoop = new SmartBoard[0];
//    this.boardsToLoopSync = new SmartBoard[0];
//  }
//
//  @Override
//  public void run() {
//    syncRunnable = new SmartBoardSyncRunnable();
//    if (plugin != null) {
//      syncRunnable.runTaskTimer(plugin, 0L, 1L);
//    }
//    while (!stopped) {
//      try {
//        SmartBoard[] boards = getLoopBoards();
//        if (boards.length > 0) {
//          synchronized (lockBoards) {
//            updateBoards(boards);
//          }
//          removeFlaggedBoards();
//          synchronized (lockBoards) {
//            renderBoards(boards);
//          }
//          removeFlaggedBoards();
//        }
//        if (plugin == null) {
//          syncRunnable.run();
//        }
//      } catch (Exception e) {
//        System.out.println("An exception has occurred in the SmartBoard thread. (async)");
//        e.printStackTrace();
//      }
//      try {
//        Thread.sleep(SLEEP_TIME);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//    syncRunnable.cancel();
//  }
//
//  private void updateBoards(@NotNull SmartBoard[] boards) {
//    // Update the MapBoards that are set to update.
//    for (SmartBoard board : boards) {
//      if (board.canUpdate()) {
//        try {
//          board.update();
//        } catch (Exception e) {
//          System.err.println(
//              "The SmartBoard "
//                  + board.getClass().getSimpleName()
//                  + "(ID: "
//                  + board.getId()
//                  + ") has encountered an uncaught exception and has been disabled."
//                  + "(Update)");
//          e.printStackTrace(System.out);
//          synchronized (listFlaggedBoards) {
//            // Add the board to be removed.
//            listFlaggedBoards.add(board);
//          }
//        }
//      }
//      BoardGraphics graphics = board.getGraphics();
//    }
//  }
//
//  private void renderBoards(@NotNull SmartBoard[] boards) {
//    // Go through each board that is listed as dirty and render them to their MapImage
//    // caches.
//    for (SmartBoard board : boards) {
//      // Check the graphics and update it if necessary.
//      BoardGraphics graphics = board.getGraphics();
//      if (graphics.canUpdate()) {
//        graphics.update();
//      }
//      if (board.isDirty()) {
//        try {
//          // Render the board.
//          board.render();
//          // Let the thread know that this board is now drawn and doesn't need to be redrawn.
//          board.setDirty(false);
//          // Go through the rendered mini-maps and send them out to players nearby.
//          board.dispatch();
//        } catch (Exception e) {
//          System.err.println(
//              "The SmartBoard "
//                  + board.getClass().getSimpleName()
//                  + "(ID: "
//                  + board.getId()
//                  + ") has encountered an uncaught exception and has been disabled."
//                  + "(Render)");
//          e.printStackTrace(System.out);
//          synchronized (listFlaggedBoards) {
//            // Add the board to be removed.
//            listFlaggedBoards.add(board);
//          }
//        }
//      }
//    }
//  }
//
//  /**
//   * @param player The Player to test.
//   * @param maxDistance The maximum distance in blocks from the player to search for Boards.
//   * @return Returns a BoardSearch result if the Player is looking at a Board within the maximum
//   *     distance given. If the player is not looking at a board, null is returned.
//   */
//  @Nullable
//  public SmartBoardSearch getBoardAndUVLookedAt(@NotNull Player player, int maxDistance) {
//    // Attempt to grab any boards that the player is looking at within 12 blocks.
//    List<SmartBoard> boards = getBoardsLookedAt(player, maxDistance);
//    // No boards were looked at to interact with.
//    if (boards == null) {
//      System.out.println("No boards looked at: " + player.getName());
//      return null;
//    }
//    // Attempt to grab the first board that is directly clicked on.
//    double[] uv = null;
//    SmartBoard boardClicked = null;
//    for (SmartBoard board : boards) {
//      if (!board.canClick()) continue;
//      // If the player clicked on a board.
//      uv = UVUtil.calculateUV(board, player);
//      if (uv != null) {
//        boardClicked = board;
//        break;
//      }
//    }
//    // No boards were actually looked at directly.
//    if (uv == null) return null;
//    return new SmartBoardSearch(boardClicked, uv);
//  }
//
//  @Nullable
//  public List<SmartBoard> getBoardsLookedAt(@NotNull Player player, int maxDistance) {
//    List<SmartBoard> boards;
//    synchronized (lockBoards) {
//      boards = new ArrayList<>(getBoards());
//    }
//    boards.removeIf(board -> !board.isLookingAt(player, maxDistance));
//    return boards;
//  }
//
//  private void removeFlaggedBoards() {
//    synchronized (listFlaggedBoards) {
//      // Remove any boards added to the removal list.
//      if (!listFlaggedBoards.isEmpty()) {
//        for (SmartBoard board : listFlaggedBoards) {
//          removeBoard(board);
//        }
//        listFlaggedBoards.clear();
//      }
//    }
//  }
//
//  public void addBoard(@NotNull SmartBoard board) {
//    if (!isRegistered(board)) {
//      System.out.println("SmartBoards: Adding board to thread: " + board);
//      synchronized (mapBoards) {
//        // Place the board in the registrar map.
//        mapBoards.put(board.getId(), board);
//      }
//      if (board.isAsync()) {
//        synchronized (lockBoards) {
//          // Add the board to the async loop array to be updated.
//          SmartBoard[] boardsToLoopNew = new SmartBoard[boardsToLoop.length + 1];
//          System.arraycopy(boardsToLoop, 0, boardsToLoopNew, 0, boardsToLoop.length);
//          boardsToLoopNew[boardsToLoop.length] = board;
//          boardsToLoop = boardsToLoopNew;
//        }
//      } else {
//        synchronized (lockBoardsSync) {
//          // Add the board to the sync loop array to be updated.
//          SmartBoard[] boardsToLoopSyncNew = new SmartBoard[boardsToLoopSync.length + 1];
//          System.arraycopy(boardsToLoopSync, 0, boardsToLoopSyncNew, 0, boardsToLoopSync.length);
//          boardsToLoopSyncNew[boardsToLoopSync.length] = board;
//          boardsToLoopSync = boardsToLoopSyncNew;
//        }
//      }
//      // Add any registered map IDs to be interpreted by the packet checker.
//      updateBoardIds(board);
//    }
//  }
//
//  public void removeBoard(@NotNull SmartBoard board) {
//    if (isRegistered(board)) {
//      synchronized (mapBoards) {
//        // Remove the board from the registrar map.
//        mapBoards.remove(board.getId());
//      }
//      if (board.isAsync()) {
//        synchronized (lockBoards) {
//          // Remove the board from the loop array.
//          SmartBoard[] boardsToLoopNew = new SmartBoard[boardsToLoop.length - 1];
//          int index = 0;
//          for (SmartBoard boardNext : boardsToLoop) {
//            if (!boardNext.equals(board)) {
//              boardsToLoopNew[index++] = boardNext;
//            }
//          }
//          boardsToLoop = boardsToLoopNew;
//        }
//      } else {
//        synchronized (lockBoardsSync) {
//          // Remove the board from the loop array.
//          SmartBoard[] boardsToLoopNew = new SmartBoard[boardsToLoopSync.length - 1];
//          int index = 0;
//          for (SmartBoard boardNext : boardsToLoopSync) {
//            if (!boardNext.equals(board)) {
//              boardsToLoopNew[index++] = boardNext;
//            }
//          }
//          boardsToLoopSync = boardsToLoopNew;
//        }
//      }
//      // Remove any registered map IDs from the board.
//      removeBoardIds(board);
//    }
//  }
//
//  public void updateBoardIds(@NotNull SmartBoard board) {
//    // Remove the board's previous ids.
//    synchronized (mapBoardIds) {
//      for (int key : new HashSet<>(mapBoardIds.keySet())) {
//        SmartBoard boardNext = mapBoardIds.get(key);
//        if (boardNext == null || boardNext.equals(board)) {
//          mapBoardIds.remove(key);
//        }
//      }
//    }
//    // Go through and add all ids to the map.
//    int[] ids = board.getMapIds();
//    if (ids != null && ids.length > 0) {
//      // Check the ID map.
//      synchronized (mapBoardIds) {
//        for (int id : board.getMapIds()) {
//          mapBoardIds.put(id, board);
//        }
//      }
//    }
//  }
//
//  public void removeBoardIds(@NotNull SmartBoard board) {
//    int[] ids = board.getMapIds();
//    if (ids != null && ids.length > 0) {
//      // Check the ID map.
//      synchronized (mapBoardIds) {
//        for (int id : board.getMapIds()) {
//          mapBoardIds.remove(id);
//        }
//      }
//    }
//  }
//
//  public void start() {
//    if (!started) {
//      this.mapBoards.clear();
//      this.mapBoardIds.clear();
//      this.listFlaggedBoards.clear();
//      this.listApprovedPackets.clear();
//      this.boardsToLoop = new SmartBoard[0];
//    }
//    this.started = true;
//    this.stopped = false;
//    thread = new Thread(this, THREAD_NAME);
//    thread.start();
//    if (SmartBoards.DEBUG) {
//      System.out.println("SmartBoards: Board Thread Started.");
//    }
//  }
//
//  public void pause() {
//    this.stopped = true;
//    if (SmartBoards.DEBUG) {
//      System.out.println("SmartBoards: Board Thread Paused.");
//    }
//  }
//
//  public void resume() {
//    this.stopped = false;
//    if (thread == null) {
//      thread = new Thread(this, THREAD_NAME);
//    }
//    if (!thread.isAlive() || thread.isInterrupted()) {
//      thread.start();
//    }
//    if (SmartBoards.DEBUG) {
//      System.out.println("SmartBoards: Board Thread Resumed.");
//    }
//  }
//
//  public void stop() {
//    this.started = false;
//    this.stopped = true;
//    if (SmartBoards.DEBUG) {
//      System.out.println("SmartBoards: Board Thread Stopped.");
//    }
//  }
//
//  private SmartBoard[] getLoopBoards() {
//    return this.boardsToLoop;
//  }
//
//  @NotNull
//  public Collection<SmartBoard> getBoards() {
//    return mapBoards.values();
//  }
//
//  public boolean isRegistered(@NotNull SmartBoard board) {
//    return mapBoards.containsKey(board.getId());
//  }
//
//  @NotNull
//  public Map<Integer, SmartBoard> getRegisteredMapIds() {
//    return this.mapBoardIds;
//  }
//
//  @NotNull
//  public Map<Integer, PacketPlayOutMap> getRegisteredMapPackets() {
//    return this.mapMapPackets;
//  }
//
//  public Map<Integer, PacketPlayOutEntityMetadata> getRegisteredMetaPackets() {
//    return this.mapMetaPackets;
//  }
//
//  private class SmartBoardSyncRunnable extends BukkitRunnable {
//    @Override
//    public void run() {
//      try {
//        SmartBoard[] boards = boardsToLoopSync;
//        if (boards.length > 0) {
//          //          System.out.println("SmartBoardThread->updateBoards() (sync)");
//          synchronized (lockBoardsSync) {
//            updateBoards(boards);
//          }
//          removeFlaggedBoards();
//          //          System.out.println("SmartBoardThread->renderBoards() (sync)");
//          synchronized (lockBoardsSync) {
//            renderBoards(boards);
//          }
//          removeFlaggedBoards();
//        }
//      } catch (Exception e) {
//        System.out.println("An exception has occurred in the SmartBoard thread. (sync)");
//        e.printStackTrace();
//      }
//      try {
//        Thread.sleep(SLEEP_TIME);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//  }
//}
