package fr.taeron.hcf.faction.argument;

import com.google.common.collect.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.faction.*;
import java.util.*;

public class FactionUninviteArgument extends CommandArgument{
	
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionUninviteArgument(final HCF plugin) {
        super("uninvite", "Revoke an invitation to a player.", new String[] { "deinvite", "deinv", "uninv", "revoke" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|joueur>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour uninvite des joueurs.");
            return true;
        }
        final Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            sender.sendMessage(ChatColor.YELLOW + "Tu as supprimé toutes les invitations en attente.");
            return true;
        }
        if (!invitedPlayerNames.remove(args[1])) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas d'invitation en attente pour " + args[1] + '.');
            return true;
        }
        playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " a anullé l'invitation de " + ConfigurationService.ENEMY_COLOUR + args[1] + ChatColor.YELLOW + " à rejoindre la faction.");
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>((Collection<? extends String>)FactionUninviteArgument.COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
