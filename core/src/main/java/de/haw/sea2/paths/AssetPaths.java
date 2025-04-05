package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("placeholderAssets/Sprite_heart.png"),
    BALL("assetsFromTut/Ball.png"),
    COIN("assetsFromTut/coin.png"),
    MAINMENUBACKGROUND("assetsFromTut/MainScreen/atlas/background.jpg"),
    MAINMENUHEADING("assetsFromTut/MainScreen/atlas/main_menu_text.png"),
    CHARANDEFFEKTATLAS("assetsFromTut/character_and_effect.atlas"),
    BUTTONATLAS("assetsFromTut/MainScreen/atlas/buttonAtlas.atlas");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
