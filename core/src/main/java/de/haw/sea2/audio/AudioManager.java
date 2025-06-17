package de.haw.sea2.audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private Audio currentAudio;
    private Music currentMusic;
    private final AssetManager assetManager;
    private float currentSliderVolumeSetting;
    private float currentMusicMaxVolume;

    private final Map<Audio, Music> musicCache;
    private final Map<Audio, Sound> soundCache;

    public AudioManager(final StudentsQuest context) {
        this.assetManager = context.getAssetManager();

        this.currentAudio = null;
        this.currentMusic = null;
        currentSliderVolumeSetting = 100.0f;

        this.musicCache = new HashMap<>();
        this.soundCache = new HashMap<>();
    }

    //TODO restart Music function is needed
    public void playAudio(final Audio type) {
        if (type.isMusic()) {
            if (currentAudio == type) {
                if (currentMusic.isPlaying()) {
                    //Der Song spielt schon
                    return;
                } else currentMusic.play();
                return;
            }
            else if (currentMusic != null) {
                currentMusic.stop();
            }

            currentAudio = type;
            currentMusic = musicCache.computeIfAbsent(type, t -> assetManager.get(t.getPath(), Music.class));
            currentMusic.setLooping(true);
            currentMusicMaxVolume = type.getVolume();
            currentMusic.setVolume(Math.min(currentMusicMaxVolume, (currentSliderVolumeSetting / 100f) * currentMusicMaxVolume));
            LoggerUtil.log(LogCategory.LOG, this, "Starte Musik: " + type.name());
            currentMusic.play();
        } else {
            //Sound abspielen
            LoggerUtil.log(LogCategory.LOG, this, "Starte Sound: " + type.name());
            soundCache.computeIfAbsent(type, t -> assetManager.get(t.getPath(), Sound.class)).play();

        }
    }

    public void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        } else {
            LoggerUtil.log(LogCategory.ERROR,this,"Es spielt gerade kein Song, Musik kann daher nicht gestoppt werden.");
        }
    }

    public void setVolume(float volume) {
        currentSliderVolumeSetting = volume;
        currentMusic.setVolume(Math.min(currentMusicMaxVolume, (currentSliderVolumeSetting / 100f) * currentMusicMaxVolume));
    }

    //TODO Threads syncen, damit die Methode nicht ungewollt noch ein anderes Lied stoppt. Erstmal wird stopCurrentMusic() benutzt.
//    public void fadeOutCurrentMusic(final float fadeDurationSeconds) {
//        if (currentMusic == null) {
//            LoggerUtil.log(LogCategory.ERROR, this,
//                "Es spielt gerade kein Song, Musik kann daher nicht ausgeblendet werden.");
//            return;
//        }
//
//        final float startVolume = currentMusic.getVolume();
//        final int targetFrames = (int)(fadeDurationSeconds * 60);  // Anzahl der Schritte
//
//        Timer.schedule(new Timer.Task() {
//            private int frame = 0;
//
//            @Override
//            public void run() {
//                frame++;
//                float progress = Math.min(1f, frame / (float)targetFrames);
//                currentMusic.setVolume(startVolume * (1f - progress));
//
//                if (progress >= 1f) {
//                    currentMusic.pause();
//                    cancel();
//                }
//            }
//        }, 0f, 1/60f);  // Delay = 0, Interval = 1/60 s
//    }

}
