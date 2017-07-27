package fr.taeron.hcf.tablist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.heavenmc.core.util.BukkitUtils;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.faction.type.PlayerFaction;
import fr.taeron.hcf.user.FactionUser;

public class Tab implements Listener{
	
    public static HCF plugin;
    public Tab instance;
    


	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
		new BukkitRunnable(){
			@Override
			public void run() {
				Player p = e.getPlayer();
		        int version = ((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion();
		    	if(version < 7){
		    		Tab.initialize(p);
		    		if(TabAPI.getPlayerTabList(p) != null){
		            	TabAPI.getPlayerTabList(p).clear();
		            }
		            TabList list = TabAPI.createTabListForPlayer(p);

		            for(int i = 0 ; i < 61 ; i++){
		            	list.setSlot(i, "", String.valueOf(i), "");
		            }
		            for(int i = 0 ; i < 61 ; i++){
		            	list.clearSlot(i);
		            }
		            list.setSlot(0, "§aPosition:");
		            list.setSlot(1, ChatColor.GOLD.toString() + ChatColor.BOLD + "  HeavenMC");
		            list.setSlot(2, "§aEnd Portal:");
		            list.setSlot(3, "", "§f[", "?]  x, z");
		            list.setSlot(5, "§f1000, 1000");
		            
		            
		            list.setSlot(9, "§aStats: ");
		            list.setSlot(11, "§aKit de la map:");
		            
		            list.setSlot(12, "", "  §aKills:§f ", "0");
		            
		            
		            list.setSlot(14, "§fP1, T1");
		            
		            list.setSlot(15, "", "  §aMorts:§f ", "0");
		            
		            list.setSlot(16, " §aheavenmc.org");
		            
		            list.setSlot(20, "§aSOTW:");
		            
		            list.setSlot(21, "§aConnectés:");
		            
		            list.setSlot(23, "§f19/08/2017");
		            
		            list.setSlot(24, "" + "§f" + "");
		            
		            list.setSlot(30, "§aMinerais:");
		           
		            list.setSlot(32, "§aFaction:");
		            
		            list.setSlot(36, "", "§bDiamond: §f", "0");
		            
		            list.setSlot(38, "", "§aChef: §f", "N/A");
		            
		            list.setSlot(39, "", "§aEmerald: §f", "0");
		            
		            list.setSlot(41, "", "§aHome: §f", "N/A");
		            
		            list.setSlot(42, "", "§7Iron: §f", "0");
		            
		            list.setSlot(44, "", "§aBalance: §f", "0");
		            
		            list.setSlot(45, "", "§6Gold: §f", "0");
		            
		            list.setSlot(47, "", "§aDTR: §f", "0");
		            
		            list.setSlot(48, "", "§cRedstone: §f", "0");
		            list.setSlot(51, "", "§0Coal: §f", "0");
		            list.setSlot(54, "", "§9Lapis: §f", "0");
		            
		            list.setSlot(57, "§7§m", BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
		            list.setSlot(58, "§7§m", "-" + BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
		            list.setSlot(59, "§7§m", "--" + BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
		            list.setDefaultPing(1);
		            list.send();
		    	}
		        if(TabAPI.getPlayerTabList(p) != null){
		        	TabAPI.getPlayerTabList(p).clear();
		        }
			}
		}.runTaskLaterAsynchronously(HCF.getPlugin(), 20l);
	}

		
    
    public static void update() {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
			public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                	int version = ((CraftPlayer)p).getHandle().playerConnection.networkManager.getVersion();
                	if(version > 7){
                		return;
                	}
                	TabList list = TabAPI.getPlayerTabList(p);
                	if(TabAPI.getPlayerTabList(p) != null){
                		
                		PlayerFaction f = null;
                		
                		if(HCF.getPlugin().getFactionManager().getPlayerFaction(p) != null){
	                    	f = HCF.getPlugin().getFactionManager().getPlayerFaction(p);
                		}
                		
                		FactionUser user = HCF.getPlugin().getUserManager().getUser(p.getUniqueId());
                			                	
	                	
	                    TabSlot coords = list.getSlot(3);
	                    coords.updatePrefixAndSuffix("", Tab.dir(p) + "] " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockZ());
	                    
	                    TabSlot kills = list.getSlot(12);
	                    kills.updatePrefixAndSuffix("", String.valueOf(user.getKills()));
	                    
	                    TabSlot deaths = list.getSlot(15);
	                    deaths.updatePrefixAndSuffix("", String.valueOf(user.getDeaths()));

	                    TabSlot serverOnline = list.getSlot(24);
	                    serverOnline.updatePrefixAndSuffix("", String.valueOf(Bukkit.getOnlinePlayers().length));
	                    	                    
	                    TabSlot diamonds = list.getSlot(36);
	                    diamonds.updatePrefixAndSuffix("", String.valueOf(user.diamonds));
	                    
	                    TabSlot fleader = list.getSlot(38);
	                    if(f != null){
	                    	fleader.updatePrefixAndSuffix("", f.getLeader().getName());
	                    } else {
	                    	fleader.updatePrefixAndSuffix("", "N/A");
	                    }
	                    
	                    TabSlot emerald = list.getSlot(39);
	                    emerald.updatePrefixAndSuffix("", String.valueOf(user.emerald));
	                    
	                    TabSlot fhome = list.getSlot(41);
	                    if(f != null && f.getHome() != null){
	                    	fhome.updatePrefixAndSuffix("", f.getHome().getBlockX() + ", " + f.getHome().getBlockZ());
	                    } else {
	                    	fhome.updatePrefixAndSuffix("", "N/A");
	                    }
	                    
	                    TabSlot iron = list.getSlot(42);
	                    iron.updatePrefixAndSuffix("", String.valueOf(user.iron));
	                    
	                    TabSlot fbalance = list.getSlot(44);
	                    if(f != null){
	                    	fbalance.updatePrefixAndSuffix("", String.valueOf(f.getBalance()));
	                    } else {
	                    	fbalance.updatePrefixAndSuffix("", "N/A");
	                    }
	                    
	                    TabSlot gold = list.getSlot(45);
	                    gold.updatePrefixAndSuffix("", String.valueOf(user.gold));
	                    
	                    TabSlot dtr = list.getSlot(47);
	                    if(f != null){
	                    	String a = null;
                    		String d = String.valueOf(f.getDeathsUntilRaidable());
                    		if(d.length() >= 4){
	                    		a = String.valueOf(d.substring(0, 4));
                    		} else {
                    			a = d;
                    		}
                        	dtr.updatePrefixAndSuffix("", a);
	                    } else {
	                    	dtr.updatePrefixAndSuffix("", "N/A");
	                    }
	                    
	                    TabSlot redstone = list.getSlot(48);
	                    redstone.updatePrefixAndSuffix("", String.valueOf(user.redstone));
	                    
	                    TabSlot coal = list.getSlot(51);
	                    coal.updatePrefixAndSuffix("", String.valueOf(user.coal));
	                    
	                    TabSlot lapis = list.getSlot(54);
	                    lapis.updatePrefixAndSuffix("", String.valueOf(user.lapis));
	                    
                	} else {
                		initialize(p);
                	}
		        }
            }        
        }.runTaskTimerAsynchronously(Tab.plugin, 20L, 20L);
    }
    
    
    
    
    public Tab getInstance() {
        return this.instance;
    }
    
	public static void initialize(Player p){
    	
    	if(TabAPI.getPlayerTabList(p) != null){
        	TabAPI.getPlayerTabList(p).clear();
        }
        TabList list = TabAPI.createTabListForPlayer(p);

        for(int i = 0 ; i < 61 ; i++){
        	list.setSlot(i, "", String.valueOf(i), "");
        }
        for(int i = 0 ; i < 61 ; i++){
        	list.clearSlot(i);
        }
        list.setSlot(0, "§aPosition:");
        list.setSlot(1, ChatColor.GOLD.toString() + ChatColor.BOLD + "  HeavenMC");
        list.setSlot(2, "§aEnd Portal:");
        list.setSlot(3, "", "§f[", "x, x) [?]");
        list.setSlot(5, "§f1000, 1000");
        
        
        list.setSlot(9, "§aStats: ");
        list.setSlot(11, "§aKit de la map:");
        
        list.setSlot(12, "", "  §aKills:§f ", "0");
        
        
        list.setSlot(14, "§fP1, T1");
        
        list.setSlot(15, "", "  §aMorts:§f ", "0");
        
        list.setSlot(16, " §aheavenmc.org");
        
        list.setSlot(20, "§aSOTW:");
        
        list.setSlot(21, "§aConnectés:");
        
        list.setSlot(23, "§f19/08/2017");
        
        list.setSlot(24, "" + "§f" + "");
        
        list.setSlot(30, "§aMinerais:");
       
        list.setSlot(32, "§aFaction:");
        
        list.setSlot(36, "", "§bDiamond: §f", "0");
        
        list.setSlot(38, "", "§aChef: §f", "N/A");
        
        list.setSlot(39, "", "§aEmerald: §f", "0");
        
        list.setSlot(41, "", "§aHome: §f", "N/A");
        
        list.setSlot(42, "", "§7Iron: §f", "0");
        
        list.setSlot(44, "", "§aBalance: §f", "0");
        
        list.setSlot(45, "", "§6Gold: §f", "0");
        
        list.setSlot(47, "", "§aDTR: §f", "0");
        
        list.setSlot(48, "", "§cRedstone: §f", "0");
        list.setSlot(51, "", "§0Coal: §f", "0");
        list.setSlot(54, "", "§9Lapis: §f", "0");
        
        list.setSlot(57, "§7§m", BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
        list.setSlot(58, "§7§m", "-" + BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
        list.setSlot(59, "§7§m", "--" + BukkitUtils.STRAIGHT_LINE_DEFAULT, "");
        list.setDefaultPing(1);
        list.send();
    }
    
    public static String dir(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    private static String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "W";//W
        } else if (22.5 <= rot && rot < 67.5) {
            return "NW";//NW
        } else if (67.5 <= rot && rot < 112.5) {
            return "N";//N
        } else if (112.5 <= rot && rot < 157.5) {
            return "NE";//NE
        } else if (157.5 <= rot && rot < 202.5) {
            return "E";//E
        } else if (202.5 <= rot && rot < 247.5) {
            return "SE";//SE
        } else if (247.5 <= rot && rot < 292.5) {
            return "S";//S
        } else if (292.5 <= rot && rot < 337.5) {
            return "SW";//SW
        } else if (337.5 <= rot && rot < 360.0) {
            return "W";//W
        } else {
            return null;
        }
    }
}