package fr.taeron.hcf.listener;

import fr.taeron.hcf.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.kits.events.KitApplyEvent;

import org.bukkit.event.*;

public class KitListener implements Listener
{
    private final HCF plugin;
    
    public KitListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKitApply(KitApplyEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        final Faction playerFaction;
        if (!factionAt.isSafezone() && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) == null || !playerFaction.equals(factionAt))) {
            player.sendMessage(ChatColor.RED + "Tu doit être dans un spawn ou dans tes propres claims pour recevoir un kit.");
            event.setCancelled(true);
        }
    }
}
