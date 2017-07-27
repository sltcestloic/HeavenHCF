package fr.taeron.hcf.eventgame.conquest;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.*;

public class ConquestExecutor extends ArgumentExecutor{
	
    public ConquestExecutor(HCF plugin) {
        super("conquest");
        this.addArgument(new ConquestSetpointsArgument(plugin));
    }
}
