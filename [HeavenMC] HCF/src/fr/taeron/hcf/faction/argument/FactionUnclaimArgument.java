package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.chat.ClickAction;
import org.heavenmc.core.util.chat.Text;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionUnclaimArgument extends CommandArgument{
	
    private static final HashSet<String> stuff;
    private final HCF plugin;
    
    public FactionUnclaimArgument(final HCF plugin) {
        super("unclaim", "Unclaims land from your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " ";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "Seul le chef de faction peut unclaim des territoires.");
            return true;
        }
        final Collection<Claim> factionClaims = playerFaction.getClaims();
        if (factionClaims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Ta faction n'as pas de claim.");
            return true;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("yes") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                for (final Claim claims : factionClaims) {
                    playerFaction.removeClaim(claims, (CommandSender)player);
                }
                factionClaims.clear();
                return true;
            }
            if (args[1].equalsIgnoreCase("no") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                FactionUnclaimArgument.stuff.remove(player.getName());
                player.sendMessage(ChatColor.YELLOW + "Tu as bien annulé l'unclaim.");
                return true;
            }
        }
        FactionUnclaimArgument.stuff.add(player.getName());
        new Text(ChatColor.YELLOW + "Est tu sur de vouloir unclaim " + ChatColor.BOLD + "tous" + ChatColor.YELLOW + " tes territoires?").send((CommandSender)player);
        new Text(ChatColor.YELLOW + "Si oui, fait " + ChatColor.DARK_GREEN + "/f unclaim yes" + ChatColor.YELLOW + " sinon fait" + ChatColor.DARK_RED + " /f unclaim no" + ChatColor.GRAY + " (Clique ici pour unclaim)").setHoverText(ChatColor.GOLD + "Clique ici pour tout unclaim").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send((CommandSender)player);
        return true;
    }
    
    static {
        stuff = new HashSet<String>();
    }
}
