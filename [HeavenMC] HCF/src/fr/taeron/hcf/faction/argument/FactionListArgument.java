package fr.taeron.hcf.faction.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.bukkit.scheduler.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.MapSorting;
import org.heavenmc.core.util.command.CommandArgument;
import org.bukkit.plugin.*;
import fr.taeron.hcf.faction.type.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;

public class FactionListArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public FactionListArgument(final HCF plugin) {
        super("list", "See a list of all factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "l" };
    }
    
	private static net.md_5.bungee.api.ChatColor fromBukkit(final ChatColor chatColor) {
        return net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar());
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Integer page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            page = JavaUtils.tryParseInt(args[1]);
            if (page == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valide.");
                return true;
            }
        }
        new BukkitRunnable() {
            public void run() {
                FactionListArgument.this.showList(page, label, sender);
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	private void showList(final int pageNumber, final String label, final CommandSender sender) {
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "Tu ne peux pas voir une page en dessous de 1.");
            return;
        }
        final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
        final Player senderPlayer = (Player)sender;
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer == null || senderPlayer.canSee(target)) {
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
                if (playerFaction == null) {
                    continue;
                }
                factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
            }
        }
        final Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
        final List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>)MapSorting.sortedValues((Map)factionOnlineMap, (Comparator)Comparator.reverseOrder());
        for (final Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();
            List<BaseComponent[]> results = pages.get(currentPage);
            if (results == null || results.size() >= 10) {
                pages.put(++currentPage, results = new ArrayList<BaseComponent[]>(10));
            }
            final PlayerFaction playerFaction2 = entry.getKey();
            final String displayName = playerFaction2.getDisplayName(sender);
            final int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * 10) : 0) + 1;
            final ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.WHITE);
            builder.append(displayName).color(net.md_5.bungee.api.ChatColor.RED).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.YELLOW + "Clique pour voir " + displayName + ChatColor.YELLOW + '.').create()));
            builder.append(" [" + ChatColor.GREEN + entry.getValue() + ChatColor.GRAY + '/' + playerFaction2.getMembers().size() + " En ligne] ", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(" [").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(JavaUtils.format((Number)playerFaction2.getDeathsUntilRaidable())).color(fromBukkit(playerFaction2.getDtrColour()));
            builder.append('/' + JavaUtils.format((Number)playerFaction2.getMaximumDeathsUntilRaidable()) + " DTR]").color(net.md_5.bungee.api.ChatColor.GRAY);
            results.add(builder.create());
        }
        final int maxPages = pages.size();
        if (pageNumber > maxPages) {
            sender.sendMessage(ChatColor.RED + "Il " + ((maxPages == 1) ? ("y a seulement " + maxPages + " page") : ("y a seulement " + maxPages + " pages")) + ".");
            return;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + " Liste des factions " + ChatColor.GRAY + "(Page " + pageNumber + '/' + maxPages + ')');
        final Player player = (Player)sender;
        final Collection<BaseComponent[]> components = pages.get(pageNumber);
        for (final BaseComponent[] component : components) {
            if (component == null) {
                continue;
            }
            if (player != null) {
                player.spigot().sendMessage(component);
            }
            else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }
        sender.sendMessage(ChatColor.GOLD + " Pour voir les autres pages, fait " + ChatColor.YELLOW + '/' + label + ' ' + this.getName() + " <page#>" + ChatColor.GOLD + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
