package fr.taeron.hcf.command;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;
import org.bukkit.event.player.*;
import org.heavenmc.core.util.BukkitUtils;
import org.bukkit.*;
import java.util.*;
import java.util.stream.*;

public class SpawnCommand implements CommandExecutor, TabCompleter{
	
	
    final HCF plugin;
    
    
    public SpawnCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
        if (!sender.hasPermission(command.getPermission() + ".teleport")) {
            sender.sendMessage(ChatColor.RED + "Tu es sur un serveur hcf, la commande /spawn n'existe pas. " + "Le spawn est trouvable aux coordonées " + ChatColor.YELLOW + '(' + spawn.getBlockX() + ", " + spawn.getBlockZ() + ')');
            return true;
        }
        if (!sender.hasPermission(command.getPermission() + ".teleport")) {
            this.plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), TimeUnit.SECONDS.toMillis(15L), ChatColor.YELLOW + "Teleporting to spawn in " + 15 + " seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }
        if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "Impossible de trouver le monde " + args[0] + '.');
                return true;
            }
            spawn = world.getSpawnLocation().clone().add(0.5, 0.0, 0.5);
        }
        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1 || !sender.hasPermission(command.getPermission() + ".teleport")) {
            return Collections.emptyList();
        }
        return (List<String>)BukkitUtils.getCompletions(args, (List)Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
    }
}
