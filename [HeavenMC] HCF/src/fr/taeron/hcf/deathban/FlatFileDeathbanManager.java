package fr.taeron.hcf.deathban;

import net.minecraft.util.gnu.trove.map.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;

import org.bukkit.plugin.java.*;
import org.heavenmc.core.util.Config;
import org.heavenmc.core.util.PersistableLocation;
import org.bukkit.configuration.*;
import net.minecraft.util.gnu.trove.map.hash.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FlatFileDeathbanManager implements DeathbanManager
{
    private final HCF plugin;
    private TObjectIntMap<UUID> livesMap;
    private Config livesConfig;
    
    public FlatFileDeathbanManager(final HCF plugin) {
        this.plugin = plugin;
        this.reloadDeathbanData();
    }
    
    @Override
    public TObjectIntMap<UUID> getLivesMap() {
        return this.livesMap;
    }
    
    @Override
    public int getLives(final UUID uuid) {
        return this.livesMap.get((Object)uuid);
    }
    
    @Override
    public int setLives(final UUID uuid, final int lives) {
        this.livesMap.put(uuid, lives);
        return lives;
    }
    
    @Override
    public int addLives(final UUID uuid, final int amount) {
        return this.livesMap.adjustOrPutValue(uuid, amount, amount);
    }
    
    @Override
    public int takeLives(final UUID uuid, final int amount) {
        return this.setLives(uuid, this.getLives(uuid) - amount);
    }
    
    @Override
    public double getDeathBanMultiplier(final Player player) {
        if (player.hasPermission("hcf.deathban.extra")) {
            for (int i = 5; i < 21600; --i) {
                if (player.hasPermission("hcf.deathban.seconds." + i)) {
                    return i / 1000;
                }
            }
        }
        return ConfigurationService.DEFAULT_DEATHBAN_DURATION;
    }
    
    @Override
    public Deathban applyDeathBan(final Player player, final String reason) {
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
        if (!factionAt.isDeathban()) {
            duration /= 2L;
        }
        if (player.hasPermission("heaven")) {
            duration = TimeUnit.MINUTES.toMillis(5L);
        }
        else if (player.hasPermission("master")) {
            duration = TimeUnit.MINUTES.toMillis(15L);
        }
        else if (player.hasPermission("hero")) {
            duration = TimeUnit.MINUTES.toMillis(30L);
        }
        else if (player.hasPermission("elite")) {
            duration = TimeUnit.MINUTES.toMillis(45L);
        }
        else if (player.hasPermission("premium")) {
            duration = TimeUnit.MINUTES.toMillis(60L);
        }
        return this.applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(FlatFileDeathbanManager.MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
    }
    
    @Override
    public Deathban applyDeathBan(final UUID player, final String reason) {
        long duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
        return this.applyDeathBan(player, new Deathban(reason, Math.min(FlatFileDeathbanManager.MAX_DEATHBAN_TIME, duration), new PersistableLocation(Bukkit.getWorlds().get(0).getSpawnLocation())));
    }
    
    @Override
    public Deathban applyDeathBan(final UUID uuid, final Deathban deathban) {
        this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void reloadDeathbanData() {
        this.livesConfig = new Config((JavaPlugin)this.plugin, "lives");
        final Object object = this.livesConfig.get("lives");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            this.livesMap = new TObjectIntHashMap(keys.size(), 0.5f, 0);
            for (final String id : keys) {
                this.livesMap.put(UUID.fromString(id), this.livesConfig.getInt(section.getCurrentPath() + "." + id));
            }
        }
        else {
            this.livesMap = new TObjectIntHashMap(10, 0.5f, 0);
        }
    }
    
    @Override
    public void saveDeathbanData() {
        final Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
        this.livesMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.livesConfig.set("lives", (Object)saveMap);
        this.livesConfig.save();
    }
}
