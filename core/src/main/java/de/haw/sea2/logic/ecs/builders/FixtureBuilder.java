package de.haw.sea2.logic.ecs.builders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;

/**
 * Bauen und zerstroeren von Fixtures. Hier fuer Fixtures gedacht, die nur temporaer sein sollen und erst nach der Erstellung der Entitaet hinzugefuegt und
 * entfernt werden sollen. Bsp Angriffsflaechen des Spielers bei Angriffen.
 */
public class FixtureBuilder {

    public static void createAPlayerAttackSensor(PlayerComponent.Sensors sensorDirection, Box2DComponent playerBox2dComp) {
        float width = EntityCreator.HUMAN_ANIMATION_WIDTH;
        float height = EntityCreator.HUMAN_ANIMATION_HEIGHT;
        Body body = playerBox2dComp.body;

        switch (sensorDirection) {
            case UP: {
                //erster Vector ist linker Eckpunkt des Dreiecks. Der Zweite Vector ist rechter Eckpunkt des Dreiecks
                // (links rechts, jeweils vom Mittelpunkt (0,0), je Blickrichtung)
                createAPlayerAttackSensor(PlayerComponent.Sensors.UP, new Vector2(-width, height), new Vector2(width, height), body);
                break;
            }
            case DOWN: {
                createAPlayerAttackSensor(PlayerComponent.Sensors.DOWN, new Vector2(width, -height), new Vector2(-width, -height), body);
                break;
            }

            // bei links, rechts wechseln x und y. Das ist so gewollt. Mit Papier und Stift kann man das veranschaulichen
            case LEFT: {
                createAPlayerAttackSensor(PlayerComponent.Sensors.LEFT, new Vector2(-height, -width), new Vector2(-height, width), body);
                break;
            }
            case RIGHT: {
                createAPlayerAttackSensor(PlayerComponent.Sensors.RIGHT, new Vector2(height, width), new Vector2(height, -width), body);
                break;
            }
            default: {
                throw new RuntimeException("Unsupported sensor direction");
            }
        }
    }

    private static void createAPlayerAttackSensor(PlayerComponent.Sensors sensorType, Vector2 left, Vector2 right, Body body) {
        PolygonShape triangleShape = new PolygonShape();
        Vector2[] points = {Vector2.Zero, left, right};
        triangleShape.set(points);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = triangleShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(sensorType);
        triangleShape.dispose();
    }

    public static void destroyPlayerSensor(Box2DComponent playerBox2dComp) {
        Body body = playerBox2dComp.body;
        for (int i = body.getFixtureList().size - 1; i >= 0; i--) {
            if (body.getFixtureList().get(i).isSensor()) {
                body.destroyFixture(body.getFixtureList().get(i));
                break;
            }
        }
    }
}
