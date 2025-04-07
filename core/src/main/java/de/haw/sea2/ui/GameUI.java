package de.haw.sea2.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.haw.sea2.StudentsQuest;
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
    
    // Coins
    private final Array<TextureRegion> coinFrames;
    private final Animation<TextureRegion> coinAnimation;
    private final Array<TextureRegion> grayCoinFrames;
    private final Animation<TextureRegion> grayCoinAnimation;
    private float coinAnimTime = 0f;
    private static final float COIN_ANIM_SPEED = 0.15f;
    private static final int COINS_PER_ROW = 5;

    /**
     * Erstellt das UI für das Spiel
     */
    public GameUI(StudentsQuest context) {
        this.context = context;
        this.ecsEngine = context.getEngine();
        players = ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class, HearthComponent.class).get());

        this.heartIdleTexture = context.getAssetManager().get(AssetPaths.HEARTH.getPath(), Texture.class);
        
        // Coin-Animation aus dem Atlas laden
        TextureAtlas coinAtlas = context.getAssetManager().get(AssetPaths.COIN_ATLAS.getPath(), TextureAtlas.class);
        
        // Erstelle die Goldmünzenanimation aus MonedaD
        TextureRegion coinStrip = coinAtlas.findRegion("MonedaD");//TODO REFACTOR ANIMATION
        int frameWidth = coinStrip.getRegionWidth() / 5; // Die Textur enthält 5 Frames
        coinFrames = new Array<>(5);
        
        // Die 5 Frames aus dem Strip extrahieren
        for (int i = 0; i < 5; i++) {
            coinFrames.add(new TextureRegion(coinStrip, i * frameWidth, 0, frameWidth, coinStrip.getRegionHeight()));
        }
        
        // Erstelle die Animation für goldene Münzen
        coinAnimation = new Animation<>(COIN_ANIM_SPEED, coinFrames, Animation.PlayMode.LOOP);
        
        // Graue Münzanimation erstellen
        grayCoinFrames = new Array<>(5);
        TextureRegion grayFrame = coinAtlas.findRegion("MonedaP");//TODO REFACTOR ANIMATION
        
        // Wir teilen die graue Münze genau wie die goldene in 5 Frames auf
        int grayFrameWidth = grayFrame.getRegionWidth() / 5;
        for (int i = 0; i < 5; i++) {
            grayCoinFrames.add(new TextureRegion(grayFrame, i * grayFrameWidth, 0, grayFrameWidth, grayFrame.getRegionHeight()));
        }
        
        // Erstelle die Animation für graue Münzen
        grayCoinAnimation = new Animation<>(COIN_ANIM_SPEED, grayCoinFrames, Animation.PlayMode.LOOP);
        
    }


    /**
     * Zeichnet das UI auf dem Bildschirm
     * @param batch der SpriteBatch, der bereits im render()-Loop gestartet wurde
     */
    public void render(SpriteBatch batch) {
        // Update coin animation time
        coinAnimTime += Gdx.graphics.getDeltaTime();
        
        // UI-Elemente rendern
        renderHearts(batch);
        renderCoins(batch);
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
            int row = i / COINS_PER_ROW;
            int col = i % COINS_PER_ROW;
            
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
            int row = i / COINS_PER_ROW;
            int col = i % COINS_PER_ROW;
            
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
