package jab.spigot.smartboards.utils;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.EnumDirection;
import net.minecraft.server.v1_13_R2.EnumHand;
import net.minecraft.server.v1_13_R2.PacketPlayInBlockDig;
import net.minecraft.server.v1_13_R2.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.v1_13_R2.PacketPlayInBlockPlace;
import net.minecraft.server.v1_13_R2.PacketPlayInUseEntity;
import net.minecraft.server.v1_13_R2.PacketPlayInUseEntity.EnumEntityUseAction;
import net.minecraft.server.v1_13_R2.PacketPlayInUseItem;
import net.minecraft.server.v1_13_R2.PacketPlayOutMap;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_13_R2.Vec3D;
import org.jetbrains.annotations.NotNull;

/**
 * This utility gives additional tools to handle packets being sent to and from the client.
 *
 * @author Jab
 */
public class PacketUtils {

  /** This field stores the raw byte array that is cloned from one given in the constructor. */
  private static Field fieldMapPacketByteArray;

  /** This field stores the index for the mini-map packet. */
  private static Field fieldMapPacketIndex;

  public static int getMapId(@NotNull PacketPlayOutMap packet) {
    int mapId = 0;
    try {
      mapId = fieldMapPacketIndex.getInt(packet);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return mapId;
  }

  /**
   * Sets the PacketPlayOutMap's byte array directly.
   *
   * @param packet The packet to modify.
   * @param data The data array to set for the packet.
   */
  public static void setRawByteArrayForMapPacket(
      @NotNull PacketPlayOutMap packet, @NotNull byte[] data) {
    try {
      fieldMapPacketByteArray.set(packet, data);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static void printBlockDigPacket(@NotNull PacketPlayInBlockDig packet) {
    try {
      Class clazz = PacketPlayInBlockDig.class;
      Field fieldBlockPosition = clazz.getDeclaredField("a");
      Field fieldDirection = clazz.getDeclaredField("b");
      Field fieldDigType = clazz.getDeclaredField("c");
      fieldBlockPosition.setAccessible(true);
      fieldDirection.setAccessible(true);
      fieldDigType.setAccessible(true);
      // Get the field values.
      BlockPosition blockPosition = (BlockPosition) fieldBlockPosition.get(packet);
      EnumDirection direction = (EnumDirection) fieldDirection.get(packet);
      EnumPlayerDigType digType = (EnumPlayerDigType) fieldDigType.get(packet);
      System.out.println("BLOCK_DIG:");
      System.out.println("\tBlockPosition: " + blockPosition.toString());
      System.out.println("\tDirection: " + direction);
      System.out.println("\tUnknown: " + digType);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printBlockPlacePacket(@NotNull PacketPlayInBlockPlace packet) {
    try {
      Class clazz = PacketPlayInBlockPlace.class;
      Field fieldHand = clazz.getDeclaredField("a");
      fieldHand.setAccessible(true);
      EnumHand hand = (EnumHand) fieldHand.get(packet);
      System.out.println("BLOCK_PLACE:");
      System.out.println("\tEnumHand: " + hand);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printUseEntityPacket(@NotNull PacketPlayInUseEntity packet) {
    try {
      Class clazz = PacketPlayInUseEntity.class;
      Field fieldEntityID = clazz.getDeclaredField("a");
      Field fieldUseAction = clazz.getDeclaredField("action");
      Field fieldVec3D = clazz.getDeclaredField("c");
      Field fieldHand = clazz.getDeclaredField("d");
      fieldEntityID.setAccessible(true);
      fieldUseAction.setAccessible(true);
      fieldVec3D.setAccessible(true);
      fieldHand.setAccessible(true);
      int entityId = fieldEntityID.getInt(packet);
      EnumEntityUseAction action = (EnumEntityUseAction) fieldUseAction.get(packet);
      Vec3D vec3D = (Vec3D) fieldVec3D.get(packet);
      EnumHand hand = (EnumHand) fieldHand.get(packet);
      System.out.println("ENTITY_USE: " + entityId);
      System.out.println("\tUseAction: " + action);
      System.out.println("\tVec3D: " + vec3D);
      System.out.println("\tEnumHand: " + hand);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printUseItemPacket(@NotNull PacketPlayInUseItem packet) {
    try {
      Class clazz = PacketPlayInUseItem.class;
      Field fieldBlockPosition = clazz.getDeclaredField("a");
      Field fieldDirection = clazz.getDeclaredField("b");
      Field fieldHand = clazz.getDeclaredField("c");
      Field fieldX = clazz.getDeclaredField("d");
      Field fieldY = clazz.getDeclaredField("e");
      Field fieldZ = clazz.getDeclaredField("f");
      fieldBlockPosition.setAccessible(true);
      fieldDirection.setAccessible(true);
      fieldHand.setAccessible(true);
      fieldX.setAccessible(true);
      fieldY.setAccessible(true);
      fieldZ.setAccessible(true);
      BlockPosition blockPosition = (BlockPosition) fieldBlockPosition.get(packet);
      EnumDirection enumDirection = (EnumDirection) fieldDirection.get(packet);
      EnumHand hand = (EnumHand) fieldHand.get(packet);
      float x = fieldX.getFloat(packet);
      float y = fieldY.getFloat(packet);
      float z = fieldZ.getFloat(packet);
      System.out.println("ITEM_USE: ");
      System.out.println("\tBlockPosition: " + blockPosition);
      System.out.println("\tEnumDirection: " + enumDirection);
      System.out.println("\tHand: " + hand);
      System.out.println("\tX: " + x);
      System.out.println("\tY: " + y);
      System.out.println("\tZ: " + z);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printSpawnEntityPacket(PacketPlayOutSpawnEntity packet) {
    try {
      Class clazz = PacketPlayOutSpawnEntity.class;
      Field fieldEntityId = clazz.getDeclaredField("a");
      Field fieldUniqueId = clazz.getDeclaredField("b");
      Field fieldPosX = clazz.getDeclaredField("c");
      Field fieldPosY = clazz.getDeclaredField("d");
      Field fieldPosZ = clazz.getDeclaredField("e");
      Field fieldVelX = clazz.getDeclaredField("f");
      Field fieldVelY = clazz.getDeclaredField("g");
      Field fieldVelZ = clazz.getDeclaredField("h");
      Field fieldPitch = clazz.getDeclaredField("i");
      Field fieldYaw = clazz.getDeclaredField("j");
      Field fieldHeadYaw = clazz.getDeclaredField("k");
      Field fieldUnknown = clazz.getDeclaredField("l");
      // Set the fields accessible.
      fieldEntityId.setAccessible(true);
      fieldUniqueId.setAccessible(true);
      fieldPosX.setAccessible(true);
      fieldPosY.setAccessible(true);
      fieldPosZ.setAccessible(true);
      fieldVelX.setAccessible(true);
      fieldVelY.setAccessible(true);
      fieldVelZ.setAccessible(true);
      fieldPitch.setAccessible(true);
      fieldYaw.setAccessible(true);
      fieldHeadYaw.setAccessible(true);
      fieldUnknown.setAccessible(true);
      int entityId = fieldEntityId.getInt(packet);
      UUID uniqueId = (UUID) fieldUniqueId.get(packet);
      double posX = fieldPosX.getDouble(packet);
      double posY = fieldPosY.getDouble(packet);
      double posZ = fieldPosZ.getDouble(packet);
      int pitch = fieldVelX.getInt(packet);
      int yaw = fieldVelY.getInt(packet);
      int headYaw = fieldVelZ.getInt(packet);
      int velX = fieldPitch.getInt(packet);
      int velY = fieldYaw.getInt(packet);
      int velZ = fieldHeadYaw.getInt(packet);
      int unknown = fieldUnknown.getInt(packet);
      System.out.println("SPAWN_ENTITY: " + entityId);
      System.out.println("\tUUID: " + uniqueId.toString());
      System.out.println("\tPosX: " + posX);
      System.out.println("\tPosY: " + posY);
      System.out.println("\tPosZ: " + posZ);
      System.out.println("\tVelX: " + pitch);
      System.out.println("\tVelY: " + yaw);
      System.out.println("\tVelZ: " + headYaw);
      System.out.println("\tPitch: " + velX);
      System.out.println("\tYaw: " + velY);
      System.out.println("\tHeadYaw: " + velZ);
      System.out.println("\tUnknown: " + unknown);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static {
    try {
      // The map index field for map packets.
      fieldMapPacketIndex = PacketPlayOutMap.class.getDeclaredField("a");
      fieldMapPacketIndex.setAccessible(true);
      // The byte[] field for map packets. Being able to replace this helps with linking cache data
      // with the packet directly, so when the cache changes, this already changes the map packet.
      fieldMapPacketByteArray = PacketPlayOutMap.class.getDeclaredField("i");
      fieldMapPacketByteArray.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
