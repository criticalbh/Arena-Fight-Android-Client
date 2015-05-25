package info.admirsabanovic.arenafight.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.github.nkzawa.emitter.Emitter;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import info.admirsabanovic.arenafight.tcp.SocketIO;

/**
 * Created by asabanovic on 5/15/15.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener {
    private static final String TAG_ENTITY = "entity";
    private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
    private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
    private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";

    private HUD gameHUD;
    private Text scoreText;
    private int score = 0;
    private PhysicsWorld physicsWorld;

    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER2 = "player2";
    private Player player;
    private Player player2;
    private boolean firstTouch = false;

    private Text gameOverText;
    private Text teamMateDeadText;
    private boolean gameOverDisplayed = false;
    private boolean teamMateDeadDisplay = false;
    private boolean ifFirst = false;
    private boolean firstCame = false;
    private boolean thisIsSecond;

    //life saver
    private AtomicBoolean myBoolean;

    private void signals(){
        SocketIO.getInstance().on("firstPlayer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        firstCame = true;
                        JSONObject data = (JSONObject) args[0];
                        try {
                            boolean first_login = data.getBoolean("first");
                            myBoolean.set(first_login);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }



    @Override
    public void createScene() {
        myBoolean =  new AtomicBoolean(false);
        SocketIO.getInstance().emit("checkFirst");
        signals();
        while(firstCame == false){}
        setBackground(new Background(Color.BLUE));
        createHUD();
        createPhysics();
        loadLevel(1);
        createGameOverText();
        setOnSceneTouchListener(this);
    }

    @Override
    public void onBackKeyPressed()
    {
        SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return null;
    }

    @Override
    public void disposeScene() {
        camera.setHUD(null);
        camera.setCenter(400, 240);
        camera.setChaseEntity(null);
    }

    private void createGameOverText()
    {
        gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
        teamMateDeadText = new Text(0, 0, resourcesManager.font, "Team Mate Dead!", vbom);
    }

    private void displayGameOverText()
    {
        camera.setChaseEntity(null);
        gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
        attachChild(gameOverText);
        gameOverDisplayed = true;
    }

    private void displayTeamMateDeadText()
    {
        teamMateDeadText.setPosition(camera.getCenterX(), camera.getCenterY());

        attachChild(teamMateDeadText);
        engine.registerUpdateHandler(new TimerHandler(0.9f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                pTimerHandler.reset();
                engine.unregisterUpdateHandler(pTimerHandler);
                detachChild(teamMateDeadText);
            }
        }));
        teamMateDeadDisplay = true;
    }

    private void createHUD(){
        final Rectangle right = new Rectangle(720, 200, 60, 60, vbom)
        {
            public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
            {
                if (touchEvent.isActionUp())
                {
                    spawnBullet();
                }
                return true;
            };
        };
        gameHUD = new HUD();

        // CREATE SCORE TEXT
        scoreText = new Text(50, 420, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setText("Score: 0");
        gameHUD.attachChild(scoreText);

        gameHUD.registerTouchArea(right);
        gameHUD.attachChild(right);
        camera.setHUD(gameHUD);
    }


    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + score);
    }

    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
        physicsWorld.setContactListener(contactListener());
        registerUpdateHandler(physicsWorld);
    }

    private void loadLevel(int levelID)
    {
        final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);

        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);

        levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
        {
            public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
            {
                final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
                final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);

                camera.setBounds(0, 0, width, height); // here we set camera bounds
                camera.setBoundsEnabled(true);

                return GameScene.this;
            }
        });

        levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY) {
            public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
                final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
                final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
                final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);

                final Sprite levelObject;


                if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1)) {
                    levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
                    PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyDef.BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
                } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2)) {
                    levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
                    final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyDef.BodyType.StaticBody, FIXTURE_DEF);
                    body.setUserData("platform2");
                    physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
                } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3)) {
                    levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
                    final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyDef.BodyType.StaticBody, FIXTURE_DEF);
                    body.setUserData("platform3");
                    physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
                } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN)) {
                    levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom) {
                        @Override
                        protected void onManagedUpdate(float pSecondsElapsed) {
                            super.onManagedUpdate(pSecondsElapsed);

                            if (player.collidesWith(this) || player2.collidesWith(this)) {
                                addToScore(10);
                                this.setVisible(false);
                                this.setIgnoreUpdate(true);
                            }
                            if(bulletShooted){
                                if(mBulletSprite.collidesWith(this)){
                                    this.setVisible(false);
                                    this.setIgnoreUpdate(true);
                                }
                            }
                        }
                    };
                    levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
                } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
                    player = new Player(x, y, vbom, camera, physicsWorld, "player", myBoolean.get()) {
                        @Override
                        public void onDie() {
                            if (myBoolean.get() == true) {
                                if (!gameOverDisplayed) {
                                    displayGameOverText();
                                }
                            } else {
                                if (!teamMateDeadDisplay) {
                                    displayTeamMateDeadText();
                                }
                            }
                        }
                    };
                    thisIsSecond = !myBoolean.get();
                    levelObject = player;
                } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER2)) {
                    player2 = new Player(x, y, vbom, camera, physicsWorld, "player2", thisIsSecond) {
                        @Override
                        public void onDie() {
                            if (ifFirst == false) {
                                if (!gameOverDisplayed) {
                                    displayGameOverText();
                                }
                            } else {
                                if (!teamMateDeadDisplay) {
                                    displayTeamMateDeadText();
                                }
                            }
                        }
                    };
//                    engine.registerUpdateHandler(new TimerHandler(2.0f, new ITimerCallback() {
//                        public void onTimePassed(final TimerHandler pTimerHandler) {
//                            pTimerHandler.reset();
//                            unregisterUpdateHandler(pTimerHandler);
//                            SocketIO.getInstance().emit("updatePosition", player2.getX(), player2.getY());
//                        }
//                    }));
                    levelObject = player2;
                } else {
                    throw new IllegalArgumentException();
                }

                levelObject.setCullingEnabled(true);

                return levelObject;
            }
        });

        levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
    }
    AnimatedSprite mBulletSprite;
    private boolean bulletShooted = false;
    public void spawnBullet(){
        bulletShooted = true;
        mBulletSprite = new AnimatedSprite(player2.getX(), player2.getY() + player2.getHeight() / 4,
                ResourcesManager.getInstance().bullet, vbom);
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);

        Body mBulletBody = PhysicsFactory.createCircleBody(physicsWorld, mBulletSprite, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
        mBulletBody.setUserData("metak");

        mBulletBody.setBullet(true);

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(mBulletSprite, mBulletBody, true, true));
        mBulletSprite.setUserData("metak");
        mBulletBody.setLinearVelocity(1 * 20, 0);

        attachChild(mBulletSprite);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (pSceneTouchEvent.isActionDown())
        {

            if (!firstTouch)
            {
                if(myBoolean.get() == true){
                    player.setRunning();
                }else{
                    player2.setRunning();
                }

                firstTouch = true;
                SocketIO.getInstance().emit("player_running");
            }
            else
            {
                SocketIO.getInstance().emit("player_jump");
                if(myBoolean.get() == true){
                    player.jump();
                }else{
                    player2.jump();
                }
            }
        }
        return false;
    }

    private ContactListener contactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            public void beginContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

//                if(x1.getBody().getUserData()!=null && x2.getBody().getUserData() !=null){
//                    if((x2.getBody().getUserData().equals("player") && x1.getBody().getUserData().equals("player2")) ||
//                            (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("player2"))
//                            ){
//
//                        x1.setSensor(true);
//                        x2.setSensor(true);
//                    }
//                } not working :(
                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                    if (x2.getBody().getUserData().equals("player"))
                    {
                        player.increaseFootContacts();
                    }
                    if (x2.getBody().getUserData().equals("player2"))
                    {
                        player2.increaseFootContacts();
                    }
                }
                if (x1.getBody().getUserData().equals("platform3") && (x2.getBody().getUserData().equals("player") || (x2.getBody().getUserData().equals("player2")) ) )
                {
                    x1.getBody().setType(BodyDef.BodyType.DynamicBody);
                }
                if (x1.getBody().getUserData().equals("platform2") && (x2.getBody().getUserData().equals("player") || (x2.getBody().getUserData().equals("player2")) ))
                {
                    engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback() {
                        public void onTimePassed(final TimerHandler pTimerHandler) {
                            pTimerHandler.reset();
                            engine.unregisterUpdateHandler(pTimerHandler);
                            x1.getBody().setType(BodyDef.BodyType.DynamicBody);
                        }
                    }));
                }
            }

            public void endContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                    if (x2.getBody().getUserData().equals("player"))
                    {
                        player.decreaseFootContacts();
                    }
                    if (x2.getBody().getUserData().equals("player2"))
                    {
                        player2.decreaseFootContacts();
                    }
                }
            }

            public void preSolve(Contact contact, Manifold oldManifold)
            {

            }

            public void postSolve(Contact contact, ContactImpulse impulse)
            {

            }
        };
        return contactListener;
    }
}
