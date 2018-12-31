package jab.spigot.smartboards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class PluginSmartBoards extends JavaPlugin {

  public static PluginSmartBoards instance;
  public static ProtocolManager protocolManager;
  public static int NEXT_ID = 0;
  private static int THREAD_COUNT = 1;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    protocolManager = ProtocolLibrary.getProtocolManager();
    // Create and start the main thread for processing smartboards.
    SmartBoardThread.instance = new SmartBoardThread();
    CompilerManager.instance = new CompilerManager(THREAD_COUNT);
    SmartBoardThread.instance.start();
    CompilerManager.instance.start();
  }

  @Override
  public void onDisable() {
    SmartBoardThread.instance.stop();
    CompilerManager.instance.stop();
  }
}
