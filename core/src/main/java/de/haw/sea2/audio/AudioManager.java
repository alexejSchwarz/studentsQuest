package de.haw.sea2.audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

public class AudioManager {
    private AudioType currentMusicType;
    private Music currentMusic;
    private final AssetManager assetManager;

    public AudioManager(final StudentsQuest context) {
        this.assetManager = context.getAssetManager();

        this.currentMusicType = null;
        this.currentMusic = null;
    }

    public void playAudio(final AudioType type) {
        if (type.isMusic()) {
            if (currentMusicType == type) {
                if (currentMusic.isPlaying()) {
                    //Der Song spielt schon
                    return;
                } else currentMusic.play();
                return;
            }
            else if (currentMusic != null) {
                currentMusic.stop();
            }

            currentMusicType = type;
            currentMusic = assetManager.get(type.getPath(), Music.class);
            currentMusic.setLooping(true);
            currentMusic.setVolume(type.getVolume());
            currentMusic.play();
        } else {
            //Sound abspielen
            assetManager.get(type.getPath(), Sound.class).play();
        }
    }

    public void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        } else {
            LoggerUtil.log(LogCategory.ERROR,this,"Es spielt gerade kein Song, Musik kann daher nicht gestoppt werden.");
        }
    }

}
