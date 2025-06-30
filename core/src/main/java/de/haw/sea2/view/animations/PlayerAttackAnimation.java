package de.haw.sea2.view.animations;

public enum PlayerAttackAnimation {
    ATTACK_TOP("attackTop"),
    ATTACK_RIGHT("attackRight"),
    ATTACK_LEFT("attackLeft"),
    ATTACK_DOWN("attackDown");

    public final String attalasKey;

    PlayerAttackAnimation(String attalasKey) {
        this.attalasKey = attalasKey;
    }
}
