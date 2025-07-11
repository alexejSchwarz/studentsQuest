package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import de.haw.sea2.logic.entityLogic.MovementDirection;

public class PlayerComponent implements Component, Pool.Poolable {

    /**
     * Brauchen wir gesondert, da im Gegensatz zu den Mevementdirections auch ein NONE benoetigt ist, wenn Spieler gerade nicht angreift und daher keinen Sensor hat
     * und daher auch keine Richtung.
     */
    public enum Sensors {
        UP,
        RIGHT,
        DOWN,
        LEFT,
        NONE;

        public static Sensors fromFacingDirection(MovementDirection direction) {
            switch (direction) {
                case UP: {
                    return UP;
                }
                case RIGHT: {
                    return RIGHT;
                }
                case DOWN: {
                    return DOWN;
                }
                case LEFT: {
                    return LEFT;
                }
                default: {
                    return NONE;
                }
            }
        }
    }

    public Vector2 speed = new Vector2();

    public int neededCoins = 1;

    public int collectedCoins = 0;

    public MovementDirection curentFacing = MovementDirection.DOWN;

    /**
     * Components no longer in use are freed up. Then reset is called.
     * Resets any fields to default
     */
    @Override
    public void reset() {
        this.collectedCoins = 0;
        this.speed.set(Vector2.Zero);
        this.curentFacing = MovementDirection.DOWN;
    }
}
