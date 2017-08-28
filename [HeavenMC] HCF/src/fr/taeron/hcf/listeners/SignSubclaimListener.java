package fr.taeron.hcf.listeners;

import fr.taeron.hcf.faction.struct.*;
import java.util.stream.*;
import fr.taeron.hcf.*;
import org.bukkit.material.*;
import org.bukkit.material.Sign;
import org.bukkit.entity.*;
import fr.taeron.hcf.faction.type.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.block.*;
import org.bukkit.block.Chest;

import com.google.common.collect.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import java.util.*;
import org.bukkit.inventory.*;

public class SignSubclaimListener implements Listener
{
    private static final String SUBCLAIM_PREFIX;
    private static final BlockFace[] SIGN_FACES;
    private final HCF plugin;
    
    public SignSubclaimListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    private boolean isSubclaimable(final Block block) {
        final Material type = block.getType();
        return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
    }
    
    @SuppressWarnings({ "deprecation", "rawtypes" })
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(final SignChangeEvent event) {
        final String[] lines = event.getLines();
        if (!StringUtils.containsIgnoreCase((CharSequence)lines[0], (CharSequence)"subclaim")) {
            return;
        }
        final Block block = event.getBlock();
        final MaterialData materialData = block.getState().getData();
        if (materialData instanceof Sign) {
            final Sign sign = (Sign)materialData;
            final Block attatchedBlock = block.getRelative(sign.getAttachedFace());
            if (this.isSubclaimable(attatchedBlock)) {
                final Player player = event.getPlayer();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                final Role role;
                if (playerFaction == null || (role = playerFaction.getMember(player).getRole()) == Role.MEMBER) {
                    return;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(block.getLocation());
                if (playerFaction.equals(factionAt)) {
                    final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(attatchedBlock);
                    for (final org.bukkit.block.Sign attachedSign : attachedSigns) {
                        if (attachedSign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                            player.sendMessage(ChatColor.RED + "Il y a déjà un panneau de subclaim sur ce block de " + attatchedBlock.getType().toString() + '.');
                            return;
                        }
                    }
                    final List<String> memberList = new ArrayList<String>(3);
                    for (int i = 1; i < lines.length; ++i) {
                        final String line = lines[i];
                        if (StringUtils.isNotBlank((CharSequence)line)) {
                            memberList.add(line);
                        }
                    }
                    if (memberList.isEmpty()) {
                        event.setLine(1, player.getName());
                        player.sendMessage(ChatColor.YELLOW + "Aucun pseudo spécifié, le subclaim a donc été mit à ton nom.");
                    }
                    final boolean leaderChest = lines[1].equals(Role.LEADER.getAstrix()) || StringUtils.containsIgnoreCase((CharSequence)lines[1], (CharSequence)"leader");
                    final boolean captainChest = lines[1].equals(Role.CAPTAIN.getAstrix()) || StringUtils.containsIgnoreCase((CharSequence)lines[1], (CharSequence)"captain");
                    if (captainChest) {
                        event.setLine(2, (String)null);
                        event.setLine(3, (String)null);
                        event.setLine(1, ChatColor.YELLOW + "Officiers");
                    }
                    if (leaderChest) {
                        if (role != Role.LEADER) {
                            player.sendMessage(ChatColor.RED + "Seul les chefs de faction peuvent faire des subclaim au nom d'un chef de faction.");
                            return;
                        }
                        event.setLine(2, (String)null);
                        event.setLine(3, (String)null);
                        event.setLine(1, ChatColor.DARK_RED + "Chefs");
                    }
                    event.setLine(0, SignSubclaimListener.SUBCLAIM_PREFIX);
                    final List<String> actualMembers = memberList.stream().filter(member -> playerFaction.getMember(member) != null).collect(Collectors.toList());
                    playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + player.getName() + ChatColor.YELLOW + " a créé un subclaim sur un " + ChatColor.LIGHT_PURPLE + attatchedBlock.getType().toString() + ChatColor.YELLOW + " aux coordonées " + ChatColor.WHITE + '[' + attatchedBlock.getX() + ", " + attatchedBlock.getZ() + ']' + ChatColor.YELLOW + " pour " + (leaderChest ? "leaders" : (actualMembers.isEmpty() ? "captains" : ("members " + ChatColor.GRAY + '[' + ChatColor.DARK_GREEN + StringUtils.join((Iterable)actualMembers, ", ") + ChatColor.GRAY + ']'))));
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
            return;
        }
        final Block block = event.getBlock();
        final BlockState state = block.getState();
        if (state instanceof org.bukkit.block.Sign || this.isSubclaimable(block)) {
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction == null) {
                return;
            }
            final boolean hasAccess = playerFaction.getMember(player).getRole() != Role.MEMBER;
            if (hasAccess) {
                return;
            }
            if (state instanceof org.bukkit.block.Sign) {
                final org.bukkit.block.Sign sign = (org.bukkit.block.Sign)state;
                if (sign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Tu ne peux pas casser de panneau de subclaim");
                }
                return;
            }
            final Faction factionAt = this.plugin.getFactionManager().getFactionAt(block);
            final String search = this.getShortenedName(player.getName());
            if (playerFaction.equals(factionAt) && !playerFaction.isRaidable()) {
                final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
                for (final org.bukkit.block.Sign attachedSign : attachedSigns) {
                    final String[] lines = attachedSign.getLines();
                    if (!lines[0].equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                        continue;
                    }
                    for (int i = 1; i < lines.length; ++i) {
                        if (lines[i].contains(search)) {
                            return;
                        }
                    }
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Tu ne peux pas casser ce block car il est subclaim.");
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryMoveItem(final InventoryMoveItemEvent event) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final InventoryHolder holder = event.getSource().getHolder();
        Collection<Block> sourceBlocks;
        if (holder instanceof Chest) {
            sourceBlocks = Collections.singletonList(((Chest)holder).getBlock());
        }
        else {
            if (!(holder instanceof DoubleChest)) {
                return;
            }
            final DoubleChest doubleChest = (DoubleChest)holder;
            sourceBlocks = Lists.newArrayList(new Block[] { ((Chest)doubleChest.getLeftSide()).getBlock(), ((Chest)doubleChest.getRightSide()).getBlock() });
        }
        for (final Block block : sourceBlocks) {
            final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
            for (final org.bukkit.block.Sign attachedSign : attachedSigns) {
                if (attachedSign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
    
    private String getShortenedName(String originalName) {
        if (originalName.length() == 16) {
            originalName = originalName.substring(0, 15);
        }
        return originalName;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
            return;
        }
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final Block block = event.getClickedBlock();
        if (this.isSubclaimable(block)) {
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction == null || playerFaction.isRaidable()) {
                return;
            }
            final Role role = playerFaction.getMember(player).getRole();
            if (role == Role.LEADER) {
                return;
            }
            if (playerFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
                if (attachedSigns.isEmpty()) {
                    return;
                }
                final String search = this.getShortenedName(player.getName());
                for (final org.bukkit.block.Sign attachedSign : attachedSigns) {
                    final String[] lines = attachedSign.getLines();
                    if (!lines[0].equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                        continue;
                    }
                    if (!Role.LEADER.getAstrix().equals(lines[1])) {
                        for (int i = 1; i < lines.length; ++i) {
                            if (lines[i].contains(search)) {
                                return;
                            }
                        }
                    }
                    if (role != Role.CAPTAIN) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "Tu n'as pas accès a ce block car il est subclaim.");
                        break;
                    }
                    if (lines[1].contains("Leader")) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "Tu n'as pas accès a ce block car il est subclaim.");
                        break;
                    }
                }
            }
        }
    }
    
    public Collection<org.bukkit.block.Sign> getAttachedSigns(final Block block) {
        final Set<org.bukkit.block.Sign> results = new HashSet<org.bukkit.block.Sign>();
        this.getSignsAround(block, results);
        final BlockState state = block.getState();
        if (state instanceof Chest) {
            final Inventory chestInventory = ((Chest)state).getInventory();
            if (chestInventory instanceof DoubleChestInventory) {
                final DoubleChest doubleChest = ((DoubleChestInventory)chestInventory).getHolder();
                final Block left = ((Chest)doubleChest.getLeftSide()).getBlock();
                final Block right = ((Chest)doubleChest.getRightSide()).getBlock();
                this.getSignsAround(left.equals(block) ? right : left, results);
            }
        }
        return results;
    }
    
    private Set<org.bukkit.block.Sign> getSignsAround(final Block block, final Set<org.bukkit.block.Sign> results) {
        for (final BlockFace face : SignSubclaimListener.SIGN_FACES) {
            final Block relative = block.getRelative(face);
            final BlockState relativeState = relative.getState();
            if (relativeState instanceof org.bukkit.block.Sign) {
                final Sign materialSign = (Sign)relativeState.getData();
                if (relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
                    results.add((org.bukkit.block.Sign)relative.getState());
                }
            }
        }
        return results;
    }
    
    static {
        SUBCLAIM_PREFIX = ChatColor.AQUA.toString() + "[Subclaim]";
        SIGN_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP };
    }
}
