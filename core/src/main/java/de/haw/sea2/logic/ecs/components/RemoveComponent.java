package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Simple Component added to an entity to be removed
 */
public class RemoveComponent implements Component, Pool.Poolable{
    @Override
    public void reset() {

    }
}
