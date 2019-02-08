package jab.spigot.smartboards.boards.menu;

import jab.spigot.smartboards.PluginSmartBoards;
import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MenuAssets {

  private static File assetsFolder;

  private static Map<String, MapImage> mapAssets;
  private static boolean loaded;

  public static void load(boolean force) {
    if (loaded && !force) {
      return;
    }
    assetsFolder =
        new File(PluginSmartBoards.instance.getDataFolder(), "images" + File.separator + "gui");
    mapAssets = new HashMap<>();
    try {
      // Frame 1
      loadAsset("frames" + File.separator + "frame1_l.png");
      loadAsset("frames" + File.separator + "frame1_r.png");
      loadAsset("frames" + File.separator + "frame1_b.png");
      loadAsset("frames" + File.separator + "frame1_bl.png");
      loadAsset("frames" + File.separator + "frame1_br.png");
      loadAsset("frames" + File.separator + "frame1_bl.png");
      loadAsset("frames" + File.separator + "frame1_br.png");
      loadAsset("frames" + File.separator + "frame1_t.png");
      loadAsset("frames" + File.separator + "frame1_tl.png");
      loadAsset("frames" + File.separator + "frame1_tr.png");
      // Frame 2
      loadAsset("frames" + File.separator + "frame2_l.png");
      loadAsset("frames" + File.separator + "frame2_r.png");
      loadAsset("frames" + File.separator + "frame2_b.png");
      loadAsset("frames" + File.separator + "frame2_bl.png");
      loadAsset("frames" + File.separator + "frame2_br.png");
      loadAsset("frames" + File.separator + "frame2_bl.png");
      loadAsset("frames" + File.separator + "frame2_br.png");
      loadAsset("frames" + File.separator + "frame2_t.png");
      loadAsset("frames" + File.separator + "frame2_tl.png");
      loadAsset("frames" + File.separator + "frame2_tr.png");
      // Arrow Buttons
      loadAsset("buttons" + File.separator + "arrow_left.png");
      loadAsset("buttons" + File.separator + "arrow_right.png");
      loadAsset("buttons" + File.separator + "arrow_left_hover.png");
      loadAsset("buttons" + File.separator + "arrow_right_hover.png");
      // Lock button
      loadAsset("buttons" + File.separator + "button_lock_locked.png");
      loadAsset("buttons" + File.separator + "button_lock_unlocked.png");
      loadAsset("buttons" + File.separator + "button_lock_locked_hover.png");
      loadAsset("buttons" + File.separator + "button_lock_unlocked_hover.png");
      loadAsset("buttons" + File.separator + "button_lock_locked_disabled.png");
      loadAsset("buttons" + File.separator + "button_lock_unlocked_disabled.png");
      // Misc Buttons
      loadAsset("buttons" + File.separator + "button.png");
      loadAsset("buttons" + File.separator + "button_book.png");
      loadAsset("buttons" + File.separator + "button_book_hover.png");
      loadAsset("buttons" + File.separator + "mail.png");
      loadAsset("buttons" + File.separator + "mail_hover.png");
      // Slots
      loadAsset("slots" + File.separator + "menu2_slot_grey1.png");
      loadAsset("slots" + File.separator + "menu2_slot_grey2.png");
      loadAsset("slots" + File.separator + "menu2_slot_red1.png");
      loadAsset("slots" + File.separator + "menu2_slot_red2.png");
      loadAsset("slots" + File.separator + "toolbar.png");
      loadAsset("slots" + File.separator + "toolbar_extra1.png");
      loadAsset("slots" + File.separator + "toolbar_extra2.png");
      // Misc
      loadAsset("barrier.png");
      loadAsset("menu_textbar.png");
      loadAsset("menu_textbar_disabled.png");
      loadAsset("menu_textbar_hover.png");
      loadAsset("recipe_tab.png");
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
    File file = new File(assetsFolder, path);
    String name = file.getName().split("\\.")[0].toLowerCase().trim();
    mapAssets.put(name, new MapImage(ImageIO.read(file)));
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
