package jab.spigot.smartboards.test;

import com.sun.imageio.plugins.gif.GIFImageWriter;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import jab.spigot.smartboards.boards.graphics.AnimationEffect;
import jab.spigot.smartboards.boards.graphics.ScaleEffect;
import jab.spigot.smartboards.boards.graphics.TimeEffect;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
import javafx.scene.effect.PerspectiveTransform;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestTransition {

  BufferedImage image0;
  BufferedImage image1;

  BufferedImage image2;
  BufferedImage image3;

  @Test
  public void test1() {
    loadImages();
    try {
      int frameCount = 10;
      BufferedImage[] frames = TransitionEffects.createTransition(image0, image1, frameCount);

      ImageOutputStream output = new FileImageOutputStream(new File("test_1.gif"));
      GifSequenceWriter writer =
          new GifSequenceWriter(output, BufferedImage.TYPE_4BYTE_ABGR, 1, true);

      for (int index = 0; index < frameCount; index++) {
        writer.writeToSequence(frames[index]);
        //        ImageIO.write(frames[index], "PNG", new File("test1_" + index + ".png"));
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test2() {
    loadImages();
    try {
      int frameCount = 20;
      BufferedImage[] frames =
          TransitionEffects.createTransition(
              image2,
              image3,
              frameCount,
              TimeEffect.EASE_IN_OUT_SMOOTH,
              ScaleEffect.NONE,
              AnimationEffect.SLIDE_BOTTOM_RIGHT);

      ImageOutputStream output = new FileImageOutputStream(new File("test_2.gif"));
      GifSequenceWriter writer =
          new GifSequenceWriter(output, BufferedImage.TYPE_4BYTE_ABGR, 1, true);

      for (int index = 0; index < frameCount; index++) {
        writer.writeToSequence(image2);
      }

      for (int index = 0; index < frameCount; index++) {
        writer.writeToSequence(frames[index]);
        //        ImageIO.write(frames[index], "PNG", new File("test1_" + index + ".png"));
      }

      for (int index = 0; index < frameCount; index++) {
        writer.writeToSequence(image3);
      }

      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test3() {
    loadImages();

    Graphics2D g = (Graphics2D) image2.getGraphics();
    //    g.translate(0, -image2.getHeight());
    g.rotate(Math.toRadians(45), image2.getWidth() / 2, image2.getHeight() / 2);
    //    g.translate(0, image2.getHeight());
    g.dispose();

    try {
      ImageIO.write(image2, "PNG", new File("test_3.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadImages() {
    try {
      image0 = ImageIO.read(new File("image_0.png"));
      image1 = ImageIO.read(new File("image_1.png"));
      image2 = ImageIO.read(new File("image_2.jpg"));
      image3 = ImageIO.read(new File("image_3.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
