package jab.spigot.smartboards.test;

import jab.smartboards.commons.utils.MapImage;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestFormatConversion {

  //  @Test
  public void test() {
    BufferedImage source = createSource();
    try {
      ImageIO.write(source, "PNG", new File("mapImage_source.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    MapImage mapImage = new MapImage(source);

    BufferedImage dest = mapImage.toBufferedImage();
    try {
      ImageIO.write(dest, "PNG", new File("mapImage_dest.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedImage createSource() {
    BufferedImage imageSource = new BufferedImage(768, 512, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = (Graphics2D) imageSource.getGraphics();
    g.setColor(Color.RED);
    g.fillRect(20, 20, 400, 200);
    g.setColor(Color.BLUE);
    g.fillRect(imageSource.getWidth() - 420, imageSource.getHeight() - 220, 400, 200);
    g.setColor(new Color(0, 255, 0, 127));
    g.fillRect((imageSource.getWidth() / 2) - 200, (imageSource.getHeight() / 2) - 100, 400, 200);
    g.setColor(Color.BLACK);
    g.fillRect(0, imageSource.getHeight() - 144, 128, 128);
    g.setColor(Color.WHITE);
    g.fillRect(imageSource.getWidth() - 144, 0, 128, 128);
    g.dispose();
    return imageSource;
  }
}
