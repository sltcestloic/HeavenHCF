package fr.taeron.hcf.kits;

import org.bukkit.plugin.*;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.util.*;
import org.heavenmc.core.util.Config;
import org.heavenmc.core.util.GenericUtils;

import com.google.common.collect.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.events.*;

public class FlatFileKitManager implements KitManager, Listener{
	
    private final HashMap<String, Kit> kitNameMap = new HashMap<String, Kit>();
    private final Map<UUID, Kit> kitUUIDMap;
    private final HCF plugin;
    private Config config;
    private List<Kit> kits;
    
    public FlatFileKitManager(final HCF plugin) {
        this.kits = new ArrayList<Kit>();
        this.kitUUIDMap = new HashMap<UUID, Kit>();
        this.plugin = plugin;
        this.reloadKitData();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitRename(final KitRenameEvent event) {
        this.kitNameMap.remove(event.getOldName());
        this.kitNameMap.put(event.getNewName(), event.getKit());
    }
    
    @Override
    public List<Kit> getKits() {
        return this.kits;
    }
    
    @Override
    public Kit getKit(final UUID uuid) {
        return this.kitUUIDMap.get(uuid);
    }
    
    @Override
    public Kit getKit(final String id) {
        return this.kitNameMap.get(id);
    }
    
    @Override
    public boolean containsKit(final Kit kit) {
        return this.kits.contains(kit);
    }
    
    @Override
    public void createKit(final Kit kit) {
        if (this.kits.add(kit)) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }
    
    @Override
    public void removeKit(final Kit kit) {
        if (this.kits.remove(kit)) {
            this.kitNameMap.remove(kit.getName());
            this.kitUUIDMap.remove(kit.getUniqueID());
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public Inventory getGui(final Player player) {
        final UUID uuid = player.getUniqueId();
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, (this.kits.size() + 9 - 1) / 9 * 9, ChatColor.BLUE + "Selecteur de kit");
        for (final Kit kit : this.kits) {
            final ItemStack stack = kit.getImage();
            final String description = kit.getDescription();
            final String kitPermission = kit.getPermissionNode();
            List<String> lore;
            if (kitPermission == null || player.hasPermission(kitPermission)) {
                lore = new ArrayList<String>();
                if (kit.getDelayMillis() > 0L) {
                	lore.add(ChatColor.YELLOW + "Cooldown de " + kit.getDelayWords().replace("seconds", "secondes"));
                }
                else {
                    lore.add(ChatColor.RED + "Désactivé");
                }
                final int maxUses = kit.getMaximumUses();
                if (maxUses != Integer.MAX_VALUE) {
                    lore.add(ChatColor.YELLOW + "Utilisé " + this.plugin.getUserManager().getUser(uuid).getKitUses(kit) + '/' + maxUses + " fois.");
                }
                if (description != null) {
                    lore.add(" ");
                    for (final String part : ChatPaginator.wordWrap(description, 24)) {
                        lore.add(ChatColor.WHITE + part);
                    }
                }
            }
            else {
                lore = Lists.newArrayList(new String[] { ChatColor.RED + "Tu ne possèdes pas ce kit." });
            }
            final ItemStack cloned = stack.clone();
            final ItemMeta meta = cloned.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + kit.getName());
            meta.setLore((List)lore);
            cloned.setItemMeta(meta);
            inventory.addItem(new ItemStack[] { cloned });
        }
        return inventory;
    }
    
    @Override
    public void reloadKitData() {
        this.config = new Config(this.plugin, "kits");
        final Object object = this.config.get("kits");
        if (object instanceof List) {
            this.kits = GenericUtils.createList(object, Kit.class);
            for (final Kit kit : this.kits) {
                this.kitNameMap.put(kit.getName(), kit);
                this.kitUUIDMap.put(kit.getUniqueID(), kit);
            }
        }
    }
    
    @Override
    public void saveKitData() {
        this.config.set("kits", (Object)this.kits);
        this.config.save();
    }
}
