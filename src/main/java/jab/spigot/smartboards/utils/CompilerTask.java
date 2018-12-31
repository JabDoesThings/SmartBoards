package jab.spigot.smartboards.utils;

import jab.spigot.smartboards.CompilerManager;
import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.exceptions.AlreadyCompiledException;
import jab.spigot.smartboards.exceptions.AlreadyRegisteredException;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class CompilerTask {

  private final List<Consumer<CompilerTask>> tasks;

  private BufferedImage image;
  private volatile boolean complete;
  private boolean runTasksAsync;

  private MapImage result;

  public CompilerTask(@NotNull BufferedImage image) {
    this();
    this.image = image;
  }

  private CompilerTask() {
    this.tasks = new ArrayList<>();
  }

  /**
   * Registers the compiler task to be compiled by a compiler thread.
   *
   * <p>NOTE: Only use this when you are ready to push the task to be compiled.
   */
  public void register() {
    CompilerManager.instance.register(this);
  }

  /**
   * Compiles the compiler task.
   *
   * <p>NOTE: Only compiler threads should execute this method.
   */
  public void compile() {
    result = new MapImage(image);
    // This flag will be used for checks to ensure that no task is compiled twice.
    this.complete = true;
    if (runTasksAsync) {
      // Execute the tasks immediately.
      synchronized (tasks) {
        for (Consumer<CompilerTask> task : tasks) {
          task.accept(this);
        }
      }
    } else {
      // Execute any tasks in the main thread.
      (new BukkitRunnable() {
            @Override
            public void run() {
              synchronized (tasks) {
                for (Consumer<CompilerTask> task : tasks) {
                  task.accept(CompilerTask.this);
                }
              }
            }
          })
          .runTaskLater(PluginSmartBoards.instance, 1L);
    }
  }

  /**
   * Registers a consumer task to the list of tasks to be executed when the compiler task is
   * complete.
   *
   * @param task The task to register.
   * @throws NullPointerException Thrown if the consumer task is null.
   * @throws AlreadyCompiledException Thrown if the compiler task is already compiled.
   * @throws AlreadyRegisteredException Thrown if the consumer task is already registered.
   */
  public void register(@NotNull Consumer<CompilerTask> task) {
    if (complete) {
      throw new AlreadyCompiledException(this);
    }
    if (tasks.contains(task)) {
      throw new AlreadyRegisteredException(this, task);
    }
    synchronized (tasks) {
      tasks.add(task);
    }
  }

  /** @return Returns true if the compiler task is complete and has compiled. */
  public boolean isComplete() {
    return this.complete;
  }

  /**
   * Sets the flag for executing any registered tasks immediately upon completion of the compiler
   * task.
   *
   * @param flag The flag to set.
   */
  public void setTasksAsync(boolean flag) {
    this.runTasksAsync = flag;
  }

  /**
   * @return Returns the compiled result.
   * @throws IllegalStateException Thrown if the method is invoked prior to completion of the task.
   */
  @NotNull
  public MapImage getResult() {
    if (!this.complete) {
      throw new IllegalStateException("The compiler task is not complete!");
    }
    return this.result;
  }
}
