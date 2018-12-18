package jab.spigot.smartboards;

import java.util.HashMap;
import java.util.Map;

public class BoardThread extends Thread {

  private final Map<Integer, SmartBoard> mapIds;

  private final List<PacketPlayOutMap> listApprovedPackets;

  private volatile boolean stopped;

  /** Main constructor. */
  BoardThread() {
    this.mapIds = new HashMap<>();
  }

  @Override
  public void run() {}

  public void enable() {
    start();
  }

  public void disable() {
    this.stopped = true;
  }
}
