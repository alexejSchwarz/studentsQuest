package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * Component for physics
 */
public class Box2DComponent implements Component, Pool.Poolable {

    public Body body;
    public float width;
    public float height;

    @Override
    public void reset() {
        if (body != null) {
            // removes body from Box2DWorld
            this.body.getWorld().destroyBody(this.body);
            this.body = null;
        }

        this.width = 0f;
        this.height = 0f;
    }
}
