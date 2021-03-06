package game.scenes;

import game.engine.*;
import game.entities.*;
import game.entities.antivirus.*;
import game.entities.component.*;
import game.entities.walls.*;
import game.resources.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

/**
 * Parent class of all game levels
 */
public abstract class GameScene extends Scene {

  // Size, in pixels, of each tile in the grid
  public static final int TILE_SIZE           = 32;
  public static final Dimension MIN_VIEW_SIZE = new Dimension(640, 480);
  public static final Dimension MAX_VIEW_SIZE =
      new Dimension((int) (1.75 * 640), (int) (1.75 * 480));
  private static final int HUD_HEIGHT = 100;

  private static final int VIEW_THRESHHOLD     = 300;
  private static final int SPEED_MEMORY_SIZE   = 20;
  private static final int NUM_ZOOM_OUT_FRAMES = 30;
  private static final int NUM_FADE_OUT_FRAMES = 30;
  private static final int SWOOPER_TIME        = 200;
  private static final int AMALGAMATE_TIME     = 160;

  /// Level number this game represents
  private final int level;

  /// Used by ants for path finding
  private boolean[][] walls;

  /// Zoom in and zoom out effects
  private int zoomFrameNumber;
  private int fadeFrameNumber;
  private Point2d playerStartPosition;

  /// Physics
  private double frictionCoefficient;

  /// Zoom in and out with speed
  private View absoluteView;
  private double zoomLevel;
  private double[] recentSpeeds; /* Rolling average of speeds */
  private int speedIndex;

  // Heads-up display image
  private BufferedImage hudImage;

