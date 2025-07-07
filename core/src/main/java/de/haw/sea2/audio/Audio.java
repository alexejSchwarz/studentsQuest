package de.haw.sea2.audio;

public enum Audio {
    LEVEL_1_MUSIC("audio/back_ground_music.mp3",true, 0.7f),
    START_SCREEN_MUSIC("audio/start_screen_music.mp3",true,0.4f),
    COIN_PICKUP_SOUND("audio/coin_sound.wav",false,0.4f),
    HEART_PICKUP_SOUND("audio/heart_pickup_sound.mp3",false,0.7f),
    PLAYER_HIT_SOUND("audio/player_hit_sound.mp3",false,0.7f),
    PLAYER_ATTACK_SOUND_1("audio/player_attack_sound_1.wav",false,0.9f),
    PLAYER_ATTACK_SOUND_2("audio/player_attack_sound_2.wav",false,0.9f),
    ENEMY_HIT_SOUND("audio/enemy_hit_sound.mp3",false,0.8f);

    private final String path;
    private final boolean isMusic;
    private final float volume;

    Audio(String path, boolean isMusic, float volume) {
        this.path = path;
        this.isMusic = isMusic;
        this.volume = volume;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public float getVolume() {
        return this.volume;
    }
}
