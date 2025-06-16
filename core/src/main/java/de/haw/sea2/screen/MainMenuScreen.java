package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

/**
 * MainMenuScreen ist der Startbildschirm des Spiels, der ein Hauptmenü
 * mit verschiedenen Schaltflächen darstellt.
 *
 * <p>
 * Das Menü zeigt Buttons für Start, Key-Binds, Einstellungen, Credits, Story und Tutorial an.
 * Durch Drücken des Start-Buttons wird der GameScreen mit einem Ladebildschirm aufgerufen.
 * </p>
 */
public class MainMenuScreen implements Screen, ScreenKeyInputListener {

    // Konstanten zur Positionierung und Skalierung der Buttons
    private static final float START_BUTTON_Y_VALUE = 4.5f;
    private static final float START_BUTTON_X_VALUE = 5f;
    private static final float PADDING = 0.05f;
    private static final float BUTTON_SCALE = 2f;
    private static final int BUTTONS_PER_ROW = 3;

    private final StudentsQuest context;
    private final Stage stage;

    // TextureAtlas enthält die Schaltflächen-Grafiken
    private final TextureAtlas buttonAtlas;

    // TextureRegionDrawables für die einzelnen Buttons
    private final TextureRegionDrawable startButtonRegion;
    private final TextureRegionDrawable keyBindsRegion;
    private final TextureRegionDrawable settingsRegion;
    private final TextureRegionDrawable creditsRegion;
    private final TextureRegionDrawable storyRegion;
    private final TextureRegionDrawable tutorialRegion;

    // Hintergrundtextur
    private final Texture background;

    //Schriftzug des Menüs
    private final Texture heading;

    private final Image backgroundImage;
    private final Image headingImage;

    private Array<ImageButton> imageButtons;

