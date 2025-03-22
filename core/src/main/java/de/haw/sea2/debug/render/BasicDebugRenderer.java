package de.haw.sea2.debug.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import de.haw.sea2.debug.DebugConfig;

/**
 * Standard-Renderer für grundlegende Debug-Informationen.
 */
public class BasicDebugRenderer implements DebugRenderer {

    private final long startTime = TimeUtils.millis();

    @Override
    public float render(SpriteBatch batch, BitmapFont font, float x, float y) {
        y = renderGameTime(batch, font, x, y);
        y = renderFrameTime(batch, font, x, y);
        y = renderMemoryInfo(batch, font, x, y);
        y = renderOpenGLInfo(batch, font, x, y);

        return y;
    }

    /**
     * Zeigt die Gesamtspielzeit an.
     */
    private float renderGameTime(SpriteBatch batch, BitmapFont font, float x, float y) {
        long runTimeSeconds = (TimeUtils.millis() - startTime) / 1000;
        long hours = runTimeSeconds / 3600;
        long minutes = (runTimeSeconds % 3600) / 60;
        long seconds = runTimeSeconds % 60;

        font.draw(batch, String.format(DebugConfig.TIME_FORMAT, hours, minutes, seconds), x, y);
        return y - DebugConfig.LINE_SPACING;
    }

    /**
     * Zeigt die Frame-Zeit des aktuellen Frames an.
     */
    private float renderFrameTime(SpriteBatch batch, BitmapFont font, float x, float y) {
        font.draw(batch, "Frame-Zeit: " + String.format("%.3f ms", Gdx.graphics.getDeltaTime() * 1000), x, y);
        return y - DebugConfig.LINE_SPACING;
    }

    /**
     * Zeigt Speichernutzungsinformationen an.
     */
    private float renderMemoryInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        font.draw(batch, "Speicher: " + (totalMemory - freeMemory) + "/" + totalMemory + " MB", x, y);
        return y - DebugConfig.LINE_SPACING;
    }

    /**
     * Zeigt OpenGL-Informationen zeilenweise an.
     */
    private float renderOpenGLInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        String glVersion = Gdx.graphics.getGLVersion().getDebugVersionString();
        String[] glInfo = glVersion.split("\n");

        font.draw(batch, "OpenGL Info:", x, y);
        y -= DebugConfig.LINE_SPACING;

        for (String info : glInfo) {
            if (info.contains(":")) {
                String[] parts = info.split(":", 2);
                font.draw(batch, parts[0].trim() + ": " + parts[1].trim(), x + 10, y);
            } else {
                font.draw(batch, info, x + 10, y);
            }
            y -= DebugConfig.LINE_SPACING;
        }

        return y;
    }
}