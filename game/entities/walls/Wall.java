package game.entities.walls;

import game.engine.*;

/**
 * Generic Wall entity, cannot be moved or destroyed
 */
public abstract class Wall extends Entity {

  public Wall() {
    this.drawingPriority = 100;
  }

  @Override
  protected void onCreate() {}

  @Override
  protected void onDestroy() {}

  @Override
  protected void onTimer(int timerIndex) {}

  @Override
  protected void onStep() {}
}
