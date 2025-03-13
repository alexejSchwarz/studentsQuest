package de.haw.sea2.Debug;

import com.badlogic.gdx.Gdx;

public class LoggerUtil {

    public static void log(LogCategory category, Object source, String message) {
        Gdx.app.log(category.getLogCategory()+ ": " + source.getClass().getSimpleName(), message);
    }

    public static void error(LogCategory category, Object source, String message) {
        Gdx.app.error(category.getLogCategory() + ": " + source.getClass().getSimpleName(), message);
    }

    public static void debug(LogCategory category, Object source, String message) {
        Gdx.app.debug(category.getLogCategory() + ": " + source.getClass().getSimpleName(), message);
    }
}

