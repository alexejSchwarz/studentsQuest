package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;

import de.haw.sea2.StudentsQuest;

/**
 * Der Startbildschirm des Spiels mit dem Hauptmenü.
 * 
 * <p>
 * Diese Klasse implementiert das libGDX Screen-Interface und stellt einen
 * einfachen
 * Willkommensbildschirm dar, der den Spieltitel anzeigt und auf Tastendruck
 * wartet,
 * um das eigentliche Spiel zu starten.
 * </p>
 * 
 * <p>
 * Das Hauptmenü ist absichtlich einfach gehalten und zeigt nur den Titel
 * "StudentQuest" und eine Aufforderung, eine beliebige Taste zu drücken.
 * Bei Tastendruck wird zum GameScreen gewechselt, der dann das eigentliche
 * Spiel startet.
 * </p>
 */
public class MainMenuScreen implements Screen {

    /**
     * Der Hauptkontext des Spiels, der Zugriff auf zentrale Ressourcen und Systeme
     * bietet.
     * 
     * <p>
     * Über dieses Objekt hat der MainMenuScreen Zugriff auf wichtige Komponenten
     * wie:
     * <ul>
     * <li>Den SpriteBatch zum Zeichnen von Text und Grafiken</li>
     * <li>Den Viewport für die Bildschirmdarstellung</li>
     * <li>Die Methode setScreen() zum Wechseln zwischen Bildschirmen</li>
     * </ul>
     * </p>
     */
    private final StudentsQuest context;

    // TODO placeholder apply custom font at some point
    /**
     * Die Schriftart für die Anzeige von Text im Hauptmenü.
     * 
     * <p>
     * Aktuell wird die Standardschrift von libGDX verwendet. In Zukunft soll
     * hier eine benutzerdefinierte Schriftart verwendet werden, wie im TODO
     * vermerkt.
     * </p>
     */
    private final BitmapFont font;

    /**
     * Erstellt einen neuen MainMenuScreen mit dem angegebenen Spiel-Kontext.
     *
     * @param context Der StudentsQuest-Kontext, der Zugriff auf zentrale
     *                Ressourcen und Systeme bietet
     */
    public MainMenuScreen(StudentsQuest context) {
        this.context = context;
        this.font = new BitmapFont();
    }

    /**
     * Wird aufgerufen, wenn dieser Screen der aktive Screen wird.
     * 
     * <p>
     * Diese Methode konfiguriert die Schriftart für die Anzeige:
     * <ul>
     * <li>Deaktiviert die Verwendung von ganzzahligen Positionen für eine glattere
     * Darstellung</li>
     * <li>Skaliert die Schriftgröße entsprechend dem Viewport</li>
     * <li>Setzt die Textfarbe auf Weiß</li>
     * </ul>
     * </p>
     */
    @Override
    public void show() {
        // eventuell hier oder im ctor

        // font has 15pt, but we need to scale it to our viewport by ratio of viewport
        // height to screen height
        this.font.setUseIntegerPositions(false);
        // default scled 15p scaled 30p
        this.font.getData().setScale((this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 2);
        this.font.setColor(Color.WHITE);
    }

    /**
     * Wird in jedem Frame aufgerufen, um das Hauptmenü zu aktualisieren und
     * darzustellen.
     * 
     * <p>
     * Diese Methode:
     * <ul>
     * <li>Löscht den Bildschirm mit schwarzer Farbe</li>
     * <li>Aktiviert den Viewport für die korrekte Skalierung</li>
     * <li>Zeichnet den Spieltitel und die Startaufforderung zentriert auf dem
     * Bildschirm</li>
     * <li>Prüft, ob eine Taste gedrückt wurde, um zum GameScreen zu wechseln</li>
     * </ul>
     * </p>
     *
     * @param delta Die Zeit in Sekunden seit dem letzten Frame
     */
    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);
        this.context.viewport.apply();
        this.context.getSpriteBatch().setProjectionMatrix(this.context.viewport.getCamera().combined);

        this.context.getSpriteBatch().begin();

        this.font.draw(this.context.getSpriteBatch(), "StudentQuest",
                (this.context.viewport.getWorldWidth() / 2) - 1f, (this.context.viewport.getWorldHeight() / 2) + 1f);
        this.font.draw(this.context.getSpriteBatch(), "Click any Key to start!",
                (this.context.viewport.getWorldWidth() / 2) - 2f, (this.context.viewport.getWorldHeight() / 2) - 1f);

        this.context.getSpriteBatch().end();

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            // TODO change to LoadingScreen later, do initial asset loading there with
            // progress bar in background
            this.context.setScreen(new GameScreen(this.context));
            // TODO apply correct Logging
            System.out.println("dispose from MainMenu instance was called!");
            dispose();
        }

    }

    /**
     * Wird aufgerufen, wenn die Größe des Fensters geändert wird.
     * 
     * <p>
     * Aktualisiert den Viewport, damit die Grafiken korrekt skaliert werden.
     * Der Parameter true bewirkt, dass die Kamera an der Position zentriert wird.
     * </p>
     *
     * @param width  Die neue Breite des Fensters in Pixeln
     * @param height Die neue Höhe des Fensters in Pixeln
     */
    @Override
    public void resize(int width, int height) {
        this.context.viewport.update(width, height, true);
    }

    /**
     * Wird aufgerufen, wenn das Spiel pausiert wird (z.B. wenn die App in den
     * Hintergrund wechselt).
     * 
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um Ressourcen freizugeben oder den Menüzustand zu speichern.
     * </p>
     */
    @Override
    public void pause() {

    }

    /**
     * Wird aufgerufen, wenn das Spiel fortgesetzt wird (z.B. nach einer Pause).
     * 
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um Ressourcen neu zu laden oder den Menüzustand wiederherzustellen.
     * </p>
     */
    @Override
    public void resume() {

    }

    /**
     * Wird aufgerufen, wenn dieser Screen nicht mehr der aktive Screen ist.
     * 
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um temporäre Ressourcen freizugeben.
     * </p>
     */
    @Override
    public void hide() {

    }

    /**
     * Wird aufgerufen, wenn dieser Screen zerstört wird.
     * 
     * <p>
     * Gibt alle Ressourcen frei, die explizit für diesen Screen geladen wurden.
     * In diesem Fall die BitmapFont, um Speicherlecks zu vermeiden.
     * </p>
     */
    @Override
    public void dispose() {
        this.font.dispose();
    }
}
