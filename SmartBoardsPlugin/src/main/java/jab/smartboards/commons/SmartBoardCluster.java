package jab.smartboards.commons;

import jab.smartboards.commons.board.SmartBoard;
import jab.smartboards.commons.board.SmartBoardSearch;
import jab.smartboards.commons.board.graphics.BoardGraphics;
import jab.smartboards.commons.utils.UVUtil;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * SmartBoardClusters are designed to cluster SmartBoards for update loop control. Clusters are
 * designed to be thread-safe, and optimized to support implementing both synchronized and
 * a-synchronized loops.
 *
 * @author Josh
 */
public abstract class SmartBoardCluster implements Runnable {

  private final Object lockBoards = new Object();

  private final Map<Integer, PacketPlayOutEntityMetadata> mapMetaPackets;
  /** Store all maps to the thread. */
  private final Map<Integer, SmartBoard> mapBoards;

  /** Key: Mini-Map ID, Value: Board using the ID. */
  private final Map<Integer, SmartBoard> mapBoardIds;

  private final List<SmartBoard> listFlaggedBoards;

  private SmartBoard[] boardsToLoop;
  private int size;
  private volatile boolean running;

  /** Main constructor. */
  public SmartBoardCluster() {
    this.mapBoards = new HashMap<>();
    this.mapBoardIds = new HashMap<>();
    this.mapMetaPackets = new HashMap<>();
    this.listFlaggedBoards = new ArrayList<>();
    this.boardsToLoop = new SmartBoard[0];
  }

  @Override
  public void run() {
    try {
      SmartBoard[] boards = getBoards();
      if (boards.length > 0) {
        synchronized (lockBoards) {
          updateBoards(boards);
        }
        removeFlaggedBoards();
        synchronized (lockBoards) {
          renderBoards(boards);
        }
        removeFlaggedBoards();
      }
    } catch (Exception e) {
      System.err.println(
          "An exception has occurred while updating the "
              + getClass().getSimpleName()
              + "'s update loop.");
      e.printStackTrace();
    }
  }

  /**
   * Starts the cluster's update loop.
   *
   * @throws IllegalStateException Thrown if the method is invoked when the cluster's update loop is
   *     already running.
   */
  public void start() {
    // Make sure the loop is not already running.
    if (running) {
      throw new IllegalStateException("The Cluster is already running and cannot be started.");
    }
    try {
      onStart();
    } catch (Exception e) {
      System.err.println("Failed to start the " + getClass().getSimpleName() + ".");
      e.printStackTrace();
    }
    running = true;
  }

  /**
   * Stops the cluster's update loop.
   *
   * @throws IllegalStateException Thrown if the method is invoked when the cluster's update loop is
   *     not running.
   */
  public void stop() {
    // Make sure the loop is actually running.
    if (!running) {
      throw new IllegalStateException("The Cluster is not running and cannot be stopped.");
    }
    try {
      onStop();
    } catch (Exception e) {
      System.err.println("Failed to stop the " + getClass().getSimpleName() + ".");
      e.printStackTrace();
    }
    running = false;
  }

  protected void updateBoards(@NotNull SmartBoard[] boards) {
    // Update the MapBoards that are set to update.
    for (SmartBoard board : boards) {
      if (board.canUpdate()) {
        try {
          board.update();
        } catch (Exception e) {
          System.err.println(
              "The SmartBoard "
                  + board.getClass().getSimpleName()
                  + "(ID: "
                  + board.getId()
                  + ") has encountered an uncaught exception and has been disabled."
                  + "(Update)");
          e.printStackTrace(System.out);
          synchronized (listFlaggedBoards) {
            // Add the board to be removed.
            listFlaggedBoards.add(board);
          }
        }
      }
    }
  }

  protected void renderBoards(@NotNull SmartBoard[] boards) {
    // Go through each board that is listed as dirty and render them to their MapImage
    // caches.
    for (SmartBoard board : boards) {
      // Check the graphics and update it if necessary.
      BoardGraphics graphics = board.getGraphics();
      if (graphics.canUpdate()) {
        graphics.update();
      }
      if (board.isDirty()) {
        try {
          // Render the board.
          board.render();
          // Let the thread know that this board is now drawn and doesn't need to be redrawn.
          board.setDirty(false);
          // Go through the rendered mini-maps and send them out to players nearby.
          board.dispatch();
        } catch (Exception e) {
          System.err.println(
              "The SmartBoard "
                  + board.getClass().getSimpleName()
                  + "(ID: "
                  + board.getId()
                  + ") has encountered an uncaught exception and has been disabled."
                  + "(Render)");
          e.printStackTrace(System.out);
          synchronized (listFlaggedBoards) {
            // Add the board to be removed.
            listFlaggedBoards.add(board);
          }
        }
      }
    }
  }

