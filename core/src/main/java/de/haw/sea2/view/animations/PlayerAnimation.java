package de.haw.sea2.view.animations;

public enum PlayerAnimation {

    HERO_MOVE_UP(new AnimationType("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 0)),
    HERO_MOVE_LEFT(new AnimationType("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 1)),
    HERO_MOVE_DOWN(new AnimationType("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 2)),
    HERO_MOVE_RIGHT(new AnimationType("assetsFromTut/character_and_effect.atlas", "hero", 0.05f, 3));

    public final AnimationType animationType;

    PlayerAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
