package de.haw.sea2.map.mapObjectEnums;

/**
 * Enum fuer Werte von Layers im TiledEditor
 */
public enum MapLayers {
    COLLISION("collision"),
    ENTITY_SPAWN_POINTS("entitySpawnPoints");

    public final String value;

    MapLayers(String value) {
        this.value = value;
    }
}
