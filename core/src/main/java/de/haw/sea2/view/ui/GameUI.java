package de.haw.sea2.view.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.HearthComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.animations.AnimationUtils;
import de.haw.sea2.view.animations.CoinAnimation;

/**
 * Verwaltet das UI des Spiels (Lebensanzeige, Positionsinfo, etc.)
 */
public class GameUI implements Disposable {

    private final StudentsQuest context;
    private final ImmutableArray<Entity> players;

    // UI-Texturen
    private final Texture heartIdleTexture;

    private final Animation<Sprite> coinAnimation;
    private final Animation<Sprite> grayCoinAnimation;
    private final int coinsPerRow =  CoinAnimation.GOLD_COIN_SPIN.animationType.frameCount();
    private float coinAnimTime = 0f;

    /**
     * Erstellt das UI für das Spiel
     */
    public GameUI(StudentsQuest context) {
        this.context = context;
        ECSEngine ecsEngine = context.getEngine();
        players = ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class, HearthComponent.class).get());
        this.heartIdleTexture = context.getAssetManager().get(AssetPaths.HEARTH.getPath(), Texture.class);
        TextureAtlas coinAtlas = context.getAssetManager().get(AssetPaths.COIN_ATLAS.getPath(), TextureAtlas.class);

        // Erstelle die Animation für goldene Münzen
        this.coinAnimation = AnimationUtils.getAnimation(CoinAnimation.GOLD_COIN_SPIN.animationType, coinAtlas);

        // Erstelle die Animation für graue Münzen
        this.grayCoinAnimation = AnimationUtils.getAnimation(CoinAnimation.GREY_COIN_SPIN.animationType, coinAtlas);
    }

    public void render() {
        this.context.viewport.apply();

        SpriteBatch batch = this.context.getSpriteBatch();
        batch.setProjectionMatrix(context.viewport.getCamera().combined);
        batch.begin();

        // Update coin animation time
        coinAnimTime += Gdx.graphics.getDeltaTime();

        // UI-Elemente rendern
        renderHearts(batch);
        renderCoins(batch);

        batch.end();
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

    /**
     * Zeichnet die Münzen und den Münzzähler
     */
    private void renderCoins(SpriteBatch batch) {
        Camera camera = context.viewport.getCamera();

        // Startposition für die Münzleiste
        float startX = camera.position.x - 7.5f;
        float startY = camera.position.y + 3.0f;
        float coinSize = 0.5f;
        float spacing = 0.6f; // Abstand zwischen den Münzen

        // Spielerkomponente holen für Münzstatus
        PlayerComponent playerComponent = ECSEngine.PLAYER_COMP_MAPPER.get(players.first());
        int coinCount = playerComponent.collectedCoins;
        int maxCoins = playerComponent.neededCoins;

        // Gesammelte Münzen darstellen (animiert und golden)
        for (int i = 0; i < coinCount; i++) {
            int row = i / coinsPerRow;
            int col = i % coinsPerRow;

            float x = startX + col * spacing;
            float y = startY - row * spacing;

            // Aktuellen Frame aus der Animation holen
            TextureRegion currentFrame = coinAnimation.getKeyFrame(coinAnimTime);

            batch.draw(currentFrame,
                x, y, // Position
                coinSize/2, coinSize/2, // Origin center
                coinSize, coinSize, // Size
                1, 1, // Scale
                0); // Rotation
        }

        // Noch nicht gesammelte Münzen darstellen (grau und animiert)
        for (int i = coinCount; i < maxCoins; i++) {
            int row = i / coinsPerRow;
            int col = i % coinsPerRow;

            float x = startX + col * spacing;
            float y = startY - row * spacing;

            // Aktuellen Frame aus der grauen Animation holen
            TextureRegion currentGrayFrame = grayCoinAnimation.getKeyFrame(coinAnimTime);

            // Graue Münze für noch nicht gesammelte Coins
            batch.setColor(1, 1, 1, 0.7f); // Leicht transparent
            batch.draw(currentGrayFrame,
                x, y, // Position
                coinSize/2, coinSize/2, // Origin center
                coinSize, coinSize, // Size
                1, 1, // Scale
                0); // Rotation
            batch.setColor(1, 1, 1, 1); // Zurücksetzen der Farbe
        }

    }

    @Override
    public void dispose() {
        heartIdleTexture.dispose();
    }
}
