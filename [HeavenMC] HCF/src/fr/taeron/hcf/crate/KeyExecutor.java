package fr.taeron.hcf.crate;

import org.heavenmc.core.util.command.ArgumentExecutor;

import fr.taeron.hcf.HCF;
import fr.taeron.hcf.crate.argument.KeyBankArgument;
import fr.taeron.hcf.crate.argument.KeyDepositArgument;
import fr.taeron.hcf.crate.argument.KeyGiveArgument;
import fr.taeron.hcf.crate.argument.KeyListArgument;
import fr.taeron.hcf.crate.argument.KeyWithdrawArgument;

public class KeyExecutor extends ArgumentExecutor{
	
    public KeyExecutor(HCF plugin) {
        super("key");
        this.addArgument(new KeyBankArgument(plugin));
        this.addArgument(new KeyDepositArgument(plugin));
        this.addArgument(new KeyGiveArgument(plugin));
        this.addArgument(new KeyListArgument(plugin));
        this.addArgument(new KeyWithdrawArgument(plugin));
    }
}
