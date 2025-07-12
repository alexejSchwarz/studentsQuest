package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.lifeCicle.GameState;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

/**
 * GameOverScreen zeigt das Game Over (Spiel verloren) an, wenn der Spieler alle Herzen verloren hat.
 *
 * <p>
 * Dieser Screen verwendet ein Hintergrundbild mit klickbaren Bereichen für die Buttons
 * "HAUPTMENÜ" und "NEU STARTEN".
 * </p>
 */
public class GameOverScreen implements Screen {

    // Verweis auf den Spielkontext, um auf zentrale Spielkomponenten zuzugreifen.
    private final StudentsQuest context;
    // Stage zur Verwaltung und Darstellung von UI-Elementen.
    private final Stage stage;

    // Hintergrundtextur für das Game Over Bild
    private final Texture gameOverBackground;
    // Hintergrundbild als Image-Actor
    private final Image gameOverImage;

    // ImageButtons für die klickbaren Bereiche
    private final ImageButton hauptmenuButton;
    private final ImageButton neuStartenButton;

    /**
     * Konstruktor: Initialisiert den GameOverScreen mit dem gegebenen Spielkontext.
     *
     * @param context Der Spielkontext
     */
    public GameOverScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();

        // Lade alle benötigten Assets
        context.getAssetManager().load(AssetPaths.GAMEOVER_SCREEN.getPath(), Texture.class);
        context.getAssetManager().finishLoading();

        gameOverBackground = context.getAssetManager().get(AssetPaths.GAMEOVER_SCREEN.getPath());
        gameOverImage = new Image(gameOverBackground);
        gameOverImage.setSize(16f, 9f);

        hauptmenuButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.BACK_TO_MAIN_MENU_BUTTON.getPath(), Texture.class)));
        neuStartenButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.RESTART_BUTTON.getPath(), Texture.class)));

        hauptmenuButton.setPosition(5.5f, 2.0f);
        hauptmenuButton.setSize(2f, 2f);

        neuStartenButton.setPosition(8f, 2.0f);
        neuStartenButton.setSize(2f, 2f);

        setupButtonListeners();
    }

    /**
     * Richtet die Click-Listener für die Buttons ein.
     */
    private void setupButtonListeners() {
        // Hauptmenü Button Listener
        hauptmenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Hauptmenü button pressed");
                // Wechsle zum Hauptmenü-Screen
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
            }
        });

        // Neu Starten Button Listener
        neuStartenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Logge, dass der Neu Starten Button gedrückt wurde
                LoggerUtil.log(LogCategory.LOG, this, "Neu Start button pressed");
                context.stateMachine.changeState(GameState.RESTART);
                context.getAudioManager().resetCurrentMusic();
                context.getScreenManager().showScreen(ScreenType.CHAR_SCREEN);
            }
        });
    }

    /**
     * Wird aufgerufen, wenn der Screen angezeigt wird.
     * Richtet die Input-Verarbeitung ein und fügt die UI-Elemente hinzu.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(context.getInputManager(), stage));

        // Füge das Hintergrundbild und die Buttons zur Stage hinzu
        this.stage.addActor(this.gameOverImage);
        this.stage.addActor(this.hauptmenuButton);
        this.stage.addActor(this.neuStartenButton);

        LoggerUtil.log(LogCategory.DEBUG, this, "GameOverScreen wird angezeigt");
    }

    /**
     * Render-Methode, die bei jedem Frame aufgerufen wird.
     *
     * @param delta Zeit in Sekunden seit dem letzten Frame
     */
    @Override
    public void render(float delta) {
        StageUtils.prepareAndDraw(this.context, delta);
    }

    /**
     * Wird aufgerufen, wenn die Bildschirmgröße geändert wird.
     *
     * @param width  Neue Breite
     * @param height Neue Höhe
     */
    @Override
    public void resize(int width, int height) {
        // Aktualisiere den Viewport der Stage, damit UI-Elemente korrekt skaliert werden
        context.viewport.update(width, height, true);
    }

    /**
     * Wird aufgerufen, wenn der Screen pausiert wird.
     */
    @Override
    public void pause() {
        // Keine spezielle Aktion bei Pause erforderlich
    }

    /**
     * Wird aufgerufen, wenn der Screen wieder aufgenommen wird.
     */
    @Override
    public void resume() {
        // Keine spezielle Aktion bei Resume erforderlich
    }

    /**
     * Wird aufgerufen, wenn der Screen ausgeblendet wird.
     */
    @Override
    public void hide() {
        stage.clear();
        LoggerUtil.log(LogCategory.DEBUG, this, "GameOverScreen ausgeblendet");
    }

    /**
     * Gibt Ressourcen frei, wenn der Screen zerstört wird.
     */
    @Override
    public void dispose() {
        // Entsorge die Texturen, um Speicher freizugeben
        gameOverBackground.dispose();
    }
}
