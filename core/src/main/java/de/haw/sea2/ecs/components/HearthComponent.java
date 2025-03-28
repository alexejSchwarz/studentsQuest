package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class HearthComponent implements Component, Pool.Poolable {
    public int maxHearths;
    public int currentHearths;

    @Override
    public void reset() {
        currentHearths = maxHearths;
    }
}
