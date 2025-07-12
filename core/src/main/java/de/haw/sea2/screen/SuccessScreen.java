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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.lifeCicle.GameState;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

/**
 * SuccessScreen zeigt den Erfolg (Level abgeschlossen) an, wenn ein Level erfolgreich beendet wurde.
 */
public class SuccessScreen implements Screen {

    // Verweis auf den Spielkontext, um auf zentrale Spielkomponenten zuzugreifen.
    private final StudentsQuest context;
    // Stage zur Verwaltung und Darstellung von UI-Elementen.
    private final Stage stage;
    // BitmapFont für die Anzeige von Text in der UI.
    private final BitmapFont font;

    private final Image background;

    private ImageButton hauptmenuButton;
    private ImageButton neuStartenButton;

    /**
     * Konstruktor: Initialisiert den SuccessScreen mit dem gegebenen Spielkontext.
     *
     * @param context Der Spielkontext
     */
    public SuccessScreen(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();
        // Erstelle ein neues BitmapFont für die Anzeige von Text
        this.font = new BitmapFont();

        // Schriftgröße anpassen: Verhindert runde Pixelpositionen für glattere Darstellung
        this.font.setUseIntegerPositions(false);
        // Berechne den Skalierungsfaktor basierend auf der Bildschirmhöhe und der Höhe der Viewport-Welt
        float scaleFactor = (this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 3.0f;
        // Setze den Skalierungsfaktor für den Font
        this.font.getData().setScale(scaleFactor);

        this.background = new Image(this.context.getAssetManager().get(AssetPaths.SUCCESS_SCREEN.getPath(), Texture.class));
        this.background.setSize(16f, 9f);

        // UI-Elemente initialisieren und konfigurieren
        setupUI();
    }

    /**
     * Richtet die UI-Elemente ein und positioniert sie auf dem Bildschirm.
     */
    private void setupUI() {

        // Definiere die UI-Stile für Labels und Buttons
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;

        // Erstelle das Titel-Label und setze die Ausrichtung und Farbe
        Label titleLabel = new Label("Level abgeschlossen!", labelStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.YELLOW);

        // Erstelle den Button für den Übergang zum nächsten Level
        neuStartenButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.RESTART_BUTTON.getPath(), Texture.class)));
        neuStartenButton.setSize(2f,2f);
        neuStartenButton.setPosition(8f, 1.5f);
        neuStartenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.stateMachine.changeState(GameState.RESTART);
                context.getScreenManager().showScreen(ScreenType.CHAR_SCREEN);
            }
        });

        // Erstelle den Button für den Wechsel zum Hauptmenü
        hauptmenuButton = new ImageButton(new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.BACK_TO_MAIN_MENU_BUTTON.getPath(), Texture.class)));
        hauptmenuButton.setSize(2f, 2f);
        hauptmenuButton.setPosition(5.5f, 1.5f);
        hauptmenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Wechsle zum Hauptmenü-Screen
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
            }
        });
    }

    /**
     * Wird aufgerufen, wenn der Screen angezeigt wird.
     * Richtet die Input-Verarbeitung ein, indem ein InputMultiplexer verwendet wird.
     */
    @Override
    public void show() {
        this.stage.addActor(this.background);
        this.stage.addActor(this.hauptmenuButton);
        this.stage.addActor(this.neuStartenButton);
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
        this.context.viewport.update(width, height, true);
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
        this.stage.clear();
    }

    /**
     * Gibt Ressourcen frei, wenn der Screen zerstört wird.
     */
    @Override
    public void dispose() {
        // Entsorge den Font, um Speicher freizugeben
        font.dispose();
    }
}
