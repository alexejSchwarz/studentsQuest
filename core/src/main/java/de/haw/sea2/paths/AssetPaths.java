package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("placeholderAssets/Sprite_heart.png"),
    BALL("assetsFromTut/Ball.png"),
    COIN("assetsFromTut/coin.png"),
    CHARANDEFFEKTATLAS("assetsFromTut/character_and_effect.atlas");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
