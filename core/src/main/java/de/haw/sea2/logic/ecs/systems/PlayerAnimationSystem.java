package de.haw.sea2.logic.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.logic.ecs.components.AnimationComponent;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.animations.BoyPlayerAnimation;
import de.haw.sea2.view.animations.GirlPlayerAnimation;

public class PlayerAnimationSystem extends IteratingSystem {

    private final StudentsQuest context;

    public PlayerAnimationSystem(StudentsQuest context) {
        super(Family.all(AnimationComponent.class, PlayerComponent.class, Box2DComponent.class).get());
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        final AnimationComponent animationComp = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);
        final PlayerComponent playerComp = ECSEngine.PLAYER_COMP_MAPPER.get(entity);

        switch (playerComp.curentFacing) {
            case UP: {
                if (this.context.chosenPlayerAnimationAtlas == AssetPaths.BOY_PLAYER_ATLAS) {
                    animationComp.animationType = BoyPlayerAnimation.PLAYER_MOVE_UP.animationType;
                } else {
                    animationComp.animationType = GirlPlayerAnimation.PLAYER_MOVE_UP.animationType;
                }
                break;
            }
            case DOWN: {
                if (this.context.chosenPlayerAnimationAtlas == AssetPaths.BOY_PLAYER_ATLAS) {
                    animationComp.animationType = BoyPlayerAnimation.PLAYER_MOVE_DOWN.animationType;
                } else {
                    animationComp.animationType = GirlPlayerAnimation.PLAYER_MOVE_DOWN.animationType;
                }
                break;
            }
            case LEFT: {
                if (this.context.chosenPlayerAnimationAtlas == AssetPaths.BOY_PLAYER_ATLAS) {
                    animationComp.animationType = BoyPlayerAnimation.PLAYER_MOVE_LEFT.animationType;
                } else {
                    animationComp.animationType = GirlPlayerAnimation.PLAYER_MOVE_LEFT.animationType;
                }
                break;
            }
            case RIGHT: {
                if (this.context.chosenPlayerAnimationAtlas == AssetPaths.BOY_PLAYER_ATLAS) {
                    animationComp.animationType = BoyPlayerAnimation.PLAYER_MOVE_RIGHT.animationType;
                } else {
                    animationComp.animationType = GirlPlayerAnimation.PLAYER_MOVE_RIGHT.animationType;
                }
                break;
            }
            default:
                break;
        }

        if (box2DComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
            // player does not move
            animationComp.animationTime = 0;
        }
    }
}
