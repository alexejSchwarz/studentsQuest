package de.haw.sea2.logic.gameLevel.pathFinding.multiThreading;

import java.util.stream.Stream;

import com.badlogic.gdx.utils.Array;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.EnemyComponent;
import de.haw.sea2.logic.gameLevel.pathFinding.NavigationGrid;
import de.haw.sea2.logic.gameLevel.pathFinding.NavigationGridBuilder;
import de.haw.sea2.logic.gameLevel.pathFinding.PathToPlayerFinder;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.map.MapChangeListener;

/**
 * Arbeitet mit Multithreading fuer PathCalculations. Ist gedacht fuer Batchverarbeitung, daher minimale Synchronisation noetig
 */
public class PathingCalculationManager implements MapChangeListener {

    private final StudentsQuest context;
    private final Array<Thread> workerThreads;
    private final Array<PathToPlayerFinder> pathToPlayerFinders;
    private int assignedTasks;
    private NavigationGrid currentNavigationGrid;

    private final int initialPathFinderPoolSize = 25;
    private final int pathFinderExpansionSize = 5;

    public PathingCalculationManager(StudentsQuest context) {
        this.context = context;
        this.workerThreads = new Array<>();
        this.pathToPlayerFinders = new Array<>(initialPathFinderPoolSize);
        this.assignedTasks = 0;
    }

    private void buildNewNavigationGrid(GameMap map) {
        this.currentNavigationGrid = NavigationGridBuilder.buildForMap(context, map);

        // Initialisiere die PathToPlayerFinder mit der neuen Grid
        this.pathToPlayerFinders.clear();
        for (int i = 0; i < initialPathFinderPoolSize; i++) {
            this.pathToPlayerFinders.add(new PathToPlayerFinder(this.currentNavigationGrid));
        }
        LoggerUtil.log(LogCategory.DEBUG, this, "PathToPlayerFinder pool repopulated with " + initialPathFinderPoolSize + " instances for the new grid.");
        this.context.getEngine().getBatchMovementSystem().onPathfindingInit();
    }

    public void submitPathCalculationTask(EnemyComponent enemyComponent, Box2DComponent enemyBox2DComponent, Box2DComponent playerB2dComp) {

        if (this.assignedTasks >= this.pathToPlayerFinders.size) {
            LoggerUtil.log(LogCategory.DEBUG, this, "Expanding PathToPlayerFinder pool by " + pathFinderExpansionSize + ".");

            this.pathToPlayerFinders.addAll(
                Stream.generate(() -> new PathToPlayerFinder(this.currentNavigationGrid))
                    .limit(this.pathFinderExpansionSize)
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
                LoggerUtil.log(LogCategory.ERROR, this, "Pathfinding worker thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // Preserve interrupt status
            }
        }
        this.assignedTasks = 0;
        this.workerThreads.clear();
    }

    @Override
    public void onMapChange(GameMap map) {
        // Assuming map is validated before this call by MapManager
        String mapIdentifier = map.getMapIdentifier();
        LoggerUtil.log(LogCategory.DEBUG, this, "Map change detected. Rebuilding navigation grid and pathfinders for map: " + mapIdentifier);
        buildNewNavigationGrid(map);
    }

    /**
     * Get access to the PathToPlayerFinders for debugging purposes
     * @return Array of PathToPlayerFinders used by this manager
     */
    public Array<PathToPlayerFinder> getPathFinders() {
        return this.pathToPlayerFinders;
    }

    /**
     * Get access to the current NavigationGrid for debugging purposes.
     * @return The current NavigationGrid.
     */
    public NavigationGrid getNavigationGrid() {
        return this.currentNavigationGrid;
    }
}
