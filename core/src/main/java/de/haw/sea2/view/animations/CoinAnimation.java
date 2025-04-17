package de.haw.sea2.view.animations;

import de.haw.sea2.paths.AssetPaths;

public enum CoinAnimation {

    GOLD_COIN_SPIN(new AnimationType(AssetPaths.COIN_ATLAS.getPath(), "MonedaD", 0.15f, 5)),
    GREY_COIN_SPIN(new AnimationType(AssetPaths.COIN_ATLAS.getPath(), "MonedaP", 0.15f, 5));

    public final AnimationType animationType;

    CoinAnimation(AnimationType animationType) {
        this.animationType = animationType;
    }
}
