package jab.spigot.smartboards.utils;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class NMSUtils {
  public static void setMapId(net.minecraft.server.v1_13_R2.ItemStack itemStack, int mapIndex) {
    NBTTagCompound tag = itemStack.getTag();
    if (tag == null) {
      tag = new NBTTagCompound();
    }
    // System.out.println("Setting mapIndex: " + mapIndex);
    tag.setInt("map", mapIndex);
    itemStack.setTag(tag);
    itemStack.convertStack();
  }
}
