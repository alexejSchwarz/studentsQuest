package de.haw.sea2.gameLevel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.builders.EntityCreator;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.map.EntitySpawnPoint;
import de.haw.sea2.map.mapObjectEnums.MapEntityTypes;

/**
 * Klasse zum Verwalten, welche Entities, zu welcher Zeit und Frequenz spawnen sollen. Nach Levelchange
 * und fuer das initiale Level muss prepareLevelStart() aufgerufen werden
 */
public class SpawnLogic {

    private final Array<EntitySpawnPoint> spawnPoints;
    private final Array<EntitySpawnPoint> availableItemSpawns;
    private final Array<EntitySpawnPoint> enemySpawnPoints;
    private final ImmutableArray<Entity> players;
    private final EntityCreator creator;

    private int collectedCoins;
    private EntitySpawnPoint lastUsedItemSpawn;

    public SpawnLogic(StudentsQuest context) {
        this.spawnPoints = new Array<>();
        this.availableItemSpawns = new Array<>();
        this.enemySpawnPoints = new Array<>();
        this.creator = context.getEntityCreator();
        this.players = context.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        this.collectedCoins = 0;
    }

    /**
     * Gruppierung der SpawnPoints und initiales Spawnen von Level / Map abhaengigen Entitäten
     */
    public void prepareLevelStart(Array<EntitySpawnPoint> spawnPoints) {
        clear();
        this.spawnPoints.addAll(spawnPoints);
        for (EntitySpawnPoint spawnPoint : this.spawnPoints) {
            if (spawnPoint.entityType().equals(MapEntityTypes.ITEM.value)) {
                this.availableItemSpawns.add(spawnPoint);
            } else if (spawnPoint.entityType().equals(MapEntityTypes.ENEMY.value)) {
                this.enemySpawnPoints.add(spawnPoint);
            }
        }

        spawnItem();

        // Spawn enemies
        for (EntitySpawnPoint spawnPoint : this.enemySpawnPoints) {
            spawnEnemy(spawnPoint);
        }
    }

    /**
     * Spawns an enemy at the given spawn point.
     *
     * @param spawnPoint The spawn point for the enemy
     */
    private void spawnEnemy(EntitySpawnPoint spawnPoint) {
        this.creator.createEnemy(spawnPoint.spawnPoint(), 1f, 1f);
    }

    /**
     * Spawnt neues Item. Es wird ein zufaelliger SpawnPunkt aus den moeglichen Punkten gewaehlt. Es wird verhindert, dass beim naechsten Methodenaufruf der gleiche Punkt
     * genommen wird
     */
    private void spawnItem() {
        int randomIndex = MathUtils.random(this.availableItemSpawns.size - 1);
        EntitySpawnPoint newUsedItemSpawn = this.availableItemSpawns.removeIndex(randomIndex);
        this.creator.createCoin(newUsedItemSpawn.spawnPoint(), 1f);

        if (this.lastUsedItemSpawn != null) {
            this.availableItemSpawns.add(this.lastUsedItemSpawn);
        }
        this.lastUsedItemSpawn = newUsedItemSpawn;
    }

    private void clear() {
        this.spawnPoints.clear();
        this.availableItemSpawns.clear();
        this.enemySpawnPoints.clear();
    }

    /**
     * Aktualisiert die Spawnlogic, Sollen neue Gegner, Items etc. gespawnt werden?
     */
    public void update(float delta) {
        int newCoinCount = ECSEngine.PLAYER_COMP_MAPPER.get(this.players.get(0)).collectedCoins;
        if (this.collectedCoins < newCoinCount) {
            this.collectedCoins = newCoinCount;
            spawnItem();
        }
    }
}
