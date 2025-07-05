package org.mangorage.swiss.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

//From BBL Core
public class DirectionUtil {
    public static Direction adjustPosition(Direction facing, Direction direction) {
        return switch (facing) {
            case NORTH, UP, DOWN -> direction;
            case SOUTH -> direction.getOpposite();
            case EAST -> direction.getClockWise();
            case WEST -> direction.getCounterClockWise();
        };
    }

    public static Rotation getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
}