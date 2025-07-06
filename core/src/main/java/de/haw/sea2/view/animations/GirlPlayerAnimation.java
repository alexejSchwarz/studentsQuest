package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum GirlPlayerAnimation {

    PLAYER_MOVE_UP(new AnimationType(AssetPaths.GIRL_PLAYER_ATLAS.getPath(), "oben", 0.075f, 5)),
    PLAYER_MOVE_LEFT(new AnimationType(AssetPaths.GIRL_PLAYER_ATLAS.getPath(), "links", 0.075f, 5)),
    PLAYER_MOVE_DOWN(new AnimationType(AssetPaths.GIRL_PLAYER_ATLAS.getPath(), "unten", 0.075f, 5)),
    PLAYER_MOVE_RIGHT(new AnimationType(AssetPaths.GIRL_PLAYER_ATLAS.getPath(), "rechts", 0.075f, 5));

    public final AnimationType animationType;

    GirlPlayerAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
