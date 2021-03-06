package game.entities.component;

import game.engine.*;
import game.scenes.*;
import java.awt.*;

/**
 * Resistor component
 */
public class Resistor extends Component {

  private static final      int RESISTOR_HEALTH   = 8;
  private static final Vector2d RESISTOR_FRICTION = new Vector2d (10, 5);
  private static final   double RESISTOR_MASS     = 1;
  private static final Dimension SIZE = new Dimension(3 * GameScene.TILE_SIZE, GameScene.TILE_SIZE);

  public Resistor(Point2d position, boolean isVertical) {
    super (RESISTOR_HEALTH, RESISTOR_FRICTION, RESISTOR_MASS);

    this.sprite.addFrames(GameAssets.getLoadedImage("resistor"));

    this.sprite.size.setSize(SIZE);
    if (isVertical) {
	   this.sprite.setAngleDegrees(90);
		this.angle = Math.toRadians(90);
    }

    this.mask = this.sprite.getMask();
    this.position.set(position);
  }
}
