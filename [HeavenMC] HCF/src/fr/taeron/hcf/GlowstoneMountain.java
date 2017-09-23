package fr.taeron.hcf;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;

public class GlowstoneMountain {

	@SuppressWarnings("deprecation")
	public static void resetMountain() {
        final Vector p1 = new Vector(-64, 89, 251);
        final Vector p2 = new Vector(-61, 92, 248);
        final CuboidRegion cube = new CuboidRegion(p1, p2);
        final EditSession session = new EditSession(new BukkitWorld(Bukkit.getWorld("world_nether")), cube.getArea());
        try {
            session.setBlocks(cube, new BaseBlock(89));
            Bukkit.broadcastMessage("§6[GlowStone Mountain] §cLa Glowstone Mountain a été reset !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void CubeReset() {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HCF.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                resetMountain();    
                CubeReset();
            }
        }, 72000L);
    }
}
