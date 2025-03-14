package de.haw.sea2.paths;

public enum MapPaths {

    MAINMAP("mainMap.tmx"),
    MAP2("map2.tmx");

    private final String path;

    MapPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
