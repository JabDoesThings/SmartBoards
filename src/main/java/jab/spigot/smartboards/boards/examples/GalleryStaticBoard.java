package jab.spigot.smartboards.boards.examples;

import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.boards.SyncSmartBoard;
import jab.spigot.smartboards.boards.graphics.*;
import jab.spigot.smartboards.enums.AnchorFlag;
import jab.spigot.smartboards.enums.ScaleFlag;
import jab.spigot.smartboards.utils.BoardProfile;
import jab.spigot.smartboards.utils.ImageUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GalleryStaticBoard extends SyncSmartBoard {

  /**
   * Main constructor.
   *
   * @param profile The BoardProfile to define the board's dimensions and location.
   */
  public GalleryStaticBoard(@NotNull BoardProfile profile) {
    super(profile);
    load();
  }

  private void load() {
    JavaPlugin plugin = PluginSmartBoards.instance;
    File directory = new File(plugin.getDataFolder(), "images");
    BufferedImage[] images = new BufferedImage[3];
    try {
      images[0] = ImageIO.read(new File(directory, "image_0.jpg"));
      images[1] = ImageIO.read(new File(directory, "image_1.jpg"));
      images[2] = ImageIO.read(new File(directory, "image_9.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    BufferedImage[] formattedImages = new BufferedImage[images.length];
    for (int index = 0; index < images.length; index++) {
      formattedImages[index] =
          ImageUtils.formatImage(
              images[index],
              getWidth(),
              getHeight(),
              Color.BLACK,
              AnchorFlag.CENTER,
              ScaleFlag.FIT_Y_NEAREST);
    }

    BufferedImage[] transition1 =
        TransitionEffects.createTransition(
            formattedImages[0],
            formattedImages[1],
            20,
            TimeEffect.EASE_IN,
            ScaleEffect.SHRINK,
            AnimationEffect.NONE);

    BufferedImage[] transition2 =
        TransitionEffects.createTransition(
            formattedImages[1],
            formattedImages[2],
            20,
            TimeEffect.EASE_IN,
            ScaleEffect.GROW,
            AnimationEffect.FALL_DOWN);

    BufferedImage[] transition3 =
        TransitionEffects.createTransition(
            formattedImages[2],
            formattedImages[0],
            20,
            TimeEffect.LINEAR,
            ScaleEffect.NONE,
            AnimationEffect.SPIN_TOP_LEFT_CW);

    SequencedBoardGraphics graphics = new SequencedBoardGraphics(getWidth(), getHeight());

    int ticksFrame = 100; /* 5 Seconds */
    int ticksTransition = 1; /* 1/20 Seconds */
    graphics.addImageAsFrame(formattedImages[0], ticksFrame);
    graphics.addImagesAsFrames(transition1, ticksTransition);
    graphics.addImageAsFrame(formattedImages[1], ticksFrame);
    graphics.addImagesAsFrames(transition2, ticksTransition);
    graphics.addImageAsFrame(formattedImages[2], ticksFrame);
    graphics.addImagesAsFrames(transition3, ticksTransition);

    setGraphics(graphics);
  }
}
