package fr.taeron.hcf.combatlog;

import net.minecraft.server.v1_7_R4.*;

public final class NpcPlayerConnection extends PlayerConnection
{
    public NpcPlayerConnection(final EntityPlayer entityplayer) {
        super(MinecraftServer.getServer(), (NetworkManager)new NpcNetworkManager(), entityplayer);
    }
    
    public void a() {
    }
    
    public void disconnect(final String s) {
    }
    
    public void a(final PacketPlayInSteerVehicle packetplayinsteervehicle) {
    }
    
    public void a(final PacketPlayInFlying packetplayinflying) {
    }
    
    public void a(final PacketPlayInBlockDig packetplayinblockdig) {
    }
    
    public void a(final PacketPlayInBlockPlace packetplayinblockplace) {
    }
    
    public void a(final IChatBaseComponent ichatbasecomponent) {
    }
    
    public void sendPacket(final Packet packet) {
    }
    
    public void a(final PacketPlayInHeldItemSlot packetplayinhelditemslot) {
    }
    
    public void a(final PacketPlayInChat packetplayinchat) {
    }
    
    public void chat(final String s, final boolean async) {
    }
    
    public void a(final PacketPlayInArmAnimation packetplayinarmanimation) {
    }
    
    public void a(final PacketPlayInEntityAction packetplayinentityaction) {
    }
    
    public void a(final PacketPlayInUseEntity packetplayinuseentity) {
    }
    
    public void a(final PacketPlayInClientCommand packetplayinclientcommand) {
    }
    
    public void a(final PacketPlayInCloseWindow packetplayinclosewindow) {
    }
    
    public void a(final PacketPlayInWindowClick packetplayinwindowclick) {
    }
    
    public void a(final PacketPlayInEnchantItem packetplayinenchantitem) {
    }
    
    public void a(final PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
    }
    
    public void a(final PacketPlayInTransaction packetplayintransaction) {
    }
    
    public void a(final PacketPlayInUpdateSign packetplayinupdatesign) {
    }
    
    public void a(final PacketPlayInKeepAlive packetplayinkeepalive) {
    }
    
    public void a(final PacketPlayInAbilities packetplayinabilities) {
    }
    
    public void a(final PacketPlayInTabComplete packetplayintabcomplete) {
    }
    
    public void a(final PacketPlayInSettings packetplayinsettings) {
    }
    
    public void a(final PacketPlayInCustomPayload packetplayincustompayload) {
    }
    
    public void a(final EnumProtocol enumprotocol, final EnumProtocol enumprotocol1) {
    }
}
