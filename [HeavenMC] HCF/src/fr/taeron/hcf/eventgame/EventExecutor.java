package fr.taeron.hcf.eventgame;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.*;
import fr.taeron.hcf.eventgame.argument.*;

public class EventExecutor extends ArgumentExecutor{
	
    public EventExecutor(HCF plugin) {
        super("event");
        this.addArgument(new EventCancelArgument(plugin));
        this.addArgument(new EventCreateArgument(plugin));
        this.addArgument(new EventDeleteArgument(plugin));
        this.addArgument(new EventRenameArgument(plugin));
        this.addArgument(new EventSetAreaArgument(plugin));
        this.addArgument(new EventSetCapzoneArgument(plugin));
        this.addArgument(new EventStartArgument(plugin));
    }
}
