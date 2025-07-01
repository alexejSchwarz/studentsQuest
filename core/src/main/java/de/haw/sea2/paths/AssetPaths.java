package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("screens/gameScreen/spriteHeart.png"),
    BALL("screens/gameScreen/ball.png"),
    COIN_ATLAS("screens/gameScreen/coins/coins.atlas"),
    MAIN_MENU_BACKGROUND("screens/mainScreen/background.jpg"),
    MAIN_MENU_HEADING("screens/mainScreen/text.png"),
    ENEMY_ATLAS("customEntities/enemyAtlas.atlas"),
    BOY_PLAYER_ATLAS("customEntities/playerAtlasCustom.atlas"),
    BUTTON_ATLAS("screens/mainScreen/button.atlas"),
    MISSION_SCREEN("screens/missionScreen.png"),
    SLIDER_BACKGROUND("screens/settingsScreen/slider_background.png"),
    SLIDER_KNOB("screens/settingsScreen/slider_knob.png"),
    ATTACK_ATLAS("customEntities/AttackAtlas.atlas"),
    HOME_BUTTON("screens/settingsScreen/home_button.png");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
