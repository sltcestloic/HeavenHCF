package fr.taeron.hcf.deathban.lives;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.*;
import fr.taeron.hcf.deathban.lives.argument.*;

public class LivesExecutor extends ArgumentExecutor{
	
    public LivesExecutor(final HCF plugin) {
        super("lives");
        this.addArgument(new LivesCheckArgument(plugin));
        this.addArgument(new LivesCheckDeathbanArgument(plugin));
        this.addArgument(new LivesClearDeathbansArgument(plugin));
        this.addArgument(new LivesGiveArgument(plugin));
        this.addArgument(new LivesReviveArgument(plugin));
        this.addArgument(new LivesSetArgument(plugin));
        this.addArgument(new LivesSetDeathbanTimeArgument());
    }
}
