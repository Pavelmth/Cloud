package cloudNettyServer.clientHandlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ClientCodeSender extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Byte aByte= (byte) msg;
        ctx.writeAndFlush(aByte);
        System.out.println("Client code sender " + aByte);
        ctx.close();
    }
}
