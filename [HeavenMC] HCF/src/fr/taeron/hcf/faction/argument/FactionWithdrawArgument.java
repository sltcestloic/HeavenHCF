package fr.taeron.hcf.faction.argument;

import com.google.common.collect.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionWithdrawArgument extends CommandArgument{
	
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionWithdrawArgument(final HCF plugin) {
        super("withdraw", "Withdraws money from the faction balance.", new String[] { "w" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|amount>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can update the faction balance.");
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
        final FactionMember factionMember = playerFaction.getMember(uuid);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour pouvoir prendre de l'argent à la faction.");
            return true;
        }
        final int factionBalance = playerFaction.getBalance();
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = factionBalance;
        }
        else if ((amount = JavaUtils.tryParseInt(args[1])) == null) {
            sender.sendMessage(ChatColor.RED + "Erreur: '" + args[1] + "' n'est pas un nombre valide.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "La valeur doit être positive.");
            return true;
        }
        if (amount > factionBalance) {
            sender.sendMessage(ChatColor.RED + "Ta faction n'as pas autant d'argent."); //+ '$' + JavaUtils.format((Number)amount) + " to do this, whilst it only has " + '$' + JavaUtils.format((Number)factionBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().addBalance(uuid, amount);
        playerFaction.setBalance(factionBalance - amount);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " a pris " + ChatColor.GREEN + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " dans la banque de la faction.");
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>)((args.length == 2) ? FactionWithdrawArgument.COMPLETIONS : Collections.emptyList());
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
