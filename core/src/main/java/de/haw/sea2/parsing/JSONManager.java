package de.haw.sea2.parsing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONObject;

public class JSONManager {

    private static FileHandle gameConfigJson;
    private static String jsonString;
    private static JSONObject gameConfigJSONObject;

    static {
        gameConfigJson = Gdx.files.local("resources/config.json");
        jsonString = gameConfigJson.readString();
        gameConfigJSONObject = new JSONObject(jsonString);

        System.out.println("Game config: " + gameConfigJSONObject);
    }

    public static float getVolume() {
        Object audioJSON = gameConfigJSONObject.get("audio");
        if (audioJSON instanceof JSONObject) {
            return ((JSONObject) audioJSON).getFloat("music_volume");
        }
        return 0.0f;
    }

    public static void updateVolume(float newVolume) {
        Object audioJSON = gameConfigJSONObject.get("audio");
        if (audioJSON instanceof JSONObject) {
            ((JSONObject) audioJSON).put("music_volume", newVolume);
        }
        updateJson();
    }

    public static void updateJson() {
        gameConfigJson.writeString(gameConfigJSONObject.toString(), false);
    }
}
