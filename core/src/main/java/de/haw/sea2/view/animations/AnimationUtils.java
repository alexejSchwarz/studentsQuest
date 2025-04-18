package de.haw.sea2.view.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

public class AnimationUtils {
    public static final ObjectMap<AnimationType, Animation<Sprite>> ANIMATION_CACHE = new ObjectMap<>();
    private static final String LOG_TAG = "AnimationUtils";

    /**
     * retrieve Animation from Atlas. Save it in a cache and use it for later
     * retrievals
     */
    public static Animation<Sprite> getAnimation(AnimationType animationType, TextureAtlas atlas) {
        Animation<Sprite> animation = ANIMATION_CACHE.get(animationType);

        if (animation != null) {
            return animation;
        }

        // create Animation
        LoggerUtil.log(LogCategory.DEBUG, LOG_TAG, "Creating new animation of type: " + animationType);
        TextureAtlas.AtlasRegion atlasRegion = atlas.findRegion(animationType.atlasKey());

        int frameCount = animationType.frameCount();
        int frameWidth = atlasRegion.getRegionWidth() / frameCount;
        int frameHeight = atlasRegion.getRegionHeight();
        Array<Sprite> keyFrames = getKeyFrames(atlasRegion, frameCount, frameWidth, frameHeight);

        animation = new Animation<>(animationType.frameTime(), keyFrames, Animation.PlayMode.LOOP);
        ANIMATION_CACHE.put(animationType, animation);
        return animation;
    }

    private static Array<Sprite> getKeyFrames(TextureAtlas.AtlasRegion atlasRegion, int frameCount, int frameWidth, int frameHeight) {
        Array<Sprite> keyFrames = new Array<>(frameCount);

        // Extract each frame from the strip
        for (int i = 0; i < frameCount; i++) {
            TextureRegion frame = new TextureRegion(atlasRegion,
                i * frameWidth, 0,
                frameWidth, frameHeight);
            Sprite sprite = new Sprite(frame);
            sprite.setOriginCenter();
            keyFrames.add(sprite);
        }
        return keyFrames;
    }
}
