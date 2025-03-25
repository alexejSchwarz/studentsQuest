package de.haw.sea2.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.Bits;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.RemoveComponent;

/**
 * Verwaltet das Laden, Aktivieren und Verwalten von Spielkarten.
 */
public class MapManager implements Disposable {

    private final StudentsQuest context;
    private final ObjectMap<String, GameMap> mapCache;
    private final Array<Body> bodies;
    private final Array<MapChangeListener> mapChangeListeners;

    private GameMap currentMap;

    public MapManager(StudentsQuest context) {
        this.context = context;
        this.mapCache = new ObjectMap<>();
        this.bodies = new Array<>();

        // for now like this. add/remove listener methods later, if needed
        this.mapChangeListeners = new Array<>();
        this.mapChangeListeners.add(this.context.getGameRenderer());
    }

    /**
     * Bereitet das Laden einer Karte vor.
     * Diese Methode wird vom LoadingScreen aufgerufen, um den Ladevorgang zu
     * starten.
     *
     * @param mapPath Pfad zur Kartendatei
     */
    public void loadMap(String mapPath) {
        // Registriert den TmxMapLoader, falls noch nicht geschehen
        if (!context.getAssetManager().isLoaded(mapPath)) {
            context.getAssetManager().setLoader(TiledMap.class,
                    new TmxMapLoader(context.getAssetManager().getFileHandleResolver()));
            context.getAssetManager().load(mapPath, TiledMap.class);
        }
    }

    /**
     * Aktiviert eine bereits geladene Karte und bereitet sie für die Verwendung
     * vor.
     * Diese Methode wird vom GameScreen aufgerufen, nachdem die Assets geladen
     * wurden.
     *
     * @param mapPath Pfad zur Kartendatei
     * @return Die aktivierte GameMap-Instanz
     */
    public void activateMap(String mapPath) {

        // Prüfe, ob die Karte bereits im Cache ist
        if (mapCache.containsKey(mapPath)) {
            GameMap cachedMap = mapCache.get(mapPath);

            if (currentMap != null && cachedMap == currentMap) {
                return;
            }

            context.getGameRenderer().getMapRenderer().setMap(cachedMap.getTiledMap());

            // alte Walls zerstoeren und neue erzeugen
            destroyCollisionWalls();
            createCollisionWalls(cachedMap);
            this.currentMap = cachedMap;
            return;
        }

        // Prüfe, ob die Karte im AssetManager geladen ist
        if (!context.getAssetManager().isLoaded(mapPath)) {
            throw new IllegalStateException("Map " + mapPath + " wurde nicht geladen!");
        }

        // Hole die geladene Karte aus dem AssetManager
        TiledMap tiledMap = context.getAssetManager().get(mapPath, TiledMap.class);

        // Erstelle ein GameMap-Objekt und cache es
        GameMap gameMap = new GameMap(tiledMap);
        mapCache.put(mapPath, gameMap);

        // alte Walls zerstoeren und neue erzeugen
        destroyCollisionWalls();
        createCollisionWalls(gameMap);

        this.currentMap = gameMap;

        notifyMapChange();
    }


    // TODO: Refactor collision wall creation to use EntityFactory/Creator for consistency.
    /**
     * Erstellt Kollisionswände in der Spielwelt basierend auf den übergebenen
     * Kollisionsbereichen.
     *
     * <p>
     * Diese Methode verarbeitet eine Liste von Kollisionsbereichen und erstellt für
     * jeden
     * eine statische Wand in der Physik-Welt. Diese Wände sind unbewegliche
     * Objekte, mit
     * denen der Spieler und andere Entitäten kollidieren können.
     * </p>
     *
     * @param map Enthaelt eine Liste von Kollisionsbereichen, die die Form und
     *            Position der Wände definieren
     */
    private void createCollisionWalls(GameMap map) {

        Array<CollisionArea> collisionAreas = map.getCollisionAreas();

        // Für jeden Kollisionsbereich in der Liste
        collisionAreas.forEach(collisionArea -> {
            // Erstelle eine neue Wand-Entität
            Entity wall = this.context.getEngine().createEntity();

            // Setze die physikalischen Eigenschaften zurück und erstelle eine
            // Box2D-Komponente
            StudentsQuest.resetBodieAndFixtureDefinition();
            final Box2DComponent b2dComp = this.context.getEngine().createComponent(Box2DComponent.class);

            // Setze die Position der Wand
            StudentsQuest.BODY_DEF.position.set(collisionArea.getX(), collisionArea.getY());
            StudentsQuest.BODY_DEF.type = BodyDef.BodyType.StaticBody; // Unbeweglicher Körper

            // Erstelle den physikalischen Körper in der Welt
            b2dComp.body = this.context.getWorld().createBody(StudentsQuest.BODY_DEF);
            b2dComp.body.setUserData(wall); // Markiert den Körper als Wand

            // Setze die Kollisionsfilter (Wände kollidieren mit allem)
            StudentsQuest.FIXTURE_DEF.filter.categoryBits = Bits.BIT_WALL.value;
            StudentsQuest.FIXTURE_DEF.filter.maskBits = Bits.BIT_COLLIDES_WITH_EVERYTHING.value;

            // Erstelle eine Kettenform für die Wand aus den Punkten im Kollisionsbereich
            final ChainShape cShape = new ChainShape();
            cShape.createChain(collisionArea.getVertices());

            // Füge die Form dem Körper hinzu
            StudentsQuest.FIXTURE_DEF.shape = cShape;
            b2dComp.body.createFixture(StudentsQuest.FIXTURE_DEF);
            cShape.dispose(); // Wichtig: Ressourcen freigeben

            // Füge die Box2D-Komponente zur Entität hinzu und registriere sie in der Engine
            wall.add(b2dComp);
            this.context.getEngine().addEntity(wall);
        });
    }

    /**
     * Soll bei jeder Mapchaneg aufgerufen werden
     */
    private void destroyCollisionWalls() {
        this.context.getWorld().getBodies(this.bodies);
        for (Body body : this.bodies) {
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.getFilterData().categoryBits == Bits.BIT_WALL.value) {
                    Entity wallEntity = (Entity) body.getUserData();
                    wallEntity.add(new RemoveComponent());
                }
            }
        }
    }

    /**
     * Gibt eine Karte frei, wenn sie nicht mehr benötigt wird.
     */
    public void disposeMap(String mapPath) {
        if (mapCache.containsKey(mapPath)) {
            mapCache.get(mapPath).dispose();
            // Hier könnten wir die Map aus dem AssetManager entfernen,
            // wenn wir sie nicht mehr benötigen
            mapCache.remove(mapPath);
        }
    }

    public GameMap getCurrentMap() {
        return this.currentMap;
    }

    @Override
    public void dispose() {
        // Alle gecachten Maps freigeben
        for (GameMap map : mapCache.values()) {
            map.dispose();
        }
        mapCache.clear();
    }

    private void notifyMapChange() {
        for (MapChangeListener listener : this.mapChangeListeners) {
            listener.onMapChange(this.currentMap);
        }
    }
}
