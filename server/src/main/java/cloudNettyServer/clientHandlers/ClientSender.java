package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.ActionStage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ClientSender extends ChannelOutboundHandlerAdapter {
    ActionStage actionStage = ActionStage.UNAUTHORIZED;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (actionStage.equals(ActionStage.UNAUTHORIZED)) {
            ByteBuf byteBuf = (ByteBuf) msg;
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            if (1 == 1) {
                actionStage = ActionStage.AUTHORIZED;
            }
            ctx.writeAndFlush(byteBuf);
            byteBuf.release();
        }
        ByteBuf byteBuf = (ByteBuf) msg;
        ctx.writeAndFlush(byteBuf);
    }
}

