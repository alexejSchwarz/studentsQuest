package de.haw.sea2.map;

import com.badlogic.gdx.math.Vector2;

public record EntitySpawnPoint(String type, String entityType, boolean hasSpawnProtection, Vector2 spawnPoint, int id) {}
