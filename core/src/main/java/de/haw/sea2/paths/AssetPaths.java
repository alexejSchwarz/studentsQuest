package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("placeholderAssets/Sprite_heart.png"),
    BALL("assetsFromTut/Ball.png"),
    COIN_ATLAS("coins/coins.atlas"),
    MAIN_MENU_BACKGROUND("assetsFromTut/MainScreen/atlas/background.jpg"),
    MAIN_MENU_HEADING("assetsFromTut/MainScreen/atlas/main_menu_text.png"),
    HERO_ATLAS("placeholderAssets/heroAtlas.atlas"),
    BUTTON_ATLAS("assetsFromTut/MainScreen/atlas/buttonAtlas.atlas");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
