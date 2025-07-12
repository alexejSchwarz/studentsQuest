package de.haw.sea2.screen;

import java.util.EnumMap;
import java.util.Map;
import com.badlogic.gdx.Screen;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

/**
 * Zentrale Verwaltungskomponente für alle Screens im Spiel.
 *
 * <p>
 * Diese Klasse ist verantwortlich für:
 * <ul>
 * <li>Verwaltung und Wechsel zwischen verschiedenen Screens</li>
 * <li>Verwaltung des vorherigen Screens (für Rückkehr-Funktionalität)</li>
 * <li>Erstellung neuer Screen-Instanzen nach Bedarf</li>
 * </ul>
 * </p>
 */
public class ScreenManager {

    private final StudentsQuest context;

    private final Map<ScreenType, Screen> screenCache;

    private Screen currentScreen;

    private Screen previousScreen;

    /**
     * Erstellt einen neuen ScreenManager mit dem angegebenen Spiel-Kontext.
     *
     * @param context Der StudentsQuest-Kontext
     */
    public ScreenManager(StudentsQuest context) {
        this.context = context;
        this.screenCache = new EnumMap<>(ScreenType.class);
    }

    /**
     * Zeigt einen Screen des angegebenen Typs an.
     *
     * @param screenType Der Typ des anzuzeigenden Screens
     */
    public void showScreen(ScreenType screenType) {
        // Speichert den aktuellen Screen als vorherigen Screen
        previousScreen = currentScreen;

        Screen screen = getOrCreateScreen(screenType);
        context.setScreen(screen);
        currentScreen = screen;
    }

    /**
     * Kehrt zum vorherigen Screen zurück.
     */
    public void returnToPreviousScreen() {
        if (previousScreen != null) {
            LoggerUtil.debug(LogCategory.DEBUG,this,"Kehre zurück zum vorherigen Screen: " + previousScreen.getClass().getSimpleName());

            // Setze den vorherigen Screen als aktuellen Screen
            context.setScreen(previousScreen);
            currentScreen = previousScreen;
            previousScreen = null;

            // Wenn zum GameScreen zurückgekehrt wird, rufe resume() auf
            if (currentScreen instanceof GameScreen) {
                currentScreen.resume();
            }
        } else {
            LoggerUtil.error(LogCategory.ERROR, this, "Kein vorheriger Screen vorhanden");
        }
    }

    /**
     * Erstellt einen neuen Screen des angegebenen Typs.
     *
     * @param screenType Der Typ des zu erstellenden Screens
     * @return Der erstellte Screen
     */
    private Screen createScreen(ScreenType screenType) {
        switch (screenType) {
            case GAME:
                return new GameScreen(context);
            case MAIN_MENU:
                return new MainMenuScreen(context);
            case INFO_SCREEN_LV1:
                return new InfoScreenLevel1(context);
            case PAUSE:
                return new PauseGameScreen(context);
            case LOADING:
                return new LoadingScreen(context);
            case SUCCESS:
                return new SuccessScreen(context);
            case SETTINGS:
                return new SettingsScreen(context);
            case GAME_OVER:
                return new GameOverScreen(context);
            case CHAR_SCREEN:
                return new CharacterScreen(context);
            case STORY_SCREEN:
                return new StoryScreen(context);
            case KEY_SCREEN:
                return new KeyBindScreen(context);
            case CREDITS_SCREEN:
                return new CreditScreen(context);
            default:
                LoggerUtil.error(LogCategory.ERROR,this,"Unbekannter ScreenType: " + screenType);
                return new MainMenuScreen(context); // Fallback
        }
    }

    /**
     * Holt einen Screen aus dem Cache oder erstellt einen neuen, falls er noch
     * nicht existiert.
     *
     * @param screenType Der Typ des Screens
     * @return Der vorhandene oder neu erstellte Screen
     */
    private Screen getOrCreateScreen(ScreenType screenType) {
        // Bei LOADING immer neue Instanz erstellen, da diese spezifische Parameter
        // enthält
        //TODO mal schauen, wegen Restart-faehigkeit, eventuell nicht noetig hier einen neuen Screen zu erstellen
        if (screenType == ScreenType.LOADING) {
            return new LoadingScreen(context);
        }

        // Prüfe, ob der Screen bereits im Cache ist
        Screen cachedScreen = screenCache.get(screenType);
        if (cachedScreen != null) {
            return cachedScreen;
        }

        // Erstelle einen neuen Screen und speichere ihn im Cache
        Screen newScreen = createScreen(screenType);
        screenCache.put(screenType, newScreen);
        return newScreen;
    }

    /**
     * Entfernt einen Screen aus dem Cache, sodass er bei der nächsten Anforderung
     * neu erstellt wird.
     * Nützlich, wenn ein Screen in einen inkonsistenten Zustand geraten ist.
     *
     * @param screenType Der Typ des zu entfernenden Screens
     */
    public void invalidateScreen(ScreenType screenType) {
        Screen screen = screenCache.remove(screenType);
        if (screen != null) {
            // Ressourcen des entfernten Screens freigeben
            screen.dispose();
        }
    }

    /**
     * Gibt alle gecachten Screens frei und leert den Cache.
     */
    public void dispose() {
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();
    }
}
