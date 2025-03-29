package de.haw.sea2.gameLevel;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.builders.EntityCreator;
import de.haw.sea2.map.EntitySpawnPoint;
import de.haw.sea2.map.mapObjectEnums.MapEntityTypes;

/**
 * Klasse zum Verwalten, welche Entities, zu welcher Zeit und Frequenz spawnen sollen. Nach Levelchange
 * und fuer das initiale Level muss prepareLevelStart() aufgerufen werden
 */
public class SpawnLogic {

    private final Array<EntitySpawnPoint> spawnPoints;
    private final Array<EntitySpawnPoint> itemSpawnPoints;
    private final Array<EntitySpawnPoint> enemySpawnPoints;

    private final EntityCreator creator;

    public SpawnLogic(StudentsQuest context) {
        this.spawnPoints = new Array<>();
        this.itemSpawnPoints = new Array<>();
        this.enemySpawnPoints = new Array<>();
        this.creator = context.getEntityCreator();
    }

    /**
     * Gruppierung der SpawnPoints und initiales Spawnen von Level / Map abhaengigen Entitäten
     */
    public void prepareLevelStart(Array<EntitySpawnPoint> spawnPoints, Array<Rectangle> walls) {
        clear();
        this.spawnPoints.addAll(spawnPoints);
        for (EntitySpawnPoint spawnPoint : this.spawnPoints) {
            if (spawnPoint.entityType().equals(MapEntityTypes.ITEM.value)) {
                this.itemSpawnPoints.add(spawnPoint);
            } else if (spawnPoint.entityType().equals(MapEntityTypes.ENEMY.value)) {
                this.enemySpawnPoints.add(spawnPoint);
            }
        }

        // Spawn enemies
        for (EntitySpawnPoint spawnPoint : this.enemySpawnPoints) {
            spawnEnemy(spawnPoint);
        }

        // Existing logic for items
        for (EntitySpawnPoint spawnPoint : this.itemSpawnPoints) {
            spawnItem(spawnPoint);
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

    private void spawnItem(EntitySpawnPoint spawnPoint) {
        this.creator.createCoin(spawnPoint.spawnPoint(), 1f);
    }

    private void clear() {
        this.spawnPoints.clear();
        this.itemSpawnPoints.clear();
        this.enemySpawnPoints.clear();
    }

    public void update(float delta) {
        // TODO Logik für Enemy Spawns mit timer, Neue Items spawnen, wenn diese eingesammelt wurden etc
    }
}
