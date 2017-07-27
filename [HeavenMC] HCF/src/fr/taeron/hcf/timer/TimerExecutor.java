package fr.taeron.hcf.timer;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.*;
import fr.taeron.hcf.timer.argument.*;

public class TimerExecutor extends ArgumentExecutor{
	
    public TimerExecutor(final HCF plugin) {
        super("timer");
        this.addArgument(new TimerCheckArgument(plugin));
        this.addArgument(new TimerSetArgument(plugin));
    }
}
