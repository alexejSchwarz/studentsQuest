package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import de.haw.sea2.contact.InteractionType;

public class InteractionComponent implements Component, Pool.Poolable{

    public InteractionType type;

    @Override
    public void reset() {
        this.type = null;
    }
}
