package fr.taeron.hcf.eventgame.koth;

import fr.taeron.hcf.*;
import fr.taeron.hcf.eventgame.koth.argument.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.ArgumentExecutor;

public class KothExecutor extends ArgumentExecutor{
	
    private final KothScheduleArgument kothScheduleArgument;
    
    public KothExecutor(final HCF plugin) {
        super("koth");
        this.addArgument(new KothHelpArgument(this));
        this.addArgument(new KothNextArgument(plugin));
        this.addArgument((this.kothScheduleArgument = new KothScheduleArgument(plugin)));
        this.addArgument(new KothSetCapDelayArgument(plugin));
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}
