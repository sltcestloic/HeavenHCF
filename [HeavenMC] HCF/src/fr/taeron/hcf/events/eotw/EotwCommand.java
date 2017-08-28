package fr.taeron.hcf.events.eotw;

import fr.taeron.hcf.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.conversations.*;

public class EotwCommand implements CommandExecutor, TabCompleter
{
    private final ConversationFactory factory;
    
    public EotwCommand(final HCF plugin) {
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut s'executer que depuis la console.");
            return true;
        }
        final Conversable conversable = (Conversable)sender;
        conversable.beginConversation(this.factory.buildConversation(conversable));
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
    
    private static final class EotwPrompt extends StringPrompt
    {
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Est tu sur de vouloir faire ça? Le serveur va passer en mode EOTW, si le mode EOTW est activé, toutes les factions seront raidable et le spawn deviendra un KOTH. " + "Écrit " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " pour confirmer ou " + ChatColor.RED + "no" + ChatColor.YELLOW + " pour annuler.";
        }
        
        public Prompt acceptInput(final ConversationContext context, final String string) {
            if (string.equalsIgnoreCase("yes")) {
                final boolean newStatus = !HCF.getPlugin().getEotwHandler().isEndOfTheWorld(false);
                final Conversable conversable = context.getForWhom();
                if (conversable instanceof CommandSender) {
                    Command.broadcastCommandMessage((CommandSender)conversable, ChatColor.GOLD + "Le mode EOTW est désormais " + newStatus + '.');
                }
                else {
                    conversable.sendRawMessage(ChatColor.GOLD + "Le mode EOTW est désormais " + newStatus + '.');
                }
                HCF.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
            }
            else if (string.equalsIgnoreCase("no")) {
                context.getForWhom().sendRawMessage(ChatColor.BLUE + "Tu as annulé le processus du mode EOTW.");
            }
            else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Réponse invalide, processus annulé.");
            }
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
