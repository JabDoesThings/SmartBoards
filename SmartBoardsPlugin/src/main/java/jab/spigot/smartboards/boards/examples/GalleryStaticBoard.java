package jab.spigot.smartboards.boards.examples;

import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.board.SyncSmartBoard;
import jab.smartboards.commons.board.graphics.SequencedBoardGraphics;
import jab.smartboards.commons.utils.AnchorFlag;
import jab.smartboards.commons.utils.ImageUtils;
import jab.smartboards.commons.utils.ScaleFlag;
import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.boards.graphics.AnimationEffect;
import jab.spigot.smartboards.boards.graphics.ScaleEffect;
import jab.spigot.smartboards.boards.graphics.TimeEffect;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
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

    File directory = new File(plugin.getDataFolder(), "images" + File.separator + "example");
    BufferedImage[] images = new BufferedImage[2];
    try {
      File image0 = new File(directory, "image_0.png");
      File image1 = new File(directory, "image_1.png");
      if (!image0.exists()) {
        plugin.saveResource("images/example/image_0.png", false);
      }
      if (!image1.exists()) {
        plugin.saveResource("images/example/image_1.png", false);
      }
      images[0] = ImageIO.read(image0);
      images[1] = ImageIO.read(image1);
    } catch (IOException e) {
      e.printStackTrace();
    }

    BufferedImage[] formattedImages = new BufferedImage[images.length];
    for (int index = 0; index < images.length; index++) {
      formattedImages[index] =
          ImageUtils.formatImage(
              images[index],
              getBlockWidth(),
              getBlockHeight(),
              Color.BLACK,
              AnchorFlag.CENTER,
              ScaleFlag.FIT_Y_NEAREST);
    }

    BufferedImage[] transition1 =
        TransitionEffects.createTransition(
            formattedImages[0],
            formattedImages[1],
            4,
            TimeEffect.EASE_IN,
            ScaleEffect.SHRINK,
            AnimationEffect.NONE);

    BufferedImage[] transition2 =
        TransitionEffects.createTransition(
            formattedImages[1],
            formattedImages[0],
            4,
            TimeEffect.EASE_IN,
            ScaleEffect.GROW,
            AnimationEffect.FALL_DOWN);

    SequencedBoardGraphics graphics = new SequencedBoardGraphics(getBlockWidth(), getBlockHeight());

    int ticksFrame = 100; /* 5 Seconds */
    int ticksTransition = 1; /* 1/20 Seconds */
    graphics.addImageAsFrame(formattedImages[0], ticksFrame);
    graphics.addImagesAsFrames(transition1, ticksTransition);
    graphics.addImageAsFrame(formattedImages[1], ticksFrame);
    graphics.addImagesAsFrames(transition2, ticksTransition);
    setGraphics(graphics);
  }

  @Override
  public void create() {
    super.create();
  }
}
