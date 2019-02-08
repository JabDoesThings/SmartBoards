package jab.spigot.smartboards.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import jab.smartboards.commons.SmartBoardThread;
import jab.smartboards.commons.board.SmartBoard;
import jab.smartboards.commons.board.BoardClick;
import jab.smartboards.commons.events.SmartBoardClickEvent;
import jab.smartboards.commons.board.SmartBoardSearch;
import net.minecraft.server.v1_13_R2.PacketPlayInBlockDig;
import net.minecraft.server.v1_13_R2.PacketPlayInBlockPlace;
import net.minecraft.server.v1_13_R2.PacketPlayInUseEntity;
import net.minecraft.server.v1_13_R2.PacketPlayInUseItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class SmartBoardsClickAdapter extends PacketAdapter {

  /**
   * Main constructor.
   *
   * @param plugin The Plug-in instance.
   */
  public SmartBoardsClickAdapter(@NotNull JavaPlugin plugin) {
    super(
        plugin,
        ListenerPriority.HIGHEST,
        PacketType.Play.Client.BLOCK_DIG,
        PacketType.Play.Client.BLOCK_PLACE,
        PacketType.Play.Client.USE_ENTITY,
        PacketType.Play.Client.USE_ITEM);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    PacketType packetType = event.getPacketType();
    PacketContainer container = event.getPacket();
    Player player = event.getPlayer();
    if (packetType == PacketType.Play.Client.BLOCK_DIG) {
      System.out.println("BLOCK_DIG");
      PacketPlayInBlockDig packet = (PacketPlayInBlockDig) container.getHandle();
      event.setCancelled(handleBlockDig(player, packet));
    } else if (packetType == PacketType.Play.Client.BLOCK_PLACE) {
      System.out.println("BLOCK_PLACE");
      PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) container.getHandle();
      event.setCancelled(handleBlockPlace(player, packet));
    } else if (packetType == PacketType.Play.Client.USE_ENTITY) {
      System.out.println("USE_ENTITY");
      PacketPlayInUseEntity packet = (PacketPlayInUseEntity) container.getHandle();
      event.setCancelled(handleUseEntity(player, packet));
    } else if (packetType == PacketType.Play.Client.USE_ITEM) {
      System.out.println("USE_ITEM");
      PacketPlayInUseItem packet = (PacketPlayInUseItem) container.getHandle();
      event.setCancelled(handleUseItem(player, packet));
    }
  }

  private boolean handleBlockDig(@NotNull Player player, @NotNull PacketPlayInBlockDig packet) {
    // Grab all the boards the Player is looking at.
    SmartBoardSearch search = SmartBoardThread.instance.getBoardAndUVLookedAt(player, 12);
    // Make sure the Player is looking at a Board to continue.
    if (search == null) {
      return false;
    }
    BoardClick click = BoardClick.LEFT;
    BoardClick.Type type = BoardClick.Type.CLICK;
    // Grab the dig-type. This is important.
    PacketPlayInBlockDig.EnumPlayerDigType digType = packet.d();
    if (digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
      type = BoardClick.Type.DOWN;
    } else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
      type = BoardClick.Type.UP;
    } else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
      type = BoardClick.Type.UP;
    } else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
      type = BoardClick.Type.UP;
    }
    SmartBoard board = search.getBoard();
    double[] uv = search.getUV();
    // Pass the event to the Bukkit engine to be interpreted first.
    SmartBoardClickEvent eventToPass =
        new SmartBoardClickEvent(board, player, true, click, type, uv);
    Bukkit.getServer().getPluginManager().callEvent(eventToPass);
    // If the event is cancelled, nothing more is needed to be done.
    if (eventToPass.isCancelled()) return true;
    // Let the board react to the event last.
    board.onClick(eventToPass);
    return true;
  }

  private boolean handleBlockPlace(@NotNull Player player, @NotNull PacketPlayInBlockPlace packet) {
    // Grab all the boards the Player is looking at.
    SmartBoardSearch search = SmartBoardThread.instance.getBoardAndUVLookedAt(player, 12);
    // Make sure the Player is looking at a Board to continue.
    if (search == null) {
      return false;
    }
    BoardClick click = BoardClick.RIGHT;
    BoardClick.Type type = BoardClick.Type.CLICK;
    // Grab the dig-type. This is important.
    SmartBoard board = search.getBoard();
    double[] uv = search.getUV();
    // Pass the event to the Bukkit engine to be interpreted first.
    SmartBoardClickEvent eventToPass =
        new SmartBoardClickEvent(board, player, true, click, type, uv);
    Bukkit.getServer().getPluginManager().callEvent(eventToPass);
    // If the event is cancelled, nothing more is needed to be done.
    if (eventToPass.isCancelled()) return true;
    // Let the board react to the event last.
    board.onClick(eventToPass);
    return true;
  }

  private boolean handleUseEntity(@NotNull Player player, @NotNull PacketPlayInUseEntity packet) {
    PacketPlayInUseEntity.EnumEntityUseAction action = packet.b();
    // This is a duplicate packet sent. Ignore it.
    if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
      System.out.println("RETURN: INTERACT");
      return false;
    }
    // Grab all the boards the Player is looking at.
    SmartBoardSearch search = SmartBoardThread.instance.getBoardAndUVLookedAt(player, 12);
    // Make sure the Player is looking at a Board to continue.
    if (search == null) {
      System.out.println("RETURN: Search is null.");
      return false;
    }
    BoardClick.Type type = BoardClick.Type.CLICK;
    BoardClick click = BoardClick.RIGHT;
    if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
      click = BoardClick.LEFT;
    }
    SmartBoard board = search.getBoard();
    double[] uv = search.getUV();
    // Pass the event to the Bukkit engine to be interpreted first.
    SmartBoardClickEvent eventToPass =
        new SmartBoardClickEvent(board, player, true, click, type, uv);
    Bukkit.getServer().getPluginManager().callEvent(eventToPass);
    // If the event is cancelled, nothing more is needed to be done.
    if (eventToPass.isCancelled()) return true;
    // Let the board react to the event last.
    board.onClick(eventToPass);
    return true;
  }

  private boolean handleUseItem(@NotNull Player player, @NotNull PacketPlayInUseItem packet) {
    // Grab all the boards the Player is looking at.
    SmartBoardSearch search = SmartBoardThread.instance.getBoardAndUVLookedAt(player, 12);
    // Make sure the Player is looking at a Board to continue.
    if (search == null) {
      return false;
    }
    BoardClick click = BoardClick.RIGHT;
    BoardClick.Type type = BoardClick.Type.CLICK;
    SmartBoard board = search.getBoard();
    double[] uv = search.getUV();
    // Pass the event to the Bukkit engine to be interpreted first.
    SmartBoardClickEvent eventToPass =
        new SmartBoardClickEvent(board, player, true, click, type, uv);
    Bukkit.getServer().getPluginManager().callEvent(eventToPass);
    // If the event is cancelled, nothing more is needed to be done.
    if (eventToPass.isCancelled()) return true;
    // Let the board react to the event last.
    board.onClick(eventToPass);
    return true;
  }
}
