package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.utils.ScreenUtils;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;

/**
 * SuccessScreen zeigt den Erfolg (Level abgeschlossen) an, wenn ein Level erfolgreich beendet wurde.
 */
public class SuccessScreen implements Screen, KeyInputListener {

    // Verweis auf den Spielkontext, um auf zentrale Spielkomponenten zuzugreifen.
    private final StudentsQuest context;
    // Stage zur Verwaltung und Darstellung von UI-Elementen.
    private final Stage stage;
    // BitmapFont für die Anzeige von Text in der UI.
    private final BitmapFont font;

    /**
     * Konstruktor: Initialisiert den SuccessScreen mit dem gegebenen Spielkontext.
     *
     * @param context Der Spielkontext
     */
    public SuccessScreen(StudentsQuest context) {
        this.context = context;
        // Erstelle eine neue Stage mit dem aktuellen Viewport
        this.stage = new Stage(context.viewport);
        // Erstelle ein neues BitmapFont für die Anzeige von Text
        this.font = new BitmapFont();

        // Schriftgröße anpassen: Verhindert runde Pixelpositionen für glattere Darstellung
        this.font.setUseIntegerPositions(false);
        // Berechne den Skalierungsfaktor basierend auf der Bildschirmhöhe und der Höhe der Viewport-Welt
        float scaleFactor = (this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 3.0f;
        // Setze den Skalierungsfaktor für den Font
        this.font.getData().setScale(scaleFactor);

        // UI-Elemente initialisieren und konfigurieren
        setupUI();
    }

    /**
     * Richtet die UI-Elemente ein und positioniert sie auf dem Bildschirm.
     */
    private void setupUI() {
        // Erstelle ein Table-Layout, das für die Positionierung der UI-Elemente verwendet wird
        Table table = new Table();
        table.setFillParent(true); // Table füllt den gesamten Stage-Bereich

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
        TextButton resumeButton = new TextButton("Weiter zum nächsten Level", buttonStyle);
        resumeButton.getLabel().setAlignment(Align.center);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Wechsle zum Spiel-Screen, um das nächste Level zu starten
                context.getScreenManager().showScreen(ScreenType.GAME);
            }
        });

        // Erstelle den Button für den Wechsel zum Hauptmenü
        TextButton mainMenuButton = new TextButton("Hauptmenü", buttonStyle);
        mainMenuButton.getLabel().setAlignment(Align.center);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Wechsle zum Hauptmenü-Screen
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
            }
        });

        // Baue das Layout des Tables auf, sodass alle Elemente zentriert und mit Abständen angeordnet sind
        table.center();
        table.add(titleLabel).width(4.0f).align(Align.center).padBottom(1.0f).padTop(1.0f).row();
        table.add(resumeButton).width(4.0f).height(1.0f).align(Align.center).padBottom(0.5f).row();
        table.add(mainMenuButton).width(4.0f).height(1.0f).align(Align.center).row();

        // Füge das Table dem Stage hinzu, damit es gerendert wird
        stage.addActor(table);
    }

    /**
     * Wird aufgerufen, wenn der Screen angezeigt wird.
     * Richtet die Input-Verarbeitung ein, indem ein InputMultiplexer verwendet wird.
     */
    @Override
    public void show() {
        // Setze den InputProcessor, damit sowohl das InputManager-Objekt als auch die Stage Eingaben erhalten
        Gdx.input.setInputProcessor(new InputMultiplexer(context.getInputManager(), stage));
        // Füge diesen Screen als KeyInputListener hinzu, um Tasteneingaben zu verarbeiten
        context.getInputManager().addKeyInputListener(this);
    }

    /**
     * Render-Methode, die bei jedem Frame aufgerufen wird.
     *
     * @param delta Zeit in Sekunden seit dem letzten Frame
     */
    @Override
    public void render(float delta) {
        // Lösche den Bildschirm und fülle ihn mit einem halbtransparenten schwarzen Hintergrund
        ScreenUtils.clear(0, 0, 0, 0.8f);

        // Wende den Viewport an und setze die Kamera für die Stage
        context.viewport.apply();
        stage.getBatch().setProjectionMatrix(context.viewport.getCamera().combined);

        // Logge die Anzahl der aktuell aktiven Actors auf der Stage (Debug-Zwecke)
        LoggerUtil.log(LogCategory.DEBUG, this, "SuccessScreen rendering: " + stage.getActors().size + " actors on stage");

        // Aktualisiere die Stage und zeichne sie neu
        stage.act(delta);
        stage.draw();
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
        stage.getViewport().update(width, height, true);
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
        // Entferne diesen Screen als KeyInputListener, wenn er nicht mehr aktiv ist
        context.getInputManager().removeKeyInputListener(this);
    }

    /**
     * Gibt Ressourcen frei, wenn der Screen zerstört wird.
     */
    @Override
    public void dispose() {
        // Entsorge die Stage und den Font, um Speicher freizugeben
        stage.dispose();
        font.dispose();
    }

    /**
     * Verarbeitet Tastatureingaben: Wird aufgerufen, wenn eine Taste gedrückt wird.
     *
     * @param manager Der InputManager
     * @param key     Die gedrückte GameKey
     */
    @Override
    public void keyDown(InputManager manager, GameKey key) {
        // Wenn die Pause-Taste erneut gedrückt wird, kehre zum vorherigen Screen zurück
        if (key == GameKey.PAUSE) {
            context.getScreenManager().returnToPreviousScreen();
        }
    }

    /**
     * Verarbeitet Tastatureingaben: Wird aufgerufen, wenn eine Taste losgelassen wird.
     *
     * @param manager Der InputManager
     * @param key     Die losgelassene GameKey
     */
    @Override
    public void keyUp(InputManager manager, GameKey key) {
        // Keine Aktion erforderlich, wenn die Taste losgelassen wird
    }
}
