package jab.smartboards.commons;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * This implementation of the SmartBoardCluster handles synchronized update loops for SmartBoards
 * that require their updates to be processed on MineCraft's main thread.
 *
 * @author Josh
 */
public class BukkitSmartBoardCluster extends SmartBoardCluster {

  /** The default sleep time used for synchronized clusters. (1 tick = 1/20th of a second) */
  private static final int DEFAULT_PERIOD_TICKS = 1;

  private final Plugin plugin;
  private final int periodTicks;

  /** The Bukkit-thread runnable instance to update the cluster. */
  private BukkitRunnable runnable;

  /**
   * Full constructor.
   *
   * @param plugin The plugin to associate with the internal BukkitRunnable used to update the
   *     cluster.
   * @param periodTicks The amount of MineCraft update ticks to occur before updating the cluster.
   */
  public BukkitSmartBoardCluster(@NotNull Plugin plugin, int periodTicks) {
    this.plugin = plugin;
    this.periodTicks = periodTicks;
  }

  /**
   * Basic constructor.
   *
   * <p>NOTE: This constructor uses <code>DEFAULT_PERIOD_TICKS</code> for the period of ticks to
   * wait until updating the cluster.
   *
   * @param plugin The plugin to associate with the internal BukkitRunnable used to update the *
   *     cluster.
   */
  public BukkitSmartBoardCluster(@NotNull Plugin plugin) {
    this(plugin, DEFAULT_PERIOD_TICKS);
  }

  @Override
  public void onStart() {
    runnable =
        new BukkitRunnable() {
          @Override
          public void run() {
            BukkitSmartBoardCluster.this.run();
          }
        };
    runnable.runTaskTimer(plugin, 0L, periodTicks);
  }

  @Override
  public void onStop() {
    runnable.cancel();
    runnable = null;
  }

  /** @return Returns the Bukkit Plugin associated with the cluster. */
  @NotNull
  public Plugin getPlugin() {
    return this.plugin;
  }

  /** @return Returns the amount of MineCraft looped ticks between updates for the cluster. */
  public int getPeriodTicks() {
    return this.periodTicks;
  }
}
