package de.haw.sea2.map.mapObjectEnums;

/**
 * Enum fuer property "entityType" Werte. Wird im Moment fuer "type": "entitySpawnPoint" verwendet
 */
public enum MapEntityTypes {

    PLAYER("player"),
    ITEM("item"),
    ENEMY("enemy");

    public final String value;

    MapEntityTypes(String value) {
        this.value = value;
    }
}
