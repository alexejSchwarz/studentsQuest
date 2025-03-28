package de.haw.sea2.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.HearthComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.paths.AssetPaths;

/**
 * Verwaltet das UI des Spiels (Lebensanzeige, Positionsinfo, etc.)
 */
public class GameUI implements Disposable {

    private final StudentsQuest context;
    private final ECSEngine ecsEngine;
    ImmutableArray<Entity> players;

    // UI-Texturen
    private final Texture heartIdleTexture;

    /**
     * Erstellt das UI für das Spiel
     */
    public GameUI(StudentsQuest context) {
        this.context = context;
        this.ecsEngine = context.getEngine();
        players = ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class, HearthComponent.class).get());

        this.heartIdleTexture = context.getAssetManager().get(AssetPaths.HEARTH.getPath(), Texture.class);
    }


    /**
     * Zeichnet das UI auf dem Bildschirm
     * @param batch der SpriteBatch, der bereits im render()-Loop gestartet wurde
     */
    public void render(SpriteBatch batch) {
        // UI-Elemente rendern
        renderHearts(batch);
    }

    /**
     * Zeichnet ein Herz für die Spieler-Lebensanzeige
     */
    private void renderHearts(SpriteBatch batch) {
        Camera camera = context.viewport.getCamera();

        //Es gibt nur einen Spieler, daher kann hier mit .first() gearbeitet werden.
        HearthComponent hearthComponent = ECSEngine.HEARTH_COMPONENT_MAPPER.get(players.first());

        for (int i = 1; i <= hearthComponent.currentHearths; i++) {
            batch.draw(heartIdleTexture, camera.position.x-7.5f+i*0.6f, camera.position.y+3.8f, 0.5f, 0.5f);
        }
    }

    @Override
    public void dispose() {
        heartIdleTexture.dispose();
    }
}
