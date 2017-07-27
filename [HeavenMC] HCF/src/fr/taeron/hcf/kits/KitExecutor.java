package fr.taeron.hcf.kits;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.kits.arguments.*;
public class KitExecutor extends ArgumentExecutor{
    
    public KitExecutor(HCF plugin) {
        super("kit");
        this.addArgument(new KitApplyArgument(plugin));
        this.addArgument(new KitCreateArgument(plugin));
        this.addArgument(new KitDeleteArgument(plugin));
        this.addArgument(new KitGuiArgument(plugin));
        this.addArgument(new KitListArgument(plugin));
        this.addArgument(new KitRenameArgument(plugin));
        this.addArgument(new KitSetDelayArgument(plugin));
        this.addArgument(new KitSetImageArgument(plugin));
        this.addArgument(new KitSetItemsArgument(plugin));
    }
}
