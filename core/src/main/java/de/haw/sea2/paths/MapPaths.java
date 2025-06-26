package de.haw.sea2.paths;

public enum MapPaths {

    MAINMAP("maps/firstMap/HAW_Map_V6.tmx"),
    MAP2("map2.tmx");

    private final String path;

    MapPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
