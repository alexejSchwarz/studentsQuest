package de.haw.sea2.debug.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.EnemyComponent;
import de.haw.sea2.gameLevel.pathFinding.GridNode;
import de.haw.sea2.gameLevel.pathFinding.NavigationGrid;
import de.haw.sea2.gameLevel.pathFinding.PathToPlayerFinder;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.input.GameKey;

/**
 * Debug renderer for visualizing enemy movement paths and detection zones.
 */
public class EnemyMovementDebugRenderer implements DebugRenderer, Disposable, KeyInputListener {

    private final StudentsQuest context;
    private final ShapeRenderer shapeRenderer;
    private PathToPlayerFinder pathFinder;

    // Debug visualization configuration
    private final static Color PATH_COLOR = new Color(0.2f, 0.8f, 0.2f, 0.8f);
    private final static Color NODE_COLOR = new Color(0.0f, 1.0f, 0.0f, 0.8f);
    private final static Color GRID_WALKABLE_COLOR = new Color(0.0f, 0.2f, 0.7f, 0.1f);
    private final static Color GRID_UNWALKABLE_COLOR = new Color(0.7f, 0.0f, 0.0f, 0.2f);
    private final static Color GRID_LINE_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f);
    private final static float PATH_LINE_WIDTH = 2.0f;
    private final static float GRID_LINE_WIDTH = 1.5f;
    private final static float NODE_SIZE = 0.1f;
    private final static float GRID_CELL_SIZE = 1.0f;

    private ImmutableArray<Entity> enemies;
    private boolean showGrid = true;
    private boolean showPaths = true;
    private boolean isRegisteredAsListener = false;

    public EnemyMovementDebugRenderer(StudentsQuest context) {
        this.context = context;
        this.shapeRenderer = new ShapeRenderer();
        this.enemies = context.getEngine().getEntitiesFor(Family.one(EnemyComponent.class).get());

        // Nur registrieren, wenn Debug-Modus aktiv ist
        if (DebugConfig.DEBUG_ENABLED) {
            context.getInputManager().addKeyInputListener(this);
            isRegisteredAsListener = true;
        }
    }

    /**
     * Try to get a PathToPlayerFinder from the PathingCalculationManager
     */
    private void tryGetPathFinder() {
        if (pathFinder != null) return;

        if (context.getPathCalcManager() != null) {
            Array<PathToPlayerFinder> finders = context.getPathCalcManager().getPathFinders();
            if (finders != null && finders.size > 0) {
                pathFinder = finders.first();
            }
        }
    }

    @Override
    public float render(SpriteBatch batch, BitmapFont font, float x, float y) {
        // Display enemy count
        font.draw(batch, "Enemies: " + enemies.size(), x, y);
        y -= DebugConfig.LINE_SPACING;

        // Count enemies with valid paths
        int enemiesWithPaths = 0;
        for (int i = 0; i < enemies.size(); i++) {
            EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(enemies.get(i));
            if (enemyComp != null && enemyComp.waypoints != null && enemyComp.waypoints.size > 0) {
                enemiesWithPaths++;
            }
        }
        font.draw(batch, "Enemies with paths: " + enemiesWithPaths, x, y);
        y -= DebugConfig.LINE_SPACING;

        font.draw(batch, "Grid Display: " + (showGrid ? "ON (Press G to toggle)" : "OFF (Press G to toggle)"), x, y);
        y -= DebugConfig.LINE_SPACING;

        font.draw(batch, "Path Display: " + (showPaths ? "ON (Press P to toggle)" : "OFF (Press P to toggle)"), x, y);
        y -= DebugConfig.LINE_SPACING;

        // End the SpriteBatch to use the ShapeRenderer
        boolean wasBatchDrawing = batch.isDrawing();
        if (wasBatchDrawing) {
            batch.end();
        }

        // Try to get a PathToPlayerFinder if we don't have one yet
        if (showGrid && pathFinder == null) {
            tryGetPathFinder();
        }

        // Draw the navigation grid first (in the background)
        if (showGrid && pathFinder != null) {
            drawNavigationGrid();
        }

        // Use ShapeRenderer to draw paths in world coordinates if paths should be shown
        if (showPaths) {
            drawPaths();
        }

        // Restore batch if it was drawing
        if (wasBatchDrawing) {
            batch.begin();
        }

        return y;
    }

    private void drawNavigationGrid() {
        // Get the reference to the NavigationGrid from PathToPlayerFinder
        NavigationGrid navGrid = pathFinder.getNavigationGrid();

        if (navGrid == null)
            return;

        // Set up projection matrix for world coordinates
        shapeRenderer.setProjectionMatrix(context.getGameCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Set the color for the grid cells, walkable and unwalkable
        // Iterate through all grid cells
        for (int x = 0; x < navGrid.getWidth(); x++) {
            for (int y = 0; y < navGrid.getHeight(); y++) {
                GridNode node = navGrid.getNode(x, y);
                Vector2 worldPos = navGrid.getWorldPosition(node);

                // Draw cell based on walkability
                if (node.isWalkable()) {
                    shapeRenderer.setColor(GRID_WALKABLE_COLOR);
                } else {
                    shapeRenderer.setColor(GRID_UNWALKABLE_COLOR);
                }

                // Draw cell as a filled rectangle
                shapeRenderer.rect(
                        worldPos.x - GRID_CELL_SIZE / 2,
                        worldPos.y - GRID_CELL_SIZE / 2,
                        GRID_CELL_SIZE,
                        GRID_CELL_SIZE);
            }
        }

        shapeRenderer.end();

        // Draw grid lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Verwende die neue Farbe für die Gitterlinien
        shapeRenderer.setColor(GRID_LINE_COLOR);
        // Setze die Linienstärke für das Gitter
        Gdx.gl.glLineWidth(GRID_LINE_WIDTH);

        // Draw horizontal grid lines
        for (int y = 0; y <= navGrid.getHeight(); y++) {
            float worldY = y * GRID_CELL_SIZE;
            shapeRenderer.line(0, worldY, navGrid.getWidth() * GRID_CELL_SIZE, worldY);
        }

        // Draw vertical grid lines
        for (int x = 0; x <= navGrid.getWidth(); x++) {
            float worldX = x * GRID_CELL_SIZE;
            shapeRenderer.line(worldX, 0, worldX, navGrid.getHeight() * GRID_CELL_SIZE);
        }

        shapeRenderer.end();

        // Zurücksetzen der Linienstärke für andere Zeichenoperationen
        Gdx.gl.glLineWidth(1.0f);
    }

    /**
     * Generates a unique color for each enemy based on their index
     *
     * @param enemyIndex Index of the enemy
     * @param baseColor Base color to mix with
     * @param saturation Saturation value for HSV
     * @param baseAlpha Alpha value for the color
     * @return Generated color
     */
    private Color getEnemyColor(int enemyIndex, Color baseColor, float saturation, float baseAlpha) {
        // Using golden ratio conjugate for an even distribution of colors
        float hue = (enemyIndex * 0.618033988749895f) % 1.0f;

        Color color = new Color();
        color.fromHsv(hue * 360f, saturation, 0.9f);

        // Mix with base color
        color.r = color.r * 0.7f + baseColor.r * 0.3f;
        color.g = color.g * 0.7f + baseColor.g * 0.3f;
        color.b = color.b * 0.7f + baseColor.b * 0.3f;
        color.a = baseAlpha;

        return color;
    }

    /**
     * Draw paths for all enemies
     */
    private void drawPaths() {
        // Set up projection matrix for world coordinates
        shapeRenderer.setProjectionMatrix(context.getGameCamera().combined);

        // Check if there are any enemies
        if (this.enemies.size() == 0 || !DebugConfig.DEBUG_ENABLED) {
            return; // No enemies to draw paths for or debugging disabled
        }

        // Enable blending and set line width once
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glLineWidth(PATH_LINE_WIDTH);

        // First pass - draw lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawPathLines();
        shapeRenderer.end();

        // Second pass - draw nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawPathNodes();
        shapeRenderer.end();
        // Always reset line width (even if an exception occurs)
        Gdx.gl.glLineWidth(1.0f);
    }

    /**
     * Draw the lines connecting path nodes
     */
    private void drawPathLines() {
        for (int enemyIndex = 0; enemyIndex < this.enemies.size(); enemyIndex++) {
            Entity enemy = this.enemies.get(enemyIndex);
            EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(enemy);
            Array<Vector2> paths = enemyComp.waypoints;

            if (paths == null || paths.size < 1)
                continue;

            // Set the color for this enemy's path
            shapeRenderer.setColor(getEnemyColor(enemyIndex, PATH_COLOR, 0.8f, PATH_COLOR.a));

            // Draw path lines
            for (int i = 0; i < paths.size - 1; i++) {
                Vector2 current = paths.get(i);
                Vector2 next = paths.get(i + 1);
                shapeRenderer.line(current.x, current.y, next.x, next.y);
            }
        }
    }

    /**
     * Draw the nodes (points) of the paths
     */
    private void drawPathNodes() {
        for (int enemyIndex = 0; enemyIndex < this.enemies.size(); enemyIndex++) {
            Entity enemy = this.enemies.get(enemyIndex);
            EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(enemy);
            Array<Vector2> paths = enemyComp.waypoints;

            if (paths == null || paths.size < 1)
                continue;

            // Set the color for this enemy's nodes
            shapeRenderer.setColor(getEnemyColor(enemyIndex, NODE_COLOR, 0.8f, NODE_COLOR.a));

            // Draw nodes
            for (int i = 0; i < paths.size; i++) {
                Vector2 node = paths.get(i);
                // Make first and last nodes larger for better visibility
                float nodeSize = NODE_SIZE;
                if (i == 0 || i == paths.size - 1) {
                    nodeSize = NODE_SIZE * 2.0f;
                }
                shapeRenderer.circle(node.x, node.y, nodeSize, 8);
            }
        }
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        // Deregistrieren des KeyInputListeners
        if (isRegisteredAsListener) {
            context.getInputManager().removeKeyInputListener(this);
            isRegisteredAsListener = false;
        }
    }

    /**
     * Wird aufgerufen, wenn eine Taste gedrückt wird.
     * Hier werden die G und P Tasten über den InputManager abgefangen.
     *
     * @param manager Der InputManager
     * @param key Die gedrückte GameKey-Taste
     */
    @Override
    public void keyDown(InputManager manager, GameKey key) {
        // Keine Aktion bei keyDown, da wir nur auf einmalige Tastendrücke reagieren wollen
    }

    /**
     * Wird aufgerufen, wenn eine Taste losgelassen wird.
     * Hier werden die G und P Tasten über den InputManager abgefangen.
     *
     * @param manager Der InputManager
     * @param key Die losgelassene GameKey-Taste
     */
    @Override
    public void keyUp(InputManager manager, GameKey key) {
        if (!DebugConfig.DEBUG_ENABLED) return;

        switch (key) {
            case DEBUG_GRID: {
                // Toggle Grid anzeigen
                this.showGrid = !this.showGrid;
                break;
            }
            case DEBUG_PATH: {
                // Toggle Path anzeigen
                this.showPaths = !this.showPaths;
                break;
            }
            default: {
                break;
            }
        }
    }
}
