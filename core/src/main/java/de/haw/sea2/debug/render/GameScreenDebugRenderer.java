package de.haw.sea2.debug.render;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.logic.ecs.components.AnimationComponent;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

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
        y = renderPlayerInfo(batch, font, x, y);
        y = renderPhysicsInfo(batch, font, x, y);
        y = renderCameraInfo(batch, font, x, y);
        y = renderEntityInfo(batch, font, x, y);

        return y;
    }

    /**
     * Rendert Spielerinformationen wie Position und Geschwindigkeit.
     */
    private float renderPlayerInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        try {
            Box2DComponent playerBox2D = context.getEngine().getEntitiesFor(
                    Family.all(PlayerComponent.class, Box2DComponent.class).get())
                    .get(0).getComponent(Box2DComponent.class);

            if (playerBox2D != null) {
                Vector2 position = playerBox2D.body.getPosition();
                Vector2 velocity = playerBox2D.body.getLinearVelocity();

                font.draw(batch, String.format(DebugConfig.POSITION_FORMAT, position.x, position.y), x, y);
                y -= DebugConfig.LINE_SPACING;
                font.draw(batch, String.format(DebugConfig.VELOCITY_FORMAT, velocity.x, velocity.y), x, y);
                y -= DebugConfig.LINE_SPACING;
            }
        } catch (Exception e) {
            Color originalColor = font.getColor();
            font.setColor(DebugConfig.ERROR_COLOR);
            font.draw(batch, "Kein Spieler gefunden", x, y);
            font.setColor(originalColor);
            y -= DebugConfig.LINE_SPACING;
        }

        return y;
    }

    /**
     * Rendert Informationen zur Physik-Engine.
     */
    private float renderPhysicsInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        World world = context.getWorld();
        font.draw(batch, "Physik-Objekte: " + world.getBodyCount(), x, y);
        y -= DebugConfig.LINE_SPACING;

        font.draw(batch, "Kontakte: " + world.getContactCount(), x, y);
        y -= DebugConfig.LINE_SPACING;

        return y;
    }

    /**
     * Rendert Informationen zur Kamera.
     */
    private float renderCameraInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        OrthographicCamera camera = context.getGameCamera();
        font.draw(batch, String.format("Kamera: %.2f, %.2f (Zoom: %.2f)",
                camera.position.x, camera.position.y, camera.zoom), x, y);
        return y - DebugConfig.LINE_SPACING;
    }

    /**
     * Rendert Informationen zu den Entities im Spiel.
     */
    private float renderEntityInfo(SpriteBatch batch, BitmapFont font, float x, float y) {
        int totalEntities = context.getEngine().getEntities().size();
        font.draw(batch, "Entities gesamt: " + totalEntities, x, y);
        y -= DebugConfig.LINE_SPACING;

        int animatedEntities = context.getEngine().getEntitiesFor(
                Family.all(AnimationComponent.class).get()).size();
        font.draw(batch, "Animierte Entities: " + animatedEntities, x, y);
        y -= DebugConfig.LINE_SPACING;

        return y;
    }
}
