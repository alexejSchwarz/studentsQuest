package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum PlayerAnimation {

    HERO_MOVE_UP(new AnimationType(AssetPaths.HERO_ATLAS.getPath(), "heroUp", 0.05f, 9)),
    HERO_MOVE_LEFT(new AnimationType(AssetPaths.HERO_ATLAS.getPath(), "heroLeft", 0.05f, 9)),
    HERO_MOVE_DOWN(new AnimationType(AssetPaths.HERO_ATLAS.getPath(), "heroDown", 0.05f, 9)),
    HERO_MOVE_RIGHT(new AnimationType(AssetPaths.HERO_ATLAS.getPath(), "heroRight", 0.05f, 9));

    public final AnimationType animationType;

    PlayerAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
