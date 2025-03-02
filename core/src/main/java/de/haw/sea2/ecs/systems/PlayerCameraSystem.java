package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;

public class PlayerCameraSystem extends IteratingSystem {

    OrthographicCamera gameCamera;

    public PlayerCameraSystem(StudentsQuest context) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        this.gameCamera = context.getGameCamera();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        gameCamera.position.set(box2DComponent.body.getPosition(),0);

    }
}
