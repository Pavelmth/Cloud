package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.ActionStage;
import cloudNettyServer.enums.CommandType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class ClientHandlerReceiveFiles extends ChannelInboundHandlerAdapter {
    private CommandType commandType = CommandType.EMPTY;
    private ActionStage actionStage = ActionStage.GETTING_CLIENT_FOLDER;

    private long fileLength;
    private int nameLength;
    private String fileName = null;
    private int clientFolder;


    private ByteBuf accumulator;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024 * 1, 10 * 1024 * 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        if (actionStage.equals(ActionStage.GETTING_CLIENT_FOLDER)) {
            if (buf.readableBytes() < 4) {
                return;
            }
            clientFolder = buf.readInt();
            actionStage = ActionStage.GETTING_COMMAND;
        }

        if (actionStage.equals(ActionStage.GETTING_COMMAND)) {
            byte firstByte = 0;
            if (buf.isReadable()) {
                firstByte = buf.readByte();
            }
            commandType = CommandType.getDataTypeFromByte(firstByte);

            switch (commandType) {
                case EMPTY:
                    break;
                case SEND_FILES:
                    actionStage = ActionStage.GETTING_FILE_LENGTH;
                    break;
                case DOWNLOAD_FILES:
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    break;
            }
        }
        switch (commandType) {
            /* get a file from a client and upload the file to the server */
            case SEND_FILES:
                System.out.println("Command 'SEND FILES' has been got");
                if (actionStage.equals(ActionStage.GETTING_FILE_LENGTH)) {
                    if (buf.readableBytes() < 8) {
                        return;
                    }
                    fileLength = buf.readLong();
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    System.out.println("file size is " + fileLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    //waiting for 'int'
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    //waiting for all the letters of the file name
                    if (buf.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    buf.readBytes(array, 0, nameLength);
                    fileName = new String(array);
                    actionStage = ActionStage.GETTING_FILE_CONTENT;
                    System.out.println("File name: " + fileName);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_CONTENT)) {

                    accumulator.writeBytes(buf);
                    buf.release();
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("server/folder/" + clientFolder + "/" + fileName, true))) {
                        while (accumulator.readableBytes() > 0) {
                            out.write(accumulator.readByte());
                        }
                        accumulator.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /** later add condition if file has been written +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                     */
                    byte[] loginError = {3};
                    ByteBuf respond = Unpooled.copiedBuffer(loginError);
                    ctx.writeAndFlush(respond);

                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            /* download file from the server*/
//            case DOWNLOAD_FILES:
//                System.out.println("Command 'DOWNLOAD FILES' has been got");
//                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
//                    if (buf.readableBytes() < 4) {
//                        return;
//                    }
//                    nameLength = buf.readInt();
//                    actionStage = ActionStage.GETTING_FILE_NAME;
//                    System.out.println("file name length is " + nameLength);
//                }
//                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
//                    if (buf.readableBytes() < nameLength) {
//                        return;
//                    }
//                    byte[] array = new byte[nameLength];
//                    buf.readBytes(array, 0, nameLength);
//                    fileName = new String(array);
//                    actionStage = ActionStage.SENDING_FILE_CONTENT;
//                    System.out.println(fileName);
//                }
//                if (actionStage.equals(ActionStage.SENDING_FILE_CONTENT)) {
//                    /** add late send file content to client ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//                     */
//                    actionStage = ActionStage.GETTING_COMMAND;
//                }
//                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
