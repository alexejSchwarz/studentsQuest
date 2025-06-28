package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ItemComponent implements Component, Pool.Poolable{
    public ItemType itemType;

    @Override
    public void reset() {
        this.itemType = null;
    }
}
