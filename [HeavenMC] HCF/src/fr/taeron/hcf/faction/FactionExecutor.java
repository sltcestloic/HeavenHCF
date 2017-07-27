package fr.taeron.hcf.faction;

import fr.taeron.hcf.*;
import fr.taeron.hcf.faction.argument.staff.*;
import fr.taeron.hcf.faction.argument.*;
import org.bukkit.command.*;
import org.heavenmc.core.util.command.ArgumentExecutor;
import org.heavenmc.core.util.command.CommandArgument;

public class FactionExecutor extends ArgumentExecutor{
	
    private final CommandArgument helpArgument;
    
    public FactionExecutor(final HCF plugin) {
        super("faction");
        this.addArgument(new FactionAcceptArgument(plugin));
        this.addArgument(new FactionAllyArgument(plugin));
        this.addArgument(new FactionChatArgument(plugin));
        this.addArgument(new FactionChatSpyArgument(plugin));
        this.addArgument(new FactionClaimArgument(plugin));
        this.addArgument(new FactionClaimChunkArgument(plugin));
        this.addArgument(new FactionClaimForArgument(plugin));
        this.addArgument(new FactionClaimsArgument(plugin));
        this.addArgument(new FactionClearClaimsArgument(plugin));
        this.addArgument(new FactionCreateArgument(plugin));
        this.addArgument(new FactionDemoteArgument(plugin));
        this.addArgument(new FactionDepositArgument(plugin));
        this.addArgument(new FactionDisbandArgument(plugin));
        this.addArgument(new FactionSetDtrRegenArgument(plugin));
        this.addArgument(new FactionForceJoinArgument(plugin));
        this.addArgument(new FactionForceKickArgument(plugin));
        this.addArgument(new FactionForceLeaderArgument(plugin));
        this.addArgument(new FactionForcePromoteArgument(plugin));
        this.addArgument(this.helpArgument = new FactionHelpArgument(this));
        this.addArgument(new FactionHomeArgument(this, plugin));
        this.addArgument(new FactionInviteArgument(plugin));
        this.addArgument(new FactionInvitesArgument(plugin));
        this.addArgument(new FactionKickArgument(plugin));
        this.addArgument(new FactionLeaderArgument(plugin));
        this.addArgument(new FactionLeaveArgument(plugin));
        this.addArgument(new FactionListArgument(plugin));
        this.addArgument(new FactionMapArgument(plugin));
        this.addArgument(new FactionOpenArgument(plugin));
        this.addArgument(new FactionRemoveArgument(plugin));
        this.addArgument(new FactionRenameArgument(plugin));
        this.addArgument(new FactionPromoteArgument(plugin));
        this.addArgument(new FactionSetDtrArgument(plugin));
        this.addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument(new FactionSetHomeArgument(plugin));
        this.addArgument(new FactionShowArgument(plugin));
        this.addArgument(new FactionStuckArgument(plugin));
        this.addArgument(new FactionUnclaimArgument(plugin));
        this.addArgument(new FactionUnallyArgument(plugin));
        this.addArgument(new FactionUninviteArgument(plugin));
        this.addArgument(new FactionWithdrawArgument(plugin));
        this.addArgument(new FactionClaimWandArgument(plugin));
       // this.addArgument(new FactionFocusArgument(plugin));
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        final CommandArgument argument = this.getArgument(args[0]);
        if (argument != null) {
            final String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}
