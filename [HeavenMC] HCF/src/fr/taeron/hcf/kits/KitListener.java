package fr.taeron.hcf.kits;

import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.heavenmc.core.Core;
import org.heavenmc.core.util.ParticleEffect;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.events.*;
import fr.taeron.hcf.user.FactionUser;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.block.*;

import java.util.*;

public class KitListener implements Listener{
	
    private HCF plugin;
    
    public KitListener(HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) {
            return;
        }
        String title = inventory.getTitle();
        if (title.contains("Kit Preview")) {
            event.setCancelled(true);
        }
        HumanEntity humanEntity = event.getWhoClicked();
        if (title.contains("Selecteur de kit") && humanEntity instanceof Player) {
            event.setCancelled(true);
            if (!Objects.equals(event.getView().getTopInventory(), event.getClickedInventory())) {
                return;
            }
            ItemStack stack = event.getCurrentItem();
            if (stack == null || !stack.hasItemMeta()) {
                return;
            }
            ItemMeta meta = stack.getItemMeta();
            if (!meta.hasDisplayName()) {
                return;
            }
            Player player = (Player)humanEntity;
            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            Kit kit = this.plugin.getKitManager().getKit(name);
            if (kit == null) {
                return;
            }
            kit.applyTo(player, false, true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitSign(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                return;
            }
            Sign sign = (Sign)state;
            String[] lines = sign.getLines();
            if (lines.length >= 2 && lines[1].contains("Kit")) {
                Kit kit = this.plugin.getKitManager().getKit((lines.length >= 3) ? lines[2] : null);
                if (kit == null) {
                    return;
                }
                event.setCancelled(true);
                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                boolean applied = kit.applyTo(player, false, false);
                if (applied) {
                    fakeLines[0] = ChatColor.GREEN + "Kit équipé";
                    fakeLines[1] = ChatColor.GREEN + "avec succès:";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                }
                else {
                    fakeLines[0] = ChatColor.RED + "Impossible de";
                    fakeLines[1] = ChatColor.RED + "prendre le kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                }
                if (Core.getInstance().getSignHandler().showLines(player, sign, fakeLines, 100L, false) && applied) {
                    ParticleEffect.FIREWORK_SPARK.display(player, sign.getLocation().clone().add(0.5, 0.5, 0.5), 0.01f, 10);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitApply(KitApplyEvent event) {
        if (event.isForce()) {
            return;
        }
        Player player = event.getPlayer();
        Kit kit = event.getKit();
        String kitPermission = kit.getPermissionNode();
        if (kitPermission != null && !player.hasPermission(kitPermission)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Tu n'as pas la permission d'utiliser ce kit.");
            return;
        }
        UUID uuid = player.getUniqueId();
        FactionUser baseUser = this.plugin.getUserManager().getUser(uuid);
        long remaining = baseUser.getRemainingKitCooldown(kit);
        if (remaining > 0L) {
            player.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser le kit " + kit.getDisplayName() + " pendant encore " + DurationFormatUtils.formatDurationWords(remaining, true, true).replace("seconds", "secondes") + '.');
            event.setCancelled(true);
            return;
        }
        int curUses = baseUser.getKitUses(kit);
        int maxUses = kit.getMaximumUses();
        if (curUses >= maxUses && maxUses != Integer.MAX_VALUE) {
            player.sendMessage(ChatColor.RED + "Tu as déjà utilisé ce kit " + curUses + "fois sur " + maxUses);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        if (!event.isForce()) {
            Kit kit = event.getKit();
            FactionUser baseUser = this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }
}
