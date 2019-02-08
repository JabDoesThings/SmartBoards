package jab.spigot.smartboards.editor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class FrameViewerTab extends JPanel {

  private final Window window;

  private FrameList list;
  private SmartBoardPanel panelBoard;

  public FrameViewerTab(@NotNull Window window) {
    this.window = window;
    GridLayoutManager layout = new GridLayoutManager(2, 1);
    setLayout(layout);
    createComponents();
    setVisible(true);
  }

  private void createComponents() {
    list = new FrameList();
    GridConstraints listConstraints =
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_NORTHWEST,
            GridConstraints.FILL_VERTICAL,
            GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_GROW,
            new Dimension(200, -1),
            new Dimension(200, -1),
            new Dimension(400, -1));
    add(list, listConstraints);
    list.addFrame(new SmartBoardFrame());
    list.addFrame(new SmartBoardFrame());
    panelBoard = new SmartBoardPanel();
    GridConstraints boardConstraints = new GridConstraints();
    add(panelBoard, boardConstraints);
  }

  @NotNull
  public Window getWindow() {
    return this.window;
  }
}
