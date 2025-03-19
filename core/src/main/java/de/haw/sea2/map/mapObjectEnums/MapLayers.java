package de.haw.sea2.map.mapObjectEnums;

public enum MapLayers {
    COLLISION("collision"),
    ENTITY_SPAWN_POINTS("entitySpawnPoints");

    public final String value;

    MapLayers(String value) {
        this.value = value;
    }
}
