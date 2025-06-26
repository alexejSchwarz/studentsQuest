package de.haw.sea2.parsing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONObject;

public class JSONManager {


    private static FileHandle USER_SOUND_CONFIG;
    private static JSONObject USER_SOUND_JSON_OBJECT;

    private static final String USER_CONFIG_FILENAME = "../soundConfig.json"; // relative to working dir
    private static final String DEFAULT_CONFIG_PATH = "resources/config.json";

    static {
        FileHandle userFile = Gdx.files.local(USER_CONFIG_FILENAME);
        if (!userFile.exists()) {
            FileHandle defaultFile = Gdx.files.internal(DEFAULT_CONFIG_PATH);
            userFile.writeString(defaultFile.readString(), false);
        }

        USER_SOUND_CONFIG = userFile;
        USER_SOUND_JSON_OBJECT = new JSONObject(userFile.readString());
    }

    public static float getVolume() {
        Object audioJSON = USER_SOUND_JSON_OBJECT.get("audio");
        if (audioJSON instanceof JSONObject) {
            return ((JSONObject) audioJSON).getFloat("music_volume");
        }
        return 0.0f;
    }

    public static void updateVolume(float newVolume) {
        Object audioJSON = USER_SOUND_JSON_OBJECT.get("audio");
        if (audioJSON instanceof JSONObject) {
            ((JSONObject) audioJSON).put("music_volume", newVolume);
        }
        updateJson();
    }

    public static void updateJson() {
        USER_SOUND_CONFIG.writeString(USER_SOUND_JSON_OBJECT.toString(), false);
    }
}
