package fr.taeron.hcf.faction.argument.staff;

import fr.taeron.hcf.*;
import org.bukkit.plugin.*;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.command.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.conversations.*;

public class FactionRemoveArgument extends CommandArgument{
	
    private final ConversationFactory factory;
    private final HCF plugin;
    
    public FactionRemoveArgument(final HCF plugin) {
        super("remove", "Remove a faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "delete", "forcedisband", "forceremove" };
        this.permission = "command.faction.argument." + this.getName();
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new RemoveAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|faction>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation: " + this.getUsage(label));
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "Cette commande ne peut être executée que par la console.");
                return true;
            }
            final Conversable conversable = (Conversable)sender;
            conversable.beginConversation(this.factory.buildConversation(conversable));
            return true;
        }
        else {
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
            	sender.sendMessage(ChatColor.RED + "La faction (ou le joueur) " + args[1] + " n'existe pas.");
                return true;
            }
            if (this.plugin.getFactionManager().removeFaction(faction, sender)) {
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Tu as supprimé la faction " + faction.getName() + ChatColor.YELLOW + '.');
            }
            return true;
        }
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
    
    private static class RemoveAllPrompt extends StringPrompt
    {
        private final HCF plugin;
        
        public RemoveAllPrompt(final HCF plugin) {
            this.plugin = plugin;
        }
        
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Est tu sur? " + ChatColor.RED + ChatColor.BOLD + "Toutes les factions" + ChatColor.YELLOW + " seront supprimées. " + "Écrit " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " pour confirmer ou" + ChatColor.RED + "no" + ChatColor.YELLOW + " pour anuller.";
        }
        
        @SuppressWarnings("unused")
		public Prompt acceptInput(final ConversationContext context, final String string) {
            final String lowerCase2;
            final String lowerCase = lowerCase2 = string.toLowerCase();
            switch (lowerCase2) {
                case "yes": {
                    for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
                        this.plugin.getFactionManager().removeFaction(faction, (CommandSender)Bukkit.getConsoleSender());
                    }
                    final Conversable conversable = context.getForWhom();
                    Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Toutes les factions ont été supprimées" + ((conversable instanceof CommandSender) ? (" par " + ((CommandSender)conversable).getName()) : "") + '.');
                    return Prompt.END_OF_CONVERSATION;
                }
                case "no": {
                    context.getForWhom().sendRawMessage(ChatColor.BLUE + "Processus anullé.");
                    return Prompt.END_OF_CONVERSATION;
                }
                default: {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Reponse invalide, processus anullé.");
                    return Prompt.END_OF_CONVERSATION;
                }
            }
        }
    }
}
