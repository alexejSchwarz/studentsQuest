package de.haw.sea2.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.exceptions.IllegalSpawnCoordinatesException;

public class ConversionUtils {
    /**
     * x und y von EntitySpawnPoint aus Tiled Editor sind in Pixel. (0, 0) im Editor sind in der Ecke Oben Links. Daher muessen die Werte
     * in logische Einheiten skaliert werden. Der Y-Wert wird auch angepasst, da bei uns in der Physik der Ursprung unten links ist
     * @return transformierten Vektor
     */
    public static Vector2 convertFromPixelToLogicPointWithTransformedY(float xInPixel, float yInPixel, int heightInTiles) throws IllegalSpawnCoordinatesException {
        if (xInPixel < 0 || yInPixel < 0 || heightInTiles <= 0) {
            throw new IllegalSpawnCoordinatesException(
                String.format("x and y must be greater than 0. Height must be greater than 0. xInPixel: %f, yInPixel: %f, heightInTiles %d", xInPixel, yInPixel, heightInTiles)
            );
        }
        float x = xInPixel * StudentsQuest.UNIT_SCALE;
        float y = heightInTiles - yInPixel * StudentsQuest.UNIT_SCALE;
        return new Vector2(x, y);
    }

    /**
     * Pixel angaben eines Tiled Rechtecks in Rectangle fuer Box2dComponente umwandeln
     */
    public static Rectangle convertFromRectangleObjectInPixelToLogicWithTransformedY(float xPixel, float yPixel, float widthPixel, float heightPixel, int heightInTiles) throws IllegalSpawnCoordinatesException {
        if (xPixel < 0 || yPixel < 0 || widthPixel <= 0 || heightPixel <= 0 || heightInTiles <= 0) {
            throw new IllegalSpawnCoordinatesException(
                String.format("x and y must be >= 0. Width and Height must be > 0. Height in tiles must be > 0. xPixel: %f, yPixel %f, widthPixel: %f, heightPixel: %f, heightInTiles: %d", xPixel, yPixel, widthPixel, heightPixel, heightInTiles)
            );
        }
        // Im Tile Editor bekommen wir den oberen linken Punkt des Rechtecks.
        // Ebenfalls in Tiled ist der Ursprung (0,0) oben Links und y erhoeht sich, wenn nach unten gegangen wird. Warum auch immer
        float width = widthPixel * StudentsQuest.UNIT_SCALE;
        float height = heightPixel * StudentsQuest.UNIT_SCALE;

        float x = xPixel * StudentsQuest.UNIT_SCALE;
        float y = (heightInTiles - yPixel * StudentsQuest.UNIT_SCALE) - height;

        return new Rectangle(x, y, width, height);
    }
}
