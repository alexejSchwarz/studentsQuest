package de.haw.sea2.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Zentrales System für das Rendern von Debug-Informationen.
 */
public class DebugSystem implements Disposable {

    private final BitmapFont font;
    private final Array<DebugRenderer> renderers;
    private float timeSinceUpdate = 0;

    // Performance-Metriken
    private int fps;
    private long javaHeap;
    private long nativeHeap;

    public DebugSystem() {
        if (!DebugConfig.DEBUG_ENABLED)
            return;

        Gdx.app.log("DebugSystem", "Debug-System wird initialisiert");

        this.font = new BitmapFont();
        this.font.setColor(DebugConfig.DEFAULT_COLOR);
        this.font.getData().setScale(DebugConfig.FONT_SCALE);

        this.renderers = new Array<>();

        // Standardrenderer für allgemeine Debug-Infos hinzufügen
        this.renderers.add(new BasicDebugRenderer());
        Gdx.app.log("DebugSystem", "Basic Debug Renderer hinzugefügt");
    }

    /**
     * Fügt einen benutzerdefinierten Debug-Renderer hinzu.
     */
    public void addRenderer(DebugRenderer renderer) {
        if (!DebugConfig.DEBUG_ENABLED)
            return;
        this.renderers.add(renderer);
    }

    /**
     * Entfernt einen Debug-Renderer.
     */
    public void removeRenderer(DebugRenderer renderer) {
        if (!DebugConfig.DEBUG_ENABLED)
            return;
        this.renderers.removeValue(renderer, true);
    }

    /**
     * Aktualisiert die Debug-Informationen.
     */
    public void update(float delta) {
        if (!DebugConfig.DEBUG_ENABLED)
            return;

        timeSinceUpdate += delta;
        if (timeSinceUpdate >= DebugConfig.UPDATE_INTERVAL) {
            fps = Gdx.graphics.getFramesPerSecond();
            javaHeap = Gdx.app.getJavaHeap() / (1024 * 1024); // In MB
            nativeHeap = Gdx.app.getNativeHeap() / (1024 * 1024); // In MB
            timeSinceUpdate = 0;
        }
        /*
         * 1) Heap memory: memory within the JVM process that is used to hold Java
         * Objects and is maintained by the JVMs Garbage Collector.
         *
         * 2) Native memory/Off-heap: is memory allocated within the processes address
         * space that is not within the heap and thus is not freed up by the Java
         * Garbage Collector.
         */
    }

    /**
     * Rendert alle Debug-Informationen.
     */
    public void render(SpriteBatch batch) {
        if (!DebugConfig.DEBUG_ENABLED)
            return;

        Gdx.app.log("DebugSystem", "Debug-System rendering wird ausgeführt");

        // Überprüfen ob der Batch bereits zeichnet
        boolean batchWasDrawing = batch.isDrawing();

        // Batch beenden, wenn er aktiv war
        if (batchWasDrawing) {
            batch.end();
        }

        // Speichere die aktuelle Matrix
        Matrix4 originalMatrix = batch.getProjectionMatrix().cpy();

        // Setze explizit auf Screen-Koordinaten
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // In Screen-Koordinaten zeichnen (keine Projektion)
        batch.begin();

        // Position nach oben rechts verschieben
        float x = Gdx.graphics.getWidth() - DebugConfig.POSITION_X_OFFSET;
        float y = Gdx.graphics.getHeight() - DebugConfig.POSITION_Y_OFFSET;

        // Kleinere Schrift verwenden
        font.getData().setScale(0.8f); // Statt 1.5f für eine kompaktere Anzeige

        // Basis-Informationen
        font.draw(batch, "FPS: " + fps, x, y);
        y -= DebugConfig.LINE_SPACING;
        font.draw(batch, "Java Heap: " + javaHeap + " MB", x, y);
        y -= DebugConfig.LINE_SPACING;
        font.draw(batch, "Native Heap: " + nativeHeap + " MB", x, y);
        y -= DebugConfig.LINE_SPACING;

        // Rendere alle benutzerdefinierten Debug-Informationen
        for (DebugRenderer renderer : renderers) {
            y = renderer.render(batch, font, x, y);
            y -= DebugConfig.LINE_SPACING;
        }

        batch.end();

        // Setze ursprüngliche Matrix zurück
        batch.setProjectionMatrix(originalMatrix);

        // Ursprünglichen Zustand wiederherstellen
        if (batchWasDrawing) {
            batch.begin();
        }

        Gdx.app.log("DebugSystem", "Debug-Rendering abgeschlossen");
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        // Renderer freigeben, falls sie Disposable implementieren
        for (DebugRenderer renderer : renderers) {
            if (renderer instanceof Disposable) {
                ((Disposable) renderer).dispose();
            }
        }
        renderers.clear();
    }
}
