package jab.spigot.smartboards.boards.examples;

import jab.smartboards.commons.board.graphics.SequencedBoardGraphics;
import jab.spigot.smartboards.PluginSmartBoards;
import jab.smartboards.commons.board.SyncSmartBoard;
import jab.spigot.smartboards.boards.graphics.*;
import jab.smartboards.commons.utils.AnchorFlag;
import jab.smartboards.commons.utils.ScaleFlag;
import jab.smartboards.commons.board.BoardProfile;
import jab.smartboards.commons.utils.ImageUtils;
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
    load2();
  }

  //  private void load() {
  //    JavaPlugin plugin = PluginSmartBoards.instance;
  //    File directory = new File(plugin.getDataFolder(), "images");
  //    BufferedImage[] images = new BufferedImage[4];
  //    try {
  //      images[0] = ImageIO.read(new File(directory, "sprite_0.png"));
  //      images[1] = ImageIO.read(new File(directory, "sprite_1.png"));
  //      images[2] = ImageIO.read(new File(directory, "sprite_2.png"));
  //      images[3] = ImageIO.read(new File(directory, "sprite_3.png"));
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //    BufferedImage[] formattedImages = new BufferedImage[images.length];
  //    for (int index = 0; index < images.length; index++) {
  //      formattedImages[index] =
  //          ImageUtils.formatImage(
  //              images[index], getBlockWidth(), getBlockHeight(), null, AnchorFlag.CENTER,
  // ScaleFlag.NONE);
  //    }
  //    SequencedBoardGraphics graphics = new SequencedBoardGraphics(getBlockWidth(),
  // getBlockHeight());
  //    graphics.addImageAsFrame(formattedImages[0], 1);
  //    graphics.addImageAsFrame(formattedImages[1], 1);
  //    graphics.addImageAsFrame(formattedImages[2], 1);
  //    graphics.addImageAsFrame(formattedImages[3], 1);
  //    setGraphics(graphics);
  //  }

  private void load2() {
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
            10,
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

    SequencedBoardGraphics graphics = new SequencedBoardGraphics(getBlockWidth(), getBlockHeight());

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
