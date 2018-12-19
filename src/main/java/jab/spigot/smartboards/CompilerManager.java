package jab.spigot.smartboards;

import jab.spigot.smartboards.exceptions.AlreadyCompiledException;
import jab.spigot.smartboards.utils.CompilerTask;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class CompilerManager implements Runnable {

  private static final long SLEEP_TIME = 250L;

  public static CompilerManager instance;

  private final Object lockTasks = new Object();
  private final int threadCount;

  private Stack<CompilerTask> tasks;
  private Thread[] threads;
  private volatile boolean stopped;

  /**
   * Main constructor.
   *
   * @param threadCount The amount of threads to run to execute tasks.
   */
  public CompilerManager(int threadCount) {
    this.threadCount = threadCount;
    this.threads = new Thread[threadCount];
  }

  @Override
  public void run() {
    while (!stopped) {
      CompilerTask task = getNextTask();
      if (task != null) task.compile();
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
  }

  public void stop() {
    stopped = true;
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
}
