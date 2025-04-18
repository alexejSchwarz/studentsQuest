package de.haw.sea2.view.animations;

public enum CoinAnimation {

    GOLD_COIN_SPIN(new AnimationType("coins/coins.atlas", "MonedaD", 0.15f, 5)),
    GREY_COIN_SPIN(new AnimationType("coins/coins.atlas", "MonedaP", 0.15f, 5));

    public final AnimationType animationType;

    CoinAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
