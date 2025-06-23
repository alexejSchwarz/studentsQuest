package de.haw.sea2.logic.gameLevel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.lifeCicle.Restartable;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.builders.EntityCreator;
import de.haw.sea2.logic.ecs.components.EnemyComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.map.EntitySpawnPoint;
import de.haw.sea2.map.mapObjectEnums.MapEntityTypes;
import de.haw.sea2.logic.EntityUtils;

/**
 * Klasse zum Verwalten, welche Entities, zu welcher Zeit und Frequenz spawnen
 * sollen. Nach Levelchange
 * und für das initiale Level muss prepareLevelStart() aufgerufen werden.
 *
 * Implementiert ein wellenbasiertes Spawn-System mit dynamischer Anpassung:
 * - Gegner spawnen in Wellen alle X Sekunden
 * - Zufällige Auswahl von Spawnpunkten pro Welle
 * - Dynamische Anpassung der Schwierigkeit mit Fortschritt
 * - Gelegentliche Spezialwellen für erhöhte Herausforderung
 */
//TODO Vorschlag alle Coins gleich spawnen lassen. Wenn angenommen, dann Todo raus und unnoetige Methoden raus
public class SpawnLogic implements Restartable {

    // Konstanten für bessere Wartbarkeit
    private static final String LOG_TAG = "SpawnLogic";

    // Spawn-Einstellungen
    private static final float DEFAULT_WAVE_INTERVAL = 30f; // Zeitintervall zwischen Wellen (Sekunden)
    private static final int MAX_ENEMIES_PER_WAVE = 3; // Maximale Anzahl von Gegnern pro Welle (Startwert)
    private static final float SPAWN_POINT_PERCENTAGE = 0.5f; // Prozentsatz der zu verwendenden Spawnpunkte
    private static final int MAX_ACTIVE_ENEMIES = 10; // Maximale Anzahl gleichzeitig aktiver Gegner
    private static final float SPECIAL_WAVE_CHANCE = 0.15f; // Wahrscheinlichkeit für eine Spezialwelle (15%)
    private static final float PLAYER_SAFE_DISTANCE = 9f; // Sicherer Abstand zum Spieler für Spawns
    private static final float FIRST_WAVE_DELAY_FACTOR = 0.5f; // Die erste Welle startet früher (Faktor)

    // Ressourcenreferenzen
    private final Array<EntitySpawnPoint> spawnPoints;
    private final Array<EntitySpawnPoint> availableItemSpawns;
    private final Array<EntitySpawnPoint> enemySpawnPoints;
    //hier erstellen von herzen, dann in update dann durchlaufen und spawnen, diese dann in  Player Attack System. bei libgdx von hinten iterieren
    public static Array<Vector2> heartSpawnPoints;
    private final EntityCreator creator;
    private final StudentsQuest context;

    // Cache für Family-Abfragen
    private final ImmutableArray<Entity> players;
    private final ImmutableArray<Entity> enemies;

    // Spielfortschritt-Variablen
    private int waveNumber = 0;
    private float waveTimer = DEFAULT_WAVE_INTERVAL;
    private int maxEnemiesPerWave = MAX_ENEMIES_PER_WAVE;
    private float currentWaveInterval = DEFAULT_WAVE_INTERVAL;
    private boolean specialWaveActive = false;
    private int collectedCoins;
    private EntitySpawnPoint lastUsedItemSpawn;

    /**
     * Konstruktor für SpawnLogic
     *
     * @param context Der Spielkontext mit Zugriff auf ECS-Engine und EntityCreator
     */
    public SpawnLogic(StudentsQuest context) {
        this.context = context;
        this.spawnPoints = new Array<>();
        this.availableItemSpawns = new Array<>();
        this.enemySpawnPoints = new Array<>();
        this.creator = context.getEntityCreator();
        this.collectedCoins = 0;
        this.heartSpawnPoints = new Array<>();

        // Initialisiere die Spielerabfrage einmalig
        this.players = context.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());

