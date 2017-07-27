package fr.taeron.hcf.combatlog;

import net.minecraft.util.io.netty.channel.*;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.io.netty.util.concurrent.*;
import java.net.*;
import javax.crypto.*;

public final class NpcNetworkManager extends NetworkManager
{
    public NpcNetworkManager() {
        super(false);
    }
    
    public void channelActive(final ChannelHandlerContext channelhandlercontext) throws Exception {
    }
    
    public void a(final EnumProtocol enumprotocol) {
    }
    
    public void channelInactive(final ChannelHandlerContext channelhandlercontext) {
    }
    
    public void exceptionCaught(final ChannelHandlerContext channelhandlercontext, final Throwable throwable) {
    }
    
    protected void a(final ChannelHandlerContext channelhandlercontext, final Packet packet) {
    }
    
    @SuppressWarnings("rawtypes")
	public void handle(final Packet packet, final GenericFutureListener... agenericfuturelistener) {
    }
    
    public void a() {
    }
    
    @SuppressWarnings("serial")
	public SocketAddress getSocketAddress() {
        return new SocketAddress() {};
    }
    
    public boolean c() {
        return false;
    }
    
    public void a(final SecretKey secretkey) {
    }
    
    public boolean isConnected() {
        return true;
    }
    
    public void g() {
    }
    
    protected void channelRead0(final ChannelHandlerContext channelhandlercontext, final Object object) {
    }
    
    public int getVersion() {
        return -1;
    }
}
