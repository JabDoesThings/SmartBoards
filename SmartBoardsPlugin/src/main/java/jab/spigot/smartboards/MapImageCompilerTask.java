package jab.spigot.smartboards;

import jab.compiler.CompilerTask;
import jab.smartboards.commons.utils.MapImage;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

/**
 * TODO: Document.
 *
 * @author Josh
 */
public class MapImageCompilerTask extends CompilerTask<MapImage> {

  private BufferedImage image;

  public MapImageCompilerTask(@NotNull BufferedImage image) {
    super();
    this.image = image;
  }

  @Override
  public MapImage onCompile() {
    return new MapImage(image);
  }
}
