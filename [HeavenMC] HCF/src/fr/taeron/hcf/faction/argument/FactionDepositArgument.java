package fr.taeron.hcf.faction.argument;

import com.google.common.collect.*;
import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import java.util.*;

public class FactionDepositArgument extends CommandArgument{
	
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionDepositArgument(final HCF plugin) {
        super("deposit", "Mettre de l'argent dans la banque de la faction", new String[] { "d" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|amount>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "La console n'est pas supportée.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        }
        else if ((amount = JavaUtils.tryParseInt(args[1])) == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valide.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Tu dois donner une valeur positive.");
            return true;
        }
        if (playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "Tu as besoin d'au moins " + '$' + JavaUtils.format((Number)amount) + " pour faire ça, tu as " + '$' + JavaUtils.format((Number)playerBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().subtractBalance(uuid, amount);
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " a déposé " + ChatColor.GREEN + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " dans la banque de la faction.");
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>)((args.length == 2) ? FactionDepositArgument.COMPLETIONS : Collections.emptyList());
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
