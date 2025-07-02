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
    MISSION_SCREEN("screens/InfoScreen.png"),
    SLIDER_BACKGROUND("screens/settingsScreen/slider_background.png"),
    SLIDER_KNOB("screens/settingsScreen/slider_knob.png"),
    ATTACK_ATLAS("customEntities/AttackAtlas.atlas"),
    GAMEOVER_SCREEN("screens/GameoverScreen/GameOver_ohneButtons.png"),
    BACK_TO_MAIN_MENU_BUTTON("screens/buttons/backToMainMenu.png"),
    RESTART_BUTTON("screens/buttons/neustart.png"),
    SUCCESS_SCREEN("screens/Win_Screen.png"),
    HOME_BUTTON("screens/settingsScreen/home_button.png");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
