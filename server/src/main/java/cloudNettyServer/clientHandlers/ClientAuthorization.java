package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.ActionStage;
import cloudNettyServer.sql.AuthService;
import cloudNettyServer.sql.CheckExistingLogin;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.util.Arrays;

@ChannelHandler.Sharable
public class ClientAuthorization extends ChannelInboundHandlerAdapter {
    ActionStage actionStage = ActionStage.UNAUTHORIZED;
    int loginLength;
    int passwordCode;
    String login;
    int clientFolder;
    int loginId;
    byte responseCod;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (actionStage.equals(ActionStage.UNAUTHORIZED)) {
            ByteBuf buf = (ByteBuf) msg;
            //waiting for 'int'
            while (buf.readableBytes() < 4) {
                return;
            }
            //getting a login length
            loginLength = buf.readInt();
            System.out.println("file name length: " + loginLength);

            //waiting for all the letters of the login
            while (buf.readableBytes() < loginLength) {
                return;
            }
            //getting a login and check
            byte[] array = new byte[loginLength];
            buf.readBytes(array, 0, loginLength);
            login = new String(array);
            System.out.println("Login: " + login);

            //check existing of login
            loginId = new CheckExistingLogin().getLogin(login);
            System.out.println("Login ID: " + loginId);

            if (loginId > 0) {
                //waiting for 'int'
                while (buf.readableBytes() < 4) {
                    return;
                }
                //getting password hashcode
                passwordCode = buf.readInt();
                System.out.println("HashCode of password: " + passwordCode);

                //check authorization
                clientFolder = new AuthService().getAccess(login, passwordCode);
                System.out.println("Client folder: " + clientFolder);
                if (clientFolder > 0) {
                    actionStage = ActionStage.AUTHORIZED;
                    buf.release();
                } else {
                    responseCod = 32;
                    ctx.writeAndFlush(responseCod);
                    // add later response 'login and password don't match' ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                }
            } else {
                responseCod = 31;
                ctx.writeAndFlush(responseCod);
                //add later response 'login hasn't been found' ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            }
        }

        if (actionStage.equals(ActionStage.AUTHORIZED)) {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
