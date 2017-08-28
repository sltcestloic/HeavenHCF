package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.events.factions.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;
import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.*;

public class FactionHomeArgument extends CommandArgument{
	
    private final FactionExecutor factionExecutor;
    private final HCF plugin;
    
    public FactionHomeArgument(final FactionExecutor factionExecutor, final HCF plugin) {
        super("home", "Teleport to the faction home.");
        this.factionExecutor = factionExecutor;
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            this.factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
            return true;
        }
        final UUID uuid = player.getUniqueId();
        PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
        long remaining = timer.getRemaining(player);
        if (remaining > 0L) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser cette commande car ton timer de " + timer.getDisplayName() + ChatColor.RED + " est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restantes]");
            return true;
        }
        if ((remaining = (timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player)) > 0L) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser cette commande car ton timer de " + timer.getDisplayName() + ChatColor.RED + " est actif [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " restantes]");
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final Location home = playerFaction.getHome();
        if (home == null) {
            sender.sendMessage(ChatColor.RED + "Ta faction n'a pas de home.");
            return true;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "Tu es dans une zone d'event, tu ne peux pas utiliser de commande de téléportation.");
            return true;
        }
        long millis = 0L;
        switch (player.getWorld().getEnvironment()) {
            case THE_END: {
                sender.sendMessage(ChatColor.RED + "Impossible tant que tu es dans l'end.");
                return true;
            }
            case NETHER: {
                millis = 30000L;
                break;
            }
            default: {
                millis = 10000L;
                break;
            }
        }
        if (!factionAt.equals(playerFaction) && factionAt instanceof PlayerFaction) {
            sender.sendMessage("§cTu ne peux pas utiliser cette commande dans un territoire ennemi.");
            return false;
        }
        if (this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId()) > 0L) {
            player.sendMessage(ChatColor.RED + "Tu ne peux pas te téléporter a ton home si ton PvP Timer est actif.");
            return true;
        }
        if (factionAt.isSafezone()) {
            millis = 0L;
        }
        this.plugin.getTimerManager().teleportTimer.teleport(player, home, millis, ChatColor.YELLOW + "Téléportation dans ton home dans " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Ne bouge pas et ne prend pas de dégats.", PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }
}
