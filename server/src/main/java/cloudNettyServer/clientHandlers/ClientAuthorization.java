package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.ActionStage;
import cloudNettyServer.sql.AuthService;
import cloudNettyServer.sql.CheckExistingLogin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

public class ClientAuthorization extends ChannelInboundHandlerAdapter {
    ActionStage actionStage = ActionStage.UNAUTHORIZED;
    int loginLength;
    int passwordCode;
    String login;
    int clientFolder;
    int loginId;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (actionStage.equals(ActionStage.UNAUTHORIZED)) {
            System.out.println("ClientAuthorization " + actionStage);
            ByteBuf buf = (ByteBuf) msg;
            while (buf.readableBytes() < 4) {
                return;
            }
            loginLength = buf.readInt();

            while (buf.readableBytes() < loginLength) {
                return;
            }
            byte[] array = new byte[loginLength];
            buf.readBytes(array, 0, loginLength);
            login = new String(array);

            //check existing of login
            loginId = new CheckExistingLogin().getLogin(login);

            if (loginId > 0) {
                while (buf.readableBytes() < 4) {
                    return;
                }
                //getting password hashcode
                passwordCode = buf.readInt();

                //check authorization
                clientFolder = new AuthService().getAccess(login, passwordCode);
                if (clientFolder > 0) {
                    ByteBuf bufFolder = Unpooled.copyInt(clientFolder);
                    ctx.fireChannelRead(bufFolder);
                    actionStage = ActionStage.AUTHORIZED;
                    //if everything OK send cod '1'
                    byte [] loginPasswordResp = {1};
                    ByteBuf respond = Unpooled.copiedBuffer(loginPasswordResp);
                    ctx.writeAndFlush(respond);
                    buf.release();
                } else {
                    //if login and password don't match send to client cod '32' and close connection
                    byte [] loginPasswordError = {32};
                    ByteBuf respond = Unpooled.copiedBuffer(loginPasswordError);
                    ctx.writeAndFlush(respond);
                    ctx.close();
                }
            } else {
                //if login doesn't exist send to client cod '31' and close connection
                byte [] loginError = {31};
                ByteBuf respond = Unpooled.copiedBuffer(loginError);
                ctx.writeAndFlush(respond);
                ctx.close();
            }
        }

        if (actionStage.equals(ActionStage.AUTHORIZED)) {
            System.out.println("ClientAuthorization " + actionStage);
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
