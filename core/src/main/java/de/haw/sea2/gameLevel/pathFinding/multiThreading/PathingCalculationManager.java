package de.haw.sea2.gameLevel.pathFinding.multiThreading;

import java.util.stream.Stream;

import com.badlogic.gdx.utils.Array;

import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.EnemyComponent;
import de.haw.sea2.gameLevel.pathFinding.PathToPlayerFinder;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.map.MapChangeListener;

/**
 * Arbeitet mit Multithreading fuer PathCalculations. Ist gedacht fuer Batchverarbeitung, daher minimale Synchronisation noetig
 */
public class PathingCalculationManager implements MapChangeListener {

    private final Array<Thread> workerThreads;
    private final Array<PathToPlayerFinder> pathToPlayerFinders;
    private int assignedTasks;

    public PathingCalculationManager() {
        this.workerThreads = new Array<>();
        this.pathToPlayerFinders = new Array<>(25);
        this.assignedTasks = 0;
    }

    public void submitPathCalculationTask(EnemyComponent enemyComponent, Box2DComponent enemyBox2DComponent, Box2DComponent playerB2dComp) {

        // Bei Bedarf mehr pathToPlayerFinders generieren
        if (this.assignedTasks >= this.pathToPlayerFinders.size) {
            this.pathToPlayerFinders.addAll(
                Stream.generate(PathToPlayerFinder::new)
                    .limit(5)
                    .toArray(PathToPlayerFinder[]::new)
            );
        }

        PathToPlayerFinder pathToPlayerFinder = this.pathToPlayerFinders.get(this.assignedTasks);
        this.assignedTasks++;

        Thread worker = new Thread(() ->
            enemyComponent.waypoints = pathToPlayerFinder.getWayPoints(
            enemyBox2DComponent.body.getPosition(),
            playerB2dComp.body.getPosition()
        ));

        this.workerThreads.add(worker);
    }

    // Only call when all tasks are submitted.
    public void executeTasks() {
        this.workerThreads.forEach(Thread::start);
        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.assignedTasks = 0;
        this.workerThreads.clear();
    }

    @Override
    public void onMapChange(GameMap map) {
        Stream.generate(PathToPlayerFinder::new)
            .limit(25)
            .forEach(this.pathToPlayerFinders::add);
    }
}
