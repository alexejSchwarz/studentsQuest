package de.haw.sea2.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.PooledEngine;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.InteractionComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.components.SimpleRenderComponent;
import de.haw.sea2.ecs.systems.AnimationSystem;
import de.haw.sea2.ecs.systems.PlayerAnimationSystem;
import de.haw.sea2.ecs.systems.PlayerCameraSystem;
import de.haw.sea2.ecs.systems.PlayerContactSystem;
import de.haw.sea2.ecs.systems.PlayerMovementSystem;

/**
 * Die zentrale Engine des Entity-Component-Systems (ECS) für das Spiel.
 *
 * <p>
 * Diese Klasse verwaltet alle Spielentitäten (wie Spieler und Wände) und ihre
 * Komponenten.
 * Sie erbt von PooledEngine, was bedeutet, dass sie Objekte wiederverwendet, um
 * Speicher
 * zu sparen und die Leistung zu verbessern.
 * </p>
 *
 * <p>
 * Ein Entity-Component-System ist ein Architekturmuster, das Spielobjekte
 * (Entities)
 * aus austauschbaren Bausteinen (Components) zusammensetzt, anstatt eine tiefe
 * Vererbungshierarchie
 * zu verwenden. Die Engine koordiniert diese Entities und führt Systeme aus,
 * die
 * die Komponenten verarbeiten.
 * </p>
 *
 * <p>
 * Diese Engine ist verantwortlich für:
 * <ul>
 * <li>Erstellung und Verwaltung von Spielobjekten (z.B. Spieler)</li>
 * <li>Erstellung der physikalischen Welt und Kollisionsobjekte</li>
 * <li>Verwaltung der Systeme, die Spiellogik verarbeiten</li>
 * </ul>
 * </p>
 */
public class ECSEngine extends PooledEngine {

    /**
     * ComponentMapper für schnellen Zugriff auf PlayerComponent-Objekte.
     *
     * <p>
     * Ein ComponentMapper ist wie ein Schlüssel, der sehr schnell eine bestimmte
     * Komponente aus einer Entity holen kann - viel schneller als die normale
     * Methode.
     * Das ist wichtig für Spiele, die flüssig laufen sollen.
     * </p>
     */
    public static final ComponentMapper<PlayerComponent> PLAYER_COMP_MAPPER = ComponentMapper
            .getFor(PlayerComponent.class);

    /**
     * ComponentMapper für schnellen Zugriff auf Box2DComponent-Objekte.
     *
     * <p>
     * Diese Komponenten enthalten alle physikalischen Eigenschaften einer Entity,
     * wie Position, Körperform und Kollisionsinformationen.
     * </p>
     */
    public static final ComponentMapper<Box2DComponent> BOX2D_COMP_MAPPER = ComponentMapper.getFor(Box2DComponent.class);
    public static final ComponentMapper<AnimationComponent> ANIMATION_COMP_MAPPER = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<SimpleRenderComponent> SIMPLE_RENDER_COMPONENT_MAPPER = ComponentMapper.getFor(SimpleRenderComponent.class);
    public static final ComponentMapper<InteractionComponent> INTERACTION_COMPONENT_MAPPER = ComponentMapper.getFor(InteractionComponent.class);

    /**
     * Erstellt eine neue ECS-Engine für das Spiel.
     *
     * <p>
     * Der Konstruktor richtet die Engine ein und fügt die benötigten Systeme hinzu,
     * die für die Spiellogik verantwortlich sind.
     * </p>
     *
     * @param context Der Hauptkontext des Spiels (StudentsQuest), der wichtige
     *                Ressourcen enthält, wie die physikalische Welt und den
     *                InputManager
     */
    public ECSEngine(final StudentsQuest context) {
        super();
        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
        this.addSystem(new PlayerAnimationSystem());
        this.addSystem(new PlayerContactSystem(context));
    }
}
