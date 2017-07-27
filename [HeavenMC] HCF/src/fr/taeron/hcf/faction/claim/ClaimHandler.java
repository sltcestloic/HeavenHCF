package fr.taeron.hcf.faction.claim;

import org.bukkit.inventory.*;
import org.heavenmc.core.util.ItemBuilder;
import org.heavenmc.core.util.cuboid.Cuboid;
import org.bukkit.entity.*;
import fr.taeron.hcf.visualise.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.type.*;
import com.google.common.base.*;
import org.bukkit.command.*;
import java.util.*;
import org.bukkit.*;

public class ClaimHandler{
	
    public static final int MIN_CLAIM_HEIGHT = 0;
    public static final int MAX_CLAIM_HEIGHT = 256;
    public static final long PILLAR_BUFFER_DELAY_MILLIS = 200L;
    public static final ItemStack CLAIM_WAND;
    public static final int MIN_SUBCLAIM_RADIUS = 2;
    public static final int MIN_CLAIM_RADIUS = 5;
    public static final int MAX_CHUNKS_PER_LIMIT = 16;
    public static final int CLAIM_BUFFER_RADIUS = 4;
    public final Map<UUID, ClaimSelection> claimSelectionMap;
    private final HCF plugin;
    
    public ClaimHandler(final HCF plugin) {
        this.plugin = plugin;
        this.claimSelectionMap = new HashMap<>();
    }
    
    public int calculatePrice(final Cuboid claim, int currentClaims, final boolean selling) {
        if (currentClaims == -1 || !claim.hasBothPositionsSet()) {
            return 0;
        }
        int multiplier = 1;
        int remaining = claim.getArea();
        double price = 0.0;
        while (remaining > 0) {
            if (--remaining % 250 == 0) {
                ++multiplier;
            }
            price += 0.25 * multiplier;
        }
        if (currentClaims != 0) {
            currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
            price += currentClaims * 500;
        }
        if (selling) {
            price *= 0.8;
        }
        return (int)price;
    }
    
    public boolean clearClaimSelection(final Player player) {
        final ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.remove(player.getUniqueId());
        if (claimSelection != null) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, null);
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("deprecation")
	public boolean canSubclaimHere(final Player player, final Location location) {
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour claim.");
            return false;
        }
        if (!this.plugin.getFactionManager().getFactionAt(location).equals(playerFaction)) {
            player.sendMessage(ChatColor.RED + "Ce territoire ne fait pas partie du territoire de ta faction.");
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public boolean canClaimHere(final Player player, final Location location) {
        final World world = location.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "Tu ne peux claim que dans l'overworld.");
            return false;
        }
        if (!(this.plugin.getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
            player.sendMessage(ChatColor.RED + "Tu n'es pas en " + ConfigurationService.WILDERNESS_COLOUR + "Wilderness" + ChatColor.RED + ". " + "Tu dois être a au moins " + ConfigurationService.WARZONE_RADIUS + " blocs du spawn.");
            return false;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
        	player.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour claim.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
            player.sendMessage(ChatColor.RED + "Ta faction a déjà le maximum de claims - " + 8);
            return false;
        }
        final int locX = location.getBlockX();
        final int locZ = location.getBlockZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = locX - 5; x < locX + 5; ++x) {
            for (int z = locZ - 5; z < locZ + 5; ++z) {
                final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (factionAtNew instanceof RoadFaction) {}
                if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "Cette position est trop près d'un territoire ennemi.");
                    return false;
                }
            }
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public boolean tryPurchasing(final Player player, final Claim claim) {
        Preconditions.checkNotNull((Object)claim, (Object)"Claim is null");
        final World world = claim.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
         player.sendMessage(ChatColor.RED + "Tu ne peux claim que dans l'overworld.");
            return false;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
        	player.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour claim.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
        	player.sendMessage(ChatColor.RED + "Ta faction a déjà le maximum de claims - " + 8);
            return false;
        }
        final int factionBalance = playerFaction.getBalance();
        final int claimPrice = this.calculatePrice((Cuboid)claim, playerFaction.getClaims().size(), false);
        if (claimPrice > factionBalance) {
            player.sendMessage(ChatColor.RED + "Ta faction a seulement " + '$' + factionBalance + ", le prix de ce claim est de " + '$' + claimPrice + '.');
            return false;
        }
        if (claim.getChunks().size() > 20) {
            player.sendMessage(ChatColor.RED + "Les claims ne peuvent pas contenir plus de " + 20 + " chunks.");
            return false;
        }
        if (claim.getWidth() < 5 || claim.getLength() < 5) {
            player.sendMessage(ChatColor.RED + "La taille du claim doit faire au moins " + 5 + 'x' + 5 + " blocs.");
            return false;
        }
        final int minimumX = claim.getMinimumX();
        final int maximumX = claim.getMaximumX();
        final int minimumZ = claim.getMinimumZ();
        final int maximumZ = claim.getMaximumZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = minimumX; x < maximumX; ++x) {
            for (int z = minimumZ; z < maximumZ; ++z) {
                final Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (factionAt != null && !(factionAt instanceof WildernessFaction)) {
                    player.sendMessage(ChatColor.RED + "Ce claim contient un territoire qui n'appartient pas a " + ChatColor.GRAY + "Wilderness" + ChatColor.RED + '.');
                    return false;
                }
            }
        }
        for (int x = minimumX - 10; x < maximumX + 10; ++x) {
            for (int z = minimumZ - 10; z < maximumZ + 10; ++z) {
                final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (factionAtNew instanceof RoadFaction) {}
                if (!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "Ce claim est trop près d'un territoire ennemi.");
                    return false;
                }
            }
        }
        final Collection<Claim> otherClaims = playerFaction.getClaims();
        final boolean conjoined = otherClaims.isEmpty();
        if (!conjoined) {
            player.sendMessage(ChatColor.RED + "Fait /f unclaim pour refaire tes claims.");
            return false;
        }
        claim.setY1(0);
        claim.setY2(256);
        if (!playerFaction.addClaim(claim, (CommandSender)player)) {
            return false;
        }
        final Location center = claim.getCenter();
        player.sendMessage(ChatColor.YELLOW + "Tu as acheté ce claim pour " + ChatColor.GREEN + '$' + claimPrice + ChatColor.YELLOW + '.');
        playerFaction.setBalance(factionBalance - claimPrice);
        playerFaction.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " a claim un territoire pour la faction en " + ChatColor.GOLD + '(' + center.getBlockX() + ", " + center.getBlockZ() + ')' + ChatColor.GREEN + '.', player.getUniqueId());
        return true;
    }
    
    static {
        CLAIM_WAND = new ItemBuilder(Material.DIAMOND_HOE).displayName(ChatColor.GREEN.toString() + "Claim Wand").lore(new String[] { ChatColor.BLUE + "Clique droit ou gauche " + ChatColor.GREEN + "sur un bloc" + ChatColor.BLUE + " pour:", ChatColor.GRAY + "Définir la première ou deuxième position de ", ChatColor.GRAY + "ta sélection.", "", ChatColor.BLUE + "Clique droit " + ChatColor.GREEN + "dans l'air" + ChatColor.BLUE + " pour:", ChatColor.GRAY + "Effacer ta sélection actuelle.", "", ChatColor.BLUE + "Sneak + " + ChatColor.BLUE + "clique gauche " + ChatColor.GREEN + "dans l'air ou sur un bloc" + ChatColor.BLUE + " pour:", ChatColor.GRAY + "Acheter le territoire que tu as sélectionné." }).build();
    }
}
