package de.haw.sea2.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

public class CharacterScreen implements Screen {

    private final StudentsQuest context;
    private final Stage stage;

    private Array<ImageButton> buttons;
    private Image background;

    public CharacterScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();
        createUI();
    }

    private void createUI() {
        this.background = new Image(this.context.getAssetManager().get(AssetPaths.CHAR_SCREEN.getPath(), Texture.class));
        ImageButton boyButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.BOY_BUTTON.getPath(), Texture.class)));
        ImageButton girlButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.GIRL_BUTTON.getPath(), Texture.class)));

        this.background.setSize(16f, 9f);
        boyButton.setSize(2f, 2f);
        boyButton.setPosition(5.5f, 2f);

        girlButton.setSize(2f, 2f);
        girlButton.setPosition(8f, 2f);

        this.buttons = new Array<>();
        this.buttons.add(boyButton);
        this.buttons.add(girlButton);

        boyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.chosenPlayerAnimationAtlas = AssetPaths.BOY_PLAYER_ATLAS;
                context.getScreenManager().showScreen(ScreenType.INFO_SCREEN_LV1);
            }
        });

        girlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.chosenPlayerAnimationAtlas = AssetPaths.GIRL_PLAYER_ATLAS;
                context.getScreenManager().showScreen(ScreenType.INFO_SCREEN_LV1);
            }
        });
    }

    @Override
    public void show() {
        this.stage.addActor(this.background);
        for (ImageButton b : this.buttons) {
            this.stage.addActor(b);
        }
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
    }

    @Override
    public void dispose() {

    }
}
