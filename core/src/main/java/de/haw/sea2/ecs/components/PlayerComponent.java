package de.haw.sea2.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {

    public Vector2 speed = new Vector2();

    public int neededCoins = 10;

    public int collectedCoins = 0;

    /**
     * Components not longer in use are freed up. Then reset is called.
     * Resets any vlas to default
     */
    @Override
    public void reset() {
        this.collectedCoins = 0;
        this.speed.set(Vector2.Zero);
    }
}
