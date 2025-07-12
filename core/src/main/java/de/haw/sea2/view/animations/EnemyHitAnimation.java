package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum EnemyHitAnimation {

    ENEMY_MOVE_UP(new AnimationType(AssetPaths.ENEMY_HIT_ATLAS.getPath(), "obenW", 0.075f, 5)),
    ENEMY_MOVE_LEFT(new AnimationType(AssetPaths.ENEMY_HIT_ATLAS.getPath(), "linksW", 0.075f, 5)),
    ENEMY_MOVE_DOWN(new AnimationType(AssetPaths.ENEMY_HIT_ATLAS.getPath(), "untenW", 0.075f, 5)),
    ENEMY_MOVE_RIGHT(new AnimationType(AssetPaths.ENEMY_HIT_ATLAS.getPath(), "rechtsW", 0.075f, 5));

    public final AnimationType animationType;

    EnemyHitAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
