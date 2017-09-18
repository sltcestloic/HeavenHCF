package fr.taeron.hcf;

import org.bukkit.plugin.java.*;

import com.sk89q.worldedit.bukkit.*;

import fr.taeron.hcf.pvpclass.*;

import org.bukkit.scheduler.*;
import org.heavenmc.core.Core;
import org.heavenmc.core.scoreboard.ScoreboardManager;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.*;

import fr.taeron.hcf.user.*;
import fr.taeron.hcf.events.*;
import fr.taeron.hcf.events.EventExecutor;
import fr.taeron.hcf.events.conquest.*;
import fr.taeron.hcf.events.eotw.*;
import fr.taeron.hcf.events.factions.*;
import fr.taeron.hcf.events.koth.*;
import fr.taeron.hcf.faction.type.*;
import fr.taeron.hcf.flip.FlipCommand;
import fr.taeron.hcf.flip.FlipListener;
import fr.taeron.hcf.kits.FlatFileKitManager;
import fr.taeron.hcf.kits.KitExecutor;
import fr.taeron.hcf.kits.KitManager;
import fr.taeron.hcf.pvpclass.archer.*;
import fr.taeron.hcf.faction.claim.*;
import fr.taeron.hcf.listeners.*;
import fr.taeron.hcf.listeners.fixes.*;
import fr.taeron.hcf.visualise.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.plugin.*;

import fr.taeron.hcf.faction.*;
import fr.taeron.hcf.faction.argument.FactionClaimArgument;
import fr.taeron.hcf.deathban.lives.*;
import fr.taeron.hcf.timer.*;
import fr.taeron.hcf.tracker.TrackerExecutor;
import fr.taeron.hcf.tracker.TrackerListener;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.*;
import org.bukkit.command.*;
import fr.taeron.hcf.deathban.*;
import fr.taeron.hcf.economy.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import fr.taeron.hcf.combatlog.CombatLogListener;
import fr.taeron.hcf.combatlog.NpcManager;
import fr.taeron.hcf.combatlog.NpcNameGeneratorFactory;
import fr.taeron.hcf.combatlog.NpcNameGeneratorImpl;
import fr.taeron.hcf.combatlog.NpcPlayerHelper;
import fr.taeron.hcf.combatlog.NpcPlayerHelperImpl;
import fr.taeron.hcf.combatlog.PlayerCache;
import fr.taeron.hcf.command.*;
import fr.taeron.hcf.crate.*;

public class HCF extends JavaPlugin{
	
    private static final long MINUTE;
    private static final long HOUR;
    private static HCF plugin;
    public EventScheduler eventScheduler;
    private Random random;
    private WorldEditPlugin worldEdit;
    private FoundDiamondsListener foundDiamondsListener;
    private ClaimHandler claimHandler;
    private CrateManager keyManager;
    private DeathbanManager deathbanManager;
    private EconomyManager economyManager;
    private EotwHandler eotwHandler;
    private FactionManager factionManager;
    private PvpClassManager pvpClassManager;
    private TimerManager timerManager;
    private UserManager userManager;
    private VisualiseHandler visualiseHandler;
    private NpcManager npcManager;
    private NpcPlayerHelper npcplayerhelper;
    private PlayerCache playerCache;
    public ProtocolManager protocolManager;
    private KitManager kitManager;
    private HCFSQLManager sqlManager;
    private ScoreboardManager scoreboardManager;
    private CompatUserManager compatManager;
      
    public HCF() {
        this.random = new Random();
    }
    
    public static HCF getPlugin() {
        return HCF.plugin;
    }
    
