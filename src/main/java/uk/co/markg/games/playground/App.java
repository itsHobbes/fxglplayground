package uk.co.markg.games.playground;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.co.markg.games.playground.entity.EntityType;
import uk.co.markg.games.playground.entity.GameFactory;

public class App extends GameApplication {

  private static final int GRAVITY = 1000;

  private Entity player;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(800);
    settings.setHeight(600);
    settings.setTitle("Game!");
    settings.setVersion("1.0");
    settings.setDeveloperMenuEnabled(true);
    settings.setApplicationMode(ApplicationMode.DEVELOPER);
  }

  @Override
  protected void initGame() {
    FXGL.getGameWorld().addEntityFactory(new GameFactory());

    var s = new SpawnData();
    s.put("width", 800);
    s.put("height", 20);
    s.put("x", 0);
    s.put("y", 0);
    FXGL.spawn("wall", s);

    s.put("width", 20);
    s.put("height", 600);
    FXGL.spawn("wall", s);

    s.put("x", 780);
    FXGL.spawn("wall", s);

    s.put("y", 580);
    s.put("x", 0);
    s.put("width", 800);
    s.put("height", 20);
    FXGL.spawn("wall", s);

    var f = FXGL.getPhysicsWorld();

    // FXGL.spawn("coin", 500, 200);
    // FXGL.spawn("oil", 150, 250);
    // FXGL.spawn("ladder", 250, 50);
    // FXGL.spawn("hole", 400, 400);
    // FXGL.spawn("monster", 500, 300);

    player = FXGL.spawn("player", 100, 100);
  }

  @Override
  protected void initPhysics() {
    FXGL.getPhysicsWorld().setGravity(0, GRAVITY);
    FXGL.getPhysicsWorld()
        .addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {
          @Override
          protected void onCollisionBegin(Entity player, Entity coin) {
            FXGL.inc("coins", 1);
            System.out.println("coins: " + FXGL.getWorldProperties().getInt("coins"));
            coin.removeFromWorld();
            FXGL.getPhysicsWorld().setGravity(GRAVITY, 0);
          }
        });

    FXGL.getPhysicsWorld()
        .addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.OIL) {
          @Override
          protected void onCollisionBegin(Entity player, Entity oil) {
            FXGL.getPhysicsWorld().setGravity(0, -GRAVITY);
            FXGL.inc("oil", 1);
            System.out.println("oil: " + FXGL.getWorldProperties().getInt("oil"));
            oil.removeFromWorld();
          }
        });
  }

  @Override
  protected void initInput() {
    var input = FXGL.getInput();
    input.addAction(new UserAction("Left") {
      @Override
      protected void onAction() {
        player.getComponent(PlayerComponent.class).moveLeft();
      }

      @Override
      protected void onActionEnd() {
        player.getComponent(PlayerComponent.class).stop();
      }
    }, KeyCode.A, VirtualButton.LEFT);

    input.addAction(new UserAction("Right") {
      @Override
      protected void onAction() {
        player.getComponent(PlayerComponent.class).moveRight();
      }

      @Override
      protected void onActionEnd() {
        player.getComponent(PlayerComponent.class).stop();
      }
    }, KeyCode.D, VirtualButton.RIGHT);

    UserAction a = new UserAction("Up") {
      @Override
      protected void onActionBegin() {
        player.getComponent(PlayerComponent.class).moveUp();
      }

    };

    input.addAction(a, KeyCode.SPACE, VirtualButton.UP);

    // input.addAction(new UserAction("DashRight") {
    // @Override
    // protected void onActionBegin() {
    // player.getComponent(PlayerComponent.class).dash();
    // FXGL.runOnce(() -> player.getComponent(PlayerComponent.class).stopDash(),
    // Duration.millis(300));
    // }

    // }, KeyCode.D, InputModifier.SHIFT);

    // input.addAction(new UserAction("DashLeft") {
    // @Override
    // protected void onActionBegin() {
    // player.getComponent(PlayerComponent.class).dash();
    // FXGL.runOnce(() -> player.getComponent(PlayerComponent.class).stopDash(),
    // Duration.millis(300));
    // }

    // }, KeyCode.A, InputModifier.SHIFT);

    try {
      var i = input.getClass().getDeclaredMethods();
      var l =
          Stream.of(i).filter(m -> m.getName().equals("addBinding")).collect(Collectors.toList());

      Method m = l.get(0);
      m.setAccessible(true);
      UserAction s = new UserAction("Dash") {
        @Override
        protected void onActionBegin() {
          System.out.println("dash");
          player.getComponent(PlayerComponent.class).dash();
          FXGL.runOnce(() -> player.getComponent(PlayerComponent.class).stopDash(),
              Duration.millis(300));
        }
      };
      m.invoke(input, s, new KeyTrigger(KeyCode.SHIFT, InputModifier.NONE));
    } catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }


    // input.addBinding(new UserAction("DashLeft") {
    // @Override
    // protected void onActionBegin() {
    // player.getComponent(PlayerComponent.class).dash();
    // FXGL.runOnce(() -> player.getComponent(PlayerComponent.class).stopDash(),
    // Duration.millis(300));
    // }

    // }, (Trigger)new KeyTrigger(KeyCode.SHIFT));

    input.addAction(new UserAction("Down") {
      @Override
      protected void onAction() {
        player.getComponent(PlayerComponent.class).moveDown();
      }

      @Override
      protected void onActionEnd() {
        player.getComponent(PlayerComponent.class).stop();
      }
    }, KeyCode.S, VirtualButton.DOWN);
  }

  @Override
  protected void initUI() {
    Text textPixels = new Text();
    textPixels.setTranslateX(50);
    textPixels.setTranslateY(100);

    FXGL.getGameScene().addUINode(textPixels);
    textPixels.textProperty().bind(FXGL.getWorldProperties().intProperty("coins").asString());
  }

  @Override
  protected void initGameVars(Map<String, Object> vars) {
    vars.put("coins", 0);
    vars.put("oil", 0);
  }
}
