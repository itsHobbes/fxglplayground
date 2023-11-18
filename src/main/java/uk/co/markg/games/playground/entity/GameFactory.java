package uk.co.markg.games.playground.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import uk.co.markg.games.playground.PlayerComponent;

public class GameFactory implements EntityFactory {

  private static final int DEFAULT_HEIGHT = 30;
  private static final int DEFAULT_WIDTH = 25;

  @Spawns("player")
  public Entity newPlayer(SpawnData data) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.addGroundSensor(
        new HitBox("GROUND_SENSOR", new Point2D(5, DEFAULT_HEIGHT), BoundingShape.box(15, 2)));
    var ground = new Rectangle(5, DEFAULT_HEIGHT, 15, 2);
    ground.setFill(Color.GREEN);

    var left = new Rectangle(-2, 2, 3, 25);
    left.setFill(Color.GREEN);

    var right = new Rectangle(23, 2, 3, 25);
    right.setFill(Color.GREEN);

    return FXGL.entityBuilder(data).type(EntityType.PLAYER)
        .viewWithBBox(new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLUE)).with(physics)
        .view(ground).view(left).view(right).with(new CollidableComponent(true))
        .with(new IrremovableComponent()).with(new PlayerComponent()).build();
  }

  @Spawns("wall")
  public Entity newWall(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.WALL)
        // .view(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.RED))
        .bbox(
            new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
        .with(new PhysicsComponent()).build();
  }

  @Spawns("launch_pad")
  public Entity newLaunchPad(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.LAUNCH_PAD)
        .bbox(
            new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
        .with(new PhysicsComponent()).build();
  }
}