    public static String getRemaining(final long millis, final boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    
    public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail) {
        if (milliseconds && duration < HCF.MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= HCF.HOUR) ? "HH:" : "") + "mm:ss");
    }

    
    
    public void onEnable() {
        HCF.plugin = this;
        ProtocolLibHook.hook(this);
        final Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = ((WorldEditPlugin)wep);
        this.registerConfiguration();
        this.registerCommands();
        this.registerManagers();
        this.registerListeners();
        Cooldowns.createCooldown("Assassin_item_cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown");
        this.runAutoSave();
        this.loadKnockbacks();
        new BukkitRunnable(){
        	public void run(){
        		GlowstoneMountain.CubeReset();
        	}
        }.runTaskLater(this, 200L);
    }
    
    private void loadKnockbacks(){
    	File f = new File("knockbacks.yml");
    	if(!f.exists()){
    		return;
    	} 
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
    	SetKnockbackCommand.xz = config.getDouble("xz");
    	SetKnockbackCommand.y = config.getDouble("y");
    }
    
    private void saveData() {
        this.deathbanManager.saveDeathbanData();
        this.economyManager.saveEconomyData();
        this.factionManager.saveFactionData();
        this.keyManager.saveKeyData();
        this.timerManager.saveTimerData();
        this.userManager.saveUserData();
        this.kitManager.saveKitData();
        Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "§aSauvegarde automatique des factions effectuée avec succès.");
    }
    
	public void onDisable() {
        this.pvpClassManager.onDisable();
        this.foundDiamondsListener.saveConfig();
        this.saveData();
        HCF.plugin = null;
    }
    
	private void registerConfiguration() {
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(GlowMountainFaction.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
    }
    
	private void registerListeners() {
        final PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ArcherClass(this), this);
        manager.registerEvents(new PortalTrapFixListener(), this);
        manager.registerEvents(new AutoSmeltOreListener(), this);
        manager.registerEvents(new BlockHitFixListener(), this);
        manager.registerEvents(new BlockJumpGlitchFixListener(), this);
        manager.registerEvents(new BoatGlitchFixListener(), this);
        manager.registerEvents(new BookDeenchantListener(), this);
        manager.registerEvents(new BorderListener(), this);
        manager.registerEvents(new BottledExpListener(), this);
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(new CombatLogListener(this), this);
        manager.registerEvents(new CoreListener(this), this);
        manager.registerEvents(new CrowbarListener(this), this);
        manager.registerEvents(new DeathListener(this), this);
        manager.registerEvents(new DeathMessageListener(this), this);
        manager.registerEvents(new DeathSignListener(this), this);
        manager.registerEvents(new DeathbanListener(this), this);
        manager.registerEvents(new EnchantLimitListener(), this);
        manager.registerEvents(new EnderChestRemovalListener(), this);
        manager.registerEvents(new EntityLimitListener(), this);
        manager.registerEvents(new FlatFileFactionManager(this), this);
        manager.registerEvents(new EndListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new EventSignListener(), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents((this.foundDiamondsListener = new FoundDiamondsListener(this)), this);
        manager.registerEvents(new InfinityArrowFixListener(), this);
        manager.registerEvents(new KitListener(this), this);
        manager.registerEvents(new ServerSecurityListener(), this);
        manager.registerEvents(new HungerFixListener(), this);
        manager.registerEvents(new PearlGlitchListener(), this);
        manager.registerEvents(new PortalListener(this), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new ProtectionListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new ShopSignListener(this), this);
        manager.registerEvents(new SkullListener(), this);
        manager.registerEvents(new BeaconStrengthFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(), this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(new WorldListener(this), this);
        manager.registerEvents(new ExpListener(), this);
        manager.registerEvents(new ElevatorListener(), this);
        manager.registerEvents(new BlockBreakListener(), this);
        manager.registerEvents(new WeatherFixListener(), this);
        manager.registerEvents(new FactionClaimArgument(this), this);
        manager.registerEvents(new TrackerListener(), this);
        manager.registerEvents(new FlipListener(), this);
        manager.registerEvents(new LivesListener(), this);
        manager.registerEvents(new KnockbackListener(), this);
        manager.registerEvents(new MobTargetListener(), this);
        manager.registerEvents(new TabListener(), this);
    }
	
	public void runAutoSave(){
		new BukkitRunnable(){
			public void run(){
				HCF.this.foundDiamondsListener.saveConfig();
				HCF.this.saveData();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			}
		}.runTaskTimerAsynchronously(this, 20, 18000);
	}
	
    
    private void registerCommands() {
        this.getCommand("angle").setExecutor(new AngleCommand());
        this.getCommand("conquest").setExecutor(new ConquestExecutor(this));
        this.getCommand("crowbar").setExecutor(new CrowbarCommand());
        this.getCommand("economy").setExecutor(new EconomyCommand(this));
        this.getCommand("eotw").setExecutor(new EotwCommand(this));
        this.getCommand("game").setExecutor(new EventExecutor(this));
        this.getCommand("help").setExecutor(new HelpCommand());
        this.getCommand("faction").setExecutor(new FactionExecutor(this));
        this.getCommand("gopple").setExecutor(new GoppleCommand(this));
        this.getCommand("koth").setExecutor(new KothExecutor(this));
        this.getCommand("lives").setExecutor(new LivesExecutor(this));
        this.getCommand("location").setExecutor(new LocationCommand(this));
        this.getCommand("logout").setExecutor(new LogoutCommand(this));
        this.getCommand("mapkit").setExecutor(new MapKitCommand(this));
        this.getCommand("pay").setExecutor(new PayCommand(this));
        this.getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        this.getCommand("refund").setExecutor(new RefundCommand());
        this.getCommand("servertime").setExecutor(new ServerTimeCommand());
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("timer").setExecutor(new TimerExecutor(this));
        this.getCommand("togglecapzone").setExecutor(new ToggleCapzoneCommand(this));
        this.getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
        this.getCommand("reclaim").setExecutor(new ReclaimCommand());
        this.getCommand("togglecobblestone").setExecutor(new ToggleCobblestoneCommand());
        this.getCommand("ores").setExecutor(new OresCommand());
        this.getCommand("tracker").setExecutor(new TrackerExecutor());
        this.getCommand("flip").setExecutor(new FlipCommand());
        this.getCommand("kb").setExecutor(new SetKnockbackCommand());
        this.getCommand("coords").setExecutor(new CoordsCommand());
        this.getCommand("ts").setExecutor(new TeamspeakCommand());
        this.getCommand("spawner").setExecutor(new SpawnerCommand());
        this.getCommand("glowstonemountain").setExecutor(new GlowstoneMountainCommand());
        this.getCommand("kit").setExecutor(new KitExecutor(this));
        final Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>)this.getDescription().getCommands();
        for (final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = this.getCommand((String)entry.getKey());
            command.setPermission("command." + entry.getKey());
            command.setPermissionMessage(ChatColor.RED + "Tu n'as pas la permission.");
        }
    }
    
    private void registerManagers() {
        this.claimHandler = new ClaimHandler(this);
        this.deathbanManager = new FlatFileDeathbanManager(this);
        this.economyManager = new FlatFileEconomyManager(this);
        this.eotwHandler = new EotwHandler(this);
        this.eventScheduler = new EventScheduler(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.pvpClassManager = new PvpClassManager(this);
        this.timerManager = new TimerManager(this);
        this.userManager = new UserManager(this);
        this.visualiseHandler = new VisualiseHandler();
        this.getCommand("setborder").setExecutor(new SetBorderCommand());
        this.keyManager = new CrateManager(this);
        this.getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        this.getCommand("key").setExecutor(new KeyExecutor(this));
        this.npcManager = new NpcManager(this);
        this.npcplayerhelper = new NpcPlayerHelperImpl();
        NpcNameGeneratorFactory.setNameGenerator(new NpcNameGeneratorImpl(this));
        this.playerCache = new PlayerCache();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.kitManager = new FlatFileKitManager(this);
        this.sqlManager = new HCFSQLManager(this);
        this.scoreboardManager = new ScoreboardManager(this, "§6§lHeavenHCF §e[Map 4]");
        this.scoreboardManager.runTaskTimerAsynchronously(this, 0, 20);
        this.compatManager = new CompatUserManager();
    }
    
    public ScoreboardManager getScoreboardManager(){
    	return this.scoreboardManager;
    }
    
    /*public PacketContainer buildTeamPacket(final String name, final String display, final String prefix, final String suffix, final int flag, final String members) {
        final PacketContainer packet = this.protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, flag);
        packet.getStrings().write(0, name).write(1, display).write(2, prefix).write(3, suffix);
        packet.getSpecificModifier(Collection.class).write(0, Arrays.asList(members));
        return packet;
    }*/
    
    public int getPlayerId(UUID u){
		int playerid = 0;
		java.sql.Connection c = Core.getInstance().getConnection();
		PreparedStatement s;
		try {
			s = c.prepareStatement("SELECT playerid FROM `players` WHERE uuid = ?");
			s.setString(1, u.toString());
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				playerid = rs.getInt("playerid");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerid;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }
    
    public CrateManager getKeyManager() {
        return this.keyManager;
    }
    
    public ClaimHandler getClaimHandler() {
        return this.claimHandler;
    }
    
    public DeathbanManager getDeathbanManager() {
        return this.deathbanManager;
    }
    
    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }
    
    public EotwHandler getEotwHandler() {
        return this.eotwHandler;
    }
    
    public FactionManager getFactionManager() {
        return this.factionManager;
    }
    
    public PvpClassManager getPvpClassManager() {
        return this.pvpClassManager;
    }
    
    public TimerManager getTimerManager() {
        return this.timerManager;
    }
    
    public UserManager getUserManager() {
        return this.userManager;
    }
    
    public VisualiseHandler getVisualiseHandler() {
        return this.visualiseHandler;
    }
    
    public NpcManager getNpcManager() {
        return this.npcManager;
    }
    
    public NpcPlayerHelper getNpcPlayerHelper(){
    	return this.npcplayerhelper;
    }
    
    public PlayerCache getPlayerCache(){
    	return this.playerCache;
    }
    
    public KitManager getKitManager(){
    	return this.kitManager;
    }
    
    public HCFSQLManager getSQLManager(){
    	return this.sqlManager;
    }
    
    public CompatUserManager getCompatUserManager(){
    	return this.compatManager;
    }
    
    static {
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
    }
}
