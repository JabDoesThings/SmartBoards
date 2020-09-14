package jab.compiler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class CompilerManager implements Runnable {

  private static final long SLEEP_TIME = 250L;
  public static final boolean DEBUG = false;

  public static CompilerManager instance;

  private final Object lockTasks = new Object();
  private final int threadCount;

  private Plugin plugin;
  private Stack<CompilerTask> tasks;
  private Thread[] threads;
  private volatile boolean stopped;

  /**
   * Main constructor.
   *
   * @param threadCount The amount of threads to run to execute tasks.
   */
  public CompilerManager(Plugin plugin, int threadCount) {
    this.plugin = plugin;
    this.threadCount = threadCount;
    this.threads = new Thread[threadCount];
    this.tasks = new Stack<>();
  }

  @Override
  public void run() {
    while (!stopped) {
      CompilerTask task = getNextTask();
      if (task != null) task.compile(plugin);
      try {
        Thread.sleep(SLEEP_TIME);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void start() {
    stopped = false;
    for (int index = 0; index < threadCount; index++) {
      if (threads[index] == null) {
        threads[index] = new Thread(this, "SmartBoard Compiler Thread #" + index);
      }
      if (!threads[index].isAlive() || threads[index].isInterrupted()) {
        threads[index].start();
      }
    }
    if (DEBUG) {
      System.out.println("CompilerManager: Thread Started.");
    }
  }

  public void stop() {
    stopped = true;
    if (DEBUG) {
      System.out.println("CompilerManager: Thread Stopped.");
    }
  }

  public CompilerTask getNextTask() {
    synchronized (lockTasks) {
      if (tasks.empty()) {
        return null;
      }
      return tasks.pop();
    }
  }

  /**
   * @param task
   * @throws IllegalStateException Thrown if the task to register is already registered.
   */
  public void register(@NotNull CompilerTask task) {
    if (tasks.contains(task)) {
      throw new IllegalStateException("Compiler Task is already registered: " + task);
    }
    if (task.isComplete()) {
      throw new AlreadyCompiledException(task);
    }
  }

  public Plugin getPlugin() {
    return this.plugin;
  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
  }
}
