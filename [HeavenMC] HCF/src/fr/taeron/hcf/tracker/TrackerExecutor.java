package fr.taeron.hcf.tracker;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.tracker.argument.TrackerAbandonArgument;
import fr.taeron.hcf.tracker.argument.TrackerGuiArgument;
import fr.taeron.hcf.tracker.argument.TrackerSearchArgument;

public class TrackerExecutor extends ArgumentExecutor{

	public TrackerExecutor() {
		super("tracker");
		this.addArgument(new TrackerSearchArgument());
		this.addArgument(new TrackerGuiArgument());
		this.addArgument(new TrackerAbandonArgument());
	}
}
