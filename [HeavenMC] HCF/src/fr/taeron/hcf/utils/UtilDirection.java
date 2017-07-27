package fr.taeron.hcf.utils;

import org.bukkit.block.*;

public class UtilDirection
{
    public static final BlockFace[] axis;
    public static final BlockFace[] radial;
    
    static {
        axis = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        radial = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    }
    
    public static BlockFace yawToFace(final float yaw) {
        return yawToFace(yaw, false);
    }
    
    public static BlockFace yawToFace(final float yaw, final boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return UtilDirection.radial[Math.round(yaw / 45.0f) & 0x7];
        }
        return UtilDirection.axis[Math.round(yaw / 90.0f) & 0x3];
    }
}
