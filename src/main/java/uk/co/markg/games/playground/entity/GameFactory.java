package uk.co.markg.games.playground.entity;

import java.util.concurrent.ThreadLocalRandom;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import uk.co.markg.games.playground.PlayerComponent;

public class GameFactory implements EntityFactory {

  private static final int DEFAULT_HEIGHT = 25;
  private static final int DEFAULT_WIDTH = 25;
  private static final int ITEM_RADIUS = 5;

  @Spawns("player")
  public Entity newPlayer(SpawnData data) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics
        .addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(5, 25), BoundingShape.box(15, 2)));


    return FXGL.entityBuilder(data).type(EntityType.PLAYER)
        .viewWithBBox(new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLUE)).with(physics)
        .with(new CollidableComponent(true)).with(new IrremovableComponent())
        .with(new KeepOnScreenComponent()).with(new PlayerComponent()).build();
  }

  @Spawns("monster")
  public Entity newMonster(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.MONSTER)
        .viewWithBBox(new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.PURPLE))
        .with(new CollidableComponent(true)).with(new KeepOnScreenComponent()).build();
  }

  @Spawns("coin")
  public Entity newCoin(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.COIN)
        .viewWithBBox(new Circle(ITEM_RADIUS, ITEM_RADIUS, ITEM_RADIUS, Color.GREEN))
        .with(new CollidableComponent(true)).build();
  }

  @Spawns("wall")
  public Entity newWall(SpawnData data) {
    var rect = new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.RED);
    return FXGL.entityBuilder(data).at(data.<Integer>get("x"), data.<Integer>get("y"))
        .type(EntityType.WALL).view(rect)
        .bbox(new HitBox(BoundingShape.box(rect.getWidth(), rect.getHeight())))
        .with(new PhysicsComponent()).build();
  }

  @Spawns("oil")
  public Entity newOil(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.OIL)
        .viewWithBBox(new Circle(ITEM_RADIUS, ITEM_RADIUS, ITEM_RADIUS, Color.BLACK))
        .with(new CollidableComponent(true)).build();
  }

  @Spawns("ladder")
  public Entity newLadder(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.LADDER)
        .viewWithBBox(new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BROWN))
        .with(new CollidableComponent(true)).build();
  }

  @Spawns("hole")
  public Entity newHole(SpawnData data) {
    return FXGL.entityBuilder(data).type(EntityType.HOLE)
        .viewWithBBox(new Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK))
        .with(new CollidableComponent(true)).build();
  }
}
