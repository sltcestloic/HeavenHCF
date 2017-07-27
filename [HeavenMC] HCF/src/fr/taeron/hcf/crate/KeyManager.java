package fr.taeron.hcf.crate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.heavenmc.core.util.Config;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.crate.type.AquaKey;
import fr.taeron.hcf.crate.type.HeavenKey;
import fr.taeron.hcf.crate.type.KillerKey;
import fr.taeron.hcf.crate.type.KothKey;
import fr.taeron.hcf.crate.type.MasterKey;
import fr.taeron.hcf.crate.type.StarterKey;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Sets;
import net.minecraft.util.com.google.common.collect.Table;

public class KeyManager{
	
    private KothKey kothKey;
    private StarterKey lootKey;
    private MasterKey legendaryKey;
    private HeavenKey heavenKey;
    private AquaKey aquaKey;
    private Table<UUID, String, Integer> depositedCrateMap;
    private Set<Key> keys;
    private Config config;
    private KillerKey killerKey;
    
    public KeyManager(HCF plugin) {
        this.depositedCrateMap = HashBasedTable.create();
        this.config = new Config(plugin, "key-data");
        this.keys = Sets.newHashSet(new Key[] { this.killerKey = new KillerKey(), this.aquaKey = new AquaKey(), this.lootKey = new StarterKey(), this.legendaryKey = new MasterKey(), this.kothKey = new KothKey(), this.heavenKey = new HeavenKey() });
        this.reloadKeyData();
    }
    
    public Map<String, Integer> getDepositedCrateMap(UUID uuid) {
        return this.depositedCrateMap.row(uuid);
    }
    
    public Set<Key> getKeys() {
        return this.keys;
    }
    
    public KillerKey getKillerKey(){
    	return this.killerKey;
    }
    
    public HeavenKey getHeavenKey() {
        return this.heavenKey;
    }
    
    public KothKey getEventKey() {
        return this.kothKey;
    }
    
    public AquaKey getAquaKey(){
    	return this.aquaKey;
    }
    
    public StarterKey getLootKey() {
        return this.lootKey;
    }
    
    public MasterKey getLegendaryKey() {
        return this.legendaryKey;
    }
    
    public Key getKey(String name) {
        for (Key key : this.keys) {
            if (key.getName().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }
    
    @Deprecated
    public Key getKey(Class<? extends Key> clazz) {
        for (Key key : this.keys) {
            if (clazz.isAssignableFrom(key.getClass())) {
                return key;
            }
        }
        return null;
    }
    
    public Key getKey(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }
        for (Key key : this.keys) {
            ItemStack item = key.getItemStack();
            if (item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
                return key;
            }
        }
        return null;
    }
    
    public void reloadKeyData() {
        for (Key key : this.keys) {
            key.load(this.config);
        }
        Object object = this.config.get("deposited-key-map");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            for (String id : section.getKeys(false)) {
                object = this.config.get(section.getCurrentPath() + '.' + id);
                if (object instanceof MemorySection) {
                    section = (MemorySection)object;
                    for (String key2 : section.getKeys(false)) {
                        this.depositedCrateMap.put(UUID.fromString(id), key2, this.config.getInt("deposited-key-map." + id + '.' + key2));
                    }
                }
            }
        }
    }
    
    public void saveKeyData() {
        for (Key key : this.keys) {
            key.save(this.config);
        }
        Map<String, Map<String, Integer>> saveMap = new LinkedHashMap<String, Map<String, Integer>>(this.depositedCrateMap.size());
        for (Map.Entry<UUID, Map<String, Integer>> entry : this.depositedCrateMap.rowMap().entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.config.set("deposited-key-map", (Object)saveMap);
        this.config.save();
    }
}
