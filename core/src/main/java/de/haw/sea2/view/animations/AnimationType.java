package de.haw.sea2.view.animations;

/**
 * Record class used for every animated Entity with textues in an atlas
 *
 * @param atlasPath path to the coresponding atlas in assets
 * @param atlasKey key for the atlas region like "hero"
 * @param frameTime time its takes to switch from one frame to another
 * @param rowIndex index for the texture row in the atlas region
 */
public record AnimationType(String atlasPath, String atlasKey, float frameTime, int rowIndex) {}