  /**
   * Construct a new game scene
   *
   * @param background  Background type for the scene
   */
  public GameScene(String background, int level, double friction) {
    super(640, 480);
    this.mainView.size.setSize(MIN_VIEW_SIZE);

    this.background.addFrames(GameAssets.getLoadedImage(background));
    this.background.type = BackgroundType.Tiled;

    this.level               = level;
    this.frictionCoefficient = friction;
    this.zoomFrameNumber     = 0;
    this.fadeFrameNumber     = 0;
    this.playerStartPosition = new Point2d();

    this.absoluteView = new View(new Point2d(), new Dimension(MIN_VIEW_SIZE));
    this.zoomLevel    = 0;
    this.recentSpeeds = new double[SPEED_MEMORY_SIZE];
    this.speedIndex   = 0;

    this.hudImage = new BufferedImage(MIN_VIEW_SIZE.width, HUD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
  }

  /**
   * Get a new instance of the level scene
   *
   * @param   level   Level to get the scene for
   * @return  Level scene
   */
  public static GameScene getLevelScene(int level) {
    if (level == 1) { return new Level1(); }
    if (level == 2) { return new Level2(); }
    if (level == 3) { return new Level3(); }
    if (level == 4) { return new Level4(); }
    if (level == 5) { return new Level5(); }
    if (level == 6) { return new Level6(); }
    if (level == 7) { return null; /* Special Case */ }
    return null;
  }

  /**
   * Must override this method to create the level when the view is created
   */
  protected abstract String[] getLevelLayout();

  /**
   * Get the friction constant associated with this room
   */
  public final double getFriction() {
    return this.frictionCoefficient;
  }

  /**
   * Create method
   */
  @Override
  protected void onCreate() {
    resetLives();
    defineLevelTiles(this.getLevelLayout());
    recomputeWalls();

    this.setTimer(-1, 1, true);
    this.setTimer(-3, 100, true);
  }

  /**
   * Reset the number of player lives
   */
  private void resetLives() {
    Lives lives = this.getGame().getResouce(Lives.class);
    lives.resetLives();
  }

  /**
   * Define the level tiles one-by-one
   */
  private final void defineLevelTiles(String[] rows) {

    // Width of the scene is the maximum row string
    int maxWidth = MAX_VIEW_SIZE.width;

    for (int row = 0; row < rows.length; row += 1) {
      String rowString    = rows[row];
      int rowStringLength = rowString.length();

      maxWidth = Math.max(maxWidth, rowStringLength * TILE_SIZE);

      // Create all entities for each row
      for (int col = 0; col < rowStringLength; col += 1) {
        char c = rowString.charAt(col);
        Point2d creationPosition =
            new Point2d((TILE_SIZE / 2) + (col * TILE_SIZE), (TILE_SIZE / 2) + (row * TILE_SIZE));

        Entity e = this.createEntity(c, creationPosition);
        if (e != null) { this.createEntity(e); }
      }
    }

    // Update the size of the scene
    this.size.setSize(maxWidth, rows.length * TILE_SIZE);
  }

  /**
   * Create a single entity in the room
   *
   * @param   type      Character representation of the entity to create
   * @param   position  Position of the entity in the room
   */
  private final Entity createEntity(char type, Point2d position) {
    if (type == 's') {
      this.playerStartPosition = position;
      return new Player(position);
    }

    if (type == ' ') { return null; }
    if (type == 't') { return new Tree(position); }
    if (type == 'w') { return new SnowTree(position); }
    if (type == '@') { return new Cactus(position); }
    if (type == '*') { return new Rock(position); }
    if (type == '^') { return new Pyramid(position); }
    if (type == '#') { return new DataFile(position); }
    if (type == 'r') { return new Resistor(position, false); }
    if (type == 'R') { return new Resistor(position, true); }
    if (type == 'T') { return new Transistor(position); }
    if (type == '0') { return new Capacitor(position); }
    if (type == '-') { return new Chip(position, false); }
    if (type == '|') { return new Chip(position, true); }
    if (type == 'a') { return new AntSpawner(position, false); }
    if (type == 'A') { return new AntSpawner(position, true); }
    return null;
  }

  @Override
  protected void onTimer(int timerIndex) {
    if (timerIndex == -1) { zoomOut(); }
    if (timerIndex == -2) { fadeOut(); }
    if (timerIndex == -3) { recomputeWalls(); }
    if (timerIndex == -4) {
      this.createEntity(new Swooper());
      this.setTimer(-4, SWOOPER_TIME, false);
    }
    if (timerIndex == -5) {
      this.setTimer(-5, AMALGAMATE_TIME, false);
      this.createEntity(new Amalgamate());
    }
  }

  /**
   * Zoom-out effect for when the scene first starts
   */
  private void zoomOut() {
    this.zoomFrameNumber += 1;
    if (this.zoomFrameNumber == NUM_ZOOM_OUT_FRAMES) { this.clearTimer(-1); }
  }

  /**
   * Fade out effect when the level is completed
   */
  private void fadeOut() {
    this.fadeFrameNumber += 1;
    if (this.fadeFrameNumber == NUM_FADE_OUT_FRAMES) {
      this.clearTimer(-2);

      Lives lives = this.getGame().getResouce(Lives.class);
      Game game   = this.getGame();
      if (lives.playerHasLivesLeft()) {
        // Mark level as completed
        UnlockedLevels levels = game.getResouce(UnlockedLevels.class);
        levels.completeLevel(this.level);

        // Go to next level or the win screen
        GameScene nextScene = GameScene.getLevelScene(this.level + 1);
        if (nextScene != null) {
          game.setScene(new LevelCompleteScene(nextScene));
        } else {
          game.setScene(new game.scenes.YouWinScene());
        }
      } else {
        // Restart this scene
        game.setScene(new GameOverScene(GameScene.getLevelScene(this.level)));
      }
    }
  }

  /**
   * Perform actions when each data file is destroyed
   */
  public final void dataFileDestroyed() {
    int dataFilesLeft = this.findEntities(DataFile.class).size();
    if (dataFilesLeft == 5) { this.setTimer(-5, 1, false); }
    if (dataFilesLeft == 3) { this.setTimer(-4, 1, false); }
    if (dataFilesLeft % 2 == 0) { this.createEntity(new Worm()); }
  }

  /**
   * Get the number of tiles wide for this room
   * @return   Tiles wide
   */
  public final int getTilesWide() {
    return (int) Math.ceil(((double) this.size.width) / TILE_SIZE);
  }

  /**
   * Get the number of tiles height for the screen
   * @return   Tiles high
   */
  public final int getTilesHigh() {
    return (int) Math.ceil(((double) this.size.height) / TILE_SIZE);
  }

  /**
   * Get the boolean array of all walls in this scene
   */
  public boolean[][] getWallsMap() {
    return this.walls;
  }

  /**
   * Recompute the array used for the walls
   */
  private void recomputeWalls() {
    int tilesWide = this.getTilesWide();
    int tilesHigh = this.getTilesHigh();
    this.walls    = new boolean[tilesWide][tilesHigh];

    for (Entity e: this.findEntities(Wall.class)) { this.addEntityToWall(e); }
    for (Entity e: this.findEntities(game.entities.component.Component.class)) {
      // Ant spawners don't count as walls (Special case)
      if (e.getClass() == AntSpawner.class) { continue; }
      this.addEntityToWall(e);
    }
  }

  /**
   * Mark all of the tiles an entity occupies to "true"
   */
  private void addEntityToWall(Entity entity) {
    int tilesWide = this.getTilesWide();
    int tilesHigh = this.getTilesHigh();

    // Get tile index
    int entityTileX =
        (int) Math.floor((entity.position.x + entity.mask.relativePosition.x) / TILE_SIZE);
    int entityTileY =
        (int) Math.floor((entity.position.y + entity.mask.relativePosition.y) / TILE_SIZE);

    int entityTilesWide = (int) Math.ceil(((double) entity.mask.size.width) / TILE_SIZE);
    int entityTilesHigh = (int) Math.ceil(((double) entity.mask.size.height) / TILE_SIZE);

    for (int row = 0; row < entityTilesHigh; row += 1) {
      int tileY = row + entityTileY;
      if (tileY < 0 || tileY >= tilesHigh) { continue; }
      for (int col = 0; col < entityTilesWide; col += 1) {
        int tileX = col + entityTileX;
        if (tileX < 0 || tileX >= tilesWide) { continue; }
        this.walls[tileX][tileY] = true;
      }
    }
  }

  /**
   * Debug method to print out the wall map
   */
  private void printWallMap() {
    int tilesWide = this.getTilesWide();
    int tilesHigh = this.getTilesHigh();

    for (int y = 0; y < tilesHigh; y += 1) {
      for (int x = 0; x < tilesWide; x += 1) { System.out.print(this.walls[x][y] ? 'X' : ' '); }
      System.out.println("");
    }
  }

  @Override
  protected void onStep() {
    Game game = this.getGame();
    if (game.isKeyPressed(Key.ESCAPE)) { game.end(); }
    if (game.hasKeyBeenPressed(Key.F4)) { game.toggleFullscreen(); }

    checkPhysicsCollisions();
    moveViewToPlayer();
    computeZoomLevel();
    scaleView();
    testIfAllDataFilesDestroyed();
  }

  // detect and resolve all collisions between PhysicsEntities
  private void checkPhysicsCollisions() {
    Player player = this.findFirstEntity(Player.class);
    if (player == null) return;

    ArrayList<PhysicsEntity> physicsEntities = this.findEntities(PhysicsEntity.class);

    int size = physicsEntities.size();

    // for each PhysicsEntity
    for (int i = 0; i < size; i++) {
      if (physicsEntities.get(i) != player) {
        // check if the player is colliding with it
        if (player.isCollidingWith(physicsEntities.get(i)))
          player.collideWith(physicsEntities.get(i));

        // check if it is colliding with every remaining non-player PhysicsEntity
        for (int j = i + 1; j < size; j++) {
          if (physicsEntities.get(j) != player) {
            if (physicsEntities.get(i).isCollidingWith(physicsEntities.get(j)))
              physicsEntities.get(i).collideWith(physicsEntities.get(j));
          }
        }
      }
    }
  }

  /**
   * Move the view around to scroll with the player
   */
  private void moveViewToPlayer() {
    Player player = this.findFirstEntity(Player.class);
    if (player == null) { return; }

    double leftThreshhold   = player.position.x - VIEW_THRESHHOLD;
    double rightThreshhold  = player.position.x + VIEW_THRESHHOLD;
    double topThreshhold    = player.position.y - VIEW_THRESHHOLD;
    double bottomThreshhold = player.position.y + VIEW_THRESHHOLD;

    if (leftThreshhold < absoluteView.getLeftBoundary()) {
      absoluteView.position.x = Math.max(0, leftThreshhold);
    }
    if (rightThreshhold > absoluteView.getRightBoundary()) {
      absoluteView.position.x = Math.min(this.size.width - absoluteView.size.width,
                                         rightThreshhold - absoluteView.size.width);
    }

    if (topThreshhold < absoluteView.getTopBoundary()) {
      absoluteView.position.y = Math.max(0, topThreshhold);
    }
    if (bottomThreshhold > absoluteView.getBottomBoundary()) {
      absoluteView.position.y = Math.min(this.size.height - absoluteView.size.height,
                                         bottomThreshhold - absoluteView.size.height);
    }
  }

  /**
   * Get the zoom level based on the player's velocity
   */
  private void computeZoomLevel() {

    // Update recentSpeeds with newest speed (rolling average)
    Player player = this.findFirstEntity(Player.class);
    if (player == null) {
      recentSpeeds[this.speedIndex] = 0;
    } else {
      recentSpeeds[this.speedIndex] = player.getVelocity().length();
    }

    this.speedIndex = (speedIndex + 1) % SPEED_MEMORY_SIZE;

    // get average of recentSpeeds
    double avgSpeed = 0;
    for (double speed: recentSpeeds) { avgSpeed += speed; }
    avgSpeed /= SPEED_MEMORY_SIZE;

    // calculate zoom level
    this.zoomLevel = avgSpeed / Player.MAX_SPEED;
  }

  /**
   * Scale the size of the view to match the zoom level
   */
  private void scaleView() {
    double newWidth = Helpers.map(this.zoomLevel, 0, 1, MIN_VIEW_SIZE.width, MAX_VIEW_SIZE.width);
    double newHeight =
        Helpers.map(this.zoomLevel, 0, 1, MIN_VIEW_SIZE.height, MAX_VIEW_SIZE.height);

    double dWidth    = this.absoluteView.size.width - newWidth;
    double dHeight   = this.absoluteView.size.height - newHeight;
    double relativeX = dWidth / 2;
    double relativeY = dHeight / 2;

    this.mainView.size.setSize(newWidth, newHeight);

    this.mainView.position.x =
        (int) Helpers.clamp(absoluteView.position.x + relativeX, 0, this.size.width - newWidth);
    this.mainView.position.y =
        (int) Helpers.clamp(absoluteView.position.y + relativeY, 0, this.size.height - newHeight);
  }

  /**
   * Test if the level has been completed by destroying all data files
   */
  private void testIfAllDataFilesDestroyed() {
    if ((this.fadeFrameNumber == 0) && (this.zoomFrameNumber == NUM_ZOOM_OUT_FRAMES) &&
        (this.findEntities(DataFile.class).size() == 0)) {

      // Destroy all entities
      for (Entity e: this.getAllEntities()) { e.destroy(); }

      this.fadeFrameNumber = 1;
      this.setTimer(-2, 1, true);
    }
  }

  /**
   * Respawn the player in the room, or end the room
   */
  public void respawnPlayer() {
    Lives lives = this.getGame().getResouce(Lives.class);
    if (!lives.playerHasLivesLeft()) {

      // Destroy all entities
      for (Entity e: this.getAllEntities()) { e.destroy(); }

      // Show fade-out animation
      this.fadeFrameNumber = 1;
      this.setTimer(-2, 1, true);
    } else {
      this.createEntity(new Player(this.playerStartPosition));
    }
  }

  @Override
  protected void onDraw(Graphics2D g2d) {
    drawHud(g2d);
    drawZoomOut(g2d);
    drawFadeOut(g2d);
  }

  /**
   * Draw the game heads-up display
   */
  private void drawHud(Graphics2D imageG2d) {

    // Draw HUD to the buffered image
    Graphics2D g2d = (Graphics2D) this.hudImage.getGraphics();

    // Clear previous HUD
    g2d.setComposite(AlphaComposite.Clear);
    g2d.fillRect(0, 0, hudImage.getWidth(), hudImage.getHeight());
    g2d.setComposite(AlphaComposite.SrcOver);

    // Draw the number of lives left
    Lives lives    = this.getGame().getResouce(Lives.class);
    Rectangle rect = new Rectangle(40, 12, 150, 32);
    g2d.setColor(Color.BLACK);
    g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
    g2d.setColor(Color.WHITE);
    g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    Helpers.drawCenteredString(g2d, "Lives: " + lives.getLives(), rect,
                               new Font("Times", Font.PLAIN, 28));

    // Draw the data files left
    int dataFilesLeft                 = this.findEntities(DataFile.class).size();
    final BufferedImage dataFileImage = GameAssets.getLoadedImage("data-file");
    for (int i = 0; i < dataFilesLeft; i += 1) {
      g2d.drawImage(dataFileImage, MIN_VIEW_SIZE.width - (12 + 40 * (i + 1)), 12, 32, 32, null);
    }

    // Then draw and scale it to match the view
    imageG2d.translate(this.mainView.position.x, this.mainView.position.y);

    double scaledHeight =
        Helpers.map(HUD_HEIGHT, 0, MIN_VIEW_SIZE.height, 0, this.mainView.size.height);
    imageG2d.drawImage(hudImage, 0, 0, this.mainView.size.width, (int) scaledHeight, null);

    imageG2d.translate(-this.mainView.position.x, -this.mainView.position.y);
  }

  /**
   * Draw the cool binary zoom out effect at the start of the level
   */
  private void drawZoomOut(Graphics2D g2d) {
    if (this.zoomFrameNumber == NUM_ZOOM_OUT_FRAMES) { return; }

    BufferedImage image = new BufferedImage(this.mainView.size.width, this.mainView.size.height,
                                            BufferedImage.TYPE_INT_ARGB);

    BufferedImage randomBinaryImage =
        GameAssets.getLoadedImage("binary-" + (int) Helpers.randomRange(1, 10));

    Graphics2D imageG2d = (Graphics2D) image.getGraphics();
    imageG2d.setColor(Color.BLACK);
    imageG2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    imageG2d.drawImage(randomBinaryImage, 0, -10, image.getWidth(), image.getHeight() + 10, null);

    // "Chisel out" the image from the center
    int centerX      = image.getWidth() / 2;
    int centerY      = image.getHeight() / 2;
    int chiselWidth  = (zoomFrameNumber * this.mainView.size.width) / NUM_ZOOM_OUT_FRAMES;
    int chiselHeight = (zoomFrameNumber * this.mainView.size.height) / NUM_ZOOM_OUT_FRAMES;

    int leftX = centerX - chiselWidth / 2;
    int topY  = centerY - chiselHeight / 2;

    // Chisel out the viewport
    imageG2d.setComposite(AlphaComposite.Clear);
    imageG2d.fillRect(leftX, topY, chiselWidth, chiselHeight);
    imageG2d.setComposite(AlphaComposite.SrcOver);

    // Draw the flashing text
    if (zoomFrameNumber % 10 > 2) {
      Rectangle rect =
          new Rectangle(0, 3 * image.getHeight() / 8, image.getWidth(), image.getHeight() / 4);

      imageG2d.setColor(Color.BLACK);
      imageG2d.fillRect(rect.x, rect.y, rect.width, rect.height);
      imageG2d.setColor(Color.WHITE);
      imageG2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height);

      Helpers.drawCenteredString(imageG2d, "Destroy All Data Files...", rect,
                                 new Font("Times", Font.BOLD, 36));
    }

    // Now draw the image over the view
    g2d.drawImage(image, (int) this.mainView.position.x, (int) this.mainView.position.y,
                  image.getWidth(), image.getHeight(), null);
  }

  /**
   * Draw the cool binary fade out
   */
  private void drawFadeOut(Graphics2D g2d) {
    if (this.fadeFrameNumber == 0) { return; }

    BufferedImage randomBinaryImage =
        GameAssets.getLoadedImage("binary-" + (int) Helpers.randomRange(1, 10));

    g2d.translate(this.mainView.position.x, this.mainView.position.y);
    g2d.setComposite(
        AlphaComposite.SrcOver.derive(((float) this.fadeFrameNumber) / NUM_FADE_OUT_FRAMES));

    g2d.setColor(Color.BLACK);
    g2d.fillRect(0, 0, this.mainView.size.width, this.mainView.size.height);

    Lives lives = this.getGame().getResouce(Lives.class);
    if (lives.playerHasLivesLeft()) {
      g2d.drawImage(randomBinaryImage, 0, -10, this.mainView.size.width,
                    this.mainView.size.height + 10, null);
    }

    g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
    g2d.translate(-this.mainView.position.x, -this.mainView.position.y);
  }
}
