package de.haw.sea2.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
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

    public void fadeOutCurrentMusic(final float fadeDurationSeconds) {
        if (currentMusic == null) {
            LoggerUtil.log(LogCategory.ERROR, this,
                "Es spielt gerade kein Song, Musik kann daher nicht ausgeblendet werden.");
            return;
        }

        final float startVolume = currentMusic.getVolume();
        final int targetFrames = (int)(fadeDurationSeconds * 60);  // Anzahl der Schritte

        Timer.schedule(new Timer.Task() {
            private int frame = 0;

            @Override
            public void run() {
                frame++;
                float progress = Math.min(1f, frame / (float)targetFrames);
                currentMusic.setVolume(startVolume * (1f - progress));

                if (progress >= 1f) {
                    currentMusic.pause();
                    cancel();
                }
            }
        }, 0f, 1/60f);  // Delay = 0, Interval = 1/60 s
    }

}
