package jab.spigot.smartboards.editor;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BoardPropertiesTab extends JPanel {

  private final Window window;

  public BoardPropertiesTab(@NotNull Window window) {
    this.window = window;
  }

  @NotNull
  public Window getWindow() {
    return this.window;
  }
}
