package de.haw.sea2.screen;

import java.util.EnumMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import de.haw.sea2.StudentsQuest;

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

    /**
     * Der StudentsQuest-Kontext, der Zugriff auf zentrale Ressourcen bietet.
     */
    private final StudentsQuest game;

    /**
     * ScreenCache für bereits erstellte Screens.
     */
    private final Map<ScreenType, Screen> screenCache;

    /**
     * Der aktuell angezeigte Screen.
     */
    private Screen currentScreen;

    /**
     * Der zuvor angezeigte Screen (für Rückkehr-Funktionalität).
     */
    private Screen previousScreen;

    /**
     * Erstellt einen neuen ScreenManager mit dem angegebenen Spiel-Kontext.
     *
     * @param game Der StudentsQuest-Kontext
     */
    public ScreenManager(StudentsQuest game) {
        this.game = game;
        this.screenCache = new EnumMap<>(ScreenType.class); // durch das verwenden der ScreenType Klasse wird
                                                            // sichergestellt, dass keine doppelten Bildschirme
                                                            // entstehen können
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
        game.setScreen(screen);
        currentScreen = screen;
    }

    /**
     * Zeigt einen Screen mit Ladebildschirm an.
     *
     * @param targetScreenType Der Typ des Zielscreens
     */
    public void showScreenWithLoading(ScreenType targetScreenType) {
        // Speichert den aktuellen Screen als vorherigen Screen
        previousScreen = currentScreen;

        // Erstelle einen LoadingScreen mit dem Ziel-Screen-Typ
        Screen loadingScreen = createLoadingScreen(targetScreenType);
        game.setScreen(loadingScreen);
        currentScreen = loadingScreen;
    }

    /**
     * Kehrt zum vorherigen Screen zurück.
     */
    public void returnToPreviousScreen() {
        if (previousScreen != null) {
            // Debug-Ausgabe
            Gdx.app.debug("ScreenManager",
                    "Kehre zurück zum vorherigen Screen: " + previousScreen.getClass().getSimpleName());

            // Setze den vorherigen Screen als aktuellen Screen
            game.setScreen(previousScreen);
            currentScreen = previousScreen;
            previousScreen = null;

            // Wenn zum GameScreen zurückgekehrt wird, rufe resume() auf
            if (currentScreen instanceof GameScreen) {
                currentScreen.resume();
            }
        } else {
            Gdx.app.log("ScreenManager", "Kein vorheriger Screen vorhanden");
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
                return new GameScreen(game);
            case MAIN_MENU:
                return new MainMenuScreen(game);
            case PAUSE:
                return new PauseGameScreen(game);

                //TODO hier mal schauen, ob das nicht spaeter createScreenWithLoading gepackt werden kann
            case LOADING:
                return new LoadingScreen(game, ScreenType.MAIN_MENU);
            default:
                Gdx.app.error("ScreenManager", "Unbekannter ScreenType: " + screenType);
                return new MainMenuScreen(game); // Fallback
        }
    }

    /**
     * Erstellt einen LoadingScreen mit dem angegebenen Ziel-Screen-Typ.
     *
     * @param targetScreenType Der Ziel-Screen-Typ, zu dem nach dem Laden gewechselt
     *                         werden soll
     * @return Ein neuer LoadingScreen
     */
    private Screen createLoadingScreen(ScreenType targetScreenType) {
        return new LoadingScreen(game, targetScreenType);
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
        if (screenType == ScreenType.LOADING) {
            return new LoadingScreen(game, ScreenType.MAIN_MENU);
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
     * TODO: Sollte beim Beenden des Spiels aufgerufen werden.
     */
    public void dispose() {
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();
    }
}
