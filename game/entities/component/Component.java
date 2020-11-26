package game.entities.component;

import game.entities.*;
import game.engine.Vector2d;

/**
 * Represents an electronic component that can be destoryed
 */
public abstract class Component extends PhysicsEntity {

  public Component (int health, Vector2d fricCoef, double mass) {
    super (health, fricCoef, mass);
    this.drawingPriority = 50;
  }

  public Component (int health, double fricCoef, double mass) {
    super (health, fricCoef, mass);
    this.drawingPriority = 50;
  }

  @Override
  protected void onCreate() {}

  @Override
  protected void onTimer(int timerIndex) {}
}
