package info.admirsabanovic.arenafight.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.github.nkzawa.emitter.Emitter;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.List;

import info.admirsabanovic.arenafight.tcp.SocketIO;

/**
 * Created by asabanovic on 5/15/15.
 */
public abstract class Player_MP extends AnimatedSprite {
    public Player_MP(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
    {
        super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
        createPhysics(camera, physicsWorld);
        startListening();
    }
    public abstract void onDie();
    public void setRunning()
    {
        canRun = true;

        final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };

        animate(PLAYER_ANIMATE, 0, 2, true);
    }
    public void jump()
    {
        if (footContacts < 1)
        {
            return;
        }
        body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12));
    }
    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
    {
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

        body.setUserData("player2");
        body.setFixedRotation(true);

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                super.onUpdate(pSecondsElapsed);
                camera.onUpdate(0.1f);

                if (getY() <= 0) {
                    onDie();
                }

                if (canRun) {
                    body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y));
                }
            }
        });
    }

    private void startListening(){
        SocketIO.getInstance().on("jump", jumpHandler);
        SocketIO.getInstance().on("run", runHandler);
    }


    private Emitter.Listener jumpHandler = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jump();
                }
            }).start();
        }
    };

    private Emitter.Listener runHandler = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setRunning();
                }
            }).start();
        }
    };


    public void increaseFootContacts()
    {
        footContacts++;
    }

    public void decreaseFootContacts()
    {
        footContacts--;
    }

    // ---------------------------------------------
    // VARIABLES
    // ---------------------------------------------

    private Body body;
    private boolean canRun = false;
    private int footContacts = 0;

}
