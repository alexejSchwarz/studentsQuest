package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

/**
 * Pause-Screen, der angezeigt wird, wenn das Spiel pausiert wird.
 */
public class PauseGameScreen implements Screen, ScreenKeyInputListener {

    private final StudentsQuest context;
    private final Stage stage;
    private final Table table;

    private Image background;

    /**
     * Erstellt einen neuen PauseGameScreen.
     *
     * @param context Der Spielkontext
     */
    public PauseGameScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();
        this.table = new Table();
        setupUI();
    }

    /**
     * Richtet die UI-Elemente ein und positioniert sie.
     */
    private void setupUI() {
        table.setFillParent(true);

        this.background = new Image(this.context.getAssetManager().get(AssetPaths.PAUSE_SCREEN.getPath(), Texture.class));
        this.background.setSize(16f,9f);

        // Buttons erstellen
        ImageButton resumeButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.FORTSETZEN_BUTTON.getPath(), Texture.class)));
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().returnToPreviousScreen();
            }
        });

        ImageButton mainMenuButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.HAUPT_MENU_TEXT_BUTTON.getPath(), Texture.class)));
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
                context.getAudioManager().playAudio(Audio.START_SCREEN_MUSIC);
            }
        });


        // Layout aufbauen mit besser zentrierten Elementen
        table.center();
        table.add(resumeButton).width(4.0f).height(2.0f).align(Align.center).padBottom(0.5f).row();
        table.add(mainMenuButton).width(4.0f).height(2.0f).align(Align.center).row();
    }

    @Override
    public void show() {
        context.getInputManager().addKeyInputListener(this);
        this.stage.addActor(background);
        this.stage.addActor(table);
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
        // Keine Aktion erforderlich
    }

    @Override
    public void resume() {
        // Keine Aktion erforderlich
    }

    @Override
    public void hide() {
        this.context.getInputManager().removeKeyInputListener(this);
        this.stage.clear();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.PAUSE) {
            context.getScreenManager().returnToPreviousScreen();
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
        // Keine Aktion erforderlich
    }
}
