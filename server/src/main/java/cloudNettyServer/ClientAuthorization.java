package cloudNettyServer;

import cloudNettyServer.enums.ActionStage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ClientAuthorization extends ChannelInboundHandlerAdapter {
    ActionStage actionStage = ActionStage.UNAUTHORIZED;
    int loginLength;
    String login;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (actionStage.equals(ActionStage.UNAUTHORIZED)) {
            //getting a login length
            while (buf.readableBytes() < 4) { //waiting for int
                return;
            }
            loginLength = buf.readInt();
            System.out.println("file name length is " + loginLength);

            //getting a login and check
            while (buf.readableBytes() < loginLength) { //waiting for all the letters of the login
                return;
            }
            byte[] array = new byte[loginLength];
            buf.readBytes(array, 0, loginLength);
            login = new String(array);
            System.out.println(login);

            //check existing of login

            //getting password hashcode

            //check authorization

            actionStage = ActionStage.AUTHORIZED;

            // add later
        }

        if (actionStage.equals(ActionStage.AUTHORIZED)) {
            if (buf.isReadable()) ctx.fireChannelRead(buf);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
