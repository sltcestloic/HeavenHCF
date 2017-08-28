package fr.taeron.hcf.listeners;

import com.google.common.collect.*;
import java.util.regex.*;
import fr.taeron.hcf.*;
import java.util.concurrent.*;
import org.bukkit.event.player.*;
import org.heavenmc.core.Core;
import org.apache.commons.lang.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.scoreboard.ScoreboardHandler;

import org.bukkit.entity.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.event.*;
import java.util.*;
import org.bukkit.command.*;
import org.bukkit.event.*;

public class ChatListener implements Listener
{
    private static final Pattern PATTERN;
    static final String EOTW_CAPPER_PREFIX;
    static final ImmutableSet<UUID> EOTW_CAPPERS;
    private final ConcurrentMap<Object, Object> messageHistory;
    private final HCF plugin;
    
    public ChatListener(final HCF plugin) {
        this.plugin = plugin;
        this.messageHistory = new MapMaker().makeMap();
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    private final void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final String message = event.getMessage();
            final String playerName = player.getName();
            if (StringUtils.containsIgnoreCase(message, playerName)) {
            	Bukkit.getScheduler().runTaskLater(HCF.getPlugin(), new Runnable(){
            		@Override
            		public void run(){
	            		player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10.0f, 10.0f);
	                	player.sendMessage("§e§l" + event.getPlayer().getName() + " §et'a mentionné !");
            		}
            	}, 2L);
            }
        }
    }
    
    
    @SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        final Player player = event.getPlayer();
        final String lastMessage = (String) this.messageHistory.get(player.getUniqueId());
        final String cleanedMessage = ChatListener.PATTERN.matcher(message).replaceAll("");
        if (lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission("hcf.doublepost.bypass")) {
            player.sendMessage(ChatColor.RED + "Merci d'éviter le spam.");
            event.setCancelled(true);
            return;
        }
        this.messageHistory.put(player.getUniqueId(), cleanedMessage);
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        final ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        final Set<Player> recipients = (Set<Player>)event.getRecipients();
        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (!this.isGlobalChannel(message)) {
                final Collection<Player> online = (Collection<Player>)playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (final PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent((Event)new FactionChatEvent(true, playerFaction, player, chatChannel, (Collection<? extends CommandSender>)recipients, event.getMessage()));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        final boolean usingRecipientVersion = false;
        event.setCancelled(true);
        Boolean isTag = true;
        if (player.hasPermission("faction.removetag")) {
            isTag = true;
        }
		String rank = Core.getInstance().getUserManager().getPlayer(player).getRank().getPrefix() + " ";
		String displayName = player.getDisplayName();
		displayName = rank + displayName;
		final ConsoleCommandSender console = Bukkit.getConsoleSender();
		if(!(player.hasPermission("heaven.staff"))){
			if (message.contains("卍") || message.toLowerCase().contains("pute") || message.toLowerCase().contains("go spam") || message.toLowerCase().contains("tp mod") || message.toLowerCase().contains("mod tp") || message.toLowerCase().contains("cancer") || message.toLowerCase().contains("admin?") || message.toLowerCase().contains("admin ?")|| message.toLowerCase().contains("plugins leak") || message.toLowerCase().contains("ntm") || message.toLowerCase().contains("fdp") || message.toLowerCase().contains("ddos") || message.toLowerCase().contains("http://") || message.toLowerCase().contains("https://") || message.toLowerCase().contains("www.") || message.toLowerCase().contains(".fr") || message.toLowerCase().contains(".net") || message.toLowerCase().contains(".com") || message.toLowerCase().contains(".org")) {
				event.getRecipients().removeAll(ScoreboardHandler.getOnline());
				if(event.getPlayer().hasPermission("heaven.vip") || event.getPlayer().hasPermission("heaven.pro") || event.getPlayer().hasPermission("heaven.elite") || event.getPlayer().hasPermission("heaven.staff") || event.getPlayer().hasPermission("heaven.emerald")){
					event.getPlayer().sendMessage(displayName + " §7» " + ChatColor.WHITE + message);
				} else {
					event.getPlayer().sendMessage(displayName + " §7» " + ChatColor.GRAY + message);		            
				}
				for (final Player on : Bukkit.getOnlinePlayers()) {
					if (on.hasPermission("staff")) {
						on.sendMessage("§c[ChatFilter] §f" + player.getDisplayName() + ": §c" + message);
					}
				}
				event.setCancelled(true);
		            return;
		        }
	        }
	        String tag = (playerFaction == null) ? (ChatColor.RED + "-") : playerFaction.getDisplayName((CommandSender)console);
	        console.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " " + ChatColor.GRAY + message);
	        for (final Player recipient : event.getRecipients()) {
	        	if(event.getPlayer().hasPermission("heaven.vip") || event.getPlayer().hasPermission("heaven.pro") || event.getPlayer().hasPermission("heaven.elite")){
		            tag = ((playerFaction == null) ? (ChatColor.RED + "-") : playerFaction.getDisplayName((CommandSender)recipient));
		            if (isTag) {
		                recipient.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " §7» " + ChatColor.WHITE + message);
		            }
		            else {
		                recipient.sendMessage(displayName + " " + ChatColor.GRAY + message);
		            }
	        	} else {
	        		tag = ((playerFaction == null) ? (ChatColor.RED + "-") : playerFaction.getDisplayName((CommandSender)recipient));
		            if (isTag) {
		                recipient.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " §7» " + ChatColor.GRAY + message);
		            }
		            else {
		                recipient.sendMessage(displayName + " " + ChatColor.WHITE + message);
		            }
	        	}
	        }
    	}
    
    private boolean isGlobalChannel(final String input) {
        final int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while (i < length) {
            final char character = input.charAt(i);
            if (character == ' ') {
                ++i;
            }
            else {
                if (character == '/') {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    static {
        EOTW_CAPPER_PREFIX = ChatColor.YELLOW + "\u2605 ";
        final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
        EOTW_CAPPERS = builder.build();
        PATTERN = Pattern.compile("\\W");
    }
}
