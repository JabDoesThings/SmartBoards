package jab.spigot.smartboards.editor.project;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class BoardProject {

  private String name;
  private String author;
  private String version;
  private String description;
  private String copyright;

  /**
   * Full constructor.
   *
   * @param name The name of the board.
   * @param author The author of the board.
   * @param version The version of the board.
   * @param description The description of the board.
   * @param copyright The copyright for the board.
   */
  public BoardProject(
      @NotNull String name,
      @NotNull String author,
      @NotNull String version,
      @NotNull String description,
      @NotNull String copyright) {
    this.name = name;
    this.author = author;
    this.version = version;
    this.description = description;
    this.copyright = copyright;
  }

  /**
   * Basic constructor.
   *
   * <p>This constructor plugs in default values for a new board project.
   */
  public BoardProject() {
    this("Untitled Board", "", "1.00", "No description.", "");
  }

  @NotNull
  public String getName() {
    return this.name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @NotNull
  public String getVersion() {
    return this.version;
  }

  public void setVersion(@NotNull String version) {
    this.version = version;
  }

  @NotNull
  public String getDescription() {
    return this.description;
  }

  public void setDescription(@NotNull String description) {
    this.description = description;
  }

  @NotNull
  public String getCopyright() {
    return this.copyright;
  }

  public void setCopyright(@NotNull String copyright) {
    this.copyright = copyright;
  }
}
