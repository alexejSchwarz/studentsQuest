package de.haw.sea2.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

import java.util.Objects;

/**
 * Utility-Klasse für Entity-bezogene Hilfsfunktionen.
 * Bietet wiederverwendbare Methoden für häufige Operationen mit Entities.
 */
public class EntityUtils {

    private static final String LOG_TAG = "EntityUtils";

    /**
     * prueft den Spieler Array, sowie die Entitaet selbst. Abweichungen von Erwartungswerten werden geloggt. Im Debugmodus wird eine RuntimeException geworfen, falls
     * Spieler nicht valide ist (Spieler Anzahl != 1 oder Spieler == null).
     *
     * @param players Immutable Array aus engine zu player
     * @return valide player Entitaet
     */
    public static Entity checkAndGetPlayer(ImmutableArray<Entity> players) {
        Objects.requireNonNull(players, "Player Array nicht gesetzt");
        StringBuilder sb = new StringBuilder();
        if (players.size() != 1) {
            sb.append("Anzahl an Spieler Entitaeten soll 1 sein, ist aber: ")
                .append(String.valueOf(players.size()));
            LoggerUtil.error(LogCategory.LOG, LOG_TAG, sb.toString());
        } else if (players.get(0) == null) {
            sb.append("Player entity == null");
            LoggerUtil.error(LogCategory.LOG, LOG_TAG, sb.toString());
        }

        if (DebugConfig.DEBUG_ENABLED && sb.notEmpty()) {
            throw new RuntimeException(sb.toString());
        }
        return players.get(0);
    }
}
