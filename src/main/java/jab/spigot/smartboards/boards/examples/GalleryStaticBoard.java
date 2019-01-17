package jab.spigot.smartboards.boards.examples;

import jab.spigot.smartboards.PluginSmartBoards;
import jab.spigot.smartboards.boards.SyncSmartBoard;
import jab.spigot.smartboards.boards.graphics.BoardFrame;
import jab.spigot.smartboards.boards.graphics.SequencedBoardGraphics;
import jab.spigot.smartboards.enums.ImageAnchor;
import jab.spigot.smartboards.utils.BoardProfile;
import jab.spigot.smartboards.utils.MapImage;
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
    BufferedImage[] images = new BufferedImage[10];
    try {
      images[0] = ImageIO.read(new File(directory, "image_0.jpg"));
      images[1] = ImageIO.read(new File(directory, "image_1.jpg"));
      images[2] = ImageIO.read(new File(directory, "image_2.jpg"));
      images[3] = ImageIO.read(new File(directory, "image_3.jpg"));
      images[4] = ImageIO.read(new File(directory, "image_4.jpg"));
      images[5] = ImageIO.read(new File(directory, "image_5.jpg"));
      images[6] = ImageIO.read(new File(directory, "image_6.jpg"));
      images[7] = ImageIO.read(new File(directory, "image_7.jpg"));
      images[8] = ImageIO.read(new File(directory, "image_8.jpg"));
      images[9] = ImageIO.read(new File(directory, "image_9.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    SequencedBoardGraphics graphics = new SequencedBoardGraphics(getWidth(), getHeight());

    int width = getWidth();
    int height = getHeight();
    int size = width * height;

    int starting_index = 1000;

    MapImage[] mapImages = new MapImage[10];
    BoardFrame[] frames = new BoardFrame[10];
    for (int index = 0; index < 10; index++) {
      System.out.println("Creating MapImage(" + index + ")...");
      mapImages[index] = new MapImage(width * 128, height * 128, Color.BLACK);
      mapImages[index].draw(images[index], 0, 0, ImageAnchor.CENTER);

      System.out.println("Creating BoardFrame(" + index + ")...");
      frames[index] = new BoardFrame(width, height, starting_index += size);
      frames[index].draw(mapImages[index]);
      graphics.addFrame(frames[index], 100);
    }

    setGraphics(graphics);
  }
}
