package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Eine Komponente zur Verwaltung der visuellen Darstellung einer Entität.
 * 
 * <p>
 * Diese Komponente speichert ein Image-Objekt, das für das Rendern der
 * Entität in der Spielwelt verwendet wird. Im Entity-Component-System (ECS)
 * ermöglicht dies die Trennung der visuellen Darstellung von anderen
 * Aspekten wie Physik oder Logik.
 * </p>
 */
public class ImageComponent implements Component {

    /**
     * Das Image-Objekt, das zur visuellen Darstellung der Entität verwendet wird.
     * Dieses Scene2D-Objekt enthält die Textur und Informationen über die
     * Darstellungseigenschaften.
     */
    public Image image;

    /**
     * Standardkonstruktor für die ImageComponent.
     * Erstellt eine leere Komponente, die später mit einem Image-Objekt
     * initialisiert werden kann.
     */
    public ImageComponent() {
        // Leerer Konstruktor, Initialisierung erfolgt später
    }
}