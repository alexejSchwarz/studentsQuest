package de.haw.sea2.map.mapObjectEnums;

/**
 * Enum fuer "type" Property Werte in TiledEditor
 */
public enum CustomMapObjectTypes {
    ENTITY_SPAWN_POINT("entitySpawnPoint"),;

    public final String value;

    CustomMapObjectTypes(String value) {
        this.value = value;
    }
}
