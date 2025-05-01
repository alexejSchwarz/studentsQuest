package de.haw.sea2.map;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import de.haw.sea2.exceptions.IllegalSpawnCoordinatesException;

public class ConversionUtilsTest {

    /**
     * X negativ
     * Fall negativ 1
     */
    @Test
    public void vectorErsterTestfallNegativX() {
        float xInPixels = -1f;
        float yInPixels = 32f;
        int heightInTitles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromPixelToLogicPointWithTransformedY(xInPixels, yInPixels, heightInTitles));
    }

    /**
     * Y negativ
     * Fall negativ 2
     */
    @Test
    public void vectorZweiterTestfallNegativY() {
        float xInPixels = 64f;
        float yInPixels = -5f;
        int heightInTitles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromPixelToLogicPointWithTransformedY(xInPixels, yInPixels, heightInTitles));
    }

    /**
     * heightInTitles negativ
     * Fall negativ 3
     */
    @Test
    public void vectorDritterTestfallNegativHeight() {
        float xInPixels = 64f;
        float yInPixels = 32f;
        int heightInTitles = -3;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromPixelToLogicPointWithTransformedY(xInPixels, yInPixels, heightInTitles));
    }

    /**
     * Minimalwerte
     * Fall Grenze 1
     */
    @Test
    public void vectorErsterTestfallGrenzeNegativ() {
        float xInPixels = 0f;
        float yInPixels = 0f;
        int heightInTitles = 0;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromPixelToLogicPointWithTransformedY(xInPixels, yInPixels, heightInTitles));
    }

    /**
     * X negativ
     * Fall negativ 1
     */
    @Test
    public void rectErsterTestfallNegativX() {
        float xPixel = -1f;
        float yPixel = 0f;
        float widthPixel = 32f;
        float heightPixel = 32f;
        int heightInTiles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }

    /**
     * Y negativ
     * Fall negativ 2
     */
    @Test
    public void rectZweiterTestfallNegativY() {
        float xPixel = 0f;
        float yPixel = -1f;
        float widthPixel = 32f;
        float heightPixel = 32f;
        int heightInTiles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }

    /**
     * Width negativ
     * Fall negativ 3
     */
    @Test
    public void rectDritterTestfallNegativWidth() {
        float xPixel = 0f;
        float yPixel = 0f;
        float widthPixel = -32f;
        float heightPixel = 32f;
        int heightInTiles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }

    /**
     * Height negativ
     * Fall negativ 4
     */
    @Test
    public void rectVierterTestfallNegativHeight() {
        float xPixel = 0f;
        float yPixel = 0f;
        float widthPixel = 32f;
        float heightPixel = -32f;
        int heightInTiles = 10;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }

    /**
     * heightInTiles negativ
     * Fall negativ 5
     */
    @Test
    public void rectFuenfterTestfallNegativHeightInTiles() {
        float xPixel = 0f;
        float yPixel = 0f;
        float widthPixel = 32f;
        float heightPixel = 32f;
        int heightInTiles = -5;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }

    /**
     * Alles Null
     * Fall Grenze 1
     * Erwartet (0.0, 1.0, 0.0, 0.0)
     */
    @Test
    public void rectErsterTestfallGrenzeNegativ() {
        float xPixel = 0f;
        float yPixel = 0f;
        float widthPixel = 0f;
        float heightPixel = 32f;
        int heightInTiles = 1;

        assertThrows(IllegalSpawnCoordinatesException.class, () -> ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(xPixel, yPixel, widthPixel, heightPixel, heightInTiles));
    }
}
