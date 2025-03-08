package de.haw.sea2.view;

public enum AnimationType {
    HERO_MOVE_UP("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 0),
    HERO_MOVE_LEFT("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 1),
    HERO_MOVE_DOWN("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 2),
    HERO_MOVE_RIGHT("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 3);

    public final String atlasPath;
    public final String atlasKey;
    public final float frameTime;
    public final int rowIndex;

    AnimationType(String atlasPath, String atlasKey, float frameTime, int rowIndex) {
        this.atlasPath = atlasPath;
        this.atlasKey = atlasKey;
        this.frameTime = frameTime;
        this.rowIndex = rowIndex;
    }

}
