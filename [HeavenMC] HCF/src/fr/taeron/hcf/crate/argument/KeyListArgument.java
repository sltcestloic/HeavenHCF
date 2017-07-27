package fr.taeron.hcf.crate.argument;

import fr.taeron.hcf.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.CommandArgument;

import java.util.*;
import fr.taeron.hcf.crate.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import java.util.stream.*;
import org.bukkit.*;

public class KeyListArgument extends CommandArgument{
	
    private final HCF plugin;
    
    public KeyListArgument(final HCF plugin) {
        super("list", "Voir les types de keys qui existent");
        this.plugin = plugin;
        this.permission = "hcf.command.loot.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List<String> keyNames = this.plugin.getKeyManager().getKeys().stream().map(Key::getDisplayName).collect(Collectors.toList());
        sender.sendMessage(ChatColor.GRAY + "Liste des types de keys: " + StringUtils.join(keyNames, ChatColor.GRAY + ", "));
        return true;
    }
}
