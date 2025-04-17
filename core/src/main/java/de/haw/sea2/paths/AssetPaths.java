package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("screens/gameScreen/spriteHeart.png"),
    BALL("screens/gameScreen/ball.png"),
    COIN_ATLAS("screens/gameScreen/coins/coins.atlas"),
    MAIN_MENU_BACKGROUND("screens/mainScreen/background.jpg"),
    MAIN_MENU_HEADING("screens/mainScreen/text.png"),
    HERO_OLD_ATLAS("screens/gameScreen/heroAtlas.atlas"),
    BOY_PLAYER_ATLAS("customEntities/playerAtlasCustom.atlas"),
    BUTTON_ATLAS("screens/mainScreen/button.atlas");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
