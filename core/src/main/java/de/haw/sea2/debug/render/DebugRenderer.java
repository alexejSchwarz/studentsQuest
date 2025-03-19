package de.haw.sea2.debug.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface für benutzerdefinierte Debug-Renderer.
 */
public interface DebugRenderer {
    /**
     * Rendert Debug-Informationen.
     *
     * @param batch Der SpriteBatch zum Zeichnen
     * @param font  Die Font zum Zeichnen von Text
     * @param x     Die X-Position, an der gezeichnet werden soll
     * @param y     Die Y-Position, an der gezeichnet werden soll
     * @return Die neue Y-Position nach dem Zeichnen (für nachfolgende Renderer)
     */
    float render(SpriteBatch batch, BitmapFont font, float x, float y);
}
