package org.mangorage.mangostorage.world.block.entity;

public interface TickingBlockEntity {
    default void tick() {}

    default void tickServer() {
        tick();
    }

    default void tickClient() {
        tick();
    }
}
