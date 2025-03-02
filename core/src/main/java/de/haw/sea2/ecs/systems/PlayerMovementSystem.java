package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;

//TODO see at https://www.youtube.com/watch?v=E4tZ7e032tI&t=5s  24:38
public class PlayerMovementSystem extends IteratingSystem implements KeyInputListener {

    private boolean directionChange;
    private int xFactor;
    private int yFactor;

    public PlayerMovementSystem(final StudentsQuest context) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        context.getInputManager().addKeyInputListener(this);
        this.directionChange = false;
        this.xFactor = this.yFactor = 0;
    }

    /**
     * Called for ev every Entity part of the Family
     * @param entity The current Entity being processed
     * @param deltaTime The delta time between the last and current frame
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        if (this.directionChange) {

        // use Comp_Mappers for faster index based Access to Comps
        final PlayerComponent playerComp = ECSEngine.PLAYER_COMP_MAPPER.get(entity);
        final Box2DComponent box2DComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);

        //TODO look up what happens here
            this.directionChange = false;
            box2DComp.body.applyLinearImpulse(
                (this.xFactor * playerComp.speed.x - box2DComp.body.getLinearVelocity().x) * box2DComp.body.getMass(),
                (this.yFactor * playerComp.speed.y - box2DComp.body.getLinearVelocity().y) * box2DComp.body.getMass(),
                box2DComp.body.getWorldCenter().x, box2DComp.body.getWorldCenter().y, true
            );
        }
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        switch (key) {
            case LEFT : {
                this.directionChange = true;
                this.xFactor = -1;
                break;
            }
            case RIGHT : {
                this.directionChange = true;
                this.xFactor = 1;
                break;
            }
            case UP : {
                this.directionChange = true;
                this.yFactor = 1;
                break;
            }
            case DOWN : {
                this.directionChange = true;
                this.yFactor = -1;
                break;
            }
            default : {
                break;
            }
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
        switch (key) {
            case LEFT : {
                this.directionChange = true;
                this.xFactor = manager.isKeyDown(GameKey.RIGHT) ? 1 : 0;
                break;
            }
            case RIGHT : {
                this.directionChange = true;
                this.xFactor = manager.isKeyDown(GameKey.LEFT) ? -1 : 0;
                break;
            }
            case UP : {
                this.directionChange = true;
                this.yFactor = manager.isKeyDown(GameKey.DOWN) ? -1 : 0;
                break;
            }
            case DOWN : {
                this.directionChange = true;
                this.yFactor = manager.isKeyDown(GameKey.UP) ? 1 : 0;
                break;
            }
            default : {
                break;
            }
        }
    }
}
