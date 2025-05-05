package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum BoyPlayerAnimation {
    PLAYER_MOVE_UP(new AnimationType(AssetPaths.BOY_PLAYER_ATLAS.getPath(), "boy_player_up", 0.075f, 5)),
    PLAYER_MOVE_LEFT(new AnimationType(AssetPaths.BOY_PLAYER_ATLAS.getPath(), "boy_player_left", 0.075f, 5)),
    PLAYER_MOVE_DOWN(new AnimationType(AssetPaths.BOY_PLAYER_ATLAS.getPath(), "boy_player_down", 0.075f, 5)),
    PLAYER_MOVE_RIGHT(new AnimationType(AssetPaths.BOY_PLAYER_ATLAS.getPath(), "boy_player_right", 0.075f, 5));

    public final AnimationType animationType;

    BoyPlayerAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
