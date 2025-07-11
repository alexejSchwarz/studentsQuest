package de.haw.sea2.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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

    // Drawables
    private TextureRegionDrawable letsGoButtonDrawable;

    private Image missionImage;
    private ImageButton playButton;

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

        // Setze vorübergehend ein Platzhalter-Missionsbild
        missionTexture = context.getAssetManager().get(AssetPaths.MISSION_SCREEN.getPath(), Texture.class);

        // Setze den "Let's Go"-Button-Drawable
        letsGoButtonDrawable = new TextureRegionDrawable(this.context.getAssetManager().get(AssetPaths.PLAY_BUTTON.getPath(), Texture.class));
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
        this.playButton = new ImageButton(this.letsGoButtonDrawable);
        this.playButton.setSize(5f, 2f);
        this.playButton.setPosition(5.5f, 2f);

        this.playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getScreenManager().showScreen(ScreenType.GAME);
            }
        });
    }

    /**
     * Diese Methode wird aufgerufen, wenn dieser Screen angezeigt wird.
     */
    @Override
    public void show() {
        stage.addActor(this.missionImage);
        stage.addActor(this.playButton);
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
