package fr.taeron.hcf.faction.type;

import fr.taeron.hcf.faction.claim.*;
import org.bukkit.command.*;
import java.util.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.event.cause.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.GenericUtils;

import fr.taeron.hcf.faction.event.*;
import fr.taeron.hcf.*;
import com.google.common.collect.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ClaimableFaction extends Faction{
	
	protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = (ImmutableMap.of(World.Environment.NETHER, "Nether",
            World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End"));
	
	
    protected final Set<Claim> claims;
    
    public ClaimableFaction(final String name) {
        super(name);
        this.claims = new HashSet<Claim>();
    }
    
	public ClaimableFaction(final Map<String, Object> map) {
        super(map);
        (this.claims = new HashSet<Claim>()).addAll(GenericUtils.createList(map.get("claims"), (Class)Claim.class));
    }
    
	@Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList(this.claims));
        return map;
    }
    
    @Override
    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + this.getDisplayName(sender));
        for (final Claim claim : this.claims) {
            final Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Position: " + ChatColor.GRAY.toString() + (String)ClaimableFaction.ENVIRONMENT_MAPPINGS.get((Object)location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public Set<Claim> getClaims() {
        return this.claims;
    }
    
    public boolean addClaim(final Claim claim, final CommandSender sender) {
        return this.addClaims(Collections.singleton(claim), sender);
    }
    
    public boolean addClaims(final Collection<Claim> adding, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }
    
    public boolean removeClaim(final Claim claim, final CommandSender sender) {
        return this.removeClaims(Collections.singleton(claim), sender);
    }
    
    public boolean removeClaims(final Collection<Claim> removing, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        int previousClaims = this.claims.size();
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.removeAll(removing)) {
            return false;
        }
        if (this instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction)this;
            final Location home = playerFaction.getHome();
            final HCF plugin = HCF.getPlugin();
            int refund = 0;
            for (final Claim claim : removing) {
                refund += plugin.getClaimHandler().calculatePrice(claim, previousClaims, true);
                if (previousClaims > 0) {
                    --previousClaims;
                }
                if (home != null && claim.contains(home)) {
                    playerFaction.setHome(null);
                    playerFaction.broadcast(ChatColor.RED.toString() + "Le home de ta faction a été supprimé car son territoire a été unclaim.");
                    break;
                }
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
            playerFaction.broadcast(ChatColor.YELLOW + "Le chef de ta faction a récupéré " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " grâce a l'unclaim d'un territoire.");
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
}
