package uk.co.markg.games.playground;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.util.Map;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.co.markg.games.playground.entity.GameFactory;

public class App extends GameApplication {

  private static final int GRAVITY = 760;

  private static final KeyCode LEFT_KEY = KeyCode.A;
  private static final KeyCode RIGHT_KEY = KeyCode.D;
  private static final KeyCode JUMP_KEY = KeyCode.SPACE;
  private static final KeyCode DASH_KEY = KeyCode.SHIFT;

  private Entity player;
  private Level level;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  protected void initSettings(GameSettings settings) {
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    int width = gd.getDisplayMode().getWidth();
    int height = gd.getDisplayMode().getHeight();
    System.out.println(width);
    System.out.println(height);
    settings.setWidth(1920);
    settings.setHeight(1080);
    settings.setTitle("Game!");
    settings.setVersion("1.0");
    settings.setDeveloperMenuEnabled(true);
    settings.setApplicationMode(ApplicationMode.DEVELOPER);
    settings.setProfilingEnabled(true);
    settings.setManualResizeEnabled(true);
    settings.setPreserveResizeRatio(true);
  }

  @Override
  protected void initGame() {
    FXGL.getGameWorld().addEntityFactory(new GameFactory());
    player = FXGL.spawn("player", 100, 0);
    loadLevel(1);
  }

  private void loadLevel(int levelNum) {
    level = FXGL.setLevelFromMap("tmx/level" + levelNum + ".tmx");
    player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(100, 0));

    Viewport viewport = FXGL.getGameScene().getViewport();
    viewport.setBounds(0, 0, level.getWidth(), level.getHeight() * 2);
    viewport.bindToEntity(player, FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2);
    viewport.setLazy(true);

    FXGL.getInput().addAction(new UserAction("Test") {
      @Override
      protected void onActionBegin() {
        System.out.println("test");
      }
    }, KeyCode.ENTER);
  }

  @Override
  protected void initPhysics() {
    setGravity(Gravity.SOUTH);
    // FXGL.getPhysicsWorld()
    // .addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {
    // @Override
    // protected void onCollisionBegin(Entity player, Entity coin) {
    // FXGL.inc("coins", 1);
    // System.out.println("coins: " + FXGL.getWorldProperties().getInt("coins"));
    // coin.removeFromWorld();
    // FXGL.getPhysicsWorld().setGravity(GRAVITY, 0);
    // }
    // });

    // FXGL.getPhysicsWorld()
    // .addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.OIL) {
    // @Override
    // protected void onCollisionBegin(Entity player, Entity oil) {
    // FXGL.getPhysicsWorld().setGravity(0, -GRAVITY);
    // FXGL.inc("oil", 1);
    // System.out.println("oil: " + FXGL.getWorldProperties().getInt("oil"));
    // oil.removeFromWorld();
    // }
    // });
  }

  @Override
  protected void initInput() {
    Input input = FXGL.getInput();
    setMovementControls(input);
    setGravityControls(input);
  }

  @Override
  protected void initUI() {
    Text gravity = new Text();
    gravity.setTranslateX(50);
    gravity.setTranslateY(50);

    FXGL.getGameScene().addUINode(gravity);
    gravity.textProperty().bind(FXGL.getWorldProperties().stringProperty("debug"));
  }

  @Override
  protected void onUpdate(double tpf) {
    if (player.getY() > FXGL.getAppHeight() + 500) {
      System.out.println("dead");
      die();
    }
    player.getPosition();
    // FXGL.set("debug",
    // String.valueOf(player.getComponent(PhysicsComponent.class).getLinearVelocity()));
    FXGL.set("debug", String.valueOf(FXGL.getPhysicsWorld().getJBox2DWorld().getGravity()));
  }

  private void die() {
    loadLevel(FXGL.getWorldProperties().getInt("level"));
  }

  @Override
  protected void initGameVars(Map<String, Object> vars) {
    vars.put("coins", 0);
    vars.put("oil", 0);
    vars.put("level", 0);
    vars.put("gravity", Gravity.NORTH);
    vars.put("gravval", "");
    vars.put("debug", "");
  }

  private void setGravity(Gravity direction) {
    if (((Gravity) FXGL.geto("gravity")).equals(direction)) {
      return;
    }
    Vec2 oldGravity = new Vec2(FXGL.getPhysicsWorld().getJBox2DWorld().getGravity());
    switch (direction) {
      case NORTH -> FXGL.getPhysicsWorld().setGravity(0, -GRAVITY);
      case EAST -> FXGL.getPhysicsWorld().setGravity(GRAVITY, 0);
      case SOUTH -> FXGL.getPhysicsWorld().setGravity(0, GRAVITY);
      case WEST -> FXGL.getPhysicsWorld().setGravity(-GRAVITY, 0);
    }
    Vec2 newGravity = FXGL.getPhysicsWorld().getJBox2DWorld().getGravity();
    float angle = newGravity.angle(oldGravity);
    // player.rotateBy(angle);
    // player.getBoundingBoxComponent().setTransform(new TransformComponent(0, 0, 90, 1, 1));
    // player.rotateBy(angle);
    System.out.println(angle);
    // TransformComponent component = new TransformComponent(0, 0, 90, 1, 1);
    FXGL.set("gravity", direction);
    FXGL.set("gravval", FXGL.getPhysicsWorld().getJBox2DWorld().getGravity().toString());
  }

  private void setMovementControls(Input input) {
    input.addAction(new UserAction("Left") {
      @Override
      protected void onAction() {
        player.getComponent(PlayerComponent.class).moveLeft();
      }

      @Override
      protected void onActionEnd() {
        player.getComponent(PlayerComponent.class).stop();
      }
    }, LEFT_KEY);

    input.addAction(new UserAction("Right") {
      @Override
      protected void onAction() {
        player.getComponent(PlayerComponent.class).moveRight();
      }

      @Override
      protected void onActionEnd() {
        player.getComponent(PlayerComponent.class).stop();
      }
    }, RIGHT_KEY);

    input.addAction(new UserAction("Jump") {
      @Override
      protected void onActionBegin() {
        player.getComponent(PlayerComponent.class).jump();
      }
    }, JUMP_KEY);

    FXGL.getPrimaryStage().getScene().addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
      if (event.getCode() == DASH_KEY) {
        player.getComponent(PlayerComponent.class).dash();
        FXGL.runOnce(() -> player.getComponent(PlayerComponent.class).stopDash(),
            Duration.millis(300));
      }
    });

  }

  private void setGravityControls(Input input) {
    input.addAction(new UserAction("gravNorth") {
      @Override
      protected void onActionBegin() {
        setGravity(Gravity.NORTH);
      }
    }, KeyCode.Y);

    input.addAction(new UserAction("gravEast") {
      @Override
      protected void onActionBegin() {
        setGravity(Gravity.EAST);
      }
    }, KeyCode.U);

    input.addAction(new UserAction("gravSouth") {
      @Override
      protected void onActionBegin() {
        setGravity(Gravity.SOUTH);
      }
    }, KeyCode.I);

    input.addAction(new UserAction("gravWest") {
      @Override
      protected void onActionBegin() {
        setGravity(Gravity.WEST);
      }
    }, KeyCode.O);
  }
}
