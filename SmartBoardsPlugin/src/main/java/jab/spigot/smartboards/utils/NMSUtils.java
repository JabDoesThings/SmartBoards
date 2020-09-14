package jab.spigot.smartboards.utils;

import net.minecraft.server.v1_16_R2.NBTTagCompound;

/**
 * TODO: Document.
 *
 * @author Josh
 */
@SuppressWarnings("unused")
public class NMSUtils {

  /**
   * Sets a Native ItemStack's index for 1.13's tag compound API.
   *
   * <p>NOTE: The tag's name is 'map'.
   *
   * @param itemStack The native ItemStack to modify.
   * @param mapIndex The index to set.
   */
  public static void setMapId(net.minecraft.server.v1_16_R2.ItemStack itemStack, int mapIndex) {
    NBTTagCompound tag = itemStack.getTag();
    if (tag == null) {
      tag = new NBTTagCompound();
    }
    tag.setInt("map", mapIndex);
    itemStack.setTag(tag);
    // 1.13.2
    //    itemStack.convertStack();
  }
}
