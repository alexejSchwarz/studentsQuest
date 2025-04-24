package de.haw.sea2.audio;

public enum Audio {
    LEVEL_1_MUSIC("audio/back_ground_music.mp3",true, 0.6f),
    START_SCREEN_MUSIC("audio/start_screen_music.mp3",true,0.4f),
    COIN_PICKUP_SOUND("audio/coin_sound.wav",false,0.4f);

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
