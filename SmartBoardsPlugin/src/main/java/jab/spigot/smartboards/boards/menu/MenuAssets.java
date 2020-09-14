package jab.spigot.smartboards.boards.menu;

import jab.smartboards.commons.utils.MapImage;
import jab.spigot.smartboards.PluginSmartBoards;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MenuAssets {

  private static File guiFolder;

  private static Map<String, MapImage> mapAssets;
  private static boolean loaded;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void load(boolean force) {
    if (loaded && !force) {
      return;
    }

    JavaPlugin plugin = PluginSmartBoards.instance;
    File dir = plugin.getDataFolder();
    if (!dir.exists()) {
      dir.mkdirs();
    }

    File dirImages = new File(dir, "images");
    if (!dirImages.exists()) {
      dirImages.mkdirs();
    }

    File dirGui = new File(dirImages, "gui");
    if (!dirGui.exists()) {
      dirGui.mkdirs();
    }

    File dirGuiButtons = new File(dirGui, "buttons");
    if (!dirGuiButtons.exists()) {
      dirGuiButtons.mkdirs();
    }

    File dirGuiFrames = new File(dirGui, "frames");
    if (!dirGuiFrames.exists()) {
      dirGuiFrames.mkdirs();
    }

    File dirGuiSlots = new File(dirGui, "slots");
    if (!dirGuiSlots.exists()) {
      dirGuiSlots.mkdirs();
    }

    guiFolder = dirGui;

    mapAssets = new HashMap<>();
    String s = File.separator;
    String rootGui = "images" + s + "gui";
    try {
      // Arrow Buttons
      loadAsset(rootGui + s + "buttons" + s + "arrow_left.png");
      loadAsset(rootGui + s + "buttons" + s + "arrow_right.png");
      loadAsset(rootGui + s + "buttons" + s + "arrow_left_hover.png");
      loadAsset(rootGui + s + "buttons" + s + "arrow_right_hover.png");
      // Lock button
      loadAsset(rootGui + s + "buttons" + s + "button_lock_locked.png");
      loadAsset(rootGui + s + "buttons" + s + "button_lock_unlocked.png");
      loadAsset(rootGui + s + "buttons" + s + "button_lock_locked_hover.png");
      loadAsset(rootGui + s + "buttons" + s + "button_lock_unlocked_hover.png");
      loadAsset(rootGui + s + "buttons" + s + "button_lock_locked_disabled.png");
      loadAsset(rootGui + s + "buttons" + s + "button_lock_unlocked_disabled.png");
      // Misc Buttons
      loadAsset(rootGui + s + "buttons" + s + "button.png");
      loadAsset(rootGui + s + "buttons" + s + "button_book.png");
      loadAsset(rootGui + s + "buttons" + s + "button_book_hover.png");
      loadAsset(rootGui + s + "buttons" + s + "mail.png");
      loadAsset(rootGui + s + "buttons" + s + "mail_hover.png");
      // Slots
      loadAsset(rootGui + s + "slots" + s + "menu2_slot_grey1.png");
      loadAsset(rootGui + s + "slots" + s + "menu2_slot_grey2.png");
      loadAsset(rootGui + s + "slots" + s + "menu2_slot_red1.png");
      loadAsset(rootGui + s + "slots" + s + "menu2_slot_red2.png");
      loadAsset(rootGui + s + "slots" + s + "toolbar.png");
      loadAsset(rootGui + s + "slots" + s + "toolbar_extra_1.png");
      loadAsset(rootGui + s + "slots" + s + "toolbar_extra_2.png");
      // Misc
      loadAsset(rootGui + s + "barrier.png");
      loadAsset(rootGui + s + "menu_textbar.png");
      loadAsset(rootGui + s + "menu_textbar_disabled.png");
      loadAsset(rootGui + s + "menu_textbar_hover.png");
      loadAsset(rootGui + s + "recipe_tab.png");
      loaded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads a image file as a MapImage asset.
   *
   * @param path The path to the file. (From the plugin directory)
   * @throws IOException Thrown when the file doesn't exist or cannot load.
   */
  public static void loadAsset(@NotNull String path) throws IOException {
    File file = new File(PluginSmartBoards.instance.getDataFolder(), path);
    String name = file.getName().split("\\.")[0].toLowerCase().trim();
    if (!file.exists()) {
      PluginSmartBoards.instance.saveResource(path, false);
    }
    System.out.println(file);
    if (file.exists()) {
      mapAssets.put(name, new MapImage(ImageIO.read(file)));
    }
  }

  /**
   * @param name The name of the asset.
   * @return Returns the asset with the name given. If no asset is stored with the name given, null
   *     is returned.
   * @throws NullPointerException Thrown when the name given is null.
   * @throws IllegalArgumentException Thrown when the name given is empty.
   */
  public static MapImage get(@NotNull String name) {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("The name given is empty.");
    }
    return mapAssets.get(name.toLowerCase().trim());
  }
}
