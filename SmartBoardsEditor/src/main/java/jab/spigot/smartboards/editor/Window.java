package jab.spigot.smartboards.editor;

import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

  private static final String VERSION = "1.00";

  private GridLayoutManager gridLayoutManager;
  private SmartBoardPanel panelBoard;
  private FrameList list;

  private JTabbedPane pane;

  private MenuBar menuBar;

  private Menu menuFile;
  private Menu menuEdit;

  public Window() {
    setTitle("SmartBoards Editor V" + VERSION);

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
    pane.addTab("Properties", new BoardPropertiesTab(this));
    pane.addTab("Frame Editor", new FrameEditorTab(this));
    add(pane);
    createMenuBar();
  }

  private void createMenuBar() {
    menuFile = new Menu("File");
    MenuItem menuItemNew = new MenuItem("New");
    MenuItem menuItemOpen = new MenuItem("Open");
    MenuItem menuItemSave = new MenuItem("Save");
    MenuItem menuItemSaveAs = new MenuItem("Save-as");
    MenuItem menuItemClose = new MenuItem("Close");
    MenuItem menuItemExit = new MenuItem("Exit");
    menuFile.add(menuItemNew);
    menuFile.add(menuItemOpen);
    menuFile.add(menuItemSave);
    menuFile.add(menuItemSaveAs);
    menuFile.add(menuItemClose);
    menuFile.addSeparator();
    menuFile.add(menuItemExit);

    menuEdit = new Menu("Edit");
    MenuItem menuItemPreferences = new MenuItem("Preferences");
    menuEdit.add(menuItemPreferences);

    menuBar = new MenuBar();
    // Add all menus.
    menuBar.add(menuFile);
    menuBar.add(menuEdit);
    // Set the menu for the window.
    setMenuBar(menuBar);
  }
}
