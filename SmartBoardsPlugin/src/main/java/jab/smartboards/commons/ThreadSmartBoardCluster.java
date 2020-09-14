package jab.smartboards.commons;

public class ThreadSmartBoardCluster extends SmartBoardCluster {

  /** The default sleep time for MineCraft is 50 milliseconds. (1/20th of a second) */
  public static long DEFAULT_SLEEP_TIME = 50L;

  private static int CLUSTER_ID = 0;

  private final int id;

  private Thread thread;

  private volatile long sleepTime;

  /**
   * Full constructor.
   *
   * @param sleepTime The time between each update loop that the thread sleeps.
   */
  public ThreadSmartBoardCluster(long sleepTime) {
    this.sleepTime = sleepTime;
    this.id = CLUSTER_ID++;
  }

  /**
   * Basic constructor.
   *
   * <p><code>DEFAULT_SLEEP_TIME</code> will be used for this constructor.
   */
  public ThreadSmartBoardCluster() {
    this(DEFAULT_SLEEP_TIME);
  }

  @Override
  public void onStart() {
    thread = new Thread(this, "SmartBoard Cluster Thread " + getId());
    thread.start();
  }

  @Override
  public void onStop() {
    thread.interrupt();
    thread = null;
  }

  @Override
  public void run() {
    while (isRunning()) {
      try {
        // Run the default update loop as the Runnable implementation in SmartBoardCluster.
        super.run();
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        if (this.isRunning()) {
          stop();
        }
        break;
      } catch (Exception e) {
        System.err.println("Failed to update " + getClass().getSimpleName() + ". (Stopping!)");
        stop();
        e.printStackTrace(System.err);
      }
    }
  }

  /** @return Returns the internal ID for the cluster. */
  public int getId() {
    return this.id;
  }
}
