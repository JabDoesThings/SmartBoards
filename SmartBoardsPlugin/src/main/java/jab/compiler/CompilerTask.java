package jab.compiler;

import jab.smartboards.commons.throwables.AlreadyRegisteredException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public abstract class CompilerTask<E> {

  private final List<Consumer<CompilerTask>> tasks;

  private volatile boolean complete;
  private boolean runTasksAsync;

  private E result;

  public CompilerTask() {
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
  public void compile(@Nullable Plugin plugin) {
    result = onCompile();
    complete = true;
    // If the plugin is not provided, then run the tasks immediately.
    if (plugin != null && !runTasksAsync) {
      // Execute any tasks in the main thread.
      (new BukkitRunnable() {
            @Override
            public void run() {
              executeTasks();
            }
          })
          .runTaskLater(plugin, 1L);
    } else {
      // Execute the tasks immediately.
      executeTasks();
    }
  }

  /** Executes all tasks registered to fire after the main task is executed. */
  private void executeTasks() {
    synchronized (tasks) {
      for (Consumer<CompilerTask> task : tasks) {
        task.accept(this);
      }
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
  public E getResult() {
    if (!this.complete) {
      throw new IllegalStateException("The compiler task is not complete!");
    }
    return this.result;
  }

  public abstract E onCompile();
}
