package de.haw.sea2.screen;

import java.util.ArrayList;
import java.util.List;

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

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
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
public class MainMenuScreen implements Screen {

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

    // Konstanten zur Positionierung und Skalierung der Buttons
    private static final float START_BUTTON_Y_VALUE = 4.5f;
    private static final float startButtonXValue = 5f;
    private static final float padding = 0.05f;
    private static final float buttonScale = 2f;
    private static final int BUTTONS_PER_ROW = 3;

    private final Image backgroundImage;
    private final Image headingImage;

    private  List<ImageButton> imageButtons;

    /**
     * Konstruktor, der den Spielkontext initialisiert und die UI-Komponenten lädt.
     *
     * @param context Der übergeordnete Spielkontext
     */
    public MainMenuScreen(StudentsQuest context) {
        this.context = context;
        this.stage = this.context.getStage();

        // Lade das TextureAtlas mit den Button-Grafiken


        //Texture loading Main Menu
        context.getAssetManager().load(AssetPaths.BUTTON_ATLAS.getPath(), TextureAtlas.class);
        context.getAssetManager().load(AssetPaths.MAIN_MENU_BACKGROUND.getPath(), Texture.class);
        context.getAssetManager().load(AssetPaths.MAIN_MENU_HEADING.getPath(), Texture.class);
        context.getAssetManager().load(Audio.START_SCREEN_MUSIC.getPath(), Music.class);
        context.getAssetManager().finishLoading();

        buttonAtlas = context.getAssetManager().get(AssetPaths.BUTTON_ATLAS.getPath());

        // Initialisiere die Drawable-Regionen für die Buttons
        startButtonRegion = new TextureRegionDrawable(buttonAtlas.findRegion("start_button"));
        keyBindsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("keybinds_button"));
        creditsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("credits_button"));
        settingsRegion = new TextureRegionDrawable(buttonAtlas.findRegion("settings_button"));
        storyRegion = new TextureRegionDrawable(buttonAtlas.findRegion("story_button"));
        tutorialRegion = new TextureRegionDrawable(buttonAtlas.findRegion("tutorial_button"));

        // Lade und füge den Hintergrund hinzu
        background = context.getAssetManager().get(AssetPaths.MAIN_MENU_BACKGROUND.getPath());
        backgroundImage = new Image(background);
        backgroundImage.setSize(17f, 12f);

        //Lade und füge die Überschrift hinzu
        heading = context.getAssetManager().get(AssetPaths.MAIN_MENU_HEADING.getPath());
        headingImage = new Image(heading);
        headingImage.setSize(9f, 1f);
        headingImage.setPosition(16f / 2f - 4.3f, START_BUTTON_Y_VALUE + buttonScale * 1.3f);

       context.getAudioManager().playAudio(Audio.START_SCREEN_MUSIC);

        // Erstelle und positioniere die Buttons
        createButtons();
    }

    /**
     * Diese Methode wird aufgerufen, wenn dieser Screen angezeigt wird.
     * Aktuell werden hier keine zusätzlichen Aktionen benötigt.
     */
    @Override
    public void show() {
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
     * Die Buttons werden in zwei Reihen mit minimalem Abstand (padding) angeordnet.
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
        imageButtons = new ArrayList<>();
        imageButtons.add(startButton);
        imageButtons.add(keyBindsButton);
        imageButtons.add(settingsButton);
        imageButtons.add(creditsButton);
        imageButtons.add(storyButton);
        imageButtons.add(tutorialButton);

        // Setze die Größe aller Buttons auf den definierten Wert
        for (ImageButton button : imageButtons) {
            button.setSize(buttonScale, buttonScale);
        }

        // Positioniere die Buttons in zwei Reihen (erste Reihe: 3 Buttons, zweite Reihe: 3 Buttons)
        float currentX = startButtonXValue;
        float currentY = START_BUTTON_Y_VALUE;
        for (int i = 0; i < imageButtons.size(); i++) {
            // Bei Button 4 (Index 3) in die nächste Reihe wechseln
            if (i == BUTTONS_PER_ROW) {
                currentX = startButtonXValue;
                // Verschiebe die Y-Position um die Höhe des Buttons plus padding
                currentY -= (buttonScale + padding);
            }
            imageButtons.get(i).setPosition(currentX, currentY);
            // Erhöhe currentX für den nächsten Button
            currentX += buttonScale + padding;
        }

        // Beispiel-Listener: Wenn der Start-Button berührt wird, wird zum GameScreen gewechselt
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().showScreenWithLoading(ScreenType.GAME);
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
       context.getAudioManager().stopCurrentMusic();
       LoggerUtil.log(LogCategory.DEBUG, this, "MainMenuScreen hidden");
       this.stage.clear();
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
}
