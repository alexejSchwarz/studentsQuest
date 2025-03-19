package de.haw.sea2.map.mapObjectEnums;

public enum MapEntityTypes {

    PLAYER("player"),
    ITEM("item"),
    ENEMY("enemy");

    public final String value;

    MapEntityTypes(String value) {
        this.value = value;
    }
}
