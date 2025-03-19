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
        // Spielzeit anzeigen
        long runTimeSeconds = (TimeUtils.millis() - startTime) / 1000;
        long hours = runTimeSeconds / 3600;
        long minutes = (runTimeSeconds % 3600) / 60;
        long seconds = runTimeSeconds % 60;

        font.draw(batch, String.format(DebugConfig.TIME_FORMAT, hours, minutes, seconds), x, y);
        y -= DebugConfig.LINE_SPACING;

        // GL-Info kürzen für bessere Übersichtlichkeit
        String glVersion = Gdx.graphics.getGLVersion().getDebugVersionString();
        if (glVersion.length() > DebugConfig.MAX_STRING_LENGTH) {
            glVersion = glVersion.substring(0, DebugConfig.MAX_STRING_LENGTH - 2) + "...";
        }
        font.draw(batch, "GL: " + glVersion, x, y);
        y -= DebugConfig.LINE_SPACING;

        return y;
    }
}
