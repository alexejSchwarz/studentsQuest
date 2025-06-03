package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import de.haw.sea2.view.animations.AnimationType;

/**
 * Component for Atlas based Animations
 */
public class AnimationComponent implements Component, Pool.Poolable {

    public AnimationType animationType;
    public float animationTime;
    public float width;
    public float height;

    @Override
    public void reset() {
        this.animationType = null;
        this.animationTime = 0;
        this.width = 0;
        this.height = 0;
    }
}
