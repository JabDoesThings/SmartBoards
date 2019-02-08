package jab.spigot.smartboards.editor;

import javax.swing.*;
import java.awt.*;

public class FrameList extends JList<SmartBoardFrame> {

  private final DefaultListModel model;

  /** Main constructor. */
  public FrameList() {
    super(new DefaultListModel<>());
    this.model = (DefaultListModel) getModel();
    setCellRenderer(new ListRenderer());
  }

  public void addFrame(SmartBoardFrame frame) {
    model.addElement(frame);
  }

  private class ListRenderer extends JLabel implements ListCellRenderer {

    private final char selectedChar = '\u25ba';
    private final char unselectedChar = '\u2022';

    private Font fontBold;
    private Font font;

    ListRenderer() {
      setIconTextGap(10);
      setOpaque(true);
      font = new Font("default", Font.PLAIN, 14);
      fontBold = new Font("default", Font.BOLD, 14);
    }

    @Override
    public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      String forString = isSelected ? selectedChar + " " : unselectedChar + "  ";
      setText(forString + " Frame " + (index + 1));
      if (isSelected) {
        setFont(fontBold);
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setFont(font);
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      return this;
    }
  }
}
