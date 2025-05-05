package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.EnemyComponent;
import de.haw.sea2.view.animations.EnemyAnimation;

//TODO GegnerAnimation wird nicht ganz korrekt gehandhabt. Irgendwie mit Gehrichtung anpassen
public class EnemyAnimationSystem extends IteratingSystem {

    public EnemyAnimationSystem() {
        super(Family.all(AnimationComponent.class, EnemyComponent.class, Box2DComponent.class).get());
    }

    // TODO im Moment gleich zu Player, spaeter konkrete Animation fuer Enemy evt spaeter allgemeinere AnimationSystem fuer alle die MoveUp, Down, etc. haben
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        final AnimationComponent animationComp = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);
        if (box2DComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
            // player does not move
            animationComp.animationTime = 0;
        } else if (box2DComponent.body.getLinearVelocity().x > 0) {
            // player moves to the right
            animationComp.animationType = EnemyAnimation.HERO_MOVE_RIGHT.animationType;
        } else if (box2DComponent.body.getLinearVelocity().x < 0) {
            // player moves to the left
            animationComp.animationType = EnemyAnimation.HERO_MOVE_LEFT.animationType;
        } else if (box2DComponent.body.getLinearVelocity().y > 0) {
            // player moves up
            animationComp.animationType = EnemyAnimation.HERO_MOVE_UP.animationType;
        } else if (box2DComponent.body.getLinearVelocity().y < 0) {
            // player moves down
            animationComp.animationType = EnemyAnimation.HERO_MOVE_DOWN.animationType;
        }
    }
}
