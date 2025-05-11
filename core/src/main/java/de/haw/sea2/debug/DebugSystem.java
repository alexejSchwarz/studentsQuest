package de.haw.sea2.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.haw.sea2.debug.render.BasicDebugRenderer;
import de.haw.sea2.debug.render.DebugRenderer;

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
        this.font = new BitmapFont();
        this.renderers = new Array<>();
        
        if (!DebugConfig.DEBUG_ENABLED)
            return;

        LoggerUtil.log(LogCategory.DEBUG,this,"Debug-System wird initialisiert");

        
        this.font.setColor(DebugConfig.DEFAULT_COLOR);
        this.font.getData().setScale(DebugConfig.FONT_SCALE);

        

        // Standardrenderer für allgemeine Debug-Infos hinzufügen
        this.renderers.add(new BasicDebugRenderer());
        LoggerUtil.log(LogCategory.DEBUG,this,"Basic Debug Renderer hinzugefügt");
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
     * Sucht nach einem Renderer der angegebenen Klasse.
     * 
     * @param rendererClass Die Klasse des gesuchten Renderers
     * @return Der Renderer, oder null wenn keiner gefunden wurde
     */
    public <T extends DebugRenderer> T findRenderer(Class<T> rendererClass) {
        if (!DebugConfig.DEBUG_ENABLED)
            return null;
            
        for (DebugRenderer renderer : renderers) {
            if (rendererClass.isInstance(renderer)) {
                return rendererClass.cast(renderer);
            }
        }
        return null;
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
         * space. This is the memory where the operating system has allocated the
         * process that contains the JVM.
         *
         * If your application is making use of native OpenAL in LWJGL and your native heap
         * keeps growing then there are probably leaks in the sound system.
         */
    }

    /**
     * Rendert alle Debug-Informationen.
     */
    public void render(SpriteBatch batch) {
        if (!DebugConfig.DEBUG_ENABLED)
            return;

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
