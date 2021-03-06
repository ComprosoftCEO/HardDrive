package game.scenes;

import game.engine.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Scene effect to show logging into the game
 */
public class LoadingScene extends Scene {

  private static final String LINE_1 = "Logging in user...";
  private static final String LINE_2 = "Loading server data...";

  private static final Font GRAPHICS_FONT   = new Font("monospace", Font.PLAIN, 40);
  private static final Color GRAPHICS_COLOR = new Color(0, 255, 0);

  // Timers
  private static final int TYPING_TIMER     = 0;
  private static final int TYPING_SPEED     = 2;
  private static final int FADING_TIMER     = 1;
  private static final int FADING_SPEED     = 1;
  private static final int NEXT_SCENE_TIMER = 2;
  private static final int NEXT_SCENE_DELAY = 15;

  private Scene nextScene;
  private String line1, line2;
  private double textOpacity;

  public LoadingScene(Scene nextScene) {
    super(640, 480);
    this.mainView.size.setSize(640, 480);

    this.background.type = BackgroundType.None;
    this.backgroundColor = Color.BLACK;

    this.nextScene   = nextScene;
    this.line1       = "";
    this.line2       = "";
    this.textOpacity = 1.0;
  }

  @Override
  protected void onCreate() {
    GameAssets.getLoadedSound("typing").playSound();
    this.setTimer(TYPING_TIMER, TYPING_SPEED, true);
  }

  @Override
  protected void onTimer(int timerIndex) {
    if (timerIndex == TYPING_TIMER) {
      typeLines();
      testForTypingFinished();
    }
    if (timerIndex == FADING_TIMER) { decreaseOpacity(); }
    if (timerIndex == NEXT_SCENE_TIMER) { this.getGame().setScene(this.nextScene); }
  }

  /**
   * Add 1 character to the lines one-by-one
   */
  private void typeLines() {
    if (line1.length() < LINE_1.length()) {
      line1 += LINE_1.charAt(line1.length());
      return;
    }

    if (line2.length() < LINE_2.length()) {
      line2 += LINE_2.charAt(line2.length());
      return;
    }
  }

  /**
   * Slowly decrease the opacity of the image
   */
  private void decreaseOpacity() {
    if (this.textOpacity > 0.05) {
      this.textOpacity -= 0.05;
    } else {
      this.clearTimer(FADING_TIMER);
      this.setTimer(NEXT_SCENE_TIMER, NEXT_SCENE_DELAY, false);
    }
  }

  @Override
  protected void onStep() {
    Game game = this.getGame();
    if (game.isKeyPressed(Key.ESCAPE)) { game.end(); }
    if (game.hasKeyBeenPressed(Key.F4)) { game.toggleFullscreen(); }
  }

  /**
   * Test if all of the text has been typed (the song is finished)
   */
  private void testForTypingFinished() {
    if (GameAssets.getLoadedSound("typing").isPlaying()) { return; }
    GameAssets.getLoadedSound("corruption-bgm").loopSound();

    this.clearTimer(TYPING_TIMER);
    this.setTimer(FADING_TIMER, FADING_SPEED, true);
  }

  @Override
  protected void onDraw(Graphics2D g2d) {
    g2d.setColor(GRAPHICS_COLOR);
    g2d.setComposite(AlphaComposite.SrcOver.derive((float) this.textOpacity));

    // Draw the server text
    Helpers.drawCenteredString(g2d, line1, new Rectangle(0, 75, 640, 150), GRAPHICS_FONT);
    Helpers.drawCenteredString(g2d, line2, new Rectangle(0, 150, 640, 250), GRAPHICS_FONT);

    // Draw the binary text
    if (this.textOpacity < 1.0) {
      BufferedImage randomBinaryImage =
          GameAssets.getLoadedImage("binary-" + (int) Helpers.randomRange(1, 10));

      g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f - (float) this.textOpacity));
      g2d.drawImage(randomBinaryImage, 0, -10, this.size.width, this.size.height + 10, null);
    }
  }
}
