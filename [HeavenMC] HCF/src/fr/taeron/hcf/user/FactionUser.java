package fr.taeron.hcf.user;

import org.bukkit.configuration.serialization.*;

import fr.taeron.hcf.deathban.*;
import fr.taeron.hcf.kits.Kit;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.gnu.trove.procedure.TObjectIntProcedure;
import net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure;

import java.util.stream.*;
import com.google.common.collect.*;

import java.util.*;
import org.bukkit.entity.*;
import org.heavenmc.core.util.GenericUtils;
import org.bukkit.*;

public class FactionUser implements ConfigurationSerializable  {
	
    private Set<UUID> factionChatSpying;
    private Set<String> shownScoreboardScores;
    private UUID userUUID;
    private boolean capzoneEntryAlerts;
    private boolean showClaimMap;
    private boolean showLightning;
    private Deathban deathban;
    private long lastFactionLeaveMillis;
    private int kills;
    private int deaths;
    private int healthBrewed;
    private Player trackingUser;
    private long trackingStartMillis;
    public long lastLogoutTime;
    public boolean hasUsedFlip;
    private String trackerUserName;
    public int diamonds;
    public int iron;
    public int gold;
    public int redstone;
    public int lapis;
    public int emerald;
    public int coal;
    private TObjectIntMap<UUID> kitUseMap;
    private TObjectLongMap<UUID> kitCooldownMap;    
    private int id;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public FactionUser(UUID userUUID) {
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        this.userUUID = userUUID;
        this.trackingUser = null;
        this.trackingStartMillis = 1482431367981L;
        this.hasUsedFlip = false;
        this.lastLogoutTime = System.currentTimeMillis();
        this.diamonds = 0;
        this.iron = 0;
        this.gold = 0;
        this.redstone = 0;
        this.lapis = 0;
        this.emerald = 0;
        this.coal = 0;
        this.kills = 0;
        this.deaths = 0;
        this.kitUseMap = new TObjectIntHashMap();
        this.kitCooldownMap = new TObjectLongHashMap();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public FactionUser(Map<String, Object> map) {   
    	this.kitUseMap = new TObjectIntHashMap();
        this.kitCooldownMap = new TObjectLongHashMap();
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        this.shownScoreboardScores.addAll(GenericUtils.createList(map.get("shownScoreboardScores"), (Class)String.class));
        this.factionChatSpying.addAll((Collection<? extends UUID>)GenericUtils.createList(map.get("faction-chat-spying"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.capzoneEntryAlerts = (boolean) map.get("capzoneEntryAlerts");
        this.showLightning = (boolean) map.get("showLightning");
        this.deathban = (Deathban) map.get("deathban");
        this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
        this.healthBrewed = (int) map.get("brewed");
        this.kills = (int) map.get("kills");
        this.deaths = (int) map.get("deaths");
        this.lastLogoutTime = (long) map.get("lastLogoutTime");
        this.hasUsedFlip = (boolean) map.get("hasUsedFlip");
        this.diamonds = (int) map.get("diamonds");
        this.gold = (int) map.get("gold");
        this.iron = (int) map.get("iron");
        this.redstone = (int) map.get("redstone");
        this.lapis = (int) map.get("lapis");
        this.coal = (int) map.get("coal");
        this.emerald = (int) map.get("emerald");
        for (Map.Entry<String, Integer> entry : GenericUtils.castMap(map.get("kit-use-map"), String.class, Integer.class).entrySet()) {
            this.kitUseMap.put(UUID.fromString(entry.getKey()), (int)entry.getValue());
        }
        for (Map.Entry<String, String> entry2 : GenericUtils.castMap(map.get("kit-cooldown-map"), String.class, String.class).entrySet()) {
            this.kitCooldownMap.put(UUID.fromString(entry2.getKey()), Long.parseLong(entry2.getValue()));
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("shownScoreboardScores", new ArrayList(this.shownScoreboardScores));
        map.put("faction-chat-spying", this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("userUUID", this.userUUID.toString());
        map.put("brewed", this.healthBrewed);
        map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
        map.put("showClaimMap", this.showClaimMap);
        map.put("showLightning", this.showLightning);
        map.put("deathban", this.deathban);
        map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        map.put("lastLogoutTime", this.lastLogoutTime);
        map.put("hasUsedFlip", this.hasUsedFlip);
        map.put("diamonds", this.diamonds);
        map.put("gold", this.gold);
        map.put("iron", this.iron);
        map.put("redstone", this.redstone);
        map.put("lapis", this.lapis);
        map.put("coal", this.coal);
        map.put("emerald", this.emerald);
        Map<String, Integer> kitUseSaveMap = new HashMap<String, Integer>(this.kitUseMap.size());
        this.kitUseMap.forEachEntry((uuid, value) -> {
            kitUseSaveMap.put(uuid.toString(), value);
            return true;
        });
        new TObjectIntProcedure<UUID>() {
            public boolean execute(UUID uuid, int value) {
                kitUseSaveMap.put(uuid.toString(), value);
                return true;
            }
        };
        Map<String, String> kitCooldownSaveMap = new HashMap<String, String>(this.kitCooldownMap.size());
        this.kitCooldownMap.forEachEntry((uuid, value) -> {
            kitCooldownSaveMap.put(uuid.toString(), Long.toString(value));
            return true;
        });
        new TObjectLongProcedure<UUID>() {
            public boolean execute(UUID uuid, long value) {
                kitCooldownSaveMap.put(uuid.toString(), Long.toString(value));
                return true;
            }
        };
        map.put("kit-use-map", kitUseSaveMap);
        map.put("kit-cooldown-map", kitCooldownSaveMap);
        map.put("id", this.id);
        return map;
    }

    public long getRemainingKitCooldown(Kit kit) {
        long remaining = this.kitCooldownMap.get(kit.getUniqueID());
        if (remaining == this.kitCooldownMap.getNoEntryValue()) {
            return 0L;
        }
        return remaining - System.currentTimeMillis();
    }
    
    public void updateKitCooldown(Kit kit) {
        this.kitCooldownMap.put(kit.getUniqueID(), System.currentTimeMillis() + kit.getDelayMillis());
    }
    
    public int getKitUses(Kit kit) {
        int result = this.kitUseMap.get(kit.getUniqueID());
        return (result == this.kitUseMap.getNoEntryValue()) ? 0 : result;
    }
    
    public int incrementKitUses(Kit kit) {
        return this.kitUseMap.adjustOrPutValue(kit.getUniqueID(), 1, 1);
    }
    
    public void setTrackedUser(Player p){
    	this.trackingUser = p;
    	this.trackerUserName = p.getName();
    	if(p != null){
    		this.trackingStartMillis = System.currentTimeMillis();
    	}
    }
    
    public int getDeaths(){
    	return this.deaths;
    }
    
    public void setDeaths(int i){
    	this.deaths = i;
    }
    
    public long getTrackingStartTime(){
    	return this.trackingStartMillis;
    }
    
    public Player getTrackingUser(){
    	return this.trackingUser;
    }
    
    public String getTrackingUserName(){
    	return this.trackerUserName;
    }
    
    public boolean isTrackingPlayer(){
    	return this.trackingUser != null;
    }
    
    public boolean isCapzoneEntryAlerts() {
        return this.capzoneEntryAlerts;
    }
    
    public void setCapzoneEntryAlerts(boolean capzoneEntryAlerts) {
        this.capzoneEntryAlerts = capzoneEntryAlerts;
    }
    
    public boolean isShowClaimMap() {
        return this.showClaimMap;
    }
    
    public void setShowClaimMap(boolean showClaimMap) {
        this.showClaimMap = showClaimMap;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getHealthBrewed() {
        return this.healthBrewed;
    }
    
    public void setHealthBrewed(int healthBrewed) {
        this.healthBrewed = healthBrewed;
    }
    
    public Deathban getDeathban() {
        return this.deathban;
    }
    
    public void setDeathban(Deathban deathban) {
        this.deathban = deathban;
    }
    
    public void removeDeathban() {
        this.deathban = null;
    }
    
    public long getLastFactionLeaveMillis() {
        return this.lastFactionLeaveMillis;
    }
    
    public void setLastFactionLeaveMillis(long lastFactionLeaveMillis) {
        this.lastFactionLeaveMillis = lastFactionLeaveMillis;
    }
    
    public boolean isShowLightning() {
        return this.showLightning;
    }
    
    public void setShowLightning(boolean showLightning) {
        this.showLightning = showLightning;
    }
    
    public Set<UUID> getFactionChatSpying() {
        return this.factionChatSpying;
    }
    
    public Set<String> getShownScoreboardScores() {
        return this.shownScoreboardScores;
    }
    
    public UUID getUserUUID() {
        return this.userUUID;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(this.userUUID);
    }
}
