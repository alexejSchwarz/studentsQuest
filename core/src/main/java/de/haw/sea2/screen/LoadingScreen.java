package de.haw.sea2.screen;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.paths.MapPaths;

/**
 * Screen zum Anzeigen eines Ladebildschirms während Asset-Ladevorgängen.
 */
public class LoadingScreen implements Screen {

    private final StudentsQuest context;
    private final ScreenType targetScreenType;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    // Timer für künstliche Verzögerung und simulierten Fortschritt
    private float elapsedTime = 0;
    private final float minLoadTime = 3.0f; // Minimale Zeit in Sekunden, die der LoadingScreen angezeigt wird
    private final boolean useSimulatedLoading = false; // Für Entwicklungszwecke

    /**
     * Erstellt einen neuen LoadingScreen mit dem angegebenen Ziel-Screen.
     *
     * @param context          Der StudentsQuest-Kontext
     * @param targetScreenType Der Typ des Ziel-Screens
     */
    public LoadingScreen(StudentsQuest context, ScreenType targetScreenType) {
        this.context = context;
        this.targetScreenType = targetScreenType;
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();

        // Konfiguriere Font
        this.font.setUseIntegerPositions(false);
        this.font.getData().setScale((this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 2);
        this.font.setColor(Color.WHITE);

        initializeAssetLoading();
    }

    private void initializeAssetLoading() {
        if (Objects.requireNonNull(targetScreenType) == ScreenType.GAME) {
            loadGameScreenAssets();
        }
    }

    /**
     * Lädt die spezifischen Assets für den GameScreen.
     */
    private void loadGameScreenAssets() {

        // Karte über den MapManager laden
        context.getMapManager().loadMap(MapPaths.MAINMAP.getPath());

        // SpielerAtlas laden
        context.getAssetManager().setLoader(Texture.class,
                new TextureLoader(context.getAssetManager().getFileHandleResolver()));
        context.getAssetManager().load("assetsFromTut/character_and_effect.atlas", TextureAtlas.class);

        // Optional: Weitere Assets hier laden
        // z.B. Sound-Effekte, Musik, UI-Elemente
        //TODO use atlas for entities, items etc
        context.getAssetManager().load("assetsFromTut/Ball.png", Texture.class);
        context.getAssetManager().load("assetsFromTut/coin.png", Texture.class);
    }

    @Override
    public void show() {
        // Hier können später spezifische Assets für den Ziel-Screen geladen werden
        // Zum Beispiel je nach targetScreenType unterschiedliche Assets laden
    }

    @Override
    public void render(float delta) {
        // Zeit aktualisieren
        elapsedTime += delta;

        // Hintergrund löschen
        ScreenUtils.clear(0, 0, 0, 1);

        // Update AssetManager oder simuliere Fortschritt
        boolean finished = false;
        float progress = 0;

        context.getAssetManager().update();
        if (useSimulatedLoading) {
            // Simulierter Fortschritt (langsam von 0 auf 1)
            progress = Math.min(elapsedTime / minLoadTime, 1.0f);
        } else {
            // Tatsächliches Asset-Loading
            progress = context.getAssetManager().getProgress();
        }
        finished = progress >= 1.0f;

        // Viewport anwenden
        context.viewport.apply();

        // Fortschrittsbalken zeichnen
        float barWidth = context.viewport.getWorldWidth() * 0.6f;
        float barHeight = context.viewport.getWorldHeight() * 0.05f;
        float barX = (context.viewport.getWorldWidth() - barWidth) / 2;
        float barY = context.viewport.getWorldHeight() * 0.4f;

        shapeRenderer.setProjectionMatrix(context.viewport.getCamera().combined);

        // Rahmen
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);
        shapeRenderer.end();

        // Fortschritt
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.7f, 1f, 1);
        shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
        shapeRenderer.end();

        // Text anzeigen
        context.getSpriteBatch().begin();
        font.draw(context.getSpriteBatch(), "Laden... " + (int) (progress * 100) + "%",
                context.viewport.getWorldWidth() / 2 - 1f,
                barY + barHeight + 0.5f);
        context.getSpriteBatch().end();

        // Wenn fertig und Mindestanzeigezeit überschritten, zum Zielscreen wechseln
        if (finished && (!useSimulatedLoading || elapsedTime >= minLoadTime)) {
            // Wechsel zum Ziel-Screen
            context.getScreenManager().showScreen(targetScreenType);
        }
    }

    @Override
    public void resize(int width, int height) {
        context.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Nichts zu tun
    }

    @Override
    public void resume() {
        // Nichts zu tun
    }

    @Override
    public void hide() {
        // Nichts zu tun
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
