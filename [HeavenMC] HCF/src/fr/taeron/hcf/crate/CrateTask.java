package fr.taeron.hcf.crate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.heavenmc.core.util.ItemBuilder;

import fr.taeron.hcf.HCF;

public class CrateTask {

	public HashMap<Integer, Integer> speeds;
	
	private static void scroll(final Inventory inventory, final int n, final ItemStack itemStack) {
        for (int i = 10; i < n - 1; ++i) {
            inventory.setItem(i, inventory.getItem(i + 1));
        }
        inventory.setItem(n - 1, itemStack);
    }
	
	public static void startTask(final Location location, final Player player) {
        final ArrayList<ItemStack> crateRands = getCrateRand(player.getOpenInventory().getTopInventory());
        if (crateRands == null) {
            player.sendMessage("§cImpossible d'ouvrir la crate, contacte un administrateur.");
            CrateListener.open.remove(player.getName());
            player.closeInventory();
            return;
        }
        final Inventory inventory = player.getOpenInventory().getTopInventory();
        final OpenCrate openCrate = CrateListener.open.get(player.getName());
        player.openInventory(inventory);
        inventory.setItem(3, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(4, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(5, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(21, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(22, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(23, new ItemBuilder(Material.REDSTONE_TORCH_ON).displayName("§6Récompense").build());
        inventory.setItem(10, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(11, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(12, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(13, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(14, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(15, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        inventory.setItem(16, (ItemStack)crateRands.get(randInt(0, crateRands.size() - 1)));
        player.updateInventory();
        final Location add = location.add(new Vector(0.5, 0.5, 0.5));
        CrateTask.firework(add);
        new BukkitRunnable() {
            int step = 0;
            long startTime = System.currentTimeMillis();
            
            public void run() {
                final long n = System.currentTimeMillis() - this.startTime;
                if (this.step == 0) {
                    if (n < 6000L) {
                        CrateTask.glassRainbow(inventory, true);
                        openCrate.check();
                        if (openCrate.should()) {
                            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0f, 1.0f);
                            scroll(inventory, 17, crateRands.get(randInt(0, crateRands.size() - 1)));
                        }
                    }
                    else if (n >= 6500L) {
                        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.0f);
                        inventory.setItem(10, new ItemStack(Material.AIR, 1));
                        inventory.setItem(11, new ItemStack(Material.AIR, 1));
                        inventory.setItem(15, new ItemStack(Material.AIR, 1));
                        inventory.setItem(16, new ItemStack(Material.AIR, 1));
                        ++this.step;
                    }
                }
                else if (this.step == 1) {
                    if (n >= 9000L) {
                        ++this.step;
                        CrateTask.firework(add);
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                    }
                    else {
                    	CrateTask.glassRainbow(inventory, false);
                    }
                }
                if (this.step >= 2) {
                    this.cancel();
                }
            }
        }.runTaskTimer(HCF.getPlugin(), 1L, 1L);
    }
	
	
	public static int randInt(int min, int max) {

	    // NOTE: This will (intentionally) not run as written so that folks
	    // copy-pasting have to think about how to initialize their
	    // Random instance.  Initialization of the Random instance is outside
	    // the main scope of the question, but some decent options are to have
	    // a field that is initialized once and then re-used as needed or to
	    // use ThreadLocalRandom (if using at least Java 1.7).
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	private static void glassRainbow(final Inventory inventory, final boolean b) {
        final RandomCollection<Integer> collection = new RandomCollection<Integer>();
        collection.add(50.0, 2);
        collection.add(50.0, 4);
        collection.add(50.0, 5);
        collection.add(50.0, 1);
        collection.add(50.0, 3);
        collection.add(50.0, 6);
        final int intValue = collection.next();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (i != 10 && i != 11 && i != 12 && i != 13 && i != 14 && i != 15 && i != 16 && i != 3 && i != 5 && i != 21 && i != 23 && i != 4 && i != 22) {
                if (b) {
                    inventory.setItem(i, getItem((Material.matchMaterial("STAINED_GLASS_PANE") == null) ? Material.WOOL : Material.STAINED_GLASS_PANE, collection.next(), " ", new String[0]));
                }
                else {
                    inventory.setItem(i, getItem((Material.matchMaterial("STAINED_GLASS_PANE") == null) ? Material.WOOL : Material.STAINED_GLASS_PANE, intValue, " ", new String[0]));
                }
            }
        }
    }
	
    public static class RandomCollection<E> {
        private final NavigableMap<Double, E> map;
        private final Random random;
        private double total;
        
        public RandomCollection() {
            this(new Random());
        }
        
        public RandomCollection(final Random random) {
            this.map = new TreeMap<Double, E>();
            this.total = 0.0;
            this.random = random;
        }
        
        public void add(final double n, final E e) {
            if (n <= 0.0) {
                return;
            }
            this.total += n;
            this.map.put(this.total, e);
        }
        
        public E next() {
            return this.map.ceilingEntry(this.random.nextDouble() * this.total).getValue();
        }
    }
    
    private static ArrayList<ItemStack> getCrateRand(Inventory i){
    	final ArrayList<ItemStack> collection = new ArrayList<ItemStack>();
    	String s = i.getTitle().replace(" Crate", "");
    	for(Key key : HCF.getPlugin().getKeyManager().getKeys()){
    		if(key.getName().equalsIgnoreCase(s)){
    			EnderChestKey eKey = (EnderChestKey) key;
    			for(int intt = 0; intt < eKey.getLoot().length; intt++){
    				collection.add(eKey.getLoot()[intt]);
    			}
    		}
    	}
    	return collection;
    }
    

    public static void firework(final Location location) {
        final Firework firework = (Firework)location.getWorld().spawn(location, Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(getRandomEffect());
        firework.setFireworkMeta(fireworkMeta);
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(HCF.getPlugin(), 2L);
    }
    
    public static FireworkEffect getRandomEffect() {
        final Random random = new Random();
        final int n = random.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (n == 2) {
            type = FireworkEffect.Type.BALL_LARGE;
        }
        if (n == 3) {
            type = FireworkEffect.Type.BURST;
        }
        if (n == 4) {
            type = FireworkEffect.Type.CREEPER;
        }
        if (n == 5) {
            type = FireworkEffect.Type.STAR;
        }
        return FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(random.nextInt(17))).withFade(getColor(random.nextInt(17))).with(type).trail(random.nextBoolean()).build();
    }
    
    private static Color getColor(final int n) {
        Color color = null;
        if (n == 0) {
            color = Color.AQUA;
        }
        if (n == 1) {
            color = Color.BLACK;
        }
        if (n == 2) {
            color = Color.BLUE;
        }
        if (n == 3) {
            color = Color.FUCHSIA;
        }
        if (n == 4) {
            color = Color.GRAY;
        }
        if (n == 5) {
            color = Color.GREEN;
        }
        if (n == 6) {
            color = Color.LIME;
        }
        if (n == 7) {
            color = Color.MAROON;
        }
        if (n == 8) {
            color = Color.NAVY;
        }
        if (n == 9) {
            color = Color.OLIVE;
        }
        if (n == 10) {
            color = Color.ORANGE;
        }
        if (n == 11) {
            color = Color.PURPLE;
        }
        if (n == 12) {
            color = Color.RED;
        }
        if (n == 13) {
            color = Color.SILVER;
        }
        if (n == 14) {
            color = Color.TEAL;
        }
        if (n == 15) {
            color = Color.WHITE;
        }
        if (n == 16) {
            color = Color.YELLOW;
        }
        if (n == 17) {
            color = Color.TEAL;
        }
        return color;
    }
    
    public ItemStack getItem(final Material material, final String displayName, final String... array) {
        final ItemStack itemStack = new ItemStack(material, 1);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(Arrays.asList(array));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static ItemStack getItem(final Material material, final int n, final String displayName, final String... array) {
        final ItemStack itemStack = new ItemStack(material, 1, (short)n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(Arrays.asList(array));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static class OpenCrate
    {
        private Random random;
        private int rspeed;
        private int sb;
        private int loops;
        private int maxloops;
        private HashMap<Integer, Integer> speeds;
        
        public OpenCrate() {
            this.random = new Random();
            this.rspeed = 1;
            this.sb = 0;
            this.loops = 0;
            this.maxloops = 100;
            (this.speeds = new HashMap<Integer, Integer>()).put(0, 1);
            final double n = 1.3;
            final double n2 = 12.3;
            this.maxloops *= (int)n;
            for (int i = 1; i <= 40; ++i) {
                int n3 = (int)(i + this.random.nextInt(i) / 1.5);
                final int n4 = (int)(n3 * n);
                if (i == 20) {
                    n3 *= (int)0.85;
                }
                else if (i == 30) {
                    n3 *= (int)0.65;
                }
                else if (i == 40) {
                    n3 *= (int)0.45;
                }
                this.speeds.put((int)(n3 * n2), n4);
            }
        }
        
        public boolean should() {
            ++this.loops;
            ++this.sb;
            if (this.speeds.get(this.loops) != null) {
                this.rspeed = this.speeds.get(this.loops);
            }
            if (this.sb >= this.rspeed) {
                this.sb = 0;
                return true;
            }
            return false;
        }
        
        public void check() {
            if (this.loops >= this.maxloops) {
                this.rspeed += 2;
            }
        }
    }
}
