package fr.taeron.hcf.crate;


import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.InventoryUtils;

import fr.taeron.hcf.HCF;

public class CrateListener implements Listener {
	
    private HCF plugin;
    public static Map<String, CrateTask.OpenCrate> open;
    
    public CrateListener(HCF plugin) {
        this.plugin = plugin;
        CrateListener.open = new HashMap<String, CrateTask.OpenCrate>();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Key key = this.plugin.getKeyManager().getKey(event.getItemInHand());
        if (key != null) {
            event.setCancelled(true);
        }
    }
    
    public static boolean isInventoryFull(Player p){
    	for(int i = 0; i < 36; i ++){
    		if(p.getInventory().getItem(i) == null){
    			return false;
    		}
    	}
    	return true;
    }
    
    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (inventory != null && topInventory != null && topInventory.equals(inventory) && topInventory.getTitle().endsWith(" Crate")) {
            Player player = (Player)event.getPlayer();
        	new BukkitRunnable(){
        		public void run(){
        			if(CrateListener.open.containsKey(player.getName()) && CrateListener.open.get(player.getName()).check() &&CrateListener.open.get(player.getName()).should()){
        				//player.openInventory(event.getInventory());
        			} else if (CrateListener.open.containsKey(player.getName())){
        				Location location = player.getLocation();
        	            World world = player.getWorld();
        	            if(isInventoryFull(player) && topInventory.getItem(12) != null){
        	            	world.dropItemNaturally(location, topInventory.getItem(12));
        	            } else if (topInventory.getItem(12) != null) {
        	            	player.getInventory().addItem(topInventory.getItem(12));
        	            }
        	            if(isInventoryFull(player) && topInventory.getItem(13) != null){
        	            	world.dropItemNaturally(location, topInventory.getItem(13));
        	            } else if (topInventory.getItem(13) != null) {
        	            	player.getInventory().addItem(topInventory.getItem(13));
        	            }
        	            if(isInventoryFull(player) && topInventory.getItem(14) != null){
        	            	world.dropItemNaturally(location, topInventory.getItem(14));
        	            } else if (topInventory.getItem(14) != null) {
        	            	player.getInventory().addItem(topInventory.getItem(14));
        	            }
        			}
        		}
        	}.runTaskLater(HCF.getPlugin(), 2l);
          /*  Location location = player.getLocation();
            World world = player.getWorld();
            if(isInventoryFull(player) && topInventory.getItem(12) != null){
            	world.dropItemNaturally(location, topInventory.getItem(12));
            } else if (topInventory.getItem(12) != null) {
            	player.getInventory().addItem(topInventory.getItem(12));
            }
            if(isInventoryFull(player) && topInventory.getItem(13) != null){
            	world.dropItemNaturally(location, topInventory.getItem(13));
            } else if (topInventory.getItem(13) != null) {
            	player.getInventory().addItem(topInventory.getItem(13));
            }
            if(isInventoryFull(player) && topInventory.getItem(14) != null){
            	world.dropItemNaturally(location, topInventory.getItem(14));
            } else if (topInventory.getItem(14) != null) {
            	player.getInventory().addItem(topInventory.getItem(14));
            } */
       /* }
    }*/
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (inventory != null && topInventory != null && topInventory.equals(inventory) && topInventory.getTitle().endsWith(" Crate")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if(topInventory.getTitle().endsWith(" key:")){
        	event.setCancelled(true);
        }
        if (clickedInventory == null || topInventory == null || !topInventory.getTitle().endsWith(" Crate")) {
            return;
        }   
        event.setCancelled(true);
    }
    
    private void decrementHand(Player player) {
        ItemStack stack = player.getItemInHand();
        if (stack.getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
        else {
            stack.setAmount(stack.getAmount() - 1);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void openPreview(PlayerInteractEvent e){
    	 Player player = e.getPlayer();
         Action action = e.getAction();
         if (action != Action.LEFT_CLICK_BLOCK) {
        	 return;
         }
         Block block = e.getClickedBlock();
         if(block.getType() != Material.ENDER_CHEST){
        	 return;
         }
         for(Key key : HCF.getPlugin().getKeyManager().getKeys()){
        	 if(key instanceof EnderChestKey){
        		 EnderChestKey ekey = (EnderChestKey) key;
        		 if(ekey.getLocation().getBlockX() == block.getLocation().getBlockX() && ekey.getLocation().getBlockZ() == block.getLocation().getBlockZ()){
        			 Inventory i = Bukkit.createInventory(null, 54, key.getName() + " key:");
        			 for(int in = 0; in < ekey.getLoot().length; in++){
        				 ItemStack it = ekey.getLoot()[in];
        				 i.addItem(it);
        			 }
        			 player.openInventory(i);
        		 }
        	 }
         }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack stack = event.getItem();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Key key = this.plugin.getKeyManager().getKey(stack);
        if (key == null) {
            return;
        }
        Block block = event.getClickedBlock();
        if (key instanceof EnderChestKey && block.getType() == Material.ENDER_CHEST) {
        	 for(Key kk : HCF.getPlugin().getKeyManager().getKeys()){
            	 if(kk instanceof EnderChestKey){
            		 EnderChestKey ekey = (EnderChestKey) key;
            		 if(ekey.getLocation().getBlockX() == block.getLocation().getBlockX() && ekey.getLocation().getBlockZ() == block.getLocation().getBlockZ()){
			            InventoryView openInventory = player.getOpenInventory();
			            Inventory topInventory = openInventory.getTopInventory();
			            if (topInventory != null && topInventory.getTitle().endsWith(" Crate")) {
			                return;
			            }
			            EnderChestKey enderChestKey = (EnderChestKey)key;
			            if(enderChestKey.getLocation().getBlockX() != event.getClickedBlock().getLocation().getBlockX() ||
			            		enderChestKey.getLocation().getBlockZ() != event.getClickedBlock().getLocation().getBlockZ()){
			            	player.sendMessage("§cCette caisse semble pas vouloir s'ouvrir... peut-être n'as tu pas cliqué sur la bonne ?");
			            	return;
			            }
			            int rolls = enderChestKey.getRolls();
			            int size = InventoryUtils.getSafestInventorySize(27);
			            Inventory inventory = Bukkit.createInventory((InventoryHolder)player, size, enderChestKey.getName() + " Crate");
			            ItemStack[] loot = enderChestKey.getLoot();
			            if (loot == null) {
			                player.sendMessage(ChatColor.RED + "Cette clé n'a pas de loot défini, merci de contacter un admin.");
			                return;
			            }
			            List<ItemStack> finalLoot = new ArrayList<ItemStack>();
			            Random random = this.plugin.getRandom();
			            for (int i = 0; i < rolls; ++i) {
			                ItemStack item = loot[random.nextInt(loot.length)];
			                if (item != null && item.getType() != Material.AIR) {
			                    finalLoot.add(item);
			                    inventory.setItem(i, item);
			                }
			            }
			            player.openInventory(inventory);
			            Location location = block.getLocation();
			            CrateListener.open.put(event.getPlayer().getName(), new CrateTask.OpenCrate());   
			            CrateTask.startTask(event.getClickedBlock().getLocation(), event.getPlayer());
			            player.playSound(location, Sound.LEVEL_UP, 1.0f, 1.0f);
			            this.decrementHand(player);
			            event.setCancelled(true);
			            return;
            		 }
            	 }
        	 }
        }
    }
}
