package cloudNettyServer;

import cloudNettyServer.enums.CommandType;
import cloudNettyServer.enums.ActionStage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf accumulator;

    private CommandType commandType = CommandType.EMPTY;
    private ActionStage actionStage = ActionStage.GETTING_COMMAND; //later to add authorization AUTHORIZED type

    private long fileLength;
    private int nameLength;
    String fileName = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024 * 1, 5 * 1024 * 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);

        if (actionStage.equals(ActionStage.GETTING_COMMAND)) {
             byte firstByte = buf.readByte();
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
            case SEND_FILES: /*getting file from client*/
                //getting a file length
                if (actionStage.equals(ActionStage.GETTING_FILE_LENGTH)) {
                    if (buf.readableBytes() < 8) { //waiting for long
                        return;
                    }
                    fileLength = buf.readLong();
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    System.out.println("file size is " + fileLength);
                }
                //getting a file name length
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    if (buf.readableBytes() < 4) { //waiting for int
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                //getting a file name
                if (buf.readableBytes() < nameLength) { //waiting for all the letters of the name
                    return;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    byte[] array = new byte[nameLength];
                    buf.readBytes(array, 0, nameLength);
                    fileName = new String(array);
                    actionStage = ActionStage.GETTING_FILE_CONTENT;
                    System.out.println(fileName);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_CONTENT)) {

                   //* create a file
                    FileOutputStream outFile = new FileOutputStream("server/folder/" + fileName);

//                    while (buf.isReadable()) {
//                        outFile.write(buf.readByte());
//                    }



//                    outFile.write((Byte) msg);
//
//                    System.out.println("start write to accumulator");
//                    accumulator.writeBytes(buf);
//                    buf.release();
//                    System.out.println("buf released");
//
//
                    outFile.close();
                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            case DOWNLOAD_FILES: /*download file from server*/
                System.out.println("download file");
                //getting a file name length
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    if (buf.readableBytes() < 4) { //waiting for int
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                //getting a file name
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    if (buf.readableBytes() < nameLength) { //waiting for all the letters of the name
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    buf.readBytes(array, 0, nameLength);
                    fileName = new String(array);
                    actionStage = ActionStage.SENDING_FILE_CONTENT;
                    System.out.println(fileName);
                }
                if (actionStage.equals(ActionStage.SENDING_FILE_CONTENT)) {
                    FileInputStream inFile = new FileInputStream("server/folder/" + fileName);
//                    ctx.writeAndFlush(inFile.read());

//                    ctx.writeAndFlush("Java\n");
                    inFile.close();
                    actionStage = ActionStage.GETTING_COMMAND;
                }
                //
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
