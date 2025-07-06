package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

/**
 * InfoScreenLevel1 zeigt das Missionsbild für Level 1 an, bevor das eigentliche Gameplay beginnt.
 *
 * <p>
 * Dieser Bildschirm wird angezeigt, nachdem der Spieler auf den Start-Button im Hauptmenü geklickt hat.
 * Er zeigt ein vollständiges Missionsbild im 16:9-Format an und bietet einen "Let's Go" Button
 * in der unteren rechten Ecke, um das Spiel zu starten.
 * </p>
 */
public class InfoScreenLevel1 implements Screen {

    private final StudentsQuest context;
    private final Stage stage;

    // Mission-Textur (16:9 Verhältnis)
    private Texture missionTexture;

    // Button-Atlas und Drawables
    private TextureAtlas buttonAtlas;
    private TextureRegionDrawable letsGoButtonDrawable;

    private Image missionImage;
    private TextButton girlButton;
    private TextButton boyButton;

    // Konstanten für Positionierung und Skalierung
    private static final float MISSION_IMAGE_WIDTH = 16f;
    private static final float MISSION_IMAGE_HEIGHT = 9f;
    private static final float BUTTON_SCALE = 2f;
    private static final float BUTTON_MARGIN = 0.5f;

    /**
     * Konstruktor, der den Spielkontext initialisiert und die UI-Komponenten lädt.
     *
     * @param context Der übergeordnete Spielkontext
     */
    public InfoScreenLevel1(StudentsQuest context) {
        this.context = context;
        this.stage = context.getStage();

        // Lade die benötigten Assets
        loadAssets();

        // Erstelle die UI-Komponenten
        createUI();
    }

    /**
     * Lädt die erforderlichen Assets für diesen Screen.
     */
    private void loadAssets() {
        context.getAssetManager().load(AssetPaths.MISSION_SCREEN.getPath(), Texture.class);
        context.getAssetManager().finishLoading();

        // Lade den Button-Atlas
        buttonAtlas = context.getAssetManager().get(AssetPaths.BUTTON_ATLAS.getPath());

        // Setze vorübergehend ein Platzhalter-Missionsbild
        missionTexture = context.getAssetManager().get(AssetPaths.MISSION_SCREEN.getPath(), Texture.class);

        // Setze den "Let's Go"-Button-Drawable
        letsGoButtonDrawable = new TextureRegionDrawable(buttonAtlas.findRegion("start_button"));
    }

    /**
     * Erstellt und positioniert die UI-Komponenten.
     */
    private void createUI() {
        // Erstelle und positioniere das Missionsbild in der Mitte des Bildschirms
        missionImage = new Image(missionTexture);
        missionImage.setSize(MISSION_IMAGE_WIDTH, MISSION_IMAGE_HEIGHT);

        // Zentriere das Bild auf dem Bildschirm
        float centerX = (context.viewport.getWorldWidth() - MISSION_IMAGE_WIDTH) / 2;
        float centerY = (context.viewport.getWorldHeight() - MISSION_IMAGE_HEIGHT) / 2;
        missionImage.setPosition(centerX, centerY);

        //-------------------------
        //TODO temporaer bis Bilder fuer char Wahl vorhanden sind. Dann Imagebuttons
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = new BitmapFont();
        ;
        buttonStyle.fontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;

        // Schriftgröße anpassen
        buttonStyle.font.setUseIntegerPositions(false);
        float scaleFactor = (this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 3.0f;
        buttonStyle.font.getData().setScale(scaleFactor);

        this.girlButton = new TextButton("Mädchen", buttonStyle);
        this.girlButton.setPosition(2f, 2f);
        this.boyButton = new TextButton("Junge", buttonStyle);
        this.boyButton.setPosition(12f, 2f);
        //-------------------------

        // Füge einen ClickListener hinzu, um zum Game-Screen zu wechseln
        this.girlButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.chosenPlayerAnimationAtlas = AssetPaths.GIRL_PLAYER_ATLAS;
                context.getScreenManager().showScreen(ScreenType.GAME);
            }
        });
        this.boyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.chosenPlayerAnimationAtlas = AssetPaths.BOY_PLAYER_ATLAS;
                context.getScreenManager().showScreen(ScreenType.GAME);
            }
        });
    }

    /**
     * Diese Methode wird aufgerufen, wenn dieser Screen angezeigt wird.
     */
    @Override
    public void show() {
        stage.addActor(missionImage);
        stage.addActor(girlButton);
        stage.addActor(boyButton);
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
     * Aktualisiert den Viewport bei Änderung der Fenstergröße.
     *
     * @param width Neue Breite des Fensters
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
        // Aktuell nicht implementiert
    }

    /**
     * Wird aufgerufen, wenn das Spiel fortgesetzt wird.
     */
    @Override
    public void resume() {
        // Aktuell nicht implementiert
    }

    /**
     * Wird aufgerufen, wenn dieser Screen nicht mehr aktiv ist.
     */
    @Override
    public void hide() {
        LoggerUtil.log(LogCategory.DEBUG, this, "InfoScreenLevel1 hidden");
        this.stage.clear();
    }

    /**
     * Gibt Ressourcen frei, wenn dieser Screen zerstört wird.
     */
    @Override
    public void dispose() {
        if (missionTexture != null) {
            missionTexture.dispose();
        }
    }
}
