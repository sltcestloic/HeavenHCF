package fr.taeron.hcf.events.arguments;

import fr.taeron.hcf.*;
import fr.taeron.hcf.events.*;

import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;

import fr.taeron.hcf.faction.type.*;

public class EventCancelArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public EventCancelArgument(HCF plugin) {
        super("cancel", "Stopper un event en cours", new String[] { "stop", "end" });
        this.plugin = plugin;
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        final Faction eventFaction = eventTimer.getEventFaction();
        if (!eventTimer.clearCooldown()) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas d'event en cours.");
            return true;
        }
        Bukkit.broadcastMessage(sender.getName() + ChatColor.YELLOW + " a annulé " + ((eventFaction == null) ? "l'event" : (eventFaction.getName() + ChatColor.YELLOW)) + ".");
        return true;
    }
}
