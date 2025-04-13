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

/**
 * Debug renderer for visualizing enemy movement paths and detection zones.
 */
public class EnemyMovementDebugRenderer implements DebugRenderer, Disposable {

    private final StudentsQuest context;
    private final ShapeRenderer shapeRenderer;
    private PathToPlayerFinder pathFinder;

    // Debug visualization configuration
    private final static Color PATH_COLOR = new Color(0.2f, 0.8f, 0.2f, 0.8f);
    private final static Color NODE_COLOR = new Color(0.0f, 1.0f, 0.0f, 0.8f);
    private final static Color GRID_WALKABLE_COLOR = new Color(0.0f, 0.2f, 0.7f, 0.1f);
    private final static Color GRID_UNWALKABLE_COLOR = new Color(0.7f, 0.0f, 0.0f, 0.2f);
    private final static Color GRID_LINE_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f); // Neue hellere Farbe für
                                                                                    // Gitterlinien
    private final static float PATH_LINE_WIDTH = 2.0f;
    private final static float GRID_LINE_WIDTH = 1.5f; // Neue dickere Linienstärke für das Gitter
    private final static float NODE_SIZE = 0.1f;
    private final static float GRID_CELL_SIZE = 1.0f;

    private ImmutableArray<Entity> enemies;
    private boolean showGrid = true;

    public EnemyMovementDebugRenderer(StudentsQuest context) {
        this.context = context;
        this.shapeRenderer = new ShapeRenderer();
        this.enemies = context.getEngine().getEntitiesFor(Family.one(EnemyComponent.class).get());
    }

    public void setPathFinder(PathToPlayerFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    @Override
    public float render(SpriteBatch batch, BitmapFont font, float x, float y) {
        // Display enemy count
        ImmutableArray<Entity> enemies = context.getEngine().getEntitiesFor(
                Family.all(EnemyComponent.class).get());

        font.draw(batch, "Enemies: " + enemies.size(), x, y);
        y -= DebugConfig.LINE_SPACING;

        // Toggle grid display with key G
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
            showGrid = !showGrid;
        }

        font.draw(batch, "Grid Display: " + (showGrid ? "ON (Press G to toggle)" : "OFF (Press G to toggle)"), x, y);
        y -= DebugConfig.LINE_SPACING;

        // End the SpriteBatch to use the ShapeRenderer
        boolean wasBatchDrawing = batch.isDrawing();
        if (wasBatchDrawing) {
            batch.end();
        }

        // Draw the navigation grid first (in the background)
        if (showGrid && pathFinder != null) {
            drawNavigationGrid();
        }

        // Use ShapeRenderer to draw paths in world coordinates
        drawPaths();
        // drawEnemyVisualizations();

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

    private void drawPaths() {
        // Set up projection matrix for world coordinates
        shapeRenderer.setProjectionMatrix(context.getGameCamera().combined);

        // Check if there are any enemies
        if (this.enemies.size() == 0) {
            return; // No enemies to draw paths for
        }

        // Enable blending and set line width once
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glLineWidth(PATH_LINE_WIDTH);

        // Single rendering pass combining lines and nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        Color tempColor = new Color();
        for (int enemyIndex = 0; enemyIndex < this.enemies.size(); enemyIndex++) {
            Entity enemy = this.enemies.get(enemyIndex);
            EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(enemy);
            Array<Vector2> paths = enemyComp.waypoints;

            if (paths.size < 1)
                continue;

            // Generate color once per enemy
            float hue = (enemyIndex * 0.618033988749895f) % 1.0f;
            tempColor.fromHsv(hue * 360f, 0.8f, 0.9f);
            tempColor.r = tempColor.r * 0.7f + PATH_COLOR.r * 0.3f;
            tempColor.g = tempColor.g * 0.7f + PATH_COLOR.g * 0.3f;
            tempColor.b = tempColor.b * 0.7f + PATH_COLOR.b * 0.3f;
            tempColor.a = PATH_COLOR.a;
            shapeRenderer.setColor(tempColor);

            // Draw path lines
            for (int i = 0; i < paths.size - 1; i++) {
                Vector2 current = paths.get(i);
                Vector2 next = paths.get(i + 1);
                shapeRenderer.line(current.x, current.y, next.x, next.y);
            }
        }
        shapeRenderer.end();

        // Draw nodes in a separate pass since ShapeType is different
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int enemyIndex = 0; enemyIndex < this.enemies.size(); enemyIndex++) {
            Entity enemy = this.enemies.get(enemyIndex);
            EnemyComponent enemyComp = ECSEngine.ENEMY_COMPONENT_MAPPER.get(enemy);
            Array<Vector2> paths = enemyComp.waypoints;

            if (paths.size < 1)
                continue;

            // Use same color calculation for consistency
            float hue = (enemyIndex * 0.618033988749895f) % 1.0f;
            tempColor.fromHsv(hue * 360f, 0.8f, 1.0f);
            tempColor.r = tempColor.r * 0.7f + NODE_COLOR.r * 0.3f;
            tempColor.g = tempColor.g * 0.7f + NODE_COLOR.g * 0.3f;
            tempColor.b = tempColor.b * 0.7f + NODE_COLOR.b * 0.3f;
            tempColor.a = NODE_COLOR.a;
            shapeRenderer.setColor(tempColor);

            // Draw nodes
            for (int i = 0; i < paths.size; i++) {
                Vector2 node = paths.get(i);
                shapeRenderer.circle(node.x, node.y, NODE_SIZE, 8);
            }
        }
        shapeRenderer.end();

        // Reset line width
        Gdx.gl.glLineWidth(1.0f);
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
