package fr.taeron.hcf;

import org.bukkit.plugin.java.*;

import com.sk89q.worldedit.bukkit.*;

import fr.taeron.hcf.pvpclass.*;
import fr.taeron.hcf.scoreboard.*;

import org.bukkit.scheduler.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
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
import org.bukkit.event.*;
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
import org.bukkit.*;
import org.bukkit.command.*;
import fr.taeron.hcf.deathban.*;
import fr.taeron.hcf.economy.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

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
    private KeyManager keyManager;
    private DeathbanManager deathbanManager;
    private EconomyManager economyManager;
    private EotwHandler eotwHandler;
    private FactionManager factionManager;
    private PvpClassManager pvpClassManager;
    private ScoreboardHandler scoreboardHandler;
    private TimerManager timerManager;
    private UserManager userManager;
    private VisualiseHandler visualiseHandler;
    private NpcManager npcManager;
    private NpcPlayerHelper npcplayerhelper;
    private PlayerCache playerCache;
    private PlayerManager playerManager;
    public ProtocolManager protocolManager;
    private KitManager kitManager;
      
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
        this.scoreboardHandler.clearBoards();
        this.foundDiamondsListener.saveConfig();
        this.saveData();
        HCF.plugin = null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerConfiguration() {
        ConfigurationSerialization.registerClass((Class)CaptureZone.class);
        ConfigurationSerialization.registerClass((Class)Deathban.class);
        ConfigurationSerialization.registerClass((Class)Claim.class);
        ConfigurationSerialization.registerClass((Class)Subclaim.class);
        ConfigurationSerialization.registerClass((Class)Deathban.class);
        ConfigurationSerialization.registerClass((Class)FactionUser.class);
        ConfigurationSerialization.registerClass((Class)ClaimableFaction.class);
        ConfigurationSerialization.registerClass((Class)ConquestFaction.class);
        ConfigurationSerialization.registerClass((Class)CapturableFaction.class);
        ConfigurationSerialization.registerClass((Class)KothFaction.class);
        ConfigurationSerialization.registerClass((Class)EndPortalFaction.class);
        ConfigurationSerialization.registerClass((Class)GlowMountainFaction.class);
        ConfigurationSerialization.registerClass((Class)Faction.class);
        ConfigurationSerialization.registerClass((Class)FactionMember.class);
        ConfigurationSerialization.registerClass((Class)PlayerFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.class);
        ConfigurationSerialization.registerClass((Class)SpawnFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class)RoadFaction.WestRoadFaction.class);
    }
    
	private void registerListeners() {
        final PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents((Listener)new ArcherClass(this), (Plugin)this);
        manager.registerEvents((Listener)new PortalTrapFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new AutoSmeltOreListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockHitFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockJumpGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BoatGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new BookDeenchantListener(), (Plugin)this);
        manager.registerEvents((Listener)new BorderListener(), (Plugin)this);
        manager.registerEvents((Listener)new BottledExpListener(), (Plugin)this);
        manager.registerEvents((Listener)new ChatListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ClaimWandListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CombatLogListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CoreListener(this), (Plugin)this);
        manager.registerEvents((Listener)new CrowbarListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathMessageListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new DeathbanListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EnchantLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new EnderChestRemovalListener(), (Plugin)this);
        manager.registerEvents((Listener)new EntityLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new FlatFileFactionManager(this), (Plugin)this);
        manager.registerEvents((Listener)new EndListener(), (Plugin)this);
        manager.registerEvents((Listener)new EotwListener(this), (Plugin)this);
        manager.registerEvents((Listener)new EventSignListener(), (Plugin)this);
        manager.registerEvents((Listener)new ExpMultiplierListener(), (Plugin)this);
        manager.registerEvents((Listener)new FactionListener(this), (Plugin)this);
        manager.registerEvents((Listener)(this.foundDiamondsListener = new FoundDiamondsListener(this)), (Plugin)this);
        manager.registerEvents((Listener)new InfinityArrowFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new KitListener(this), (Plugin)this);
        manager.registerEvents((Listener)new KitMapListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ServerSecurityListener(), (Plugin)this);
        manager.registerEvents((Listener)new HungerFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new PearlGlitchListener(), (Plugin)this);
        manager.registerEvents((Listener)new PortalListener(this), (Plugin)this);
        manager.registerEvents((Listener)new PotionLimitListener(), (Plugin)this);
        manager.registerEvents((Listener)new ProtectionListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SignSubclaimListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ShopSignListener(this), (Plugin)this);
        manager.registerEvents((Listener)new SkullListener(), (Plugin)this);
        manager.registerEvents((Listener)new BeaconStrengthFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new VoidGlitchFixListener(), (Plugin)this);
        manager.registerEvents((Listener)new WallBorderListener(this), (Plugin)this);
        manager.registerEvents((Listener)new WorldListener(this), (Plugin)this);
        manager.registerEvents((Listener)new ExpListener(), (Plugin)this);
        manager.registerEvents((Listener)new ElevatorListener(), (Plugin)this);
        manager.registerEvents((Listener)new BlockBreakListener(), (Plugin)this);
        manager.registerEvents(new WeatherFixListener(), this);
        manager.registerEvents(new FactionClaimArgument(this), this);
        manager.registerEvents(new TrackerListener(), this);
        manager.registerEvents(new FlipListener(), this);
        manager.registerEvents(new LivesListener(), this);
        manager.registerEvents(new ComboKnockbackListener(), this);
        manager.registerEvents(new MobTargetListener(), this);
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
	
	/*public void scheduleAutoRestart(){
		new BukkitRunnable(){
			public void run(){
				final Calendar calendar = Calendar.getInstance();
		        calendar.setTimeInMillis(System.currentTimeMillis());
		        final int mYear = calendar.get(1);
		        final int mMonth = calendar.get(2) + 1;
		        final int mDay = calendar.get(5);
		        final int mHour = calendar.get(11);
		        final int mMin = calendar.get(12);
		        final int mSec = calendar.get(13);
		        if(mHour == 2 )
			}
		}.runTaskTimer(this, 20L, 20L);
		
	}*/
	
	
    
    private void registerCommands() {
        this.getCommand("angle").setExecutor((CommandExecutor)new AngleCommand());
        this.getCommand("conquest").setExecutor((CommandExecutor)new ConquestExecutor(this));
        this.getCommand("crowbar").setExecutor((CommandExecutor)new CrowbarCommand());
        this.getCommand("economy").setExecutor((CommandExecutor)new EconomyCommand(this));
        this.getCommand("eotw").setExecutor((CommandExecutor)new EotwCommand(this));
        this.getCommand("game").setExecutor((CommandExecutor)new EventExecutor(this));
        this.getCommand("help").setExecutor((CommandExecutor)new HelpCommand());
        this.getCommand("faction").setExecutor((CommandExecutor)new FactionExecutor(this));
        this.getCommand("gopple").setExecutor((CommandExecutor)new GoppleCommand(this));
        this.getCommand("koth").setExecutor((CommandExecutor)new KothExecutor(this));
        this.getCommand("lives").setExecutor((CommandExecutor)new LivesExecutor(this));
        this.getCommand("location").setExecutor((CommandExecutor)new LocationCommand(this));
        this.getCommand("logout").setExecutor((CommandExecutor)new LogoutCommand(this));
        this.getCommand("mapkit").setExecutor((CommandExecutor)new MapKitCommand(this));
        this.getCommand("pay").setExecutor((CommandExecutor)new PayCommand(this));
        this.getCommand("pvptimer").setExecutor((CommandExecutor)new PvpTimerCommand(this));
        this.getCommand("refund").setExecutor((CommandExecutor)new RefundCommand());
        this.getCommand("servertime").setExecutor((CommandExecutor)new ServerTimeCommand());
        this.getCommand("spawn").setExecutor((CommandExecutor)new SpawnCommand(this));
        this.getCommand("timer").setExecutor((CommandExecutor)new TimerExecutor(this));
        this.getCommand("togglecapzone").setExecutor((CommandExecutor)new ToggleCapzoneCommand(this));
        this.getCommand("togglelightning").setExecutor((CommandExecutor)new ToggleLightningCommand(this));
        this.getCommand("reclaim").setExecutor((CommandExecutor)new ReclaimCommand());
        this.getCommand("togglecobblestone").setExecutor((CommandExecutor)new ToggleCobblestoneCommand());
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
    	this.playerManager = new PlayerManager();
        this.claimHandler = new ClaimHandler(this);
        this.deathbanManager = new FlatFileDeathbanManager(this);
        this.economyManager = new FlatFileEconomyManager(this);
        this.eotwHandler = new EotwHandler(this);
        this.eventScheduler = new EventScheduler(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.pvpClassManager = new PvpClassManager(this);
        this.timerManager = new TimerManager(this);
        this.scoreboardHandler = new ScoreboardHandler(this);
        this.userManager = new UserManager(this);
        this.visualiseHandler = new VisualiseHandler();
        this.getCommand("setborder").setExecutor((CommandExecutor)new SetBorderCommand());
        this.keyManager = new KeyManager(this);
        this.getServer().getPluginManager().registerEvents((Listener)new KeyListener(this), (Plugin)this);
        this.getCommand("key").setExecutor((CommandExecutor)new KeyExecutor(this));
        this.npcManager = new NpcManager(this);
        this.npcplayerhelper = new NpcPlayerHelperImpl();
        NpcNameGeneratorFactory.setNameGenerator(new NpcNameGeneratorImpl(this));
        this.playerCache = new PlayerCache();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.kitManager = new FlatFileKitManager(this);
    }
    
    
    public PacketContainer buildTeamPacket(final String name, final String display, final String prefix, final String suffix, final int flag, final String members) {
        final PacketContainer packet = this.protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getIntegers().write(0, flag);
        packet.getStrings().write(0, name).write(1, display).write(2, prefix).write(3, suffix);
        packet.getSpecificModifier(Collection.class).write(0, Arrays.asList(members));
        return packet;
    }
    
    public Random getRandom() {
        return this.random;
    }
    
    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }
    
    public KeyManager getKeyManager() {
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
    
    public ScoreboardHandler getScoreboardHandler() {
        return this.scoreboardHandler;
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
    
    public PlayerManager getPlayerManager(){
    	return this.playerManager;
    }
    
    static {
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
    }
}
