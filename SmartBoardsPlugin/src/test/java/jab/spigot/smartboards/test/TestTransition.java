package jab.spigot.smartboards.test;

import jab.spigot.smartboards.boards.graphics.AnimationEffect;
import jab.spigot.smartboards.boards.graphics.ScaleEffect;
import jab.spigot.smartboards.boards.graphics.TimeEffect;
import jab.spigot.smartboards.boards.graphics.TransitionEffects;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings({"unused", "ConstantConditions"})
public class TestTransition {

  private BufferedImage image0;
  private BufferedImage image1;
  private BufferedImage image2;
  private BufferedImage image3;

  //  @Test
  public void test1() {
    loadImages();
    TransitionEffects.enableJavaFX();
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
      TransitionEffects.disableJavaFX();
      e.printStackTrace();
    }
    TransitionEffects.disableJavaFX();
  }

//  @Test
  public void test2() {
    loadImages();
    TransitionEffects.enableJavaFX();
    try {
      BufferedImage imageStart = image2;
      BufferedImage imageStop = image3;
      int frameCount = 20;
      BufferedImage[] frames =
          TransitionEffects.createTransition(
              imageStart,
              imageStop,
              frameCount,
              TimeEffect.EASE_OUT,
              ScaleEffect.GROW,
              AnimationEffect.FALL_DOWN);
      boolean gif = false;
      GifSequenceWriter writer = null;
      if (gif) {
        ImageOutputStream output = new FileImageOutputStream(new File("test_2.gif"));
        writer = new GifSequenceWriter(output, BufferedImage.TYPE_4BYTE_ABGR, 1, true);
        for (int index = 0; index < frameCount; index++) {
          writer.writeToSequence(imageStart);
        }
      }
      for (int index = 0; index < frameCount; index++) {
        if (gif) {
          writer.writeToSequence(frames[index]);
        } else {
          ImageIO.write(frames[index], "PNG", new File("test2_" + index + ".png"));
        }
      }
      if (gif) {
        for (int index = 0; index < frameCount; index++) {
          writer.writeToSequence(imageStop);
        }
        writer.close();
      }
    } catch (IOException e) {
      TransitionEffects.disableJavaFX();
      e.printStackTrace();
    }
    TransitionEffects.disableJavaFX();
  }

  private void loadImages() {
    try {
      image0 = ImageIO.read(new File("image_0.png"));
      image1 = ImageIO.read(new File("image_1.png"));
      image2 = ImageIO.read(new File("image_2.jpg"));
      image3 = ImageIO.read(new File("image_3.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
