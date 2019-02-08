package jab.spigot.smartboards.test;

import jab.smartboards.commons.utils.AnchorFlag;
import jab.smartboards.commons.utils.ImageUtils;
import jab.smartboards.commons.utils.ScaleFlag;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestImageUtils {

  private BufferedImage image0;
  private BufferedImage image1;
  private BufferedImage image2;
  private BufferedImage image3;

  //  @Test
  public void testScaling() {
    loadImages();
    try {
      BufferedImage result =
          ImageUtils.formatImage(
              image3, 16, 9, Color.BLACK, AnchorFlag.CENTER, ScaleFlag.FIT_X_MIPMAP);
      ImageIO.write(result, "PNG", new File("Result_1.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void loadImages() {
    try {
      image0 = ImageIO.read(new File("ratio_0.png"));
      image1 = ImageIO.read(new File("ratio_1.png"));
      image2 = ImageIO.read(new File("image_2.jpg"));
      image3 = ImageIO.read(new File("ratio_3.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
