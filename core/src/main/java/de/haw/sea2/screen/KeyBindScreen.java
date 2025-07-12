package de.haw.sea2.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

public class KeyBindScreen implements Screen, ScreenKeyInputListener {

    private final StudentsQuest context;
    private final Stage stage;

    private ImageButton backToMainMenuButton;
    private Image background;

    public KeyBindScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();
        createUI();
    }

    private void createUI() {
        this.background = new Image(this.context.getAssetManager().get(AssetPaths.KEY_BIND_SCREEN.getPath(), Texture.class));
        this.backToMainMenuButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.HAUPT_MENU_TEXT_BUTTON.getPath(), Texture.class)));

        this.background.setSize(16f, 9f);
        this.backToMainMenuButton.setSize(3f, 1f);
        this.backToMainMenuButton.setPosition(12f, 0.7f);

        this.backToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
            }
        });
    }

    @Override
    public void show() {
        this.context.getInputManager().addKeyInputListener(this);
        this.stage.addActor(this.background);
        this.stage.addActor(this.backToMainMenuButton);
    }

    @Override
    public void render(float delta) {
        StageUtils.prepareAndDraw(this.context, delta);
    }

    @Override
    public void resize(int width, int height) {
        this.context.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        this.stage.clear();
        context.getInputManager().removeKeyInputListener(this);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.PAUSE) {
            context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
        }
    }


    @Override
    public void keyUp(InputManager manager, GameKey key) {

    }
}
