package jab.spigot.smartboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sun.istack.internal.NotNull;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginSmartBoards extends JavaPlugin {

  public static PluginSmartBoards instance;
  public static ProtocolManager protocolManager;

  private BoardThread thread;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    protocolManager = ProtocolLibrary.getProtocolManager();
    // Create and start the main thread for processing smartboards.
    thread = new BoardThread();
    thread.enable();
  }

  @Override
  public void onDisable() {
    thread.disable();
  }

  @NotNull
  public BoardThread getThread() {
    return this.thread;
  }
}
