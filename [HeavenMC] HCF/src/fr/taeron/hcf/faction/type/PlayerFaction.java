package fr.taeron.hcf.faction.type;

import fr.taeron.hcf.faction.*;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.event.cause.*;
import org.bukkit.event.*;
import org.heavenmc.core.util.BukkitUtils;
import org.heavenmc.core.util.GenericUtils;
import org.heavenmc.core.util.JavaUtils;
import org.heavenmc.core.util.PersistableLocation;
import org.bukkit.command.*;
import org.bukkit.*;
import fr.taeron.hcf.timer.type.*;
import javax.annotation.*;
import fr.taeron.hcf.faction.struct.*;
import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.event.*;
import fr.taeron.hcf.user.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import fr.taeron.hcf.deathban.*;
import com.google.common.base.*;
import com.google.common.base.Objects;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.*;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class PlayerFaction extends ClaimableFaction implements Raidable{
	
    private static final UUID[] EMPTY_UUID_ARRAY;
    protected final Map requestedRelations;
    protected final Map relations;
    protected final Map members;
    protected final Set<String> invitedPlayerNames;
    protected PersistableLocation home;
    protected String announcement;
    protected boolean open;
    protected int balance;
    protected double deathsUntilRaidable;
    protected long regenCooldownTimestamp;
    private long lastDtrUpdateTimestamp;
    private PlayerFaction focusing;
    
    
	public PlayerFaction(final String name) {
        super(name);
        this.requestedRelations = new HashMap();
        this.relations = new HashMap();
        this.members = new HashMap();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
        this.focusing = null;
    }
    
    public PlayerFaction(final Map map) {
        super(map);
        this.requestedRelations = new HashMap();
        this.relations = new HashMap();
        this.members = new HashMap();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
        for (final Map.Entry entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
            this.members.put(UUID.fromString((String) entry.getKey()), entry.getValue());
        }
        this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));
        Object object2 = map.get("home");
        if (object2 != null) {
            this.home = (PersistableLocation)object2;
        }
        object2 = map.get("announcement");
        if (object2 != null) {
            this.announcement = (String)object2;
        }
        for (final Map.Entry entry3 : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
            this.relations.put(UUID.fromString((String) entry3.getKey()), Relation.valueOf((String) entry3.getValue()));
        }
        for (final Map.Entry entry3 : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
            this.requestedRelations.put(UUID.fromString((String) entry3.getKey()), Relation.valueOf((String) entry3.getValue()));
        }
        this.open = (boolean)map.get("open");
        this.balance = (int)map.get("balance");
        this.deathsUntilRaidable = (double)map.get("deathsUntilRaidable");
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
        this.focusing = (PlayerFaction) map.get("focusedFaction");
    }
    
    @Override
    public Map serialize() {
        final Map map = super.serialize();
        final HashMap relationSaveMap = new HashMap(this.relations.size());
        for (final Object entrySet : this.relations.entrySet()) {
            relationSaveMap.put(((Entry) entrySet).getKey().toString(), ((Enum<Relation>) ((Entry) entrySet).getValue()).name());
        }
        map.put("relations", relationSaveMap);
        final HashMap requestedRelationsSaveMap2 = new HashMap(this.requestedRelations.size());
        for (final Object saveMap : this.requestedRelations.entrySet()) {
            requestedRelationsSaveMap2.put(((Entry) saveMap).getKey().toString(), ((Enum<Relation>) ((Entry) saveMap).getValue()).name());
        }
        map.put("requestedRelations", requestedRelationsSaveMap2);
        final Set entrySet3 = this.members.entrySet();
        final LinkedHashMap saveMap2 = new LinkedHashMap(this.members.size());
        for (final Object entry : entrySet3) {
            saveMap2.put(((Entry) entry).getKey().toString(), ((Entry) entry).getValue());
        }
        map.put("members", saveMap2);
        map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));
        if (this.home != null) {
            map.put("home", this.home);
        }
        if (this.announcement != null) {
            map.put("announcement", this.announcement);
        }
        map.put("open", this.open);
        map.put("balance", this.balance);
        map.put("deathsUntilRaidable", this.deathsUntilRaidable);
        map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
        map.put("focusedFaction", this.focusing);
        return map;
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember, final boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }
    
    private boolean setMember(final Player player, final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        if (factionMember == null) {
            if (!force) {
                final PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            final PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent(event2);
            return true;
        }
        final PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent((Event)eventPre);
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }
    
    public Collection<UUID> getAllied() {
        return Maps.filterValues(this.relations, (Predicate)new Predicate<Relation>() {
            public boolean apply(final Relation relation) {
                return relation == Relation.ALLY;
            }
        }).keySet();
    }
    
    public PlayerFaction getFocusedFaction() {
    	return this.focusing;
    }
    
    public void setFocusedFaction(PlayerFaction f){
    	this.focusing = f;
    }
    
    public List<PlayerFaction> getAlliedFactions() {
        final Collection<UUID> allied = this.getAllied();
        final Iterator<UUID> iterator = allied.iterator();
        final List<PlayerFaction> results = new ArrayList<PlayerFaction>(allied.size());
        while (iterator.hasNext()) {
            final Faction faction = HCF.getPlugin().getFactionManager().getFaction(iterator.next());
            if (faction instanceof PlayerFaction) {
                results.add((PlayerFaction)faction);
            }
            else {
                iterator.remove();
            }
        }
        return results;
    }
    
    public Map<UUID, Relation> getRequestedRelations() {
        return (Map<UUID, Relation>)this.requestedRelations;
    }
    
    public Map<UUID, Relation> getRelations() {
        return (Map<UUID, Relation>)this.relations;
    }
    
    public Map<UUID, FactionMember> getMembers() {
        return (Map<UUID, FactionMember>)ImmutableMap.copyOf(this.members);
    }
    
    public Set getOnlinePlayers() {
        return this.getOnlinePlayers(null);
    }
    
    public Set getOnlinePlayers(final CommandSender sender) {
        final Set entrySet = this.getOnlineMembers(sender).entrySet();
        final HashSet results = new HashSet(entrySet.size());
        for (final Object entry : entrySet) {
            results.add(Bukkit.getPlayer((UUID)((Entry) entry).getKey()));
        }
        return results;
    }
    
    public Map getOnlineMembers() {
        return this.getOnlineMembers(null);
    }
    
    public Map<UUID, FactionMember> getOnlineMembers(final CommandSender sender) {
        if(sender instanceof Player){
        	
        	Player senderPlayer = (Player) sender;
        	 final HashMap<UUID, FactionMember> results = new HashMap<UUID, FactionMember>();
             for (final Object entry : this.members.entrySet()) {
                 final Player target = Bukkit.getPlayer((UUID)((Entry) entry).getKey());
                 if (target == null) {
                     continue;
                 }
                 if (senderPlayer != null && !senderPlayer.canSee(target)) {
                     continue;
                 }
                 results.put(((Entry<UUID,FactionMember>) entry).getKey(), (FactionMember) ((Entry) entry).getValue());
             }
             return results;
         }
        return members;
       }
       
    
    public FactionMember getLeader() {
        final Map<UUID, FactionMember> members = (Map<UUID, FactionMember>)this.members;
        final Iterator<Map.Entry<UUID, FactionMember>> iterator = members.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, FactionMember> entry;
            if ((entry = iterator.next()).getValue().getRole() == Role.LEADER) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Deprecated
    public FactionMember getMember(final String memberName) {
        final UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        final FactionMember factionMember = (FactionMember) this.members.get(uuid);
        return factionMember;
    }
    
    public FactionMember getMember(final Player player) {
        return this.getMember(player.getUniqueId());
    }
    
    public FactionMember getMember(final UUID memberUUID) {
        return (FactionMember) this.members.get(memberUUID);
    }
    
    public Set<String> getInvitedPlayerNames() {
        return this.invitedPlayerNames;
    }
    
    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }
    
    public void setHome(final Location home) {
        if (home == null && this.home != null) {
            final TeleportTimer timer = HCF.getPlugin().getTimerManager().teleportTimer;
            for (final Player player : Bukkit.getOnlinePlayers()) {
                final Location destination = (Location)timer.getDestination(player);
                if (Objects.equal(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Ton f home a été supprimé, ton " + timer.getDisplayName() + ChatColor.RED + " timer a été annulé.");
                }
            }
        }
        this.home = ((home == null) ? null : new PersistableLocation(home));
    }
    
    public String getAnnouncement() {
        return this.announcement;
    }
    
    public void setAnnouncement(@Nullable final String announcement) {
        this.announcement = announcement;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public int getBalance() {
        return this.balance;
    }
    
    public void setBalance(final int balance) {
        this.balance = balance;
    }
    
    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }
    
    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }
    
    @Override
    public double getMaximumDeathsUntilRaidable() {
        if (this.members.size() == 1) {
            return 1.1;
        }
        return Math.min(5.0, this.members.size() * 0.9);
    }
    
    public double getDeathsUntilRaidable(final boolean updateLastCheck) {
        if (updateLastCheck) {
            this.updateDeathsUntilRaidable();
        }
        return this.deathsUntilRaidable;
    }
    
    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();
        if (this.deathsUntilRaidable < 0.0) {
            return ChatColor.RED;
        }
        if (this.deathsUntilRaidable < 1.0) {
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }
    
    private void updateDeathsUntilRaidable() {
        if (this.getRegenStatus() == RegenStatus.REGENERATING) {
            final long now = System.currentTimeMillis();
            final long millisPassed = now - this.lastDtrUpdateTimestamp;
            if (millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES) {
                final long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES;
                final int multiplier = (int)((millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
                final double increase = multiplier * 0.1;
                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }
    
    @Override
    public double setDeathsUntilRaidable(final double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }
    
    private double setDeathsUntilRaidable(double deathsUntilRaidable, final boolean limit) {
        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if (limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if (deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            final FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if (deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    HCF.getPlugin().getLogger().info("La faction " + this.getName() + " est désormais raidable.");
                }
                this.lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }
    
    protected long getRegenCooldownTimestamp() {
        return this.regenCooldownTimestamp;
    }
    
    @Override
    public long getRemainingRegenerationTime() {
        return (this.regenCooldownTimestamp == 0L) ? 0L : (this.regenCooldownTimestamp - System.currentTimeMillis());
    }
    
    @Override
    public void setRemainingRegenerationTime(final long millis) {
        final long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;
        this.lastDtrUpdateTimestamp = systemMillis + ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2L;
    }
    
    @Override
    public RegenStatus getRegenStatus() {
        if (this.getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        }
        if (this.getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        }
        return RegenStatus.FULL;
    }
    
    public void printStats(final CommandSender sender) {
        Integer combinedKills = 0;
        Integer combinedDiamonds = 0;
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        while (this.members.entrySet().iterator().hasNext()) {
            final Map.Entry entry = (Map.Entry)this.members.entrySet().iterator().next();
            final FactionUser user = HCF.getPlugin().getUserManager().getUser((UUID) entry.getKey());
            final int kills = user.getKills();
            combinedKills += kills;
            final int diamonds = user.getDiamondsMined();
            combinedDiamonds += diamonds;
        }
        sender.sendMessage(ChatColor.YELLOW + "  Kills: " + ChatColor.GRAY + combinedKills);
        sender.sendMessage(ChatColor.YELLOW + "  Diamants minés: " + ChatColor.GRAY + combinedDiamonds);
    }
    
    @Override
    public void printDetails(final CommandSender sender) {
        String leaderName = null;
        final HashSet allyNames = new HashSet(1);
        for (Object memberNames : this.relations.entrySet())
        {
          Faction captainNames = HCF.getPlugin().getFactionManager().getFaction((UUID)((Map.Entry)memberNames).getKey());
          if ((captainNames instanceof PlayerFaction))
          {
            PlayerFaction playerFaction = (PlayerFaction)captainNames;
            allyNames.add(playerFaction.getDisplayName(sender) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlineMembers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
          }
        }
        int combinedKills2 = 0;
        final HashSet memberNames2 = new HashSet();
        final HashSet<String> captainNames2 = new HashSet<String>();
        for (final Object entry : this.members.entrySet()) {
            final FactionMember factionMember = (FactionMember) ((Entry) entry).getValue();
            final Player target = factionMember.toOnlinePlayer();
            final FactionUser user = HCF.getPlugin().getUserManager().getUser((UUID) ((Entry) entry).getKey());
            final int kills = user.getKills();
            combinedKills2 += kills;
            final Deathban deathban = user.getDeathban();
            ChatColor colour = (deathban != null && deathban.isActive()) ? ChatColor.RED : ((target == null || (sender instanceof Player && !((Player)sender).canSee(target))) ? ChatColor.GRAY : ChatColor.GREEN);
            if (deathban != null && deathban.isActive()) {
                colour = ChatColor.RED;
            }
            else if (target != null && (!(sender instanceof Player) || ((Player)sender).canSee(target))) {
                colour = ChatColor.GREEN;
            }
            else {
                colour = ChatColor.GRAY;
            }
            final String memberName = colour + factionMember.getName() + ChatColor.GRAY + '[' + ChatColor.GRAY + kills + ChatColor.GRAY + ']';
            memberNames2.add(memberName);
            if (factionMember.getRole() == Role.CAPTAIN) {
                captainNames2.add(memberName);
            }
            for (final String members : captainNames2) {
                memberNames2.remove(members);
            }
            final Role role = factionMember.getRole();
            factionMember.getRole();
            if (role == Role.LEADER) {
                leaderName = memberName;
            }
            if (memberNames2.contains(leaderName)) {
                memberNames2.remove(leaderName);
            }
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.AQUA + " " + this.getDisplayName(sender) + ChatColor.GRAY + "[" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + "] " + ChatColor.YELLOW + "Home: " + ChatColor.WHITE + ((this.home == null) ? "Non défini" : (ChatColor.WHITE.toString() + this.home.getLocation().getBlockX() + " | " + this.home.getLocation().getBlockZ())));
        if (!allyNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Alliés: " + StringUtils.join((Iterable)allyNames, ChatColor.GRAY + ", "));
        }
        if (leaderName != null) {
            sender.sendMessage(ChatColor.YELLOW + "  Chef: " + ChatColor.RED + leaderName);
        }
        if (!captainNames2.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Officiers: " + ChatColor.RED + StringUtils.join((Iterable)captainNames2, ChatColor.GRAY + ", "));
        }
        if (!memberNames2.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Membres: " + ChatColor.RED + StringUtils.join((Iterable)memberNames2, ChatColor.GRAY + ", "));
        }
        sender.sendMessage(ChatColor.YELLOW + "  Banque: " + ChatColor.GREEN + '$' + this.balance + ChatColor.YELLOW + ", Kills: " + ChatColor.GREEN + combinedKills2);
        sender.sendMessage(ChatColor.YELLOW + "  DTR: " + ChatColor.YELLOW + " [" + this.getRegenStatus().getSymbol() + this.getDtrColour() + JavaUtils.format((Number)this.getDeathsUntilRaidable(false)) + ChatColor.YELLOW + '/' + JavaUtils.format((Number)this.getMaximumDeathsUntilRaidable()) + ']');
        final long dtrRegenRemaining = this.getRemainingRegenerationTime();
        if (dtrRegenRemaining > 0L) {
            sender.sendMessage(ChatColor.YELLOW + "  Temps de régeneration: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public void broadcast(final String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String message, @Nullable final UUID... ignore) {
        this.broadcast(new String[] { message }, ignore);
    }
    
	@SuppressWarnings("unused")
	public void broadcast(final String[] messages, final UUID... ignore) {
        Preconditions.checkNotNull(messages, "Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, "Message array cannot be empty");
        final Collection<Player> players = this.getOnlinePlayers();
        final Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        if(this.getOnlinePlayers() != null){
	        for (final Player player : players) {
	        	if(player != null && messages != null){
	        		player.sendMessage(messages);      
	        	}
	        }
        }
    }
    
    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }
}