    /**
     * Konstruktor, der den Spielkontext initialisiert und die UI-Komponenten lädt.
     *
     * @param context Der übergeordnete Spielkontext
     */
    public MainMenuScreen(StudentsQuest context) {
        this.context = context;
        this.stage = this.context.getStage();

        // Main Menu Assets Loading
        context.getAssetManager().load(AssetPaths.BUTTON_ATLAS.getPath(), TextureAtlas.class);
        context.getAssetManager().load(AssetPaths.MAIN_MENU_BACKGROUND.getPath(), Texture.class);
        context.getAssetManager().load(AssetPaths.MAIN_MENU_HEADING.getPath(), Texture.class);
        context.getAssetManager().load(Audio.START_SCREEN_MUSIC.getPath(), Music.class);
        context.getAssetManager().load(AssetPaths.SLIDER_BACKGROUND.getPath(), Texture.class);
        context.getAssetManager().load(AssetPaths.SLIDER_KNOB.getPath(), Texture.class);
        context.getAssetManager().load(AssetPaths.HOME_BUTTON.getPath(), Texture.class);
        context.getAssetManager().finishLoading();

        this.buttonAtlas = context.getAssetManager().get(AssetPaths.BUTTON_ATLAS.getPath());

        // Initialisiere die Drawable-Regionen für die Buttons
        this.startButtonRegion = new TextureRegionDrawable(buttonAtlas.findRegion("start_button"));
        this.keyBindsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("keybinds_button"));
        this.creditsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("credits_button"));
        this.settingsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("settings_button"));
        this.storyRegion = new TextureRegionDrawable(buttonAtlas.findRegion("story_button"));
        this.tutorialRegion = new TextureRegionDrawable(buttonAtlas.findRegion("tutorial_button"));

        // Lade und füge den Hintergrund hinzu
        this.background = context.getAssetManager().get(AssetPaths.MAIN_MENU_BACKGROUND.getPath());
        this.backgroundImage = new Image(this.background);
        this.backgroundImage.setSize(17f, 12f);

        //Lade und füge die Überschrift hinzu
        this.heading = context.getAssetManager().get(AssetPaths.MAIN_MENU_HEADING.getPath());
        this.headingImage = new Image(this.heading);
        this.headingImage.setSize(9f, 1f);
        this.headingImage.setPosition(16f / 2f - 4.3f, START_BUTTON_Y_VALUE + BUTTON_SCALE * 1.3f);

        // Erstelle und positioniere die Buttons
        createButtons();
    }

    /**
     * Diese Methode wird aufgerufen, wenn dieser Screen angezeigt wird.
     * Aktuell werden hier keine zusätzlichen Aktionen benötigt.
     */
    @Override
    public void show() {

        context.getAudioManager().playAudio(Audio.START_SCREEN_MUSIC);
        context.getInputManager().addKeyInputListener(this);

        LoggerUtil.log(LogCategory.DEBUG, this, "MainMenuScreen wird angezeigt, InputProcessor gesetzt");
        this.stage.addActor(this.backgroundImage);
        this.stage.addActor(headingImage);
        this.imageButtons.forEach(this.stage::addActor);
    }

    /**
     * Render-Methode, die in jedem Frame aufgerufen wird.
     *
     * @param delta Zeit in Sekunden seit dem letzten Frame
     */
    @Override
    public void render(float delta) {
        StageUtils.prepareAndDraw(this.context, delta);
    }

    /**
     * Erstellt, positioniert und fügt die Schaltflächen dem Stage hinzu.
     * Die Buttons werden in zwei Reihen mit minimalem Abstand (PADDING) angeordnet.
     */
    private void createButtons() {
        // Erstelle die einzelnen Buttons
        ImageButton startButton = new ImageButton(startButtonRegion);
        ImageButton keyBindsButton = new ImageButton(keyBindsRegion);
        ImageButton settingsButton = new ImageButton(settingsRegion);
        ImageButton creditsButton = new ImageButton(creditsRegion);
        ImageButton storyButton = new ImageButton(storyRegion);
        ImageButton tutorialButton = new ImageButton(tutorialRegion);

        // Füge die Buttons einer Liste hinzu, um sie leichter verarbeiten zu können
        this.imageButtons = new Array<>();
        this.imageButtons.add(startButton);
        this.imageButtons.add(keyBindsButton);
        this.imageButtons.add(settingsButton);
        this.imageButtons.add(creditsButton);
        this.imageButtons.add(storyButton);
        this.imageButtons.add(tutorialButton);

        // Setze die Größe aller Buttons auf den definierten Wert
        for (ImageButton button : this.imageButtons) {
            button.setSize(BUTTON_SCALE, BUTTON_SCALE);
        }

        // Positioniere die Buttons in zwei Reihen (erste Reihe: 3 Buttons, zweite Reihe: 3 Buttons)
        float currentX = START_BUTTON_X_VALUE;
        float currentY = START_BUTTON_Y_VALUE;
        for (int i = 0; i < imageButtons.size; i++) {
            // Bei Button 4 (Index 3) in die nächste Reihe wechseln
            if (i == BUTTONS_PER_ROW) {
                currentX = START_BUTTON_X_VALUE;
                // Verschiebe die Y-Position um die Höhe des Buttons plus PADDING
                currentY -= (BUTTON_SCALE + PADDING);
            }
            imageButtons.get(i).setPosition(currentX, currentY);
            // Erhöhe currentX für den nächsten Button
            currentX += BUTTON_SCALE + PADDING;
        }

        // Beispiel-Listener: Wenn der Start-Button berührt wird, wird zum GameScreen gewechselt
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getAudioManager().stopCurrentMusic();
                context.getScreenManager().showScreen(ScreenType.LOADING);
            }
        });

        // Listener für die anderen Buttons: Loggen der Betätigung
        keyBindsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Keybindings button pressed");
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Settings button pressed");
                context.getScreenManager().showScreen(ScreenType.SETTINGS);
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Credits button pressed");
            }
        });

        storyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Story button pressed");
            }
        });

        tutorialButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Tutorial button pressed");
            }
        });
    }

    /**
     * Aktualisiert den Viewport bei Änderung der Fenstergröße.
     *
     * @param width  Neue Breite des Fensters
     * @param height Neue Höhe des Fensters
     */
    @Override
    public void resize(int width, int height) {
        context.viewport.update(width, height, true);
    }

    /**
     * Wird aufgerufen, wenn das Spiel pausiert wird.
     */
    @Override
    public void pause() {
    }

    /**
     * Wird aufgerufen, wenn das Spiel fortgesetzt wird.
     */
    @Override
    public void resume() {
    }

    /**
     * Wird aufgerufen, wenn dieser Screen nicht mehr aktiv ist.
     */
    @Override
    public void hide() {
       //Funktioniert gerade noch nicht richtig, da der Game Screen zu schnell geladen wird.
       //Wenn man useSimulatedLoading im Loading Screen auf true setzt, funktioniert es.
        stage.clear();
        context.getInputManager().removeKeyInputListener(this);
        LoggerUtil.log(LogCategory.LOG,this,"Stage cleared!");
        LoggerUtil.log(LogCategory.DEBUG, this, "MainMenuScreen hidden");
    }

    /**
     * Gibt Ressourcen frei, wenn dieser Screen zerstört wird.
     */
    @Override
    public void dispose() {
        buttonAtlas.dispose();
        background.dispose();
        heading.dispose();
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.PAUSE) {
            context.getScreenManager().showScreen(ScreenType.SETTINGS);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {

    }
}
