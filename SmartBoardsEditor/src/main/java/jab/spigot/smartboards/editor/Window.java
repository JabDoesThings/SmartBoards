package jab.spigot.smartboards.editor;

import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

  private GridLayoutManager gridLayoutManager;
  private SmartBoardPanel panelBoard;
  private FrameList list;

  private JTabbedPane pane;

  public Window() {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    //    gridLayoutManager = new GridLayoutManager(2, 1);
    //    setLayout(gridLayoutManager);
    setMinimumSize(new Dimension(800, 600));
    createComponents();

    Dimension size = getSize();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    // Set the window in the center of the screen.
    setLocation((screen.width / 2) - (size.width / 2), (screen.height / 2) - (size.height / 2));

    setVisible(true);
  }

  private void createComponents() {

    pane = new JTabbedPane();
    pane.addTab("Frame Viewer", new FrameViewerTab(this));
    add(pane);
  }
}
