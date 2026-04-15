package edu.ssmprn.gliders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.List;

public class Main extends ApplicationAdapter implements InputProcessor, GestureListener {

    private static final int MIN_DISTANCE = 50;
    private static final int MAX_DISTANCE = 150;
    private static final float FRAME_DURATION = 0.05f;
    private static final int ANIM_COLS = 4;
    private static final int ANIM_ROWS = 4;

    private PerspectiveCamera cam;

    private Environment environment;

    private Stage stage;

    private AssetManager assets;
    private boolean loading;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance instance;

    private double distance;
    private double rotatorY;
    private double rotatorX;
    private long lastDown;
    private long lastTime;
    private int lastX;
    private int lastY;
    private boolean touched;

    private String path = "mod/glider/st1_base.g3db";
    private String anim = null;
    private Animation<TextureRegion> animationTexture = null;
    private int animationIndex = 0;
    private float stateTime = 0f;

    private final GliderLoader gliderLoader = new GliderLoader();
    private final ButtonManager buttonManager = new ButtonManager(new ButtonManager.ButtonContainer() {

        @Override
        public SelectBox<String> createButton(int index) {
            Skin skin = LocalizedSkins.getSkinForSelectedBox();
            SelectBox<String> button = new SelectBox<>(skin);
            button.setWidth(400);
            button.setX(Gdx.graphics.getWidth() - 400);
            button.setY(Gdx.graphics.getHeight() - button.getHeight() * (index + 1));
            button.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gliderLoader.selectPath(index, button.getSelectedIndex());
                    updateGliderList();

                    loading = true;

                    ModelItem model = gliderLoader.getSelectedGlider();
                    path = model.getPath();
                    anim = model.getAnim();
                    animationIndex = model.getIndex();

                    assets.load(path, Model.class);
                    if (anim != null) assets.load(anim, Texture.class);
                }
            });
            return button;
        }

        @Override
        public void addButton(SelectBox<String> button, int index) {
            stage.addActor(button);
        }

        @Override
        public void removeButton(SelectBox<String> button) {
            button.remove();
        }
    });

    @Override
    public void create() {
        gliderLoader.init();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -40f, -32f, -20f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -40f, 32f, -20f));

        distance = 70;
        rotatorY = 135;
        rotatorX = 45;
        lastTime = -1;
        lastDown = Integer.MIN_VALUE / 2;
        touched = false;

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, (float) (distance * Math.sin(rotatorX / 180 * Math.PI)), (float) (distance * Math.cos(rotatorX / 180 * Math.PI)));
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        assets = new AssetManager();
        assets.load(path, Model.class);
        loading = true;

        modelBatch = new ModelBatch();

        stage = new Stage();

        updateGliderList();

        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(stage);
        im.addProcessor(this);

        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render() {
        if (loading) {
            if (!assets.update()) return;
            if (!assets.isLoaded(path)) return;
            if (anim != null && !assets.isLoaded(anim)) return;

            model = assets.get(path, Model.class);
            if (anim != null) {
                animationTexture = getAnimations(anim);
            } else {
                animationTexture = null;
            }
            instance = new ModelInstance(model);
            stateTime = 0f;
            loading = false;
        }

        cam.position.set(0f, (float) (distance * Math.sin(rotatorX / 180 * Math.PI)), (float) (distance * Math.cos(rotatorX / 180 * Math.PI)));
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDown > 5000 && !touched) {
            if (lastTime != -1) {
                rotatorY += (currentTime - lastTime) / 1000.0 * 30.0;
            }
        }
        lastTime = currentTime;
        instance.transform.setToRotation(Vector3.Y, (int) rotatorY);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();

        if (animationTexture != null) {
            TextureRegion currentFrame = animationTexture.getKeyFrame(stateTime, true);
            Attribute attr = instance.materials.get(animationIndex).get(TextureAttribute.Diffuse);
            ((TextureAttribute) attr).set(currentFrame);
        }

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        model.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touched = true;

        if (pointer != 0) {
            return false;
        }

        lastX = screenX;
        lastY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touched = false;

        lastDown = System.currentTimeMillis();

        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer != 0) {
            return false;
        }

        rotatorY += ((double) (screenX - lastX)) / Gdx.graphics.getWidth() * 180;
        rotatorX += ((double) (screenY - lastY)) / Gdx.graphics.getHeight() * 90;
        if (rotatorX > 90) {
            rotatorX = 90;
        } else if (rotatorX < -90) {
            rotatorX = -90;
        }
        lastX = screenX;
        lastY = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        double newDistance = distance;
        if (amountY > 0.01) {
            newDistance -= 5;
        } else if (amountY < 0.01) {
            newDistance += 5;
        }
        distance = computeDistance(newDistance);
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float begin, float end) {
        double newDistance = distance - (end - begin) / 20;
        distance = computeDistance(newDistance);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void updateGliderList() {
        List<List<String>> menu = gliderLoader.getMenu();
        List<Integer> selectedPath = gliderLoader.getSelectedPath();
        buttonManager.setButtons(menu, selectedPath);
    }

    private double computeDistance(double newDistance) {
        if (newDistance < MIN_DISTANCE) return MIN_DISTANCE;
        if (newDistance > MAX_DISTANCE) return MAX_DISTANCE;
        return newDistance;
    }

    private Animation<TextureRegion> getAnimations(String path) {
        if (path == null) return null;

        Texture texture = assets.get(path, Texture.class);
        int tileWidth = texture.getWidth() / ANIM_ROWS;
        int tileHeight = texture.getHeight() / ANIM_COLS;
        TextureRegion[][] tmp = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] walkFrames = new TextureRegion[ANIM_COLS * ANIM_ROWS];
        for (int i = 0; i < ANIM_COLS; i++) {
            System.arraycopy(tmp[i], 0, walkFrames, i * ANIM_ROWS, ANIM_ROWS);
        }

        return new Animation<>(FRAME_DURATION, walkFrames);
    }
}
