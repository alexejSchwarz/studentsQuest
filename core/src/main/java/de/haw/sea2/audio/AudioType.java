package de.haw.sea2.audio;

public enum AudioType {
    LEVEL_1("audio/back_ground_music.mp3",true, 0.4f),
    COIN_PICKUP("audio/coin_sound.wav",false,0.4f);

    private final String path;
    private final boolean isMusic;
    private final float volume;

    AudioType(String path, boolean isMusic, float volume) {
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