  /**
   * @param player The Player to test.
   * @param maxDistance The maximum distance in blocks from the player to search for Boards.
   * @return Returns a BoardSearch result if the Player is looking at a Board within the maximum
   *     distance given. If the player is not looking at a board, null is returned.
   */
  @Nullable
  public SmartBoardSearch getBoardAndUVLookedAt(@NotNull Player player, int maxDistance) {
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

  /**
   * @param entity The entity to test.
   * @param maxDistance The maximum distance from the player to look. (In blocks)
   * @return Returns all boards the player is looking at, within the given distance.
   */
  @Nullable
  public List<SmartBoard> getBoardsLookedAt(@NotNull LivingEntity entity, int maxDistance) {
    List<SmartBoard> boards = new ArrayList<>();
    synchronized (lockBoards) {
      for (int index = 0; index < boardsToLoop.length; index++) {
        if (boardsToLoop[index].isLookingAt(entity, maxDistance)) {
          boards.add(boardsToLoop[index]);
        }
      }
    }
    return boards;
  }

  /**
   * Adds a board to the cluster. This will register all Map IDs and cache information for the
   * board.
   *
   * @param board The board to add.
   */
  public void addBoard(@NotNull SmartBoard board) {
    if (!isRegistered(board)) {
      if (SmartBoards.DEBUG) {
        System.out.println("SmartBoards: Adding board to thread: " + board);
      }
      synchronized (mapBoards) {
        // Place the board in the registrar map.
        mapBoards.put(board.getId(), board);
      }
      synchronized (lockBoards) {
        // Add the board to the async loop array to be updated.
        SmartBoard[] boardsToLoopNew = new SmartBoard[boardsToLoop.length + 1];
        System.arraycopy(boardsToLoop, 0, boardsToLoopNew, 0, boardsToLoop.length);
        boardsToLoopNew[boardsToLoop.length] = board;
        boardsToLoop = boardsToLoopNew;
        size = boardsToLoop.length;
      }
      // Add any registered map IDs to be interpreted by the packet checker.
      updateBoardIds(board);
    }
  }

  /**
   * Removes a board from the cluster. This will remove all associated map IDs and cache information
   * from the cluster.
   *
   * @param board The board to remove.
   */
  public void removeBoard(@NotNull SmartBoard board) {
    if (isRegistered(board)) {
      synchronized (mapBoards) {
        // Remove the board from the registrar map.
        mapBoards.remove(board.getId());
      }
      if (board.isAsync()) {
        synchronized (lockBoards) {
          // Remove the board from the loop array.
          SmartBoard[] boardsToLoopNew = new SmartBoard[boardsToLoop.length - 1];
          int index = 0;
          for (SmartBoard boardNext : boardsToLoop) {
            if (!boardNext.equals(board)) {
              boardsToLoopNew[index++] = boardNext;
            }
          }
          boardsToLoop = boardsToLoopNew;
          size = boardsToLoop.length;
        }
      }
      // Remove any registered map IDs from the board.
      removeBoardIds(board);
    }
  }

  /**
   * Updates the map IDs used by the board in the cluster's cache.
   *
   * <p>NOTE: All Map IDs for the board are removed before processing the board.
   *
   * @param board The board to process.
   */
  protected void updateBoardIds(@NotNull SmartBoard board) {
    // Remove the board's previous ids.
    synchronized (mapBoardIds) {
      for (int key : new HashSet<>(mapBoardIds.keySet())) {
        SmartBoard boardNext = mapBoardIds.get(key);
        if (boardNext == null || boardNext.equals(board)) {
          mapBoardIds.remove(key);
        }
      }
    }
    // Go through and add all ids to the map.
    int[] ids = board.getMapIds();
    if (ids != null && ids.length > 0) {
      // Check the ID map.
      synchronized (mapBoardIds) {
        for (int id : board.getMapIds()) {
          mapBoardIds.put(id, board);
        }
      }
    }
  }

  /**
   * Removes all cached map IDs associated with the board given.
   *
   * @param board The board to process.
   */
  protected void removeBoardIds(@NotNull SmartBoard board) {
    int[] ids = board.getMapIds();
    if (ids != null && ids.length > 0) {
      // Check the ID map.
      synchronized (mapBoardIds) {
        for (int id : board.getMapIds()) {
          mapBoardIds.remove(id);
        }
      }
    }
  }

  /** Removes any flagged boards during the update loop. */
  private void removeFlaggedBoards() {
    synchronized (listFlaggedBoards) {
      // Remove any boards added to the removal list.
      if (!listFlaggedBoards.isEmpty()) {
        for (SmartBoard board : listFlaggedBoards) {
          removeBoard(board);
        }
        listFlaggedBoards.clear();
      }
    }
  }

  /** @return Returns the compiled array of registered SmartBoards for the cluster. */
  @NotNull
  private SmartBoard[] getBoards() {
    return this.boardsToLoop;
  }

  /**
   * @param board The board to test.
   * @return Returns true if the board is registered in this cluster.
   */
  public boolean isRegistered(@NotNull SmartBoard board) {
    return mapBoards.containsKey(board.getId());
  }

  /** @return Returns the amount of boards registered in the cluster. */
  public int size() {
    return this.size;
  }

  /** @return Returns true if the cluster's update loop is running. */
  public boolean isRunning() {
    return this.running;
  }

  /** Starts the cluster update loop. */
  public abstract void onStart();

  /** Stops the cluster's update loop. */
  public abstract void onStop();
}
