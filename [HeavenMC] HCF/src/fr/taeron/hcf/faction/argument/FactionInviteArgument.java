package fr.taeron.hcf.faction.argument;

import java.util.regex.*;
import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.chat.ClickAction;
import org.heavenmc.core.util.chat.Text;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.*;
import fr.taeron.hcf.faction.struct.*;
import net.minecraft.server.v1_7_R4.*;
import java.util.*;
import fr.taeron.hcf.faction.type.*;

public class FactionInviteArgument extends CommandArgument{
	
    private static final Pattern USERNAME_REGEX;
    private final HCF plugin;
    
    public FactionInviteArgument(final HCF plugin) {
        super("invite", "Invite a player to the faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "inv", "invitemember", "inviteplayer" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite to a faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        if (!FactionInviteArgument.USERNAME_REGEX.matcher(args[1]).matches()) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nom valide.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas de faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "Tu dois être officier de faction pour inviter un joueur.");
            return true;
        }
        final Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        String name = args[1];
        if (playerFaction.getMember(name) != null) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' est déjà dans ta faction.");
            return true;
        }
        if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas inviter de joueurs car ta faction est raidable.");
            return true;
        }
        if (!invitedPlayerNames.add(name)) {
            sender.sendMessage(ChatColor.RED + name + " a déjà été invité.");
            return true;
        }
        final Player target = Bukkit.getPlayer(name);
        if (target != null) {
            name = target.getName();
            final Text text = new Text(sender.getName()).setColor(Relation.ENEMY.toChatColour()).append((IChatBaseComponent)new Text(" t'as invité à rejoindre la faction ").setColor(ChatColor.YELLOW));
            text.append((IChatBaseComponent)new Text(playerFaction.getName()).setColor(Relation.ENEMY.toChatColour())).append((IChatBaseComponent)new Text(". ").setColor(ChatColor.YELLOW));
            text.append((IChatBaseComponent)new Text("Clique ici").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, '/' + label + " accept " + playerFaction.getName()).setHoverText(ChatColor.AQUA + "Clique pour rejoindre la faction " + playerFaction.getDisplayName((CommandSender)target) + ChatColor.AQUA + '.')).append((IChatBaseComponent)new Text(" pour accepter l'invitation.").setColor(ChatColor.YELLOW));
            text.send((CommandSender)target);
        }
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " a invité " + Relation.ENEMY.toChatColour() + name + ChatColor.YELLOW + " dans la faction.");
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
        final List<String> results = new ArrayList<String>();
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                final Faction targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId());
                if (targetFaction != null && targetFaction.equals(playerFaction)) {
                    continue;
                }
                results.add(target.getName());
            }
        }
        return results;
    }
    
    static {
        USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
    }
}
