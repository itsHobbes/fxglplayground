package uk.co.markg.games.playground;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.SensorCollisionHandler;
import javafx.geometry.Point2D;
import uk.co.markg.games.playground.entity.EntityType;

public class PlayerComponent extends Component {

  private static final int VELOCITY = 175;
  private static final int JUMP_VELOCITY = 400;
  private static final int MAX_JUMPS = 2;
  private static final int DASH = 400;
  private static final int DASH_UP = 200;
  private static final int MAX_DASH = 1;

  private static final int MAX_VELOCITY = 600;

  private PhysicsComponent physics;
  private int jumps = MAX_JUMPS;
  private boolean isDashing = false;
  private int dash = MAX_DASH;
  private boolean isCollidingLeft = false;
  private boolean isCollidingRight = false;

  @Override
  public void onAdded() {
    physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
      if (isOnGround) {
        jumps = MAX_JUMPS;
        dash = MAX_DASH;
      }
    });
    physics.addSensor(new HitBox("LEFT_SENSOR", new Point2D(-2, 2), BoundingShape.box(3, 25)),
        new SensorCollisionHandler() {
          @Override
          public void onCollisionBegin(Entity other) {
            isCollidingLeft = true;
          }

          @Override
          public void onCollisionEnd(Entity other) {
            isCollidingLeft = false;
          }
        });

    physics.addSensor(new HitBox("RIGHT_SENSOR", new Point2D(23, 2), BoundingShape.box(3, 25)),
        new SensorCollisionHandler() {
          @Override
          public void onCollisionBegin(Entity other) {
            isCollidingRight = true;
          }

          @Override
          public void onCollisionEnd(Entity other) {
            isCollidingRight = false;
          }
        });
  }

  public void onUpdate(double tpf) {
    if (physics.getVelocityX() > MAX_VELOCITY) {
      physics.setVelocityX(MAX_VELOCITY);
    }
    if (physics.getVelocityX() < -MAX_VELOCITY) {
      physics.setVelocityX(-MAX_VELOCITY);
    }
    if (physics.getVelocityY() > MAX_VELOCITY) {
      physics.setVelocityY(MAX_VELOCITY);
    }
    if (physics.getVelocityY() < -MAX_VELOCITY) {
      physics.setVelocityY(-MAX_VELOCITY);
    }
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
    if (isCollidingLeft || isCollidingRight) {
      // if (physics.isMovingX() && !physics.isMovingY() && !physics.isOnGround()) {
      System.out.println("edge hang");
      physics.setVelocityY(VELOCITY / 2);
    }
  }

  public void jump() {
    if (jumps == 0) {
      return;
    }
    var pads = FXGL.getGameWorld().getEntitiesByType(EntityType.LAUNCH_PAD);
    pads.stream().filter(pad -> pad.distanceBBox(entity) < 2).findFirst().ifPresentOrElse(
        entity -> physics.setVelocityY(-(JUMP_VELOCITY * 2)),
        () -> physics.setVelocityY(-JUMP_VELOCITY));
    // pads.forEach(pad -> System.out.println(entity.distanceBBox(pad)));
    // physics.setVelocityY(-JUMP_VELOCITY);
    jumps--;
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
}
