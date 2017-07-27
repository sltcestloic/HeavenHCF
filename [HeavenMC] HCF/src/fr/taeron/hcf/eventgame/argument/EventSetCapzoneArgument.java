package fr.taeron.hcf.eventgame.argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.heavenmc.core.util.command.CommandArgument;
import org.heavenmc.core.util.cuboid.Cuboid;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.eventgame.CaptureZone;
import fr.taeron.hcf.eventgame.faction.CapturableFaction;
import fr.taeron.hcf.eventgame.faction.ConquestFaction;
import fr.taeron.hcf.eventgame.faction.EventFaction;
import fr.taeron.hcf.eventgame.faction.KothFaction;
import fr.taeron.hcf.eventgame.tracker.ConquestTracker;
import fr.taeron.hcf.eventgame.tracker.KothTracker;
import fr.taeron.hcf.faction.FactionManager;
import fr.taeron.hcf.faction.claim.Claim;
import fr.taeron.hcf.faction.type.Faction;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

@SuppressWarnings("deprecation")
public class EventSetCapzoneArgument extends CommandArgument{
	
    private HCF plugin;
    
    public EventSetCapzoneArgument(HCF plugin) {
        super("setcapzone", "Définir la zone de capture d'un event");
        this.plugin = plugin;
        this.aliases = new String[] { "setcapturezone", "setcap", "setcappoint", "setcapturepoint", "setcappoint" };
        this.permission = "command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <event>";
    }
    
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation" + this.getUsage(label));
            return true;
        }
        WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
        if (worldEdit == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit n'est pas installé.");
            return true;
        }
        Selection selection = worldEdit.getSelection((Player)sender);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "Tu dois avoir une sélection WorldEdit pour faire ça.");
            return true;
        }
        if (selection.getWidth() < 2 || selection.getLength() < 2) {
            sender.sendMessage(ChatColor.RED + "La taille des zones de capture doit être au moins de " + 2 + 'x' + 2 + '.');
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof CapturableFaction)) {
            sender.sendMessage(ChatColor.RED + "Il n'y a pas de faction capturable nommée '" + args[1] + "'.");
            return true;
        }
        CapturableFaction capturableFaction = (CapturableFaction)faction;
        Collection<Claim> claims = capturableFaction.getClaims();
        if (claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Les zones de captures ne peuvent être que dans les claims de l'event.");
            return true;
        }
        Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
        World world = claim.getWorld();
        int minimumX = claim.getMinimumX();
        int maximumX = claim.getMaximumX();
        int minimumZ = claim.getMinimumZ();
        int maximumZ = claim.getMaximumZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (!factionAt.equals(capturableFaction)) {
                    sender.sendMessage(ChatColor.RED + "Les zones de captures ne peuvent être que dans les claims de l'event.");
                    return true;
                }
            }
        }
        CaptureZone captureZone;
        if (capturableFaction instanceof ConquestFaction) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Utilisation: /" + label + ' ' + this.getName() + ' ' + faction.getName() + " <red|blue|green|yellow>");
                return true;
            }
            ConquestFaction conquestFaction = (ConquestFaction)capturableFaction;
            ConquestFaction.ConquestZone conquestZone = ConquestFaction.ConquestZone.getByName(args[2]);
            if (conquestZone == null) {
                sender.sendMessage(ChatColor.RED + "Il n'y a pas de zone de capture avec le nom '" + args[2] + "'.");
                sender.sendMessage(ChatColor.RED + "Peut être pensais tu à: " + StringUtils.join(ConquestFaction.ConquestZone.getNames(), ", "));
                return true;
            }
            captureZone = new CaptureZone(conquestZone.getName(), conquestZone.getColor().toString(), (Cuboid)claim, ConquestTracker.DEFAULT_CAP_MILLIS);
            conquestFaction.setZone(conquestZone, captureZone);
        }
        else {
            ((KothFaction)capturableFaction).setCaptureZone(captureZone = new CaptureZone(capturableFaction.getName(), (Cuboid)claim, KothTracker.DEFAULT_CAP_MILLIS));
        }
        sender.sendMessage(ChatColor.YELLOW + "La zone de capture " + captureZone.getDisplayName() + ChatColor.YELLOW + " appartient désormais a la faction " + faction.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2: {
                return (List<String>) this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map((Function<? super Faction, ?>)Faction::getName).collect(Collectors.toList());
            }
            case 3: {
                Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
                if (faction2 instanceof ConquestFaction) {
                    ConquestFaction.ConquestZone[] zones = ConquestFaction.ConquestZone.values();
                    List<String> results = new ArrayList<String>(zones.length);
                    for (ConquestFaction.ConquestZone zone : zones) {
                        results.add(zone.name());
                    }
                    return results;
                }
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}
