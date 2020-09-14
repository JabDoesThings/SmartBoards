package jab.smartboards.commons;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import jab.smartboards.commons.utils.PacketUtils;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R2.PacketPlayOutMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class SmartBoardsMapAdapter extends PacketAdapter {

  /** The reflection field for the index of the map packet. */
  private static Field fieldPacketIndex;

  /**
   * Main constructor.
   *
   * @param plugin The Plug-in instance.
   */
  SmartBoardsMapAdapter(@NotNull Plugin plugin) {
    super(
        plugin,
        ListenerPriority.HIGHEST,
        PacketType.Play.Server.MAP,
        PacketType.Play.Server.ENTITY_METADATA);
  }

  @Override
  public void onPacketSending(@NotNull PacketEvent event) {
    PacketContainer container = event.getPacket();
    if (container.getHandle() instanceof PacketPlayOutMap) {
      // Grab the packet.
      PacketPlayOutMap packet = (PacketPlayOutMap) event.getPacket().getHandle();
      int index = getMapIndex(packet);
      // If the packet is not authored as our packet, cancel it.
      Map<Integer, PacketPlayOutMap> map = SmartBoards.getRegisteredMapPackets();
      synchronized (SmartBoards.lockPackets) {
        if (map.containsKey(index)) {
          PacketPlayOutMap compare = map.get(index);
          if (!compare.equals(packet)) {
            //            System.out.println("Cancelling Mini-Map packet: " + index);
            event.setCancelled(true);
          } else {
            //            System.out.println("Allowing Mini-Map packet: " + index);
          }
        }
      }
    } else if (container.getHandle() instanceof PacketPlayOutEntityMetadata) {
      PacketPlayOutEntityMetadata packet =
          (PacketPlayOutEntityMetadata) event.getPacket().getHandle();
      int entityId = PacketUtils.getEntityId(packet);
      Map<Integer, PacketPlayOutEntityMetadata> map = SmartBoards.getRegisteredMetaPackets();
      synchronized (SmartBoards.lockPackets) {
        if (map.containsKey(entityId)) {
          PacketPlayOutEntityMetadata compare = map.get(entityId);
          if (!compare.equals(packet)) {
            //            System.out.println("Cancelling Metadata packet.");
            //            PacketUtils.printEntityMetadataPacket(packet);
            event.setCancelled(true);
          } else {
            //            System.out.println("Allowing Metadata packet.");
          }
        }
      }
    }
  }

  /**
   * @param packet The packet to extract the ID.
   * @return Returns the Mini-Map ID for the given packet.
   */
  private static int getMapIndex(@NotNull PacketPlayOutMap packet) {
    int index = 0;
    try {
      index = fieldPacketIndex.getInt(packet);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return index;
  }

  static {
    try {
      // Grab the Map Index field for the map packet to identify conflicting packets for registered
      // Boards in BoardThreads.
      fieldPacketIndex = PacketPlayOutMap.class.getDeclaredField("a");
      fieldPacketIndex.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
