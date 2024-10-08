package fr.taeron.hcf.faction.claim;

import org.bukkit.configuration.serialization.*;
import org.heavenmc.core.util.GenericUtils;
import org.heavenmc.core.util.cuboid.Cuboid;
import org.heavenmc.core.util.cuboid.NamedCuboid;
import org.apache.commons.collections4.map.*;
import org.bukkit.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.*;
import java.util.*;

public class Claim extends NamedCuboid implements Cloneable, ConfigurationSerializable{
	
    private static final Random RANDOM;
    @SuppressWarnings("rawtypes")
	private final Map subclaims;
    private final UUID claimUniqueID;
    private final UUID factionUUID;
    private Faction faction;
    private boolean loaded;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Claim(final Map map) {
        super(map);
        this.subclaims = new CaseInsensitiveMap();
        this.loaded = false;
        this.name = (String) map.get("name");
        this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
        this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
        for (final Subclaim subclaim : GenericUtils.createList(map.get("subclaims"), Subclaim.class)) {
            this.subclaims.put(subclaim.getName(), subclaim);
        }
    }
    
    @SuppressWarnings("rawtypes")
	public Claim(final Faction faction, final Location location) {
        super(location, location);
        this.subclaims = new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    @SuppressWarnings("rawtypes")
	public Claim(final Faction faction, final Location location1, final Location location2) {
        super(location1, location2);
        this.subclaims = new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    @SuppressWarnings("rawtypes")
	public Claim(final Faction faction, final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        super(world, x1, y1, z1, x2, y2, z2);
        this.subclaims = new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    @SuppressWarnings("rawtypes")
	public Claim(final Faction faction, final Cuboid cuboid) {
        super(cuboid);
        this.subclaims = new CaseInsensitiveMap();
        this.loaded = false;
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> serialize() {
        final Map<String, Object> map = (Map<String, Object>)super.serialize();
        map.put("name", this.name);
        map.put("claimUUID", this.claimUniqueID.toString());
        map.put("factionUUID", this.factionUUID.toString());
        map.put("subclaims", new ArrayList(this.subclaims.values()));
        return map;
    }
    
    private String generateName() {
        return String.valueOf(Claim.RANDOM.nextInt(899) + 100);
    }
    
    public UUID getClaimUniqueID() {
        return this.claimUniqueID;
    }
    
    public ClaimableFaction getFaction() {
        if (!this.loaded && this.faction == null) {
            this.faction = HCF.getPlugin().getFactionManager().getFaction(this.factionUUID);
            this.loaded = true;
        }
        return (this.faction instanceof ClaimableFaction) ? ((ClaimableFaction)this.faction) : null;
    }
    
    @SuppressWarnings("unchecked")
	public Collection<Subclaim> getSubclaims() {
        return this.subclaims.values();
    }
    
    public Subclaim getSubclaim(final String name) {
        return (Subclaim) this.subclaims.get(name);
    }
    
    public String getFormattedName() {
        return this.getName() + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - (" + this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
    }
    
    public Claim clone() {
        return (Claim)super.clone();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Claim blocks = (Claim)o;
        if (this.loaded != blocks.loaded) {
            return false;
        }
        Label_0080: {
            if (this.subclaims != null) {
                if (this.subclaims.equals(blocks.subclaims)) {
                    break Label_0080;
                }
            }
            else if (blocks.subclaims == null) {
                break Label_0080;
            }
            return false;
        }
        Label_0116: {
            if (this.claimUniqueID != null) {
                if (this.claimUniqueID.equals(blocks.claimUniqueID)) {
                    break Label_0116;
                }
            }
            else if (blocks.claimUniqueID == null) {
                break Label_0116;
            }
            return false;
        }
        Label_0152: {
            if (this.factionUUID != null) {
                if (this.factionUUID.equals(blocks.factionUUID)) {
                    break Label_0152;
                }
            }
            else if (blocks.factionUUID == null) {
                break Label_0152;
            }
            return false;
        }
        if (this.faction != null) {
            if (!this.faction.equals(blocks.faction)) {
                return false;
            }
        }
        else if (blocks.faction != null) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        int result = (this.subclaims != null) ? this.subclaims.hashCode() : 0;
        result = 31 * result + ((this.claimUniqueID != null) ? this.claimUniqueID.hashCode() : 0);
        result = 31 * result + ((this.factionUUID != null) ? this.factionUUID.hashCode() : 0);
        result = 31 * result + ((this.faction != null) ? this.faction.hashCode() : 0);
        result = 31 * result + (this.loaded ? 1 : 0);
        return result;
    }
    
    static {
        RANDOM = new Random();
    }
}
