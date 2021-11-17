package uk.co.markg.games.playground;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

public class PlayerComponent extends Component {

  private static final int VELOCITY = 175;
  private static final int JUMP_VELOCITY = VELOCITY * 3;
  private static final int MAX_JUMPS = 2;
  private static final int DASH = 400;
  private static final int DASH_UP = 200;
  private static final int MAX_DASH = 1;

  private PhysicsComponent physics;
  private int jumps = MAX_JUMPS;
  private boolean isDashing = false;
  private int dash = MAX_DASH;

  @Override
  public void onAdded() {
    physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
      if (isOnGround) {
        jumps = MAX_JUMPS;
        dash = MAX_DASH;
      }
    });
  }

  public void moveLeft() {
    if (!isDashing) {
      physics.setVelocityX(-VELOCITY);
      checkEdgeHang();
    }
  }

  public void stop() {
    physics.setVelocityX(0);
  }

  public void moveRight() {
    if (!isDashing) {
      physics.setVelocityX(VELOCITY);
      checkEdgeHang();
    }
  }

  private void checkEdgeHang() {
    if (physics.isMovingX() && !physics.isMovingY() && !physics.isOnGround()) {
      physics.setVelocityY(VELOCITY);
    }
  }

  public void moveUp() {
    if (jumps == 0) {
      return;
    }
    physics.setVelocityY(-JUMP_VELOCITY);
    jumps--;
  }

  public void moveDown() {
    physics.setVelocityY(VELOCITY);
  }

  public void dash() {
    if (physics.getVelocityX() == 0 || dash == 0) {
      return;
    }
    isDashing = true;
    if (physics.getVelocityX() > 0) {
      physics.setVelocityX(DASH);
    } else {
      physics.setVelocityX(-DASH);
    }
    physics.setVelocityY(-DASH_UP);
    dash--;
  }

  public void stopDash() {
    isDashing = false;
  }

  /**
   * @return the physics
   */
  public PhysicsComponent getPhysics() {
    return physics;
  }

}
