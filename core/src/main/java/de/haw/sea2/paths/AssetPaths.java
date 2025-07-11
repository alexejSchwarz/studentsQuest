package de.haw.sea2.paths;

public enum AssetPaths {

    HEARTH("screens/gameScreen/spriteHeart.png"),
    BALL("screens/gameScreen/ball.png"),
    COIN_ATLAS("screens/gameScreen/coins/coins.atlas"),
    MAIN_MENU_BACKGROUND("screens/mainScreen/BackgroundMainScreen.jpg"),
    M_PLAY("screens/mainScreen/PlayButton.jpg"),
    M_CREDITS("screens/mainScreen/Credits.jpg"),
    M_SOUND("screens/mainScreen/Sounds.jpg"),
    M_STORY("screens/mainScreen/Story.jpg"),
    M_KEYBINDS("screens/mainScreen/Tutorial.jpg"),
    ENEMY_ATLAS("customEntities/enemyAtlas.atlas"),
    BOY_PLAYER_ATLAS("customEntities/BoyPlayerAtlasCustom.atlas"),
    GIRL_PLAYER_ATLAS("customEntities/GirlPlayerAtlas.atlas"),
    BUTTON_ATLAS("screens/mainScreen/button.atlas"),
    MISSION_SCREEN("screens/InfoScreen/InfoScreen.png"),
    SLIDER_BACKGROUND("screens/settingsScreen/slider_background.png"),
    SLIDER_KNOB("screens/settingsScreen/slider_knob.png"),
    ATTACK_ATLAS("customEntities/AttackAtlas.atlas"),
    GAMEOVER_SCREEN("screens/GameoverScreen/GameOver_ohneButtons.png"),
    BACK_TO_MAIN_MENU_BUTTON("screens/buttons/backToMainMenu.png"),
    RESTART_BUTTON("screens/buttons/neustart.png"),
    SUCCESS_SCREEN("screens/successScreen/SuccessScreen.png"),
    CHAR_SCREEN("screens/characterScreen/Screen_character_mit_schrift.png"),
    BOY_BUTTON("screens/characterScreen/boy_button.jpeg"),
    GIRL_BUTTON("screens/characterScreen/girl_button.jpeg"),
    PLAY_BUTTON("screens/InfoScreen/PlayButton.png"),
    HAUPT_MENU_TEXT_BUTTON("screens/buttons/HauptmenuPause_Button.png"),
    STORY_SCREEN("screens/storyScreen/StoryScreenV2.jpg"),
    FORTSETZEN_BUTTON("screens/buttons/Fortsetzen_Button.png"),
    PAUSE_SCREEN("screens/pauseScreen/PausenScreen.png"),
    KEY_BIND_SCREEN("screens/keybindScreen/keyScreen.jpg"),
    CREDIT_SCREEN("screens/creditScreen/CreditsScreen.png"),
    HOME_BUTTON("screens/settingsScreen/home_button.png");

    private final String path;

    AssetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