        // Initialisiere die Gegnerabfrage einmalig
        this.enemies = this.context.getEngine().getEntitiesFor(Family.all(EnemyComponent.class).get());
        registerAsListenerAfterCreation();
    }

    /**
     * Gruppierung der SpawnPoints und initiales Spawnen von Level/Map abhängigen
     * Entitäten.
     * Setzt auch die Parameter für das Wellen-System zurück.
     *
     * @param spawnPoints Liste von Spawnpunkten für Gegner und Items
     */
    public void prepareLevelStart(Array<EntitySpawnPoint> spawnPoints) {
        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Preparing level start with " + spawnPoints.size + " spawn points");
        clear();
        this.spawnPoints.addAll(spawnPoints);
        prepareLevelStart();
    }

    private void prepareLevelStart() {
        // Sortiere die Spawn-Punkte nach Typ
        for (EntitySpawnPoint spawnPoint : this.spawnPoints) {
            String entityType = spawnPoint.entityType();
            if (MapEntityTypes.ITEM.value.equals(entityType)) {
                this.availableItemSpawns.add(spawnPoint);
            } else if (MapEntityTypes.ENEMY.value.equals(entityType)) {
                this.enemySpawnPoints.add(spawnPoint);
            }
        }

        Vector2 playerSpawnPosition = this.context.getMapManager().getCurrentMap().getPlayerSpawnPoint();
        LoggerUtil.log(LogCategory.DEBUG, this, "player to be created at: " + playerSpawnPosition);
        this.context.getEntityCreator().createPlayer(playerSpawnPosition, EntityCreator.HUMAN_HITBOX_WIDTH, EntityCreator.HUMAN_HITBOX_HEIGHT, EntityCreator.HUMAN_ANIMATION_WIDTH, EntityCreator.HUMAN_ANIMATION_HEIGHT);

        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Found " + availableItemSpawns.size + " item spawn points and "
            + enemySpawnPoints.size + " enemy spawn points");

        // Initiales Spawnen von Items
        if (!availableItemSpawns.isEmpty()) {
            //TODO
            //spawnItem();

            for (EntitySpawnPoint spawnPoint : this.availableItemSpawns) {
                this.creator.createCoin(spawnPoint.spawnPoint());
            }
        }

        // Setze die Wellen-Parameter zurück
        resetWaveParameters();
    }

    /**
     * Updates the spawn logic with the wave-based system.
     *
     * @param delta The time elapsed since the last update in seconds
     */
    public void update(float delta) {
        // Prüfe auf neu gesammelte Münzen und spawne ggf. neue
        //TODO
        //checkAndSpawnCoins();

        // Reduce wave timer
        waveTimer -= delta;

        // When timer reaches zero, spawn a new wave
        if (waveTimer <= 0) {
            // Adjust difficulty based on game progression
            adjustDifficulty();

            // Spawn the wave
            spawnWave();

            // Reset timer for next wave
            waveTimer = currentWaveInterval;
        }
        for (int i = heartSpawnPoints.size - 1; i >= 0; i--) {
            Vector2 position = heartSpawnPoints.get(i);
            this.creator.createDropedHeart(position);
            heartSpawnPoints.removeIndex(i); 
        }
    }

    /**
     * Setzt die Wellen-Parameter auf ihre Standardwerte zurück
     */
    private void resetWaveParameters() {
        waveNumber = 0;
        // Erste Welle kommt etwas früher, um das Spiel interessanter zu starten
        waveTimer = DEFAULT_WAVE_INTERVAL * FIRST_WAVE_DELAY_FACTOR;
        maxEnemiesPerWave = MAX_ENEMIES_PER_WAVE;
        currentWaveInterval = DEFAULT_WAVE_INTERVAL;
        specialWaveActive = false;
    }

    /**
     * Spawnt einen Gegner am angegebenen Spawnpunkt.
     *
     * @param spawnPoint Der Spawnpunkt für den Gegner
     */
    private void spawnEnemy(EntitySpawnPoint spawnPoint) {
        // TODO fuers erste ohne spezialWave, wenn Param gesetzt sind, koennen wir noch schauen
        // Gegner in Spezialwellen sind etwas größer und schneller
        //float size = specialWaveActive ? 1.2f : 1.0f;
        this.creator.createEnemy(spawnPoint.spawnPoint(), 0.5f, 0.5f, EntityCreator.HUMAN_ANIMATION_WIDTH, EntityCreator.HUMAN_ANIMATION_HEIGHT);
    }

    /**
     * Spawnt ein neues Item. Es wird ein zufälliger SpawnPunkt aus den möglichen
     * Punkten gewählt.
     * Es wird verhindert, dass beim nächsten Methodenaufruf der gleiche Punkt
     * genommen wird.
     */
    private void spawnItem() {
        if (availableItemSpawns.isEmpty())
            return;

        int randomIndex = MathUtils.random(this.availableItemSpawns.size - 1);
        EntitySpawnPoint newUsedItemSpawn = this.availableItemSpawns.removeIndex(randomIndex);
        this.creator.createCoin(newUsedItemSpawn.spawnPoint());

        if (this.lastUsedItemSpawn != null) {
            this.availableItemSpawns.add(this.lastUsedItemSpawn);
        }
        this.lastUsedItemSpawn = newUsedItemSpawn;
    }

    /**
     * Entfernt alle Referenzen zu Spawnpunkten und setzt die Wellenparameter zurück
     */
    private void clear() {
        this.spawnPoints.clear();
        this.availableItemSpawns.clear();
        this.enemySpawnPoints.clear();
        this.collectedCoins = 0;

        resetWaveParameters();
    }

    private void clearForRestart() {
        this.availableItemSpawns.clear();
        this.enemySpawnPoints.clear();
        this.collectedCoins = 0;
    }

    /**
     * Wählt eine zufällige Teilmenge an Spawnpunkten aus
     * Berücksichtigt dabei die Position des Spielers
     *
     * @return Array mit ausgewählten Spawnpunkten
     */
    private Array<EntitySpawnPoint> selectRandomSpawnPoints() {
        Array<EntitySpawnPoint> validPoints = new Array<>();

        // Zu diesem Zeitpunkt sollte definitiv ein Spieler existieren
        Entity player = EntityUtils.checkAndGetPlayer(this.players);

        Vector2 playerPos = ECSEngine.BOX2D_COMP_MAPPER.get(player).body.getPosition();

        // Sammle alle gültigen Spawnpunkte (nicht zu nah am Spieler)
        for (EntitySpawnPoint point : this.enemySpawnPoints) {
            if (playerPos.dst(point.spawnPoint()) >= PLAYER_SAFE_DISTANCE) {
                validPoints.add(point);
            }
        }

        if (validPoints.size == 0) {
            LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "No valid spawn points found!");
            return validPoints; // Keine gültigen Punkte
        }

        // Bei Spezialwellen mehr Spawnpunkte verwenden
        float usePercentage = specialWaveActive ? Math.min(0.8f, SPAWN_POINT_PERCENTAGE * 1.5f)
                : SPAWN_POINT_PERCENTAGE;

        // Berechne, wie viele Spawnpunkte verwendet werden sollen
        int countToUse = Math.max(1, Math.round(validPoints.size * usePercentage));

        // Mische die Spawnpunkte
        validPoints.shuffle();

        // Wähle die ersten N Punkte aus
        Array<EntitySpawnPoint> selectedPoints = new Array<>();
        for (int i = 0; i < countToUse && i < validPoints.size; i++) {
            selectedPoints.add(validPoints.get(i));
        }

        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG,
                "Selected " + selectedPoints.size + " spawn points out of " + validPoints.size);

        return selectedPoints;
    }

    /**
     * Passt die Schwierigkeit basierend auf dem Spielerfortschritt und der
     * Wellennummer an
     */
    private void adjustDifficulty() {
        Entity player = EntityUtils.checkAndGetPlayer(this.players);

        PlayerComponent playerComp = ECSEngine.PLAYER_COMP_MAPPER.get(player);

        // Fortschrittsfaktor basierend auf gesammelten Münzen
        float progressFactor = playerComp.neededCoins > 0
                ? (float) playerComp.collectedCoins / playerComp.neededCoins
                : 0;

        // Erhöhe max. Gegner pro Welle basierend auf Wellennummer, aber weniger
        // aggressiv
        // Wir wollen eher die Frequenz erhöhen als die Anzahl der Gegner
        maxEnemiesPerWave = MAX_ENEMIES_PER_WAVE +
                Math.round(waveNumber / 5) + // Langsamer ansteigen (vorher /3)
                Math.round(progressFactor * 3); // Weniger Einfluss durch Fortschritt (vorher *5)

        // Begrenze die maximale Anzahl von Gegnern pro Welle auf einen niedrigeren Wert
        maxEnemiesPerWave = Math.min(maxEnemiesPerWave, 10); // Niedrigeres Limit (vorher 15)

        // Reduziere das Wellenintervall stärker mit Fortschritt
        // So erscheinen Wellen häufiger, was die Schwierigkeit erhöht
        currentWaveInterval = DEFAULT_WAVE_INTERVAL *
                Math.max(0.4f, 1.0f - (waveNumber * 0.05f) - (progressFactor * 0.3f));
        // Stärkere Reduzierung (vorher 0.6f, 0.03f, 0.2f)

        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG,
                "Difficulty adjusted: maxEnemies=" + maxEnemiesPerWave +
                        ", waveInterval=" + currentWaveInterval +
                        ", progressFactor=" + progressFactor);
    }

    /**
     * Spawnt eine neue Welle von Gegnern
     */
    private void spawnWave() {
        waveNumber++;

        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Starting wave #" + waveNumber);

        // Entscheide, ob es eine Spezialwelle ist
        specialWaveActive = MathUtils.random() < SPECIAL_WAVE_CHANCE;

        if (specialWaveActive) {
            LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Special wave activated!");
        }

        // Wähle zufällige Spawnpunkte für diese Welle
        Array<EntitySpawnPoint> selectedPoints = selectRandomSpawnPoints();

        if (selectedPoints.isEmpty()) {
            LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "No spawn points available for this wave");
            return; // Keine gültigen Spawnpunkte
        }

        // Begrenze die Anzahl der Gegner auf die Anzahl der verfügbaren Spawnpunkte
        // So stellen wir sicher, dass nur ein Gegner pro Punkt spawnt
        int spawnCount = Math.min(selectedPoints.size, maxEnemiesPerWave);

        // Hole aktuelle Anzahl von Gegnern und berücksichtige das Limit

        spawnCount = Math.min(spawnCount, MAX_ACTIVE_ENEMIES - enemies.size());

        if (spawnCount <= 0) {
            LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Cannot spawn more enemies (limit reached)");
            return; // Kein Platz für mehr Gegner
        }

        // Mische die Spawnpunkte noch einmal für zufällige Auswahl
        selectedPoints.shuffle();

        // Spawne Gegner an den ausgewählten Punkten (nur einen pro Punkt)
        for (int i = 0; i < spawnCount; i++) {
            spawnEnemy(selectedPoints.get(i));
        }

        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Spawned " + spawnCount + " enemies in wave #" + waveNumber);
    }

    /**
     * Prüft, ob der Spieler neue Münzen gesammelt hat und spawnt ggf. neue Münzen
     */
    private void checkAndSpawnCoins() {
        Entity player = EntityUtils.checkAndGetPlayer(this.players);

        int newCoinCount = ECSEngine.PLAYER_COMP_MAPPER.get(player).collectedCoins;

        if (this.collectedCoins < newCoinCount) {
            int coinsCollected = newCoinCount - this.collectedCoins;
            this.collectedCoins = newCoinCount;

            LoggerUtil.log(LogCategory.DEBUG, LOG_TAG,
                    "Player collected " + coinsCollected + " new coins, total: " + newCoinCount);

            // Spawne eine neue Münze für jede gesammelte
            for (int i = 0; i < coinsCollected; i++) {
                spawnItem();
            }
        }
    }

    /**
     * Liefert die aktuelle Wellennummer
     *
     * @return Wellennummer (beginnend mit 1)
     */
    public int getWaveNumber() {
        return waveNumber;
    }

    /**
     * Gibt an, ob derzeit eine Spezialwelle aktiv ist
     *
     * @return true, wenn eine Spezialwelle aktiv ist
     */
    public boolean isSpecialWaveActive() {
        return specialWaveActive;
    }

    /**
     * Gibt die Zeit bis zur nächsten Welle zurück
     *
     * @return Zeit in Sekunden
     */
    public float getTimeUntilNextWave() {
        return waveTimer;
    }

    /**
     * Gibt Informationen über die aktuelle Welle zurück
     *
     * @return Informationsstring zur aktuellen Welle
     */
    public String getWaveInfo() {
        return "Wave: " + waveNumber +
                " | Next wave in: " + String.format("%.1f", waveTimer) + "s" +
                (specialWaveActive ? " | SPECIAL WAVE!" : "");
    }

    @Override
    public void restart() {
        clearForRestart();
        prepareLevelStart();
    }

    @Override
    public void registerAsListenerAfterCreation() {
        this.context.restartables.add(this);
    }


}
