package de.haw.sea2.debug.render;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import com.badlogic.gdx.graphics.Color;

/**
 * Debug-Renderer für spielspezifische Informationen.
 */
public class GameScreenDebugRenderer implements DebugRenderer {

    private final StudentsQuest context;

    public GameScreenDebugRenderer(StudentsQuest context) {
        this.context = context;
    }

    @Override
    public float render(SpriteBatch batch, BitmapFont font, float x, float y) {
        // Spielerspezifische Informationen
        try {
            Box2DComponent playerBox2D = context.getEngine().getEntitiesFor(
                    Family.all(PlayerComponent.class, Box2DComponent.class).get())
                    .get(0).getComponent(Box2DComponent.class);

            if (playerBox2D != null) {
                Vector2 position = playerBox2D.body.getPosition();
                Vector2 velocity = playerBox2D.body.getLinearVelocity();

                // Verwende die Formatstrings aus DebugConfig
                font.draw(batch, String.format(DebugConfig.POSITION_FORMAT, position.x, position.y), x, y);
                y -= DebugConfig.LINE_SPACING;
                font.draw(batch, String.format(DebugConfig.VELOCITY_FORMAT, velocity.x, velocity.y), x, y);
                y -= DebugConfig.LINE_SPACING;
            }
        } catch (Exception e) {
            // Fehlerbehandlung - kein Spieler gefunden
            // Verwende Error-Farbe für Fehler
            Color originalColor = font.getColor();
            font.setColor(DebugConfig.ERROR_COLOR);
            font.draw(batch, "Kein Spieler gefunden", x, y);
            font.setColor(originalColor);
            y -= DebugConfig.LINE_SPACING;
        }

        // Physik-Welt-Informationen
        font.draw(batch, "Physik-Objekte: " + context.getWorld().getBodyCount(), x, y);
        y -= DebugConfig.LINE_SPACING;

        return y;
    }
}
