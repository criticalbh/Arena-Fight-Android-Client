package info.admirsabanovic.arenafight.game;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

/**
 * Created by asabanovic on 5/15/15.
 */
public class GameScene extends BaseScene {
    @Override
    public void createScene() {
        setBackground(new Background(Color.BLUE));
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return null;
    }

    @Override
    public void disposeScene() {

    }

    private HUD gameHUD;
    private Text scoreText;

    private void createHUD()
    {
        gameHUD = new HUD();

        // CREATE SCORE TEXT
      //  scoreText = new Text(20, 420, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setText("Score: 0");
        gameHUD.attachChild(scoreText);

        camera.setHUD(gameHUD);
    }
}
