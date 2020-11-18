package game.entities.component;

import game.engine.*;
import game.scenes.*;
import java.awt.*;

/**
 * Transistor component
 */
public class Transistor extends Component {

  private static final int TRANSISTOR_HEALTH = 13;
  private static final Dimension SIZE = new Dimension(GameScene.TILE_SIZE, 2 * GameScene.TILE_SIZE);

  public Transistor(Point2d position) {
    super(TRANSISTOR_HEALTH);

    this.sprite.addFrames(GameAssets.getLoadedImage("transistor"));

    this.sprite.size.setSize(SIZE);
    this.mask = this.sprite.getMask();
    this.position.set(position);
  }
}