package de.haw.sea2.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameMap {
    private final TiledMap tiledMap;
    private final Array<CollisionArea> collisiomAreas;

    public GameMap(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.collisiomAreas = new Array<>();
        parseCollisionLayer();
    }

    private void parseCollisionLayer() {
        final MapLayer collisionLayer = this.tiledMap.getLayers().get("collision");

        if (collisionLayer == null) {
            Gdx.app.debug("TAG", "There is no collision layer!");
            return;
        }

        final MapObjects mapObjects = collisionLayer.getObjects();
        if (mapObjects == null) {
            Gdx.app.debug("TAG", "There are no collision MapObjects defined!");
            return;
        }

        for (final MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject) {
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
                final Rectangle rectangle = rectangleMapObject.getRectangle();
                final float[] rectVertices = new float[10];

                // left bottom
                rectVertices[0] = 0;
                rectVertices[1] = 0;

                // left top
                rectVertices[2] = 0;
                rectVertices[3] = rectangle.height;

                // right top
                rectVertices[4] = rectangle.width;
                rectVertices[5] = rectangle.height;

                // right bottom
                rectVertices[6] = rectangle.width;
                rectVertices[7] = 0;

                // left bottom
                rectVertices[8] = 0;
                rectVertices[9] = 0;

                this.collisiomAreas.add(new CollisionArea(rectangle.x, rectangle.y, rectVertices));

            } else if (mapObject instanceof PolylineMapObject) {
                final PolylineMapObject polylineMapObject = (PolylineMapObject) mapObject;
                final Polyline polyline = polylineMapObject.getPolyline();
                this.collisiomAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));
            } else {
                Gdx.app.debug("TAG", "MoapObject of Type: " + mapObject + "is not supported!");
            }
        }


    }

    public Array<CollisionArea> getCollisionAreas() {
        return this.collisiomAreas;
    }
}
