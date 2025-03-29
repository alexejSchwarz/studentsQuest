package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.EnemyComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.gameLevel.pathFinding.PathFindingListener;
import de.haw.sea2.gameLevel.pathFinding.PathToPlayerFinder;

public class EnemyMovementSystem extends IteratingSystem implements PathFindingListener {

    private final StudentsQuest context;
    private PathToPlayerFinder pathToPlayerFinder;
    private ImmutableArray<Entity> players;

    public EnemyMovementSystem(StudentsQuest context) {
        super(Family.all(Box2DComponent.class, EnemyComponent.class).get());
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        if (pathToPlayerFinder == null) {
            return;
        }

        EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(entity);
        Box2DComponent enemyBox2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        Box2DComponent playerBox2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(this.players.first());

        // Update timer for path recalculation
        enemyComp.pathUpdateTimer += deltaTime;

        // Check if we need to update the path
        if (enemyComp.pathUpdateTimer >= enemyComp.pathUpdateInterval) {
            enemyComp.pathUpdateTimer = 0f;
            enemyComp.waypoints = this.pathToPlayerFinder.getWayPoints(enemyBox2DComponent.body.getPosition(),
                playerBox2DComponent.body.getPosition());
        }

        if (enemyComp.waypoints.isEmpty()) {
            return;
        }

        Vector2 nextWayPoint = extractNextWaypoint(enemyComp.waypoints, enemyBox2DComponent.body.getPosition());
        applyMovement(enemyBox2DComponent, enemyComp, nextWayPoint);
    }

    private Vector2 extractNextWaypoint(Array<Vector2> waypoints, Vector2 currentPosition) {


        Vector2 nextWaypoint = waypoints.first();

        // Nur dann zum nächsten Wegpunkt weitergehen, wenn wir den aktuellen erreicht haben
        if (waypoints.size > 2 && currentPosition.dst(nextWaypoint) < PathToPlayerFinder.PATH_NODE_PROXIMITY) {
            // Diesen Wegpunkt entfernen, da wir ihn erreicht haben
            waypoints.removeIndex(0);

            // Falls es noch weitere Wegpunkte gibt, den nächsten zurückgeben
            if (waypoints.size > 0) {
                return waypoints.first();
            }
        }

        // Der aktuelle Wegpunkt wurde noch nicht erreicht, also weiter dorthin bewegen
        return nextWaypoint;
    }

    private void applyMovement(Box2DComponent box2d, EnemyComponent enemyComp, Vector2 nextWayPoint) {
        float mass = box2d.body.getMass();
        Vector2 currentVelocity = box2d.body.getLinearVelocity();

        Vector2 movementDirection = new Vector2(nextWayPoint).sub(box2d.body.getPosition());


        if (movementDirection.x != 0 && movementDirection.y != 0) {
            movementDirection.nor();
        }

        // Calculate impulse siehe player Movement
        float impulseX = movementDirection.x * enemyComp.speed.x - currentVelocity.x * mass;
        float impulseY = movementDirection.y * enemyComp.speed.y - currentVelocity.y * mass;


        // Apply impulse to body
        box2d.body.applyLinearImpulse(
            impulseX,
            impulseY,
            box2d.body.getWorldCenter().x,
            box2d.body.getWorldCenter().y,
            true);
    }

    @Override
    public void pathFindingInitialized(PathToPlayerFinder pathToPlayerFinder) {
        this.pathToPlayerFinder = pathToPlayerFinder;
        this.players = this.context.getEngine().getEntitiesFor(Family.one(PlayerComponent.class).get());
    }
}
