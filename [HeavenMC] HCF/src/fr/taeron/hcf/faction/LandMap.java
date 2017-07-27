package fr.taeron.hcf.faction;

import org.bukkit.entity.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.visualise.*;
import org.bukkit.inventory.*;
import org.heavenmc.core.Core;
import org.heavenmc.core.util.BukkitUtils;
import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;
import fr.taeron.hcf.faction.type.*;

public class LandMap{
    
    @SuppressWarnings("deprecation")
	public static boolean updateMap(final Player player, final HCF plugin, final VisualType visualType, final boolean inform) {
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final int locationX = location.getBlockX();
        final int locationZ = location.getBlockZ();
        final int minimumX = locationX - 22;
        final int minimumZ = locationZ - 22;
        final int maximumX = locationX + 22;
        final int maximumZ = locationZ + 22;
        final Set<Claim> board = new LinkedHashSet<Claim>();
        boolean subclaimBased;
        if (visualType == VisualType.SUBCLAIM_MAP) {
            subclaimBased = true;
        }
        else {
            if (visualType != VisualType.CLAIM_MAP) {
                player.sendMessage(ChatColor.RED + "Non supporté: " + visualType.name().toLowerCase() + '.');
                return false;
            }
            subclaimBased = false;
        }
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                final Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
                if (claim != null) {
                    if (subclaimBased) {
                        board.addAll(claim.getSubclaims());
                    }
                    else {
                        board.add(claim);
                    }
                }
            }
        }
        if (board.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Il n'y a pas de claims dans ton champ de vision.");
            return false;
        }
        for (final Claim claim2 : board) {
            final int maxHeight = Math.min(world.getMaxHeight(), 256);
            final Location[] corners = claim2.getCornerLocations();
            final List<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
            for (final Location corner : corners) {
                for (int y = 0; y < maxHeight; ++y) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }
            final Map<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player, shown, visualType, true);
            if (dataMap.isEmpty()) {
                continue;
            }
            final String materialName = Core.getPlugin().getItemDb().getName(new ItemStack(dataMap.entrySet().iterator().next().getValue().getItemType(), 1));
            if (!inform) {
                continue;
            }
            player.sendMessage(ChatColor.YELLOW + "Faction: " + claim2.getFaction().getDisplayName((CommandSender)player) + ChatColor.GRAY + " (Représenté avec des " + materialName + ")" + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    public static Location getNearestSafePosition(final Player player, final Location origin, final int searchRadius) {
        final FactionManager factionManager = HCF.getPlugin().getFactionManager();
        final Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        final int minX = origin.getBlockX() - searchRadius;
        final int maxX = origin.getBlockX() + searchRadius;
        final int minZ = origin.getBlockZ() - searchRadius;
        final int maxZ = origin.getBlockZ() + searchRadius;
        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                final Location atPos = origin.clone().add((double)x, 0.0, (double)z);
                final Faction factionAtPos = factionManager.getFactionAt(atPos);
                if (Objects.equals(factionAtPos, playerFaction) || !(factionAtPos instanceof PlayerFaction)) {
                    return BukkitUtils.getHighestLocation(atPos, atPos);
                }
                final Location atNeg = origin.clone().add((double)x, 0.0, (double)z);
                final Faction factionAtNeg = factionManager.getFactionAt(atNeg);
                if (Objects.equals(factionAtNeg, playerFaction) || !(factionAtNeg instanceof PlayerFaction)) {
                    return BukkitUtils.getHighestLocation(atNeg, atNeg);
                }
            }
        }
        return null;
    }
}
