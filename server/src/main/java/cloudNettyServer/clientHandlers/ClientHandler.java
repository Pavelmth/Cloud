package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.CommandType;
import cloudNettyServer.enums.ActionStage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private CommandType commandType = CommandType.EMPTY;
    private ActionStage actionStage = ActionStage.GETTING_COMMAND;

    private long fileLength;
    private int nameLength;
    String fileName = null;


    private ByteBuf accumulator;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024 * 1, 10 * 1024 * 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);

        if (actionStage.equals(ActionStage.GETTING_COMMAND)) {
            byte firstByte = 0;
            if (buf.isReadable()) firstByte = buf.readByte();
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
                case DELETE_FILES:
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    break;
                case RESET:
                    actionStage = ActionStage.SENDING_FILE_NAME_LIST;
                    break;
            }
        }
        switch (commandType) {
            /*get a file from a client and upload the file to the server */
            case SEND_FILES:
                System.out.println("Command 'SEND FILES' has been got");
                //get a file length
                if (actionStage.equals(ActionStage.GETTING_FILE_LENGTH)) {
                    //waiting for 'long'
                    if (buf.readableBytes() < 8) {
                        return;
                    }
                    fileLength = buf.readLong();
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    System.out.println("file size is " + fileLength);
                }
                //get a file name length
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    //waiting for 'int'
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                //get a file name
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
                //get a file context and send it farther
                if (actionStage.equals(ActionStage.GETTING_FILE_CONTENT)) {

                    accumulator.writeBytes(buf);
                    buf.release();
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("server/folder/receivedFileFromClient.txt", true))) {
                        while (accumulator.readableBytes() > 0) {
                            out.write(accumulator.readByte());
                        }
                        accumulator.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            /*download file from the server and send to a client*/
            case DOWNLOAD_FILES:
                System.out.println("Command 'DOWNLOAD FILES' has been got");
                //get a file name length
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    //waiting for int
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                //get a file name
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    //waiting for all the letters of the name
                    if (buf.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    buf.readBytes(array, 0, nameLength);
                    fileName = new String(array);
                    actionStage = ActionStage.SENDING_FILE_CONTENT;
                    System.out.println(fileName);
                }
                if (actionStage.equals(ActionStage.SENDING_FILE_CONTENT)) {
                    // add late send file content to client ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            /*delete the file on the server and send the new list of file names to a client*/
            case DELETE_FILES:
                System.out.println("Command 'DELETE FILES' has been got");
                //get a file name length
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    //waiting for int
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                //get a file name
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    //waiting for all the letters of the name
                    if (buf.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    buf.readBytes(array, 0, nameLength);
                    fileName = new String(array);
                    actionStage = ActionStage.DELETE_FILES;
                    System.out.println(fileName);
                }
                if (actionStage.equals(ActionStage.DELETE_FILES)) {
                    // add later +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    actionStage = ActionStage.SENDING_FILE_NAME_LIST;
                }
                /*send to a client a list of file names from his server folder*/
            case RESET:
                System.out.println("Command 'RESET' has been got");
                if (actionStage.equals(ActionStage.SENDING_FILE_NAME_LIST)) {
                    //add later +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
