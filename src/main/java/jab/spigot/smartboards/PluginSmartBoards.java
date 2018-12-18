package jab.spigot.smartboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sun.istack.internal.NotNull;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class PluginSmartBoards extends JavaPlugin {

  public static PluginSmartBoards instance;
  public static ProtocolManager protocolManager;

  private SmartBoardThread thread;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    protocolManager = ProtocolLibrary.getProtocolManager();
    // Create and start the main thread for processing smartboards.
    thread = new SmartBoardThread();
    thread.start();
  }

  @Override
  public void onDisable() {
    thread.stop();
  }

  @NotNull
  public SmartBoardThread getThread() {
    return this.thread;
  }
}
