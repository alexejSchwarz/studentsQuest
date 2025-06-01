package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum EnemyAnimation {

    ENEMY_MOVE_UP(new AnimationType(AssetPaths.ENEMY_ATLAS.getPath(), "enemyUp", 0.075f, 5)),
    ENEMY_MOVE_LEFT(new AnimationType(AssetPaths.ENEMY_ATLAS.getPath(), "enemyLeft", 0.075f, 5)),
    ENEMY_MOVE_DOWN(new AnimationType(AssetPaths.ENEMY_ATLAS.getPath(), "enemyDown", 0.075f, 5)),
    ENEMY_MOVE_RIGHT(new AnimationType(AssetPaths.ENEMY_ATLAS.getPath(), "enemyRight", 0.075f, 5));

    public final AnimationType animationType;

    EnemyAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
