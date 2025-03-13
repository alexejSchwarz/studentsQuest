package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Used to render non atlas based not animated Entities
 */
public class SimpleRenderComponent implements Component, Pool.Poolable {

    public String textureFilePath;
    public float width;
    public float height;

    @Override
    public void reset() {
        this.textureFilePath = null;
        this.width = 0;
        this.height = 0;
    }
}
