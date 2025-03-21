package de.haw.sea2.map;

import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;

public class ConversionUtils {
    /**
     * x und y von EntitySpawnPoint aus Tiled Editor sind in Pixel. (0, 0) im Editor sind in der Ecke Oben Links. Daher muessen die Werte
     * in logische Einheiten skaliert werden. Der Y-Wert wird auch angepasst, da bei uns in der Physik der Ursprung unten links ist
     * @return transformierten Vektor
     */
    public static Vector2 convertFromPixelToLogicPointWithTransformedY(float xInPixel, float yInPixel, float heightInTiles) {
        float x = xInPixel * StudentsQuest.UNIT_SCALE;
        float y = heightInTiles - yInPixel * StudentsQuest.UNIT_SCALE;
        return new Vector2(x, y);
    }
}
