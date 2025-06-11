package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.view.ui.StageUtils;

/**
 * Pause-Screen, der angezeigt wird, wenn das Spiel pausiert wird.
 */
public class PauseGameScreen implements Screen, ScreenKeyInputListener {

    private final StudentsQuest context;
    private final Stage stage;
    private final BitmapFont font;
    private final Table table;

    /**
     * Erstellt einen neuen PauseGameScreen.
     *
     * @param context Der Spielkontext
     */
    public PauseGameScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();
        this.font = new BitmapFont();

        // Schriftgröße anpassen
        this.font.setUseIntegerPositions(false);
        float scaleFactor = (this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 3.0f;
        this.font.getData().setScale(scaleFactor);
        this.table = new Table();

        setupUI();
    }

    /**
     * Richtet die UI-Elemente ein und positioniert sie.
     */
    private void setupUI() {
        table.setFillParent(true);

        // UI-Stile definieren
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;

        // Titel mit expliziter Zentrierung
        Label titleLabel = new Label("PAUSE", labelStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.YELLOW);

        // Buttons erstellen
        TextButton resumeButton = new TextButton("Fortsetzen", buttonStyle);
        resumeButton.getLabel().setAlignment(Align.center);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().returnToPreviousScreen();
            }
        });

        TextButton mainMenuButton = new TextButton("Hauptmenü", buttonStyle);
        mainMenuButton.getLabel().setAlignment(Align.center);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
                context.getAudioManager().playAudio(Audio.START_SCREEN_MUSIC);
            }
        });


        // Layout aufbauen mit besser zentrierten Elementen
        table.center();
        table.add(titleLabel).width(4.0f).align(Align.center).padBottom(1.0f).padTop(1.0f).row();
        table.add(resumeButton).width(4.0f).height(1.0f).align(Align.center).padBottom(0.5f).row();
        table.add(mainMenuButton).width(4.0f).height(1.0f).align(Align.center).row();
    }

    @Override
    public void show() {
        context.getInputManager().addKeyInputListener(this);
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
        font.dispose();
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
