package fr.taeron.hcf.combatlog;

import net.minecraft.util.com.mojang.authlib.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.*;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.util.com.mojang.authlib.properties.*;
import java.util.*;

public final class NpcPlayer extends EntityPlayer
{
    private NpcIdentity identity;
    
    private NpcPlayer(final MinecraftServer minecraftserver, final WorldServer worldserver, final GameProfile gameprofile, final PlayerInteractManager playerinteractmanager) {
        super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
    }
    
    public NpcIdentity getNpcIdentity() {
        return this.identity;
    }
    
    public static NpcPlayer valueOf(final Player player) {
        final MinecraftServer minecraftServer = MinecraftServer.getServer();
        final WorldServer worldServer = ((CraftWorld)player.getWorld()).getHandle();
        final PlayerInteractManager playerInteractManager = new PlayerInteractManager((World)worldServer);
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), NpcNameGeneratorFactory.getNameGenerator().generate(player));
        for (final Map.Entry<String, Property> entry : ((CraftPlayer)player).getProfile().getProperties().entries()) {
            gameProfile.getProperties().put(entry.getKey(), entry.getValue());
        }
        final NpcPlayer npcPlayer = new NpcPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
        npcPlayer.identity = new NpcIdentity(player);
        new NpcPlayerConnection(npcPlayer);
        return npcPlayer;
    }
}
