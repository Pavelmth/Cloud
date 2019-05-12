package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.CommandType;
import cloudNettyServer.enums.ActionStage;
import cloudNettyServer.fileWork.DeleteFile;
import cloudNettyServer.fileWork.UserFile;
import cloudNettyServer.fileWork.UserFiles;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.*;
import java.util.ArrayList;

public class ClientHandler extends ChannelInboundHandlerAdapter {
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
            System.out.println("ClientHandler " + actionStage);
            if (buf.readableBytes() < 4) {
                return;
            }
            clientFolder = buf.readInt();
            actionStage = ActionStage.GETTING_COMMAND;
        }

        if (actionStage.equals(ActionStage.GETTING_COMMAND)) {
            System.out.println("ClientHandler " + actionStage);
            byte firstByte = 0;
            if (buf.isReadable()) {
                firstByte = buf.readByte();
            }
            commandType = CommandType.getDataTypeFromByte(firstByte);
            System.out.println("ClientHandler " + commandType);

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
            /* get a file from a client and upload the file to the server */
            case SEND_FILES:
                System.out.println("Command 'SEND FILES' has been got");
                if (actionStage.equals(ActionStage.GETTING_FILE_LENGTH)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (buf.readableBytes() < 8) {
                        return;
                    }
                    fileLength = buf.readLong();
                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                    System.out.println("file size is " + fileLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    System.out.println("ClientHandler " + actionStage);
                    //waiting for 'int'
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    System.out.println("ClientHandler " + actionStage);
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
                    System.out.println("ClientHandler " + actionStage);
                    accumulator.writeBytes(buf);
                    buf.release();
                    File file = new File("server/folder/" + clientFolder + "/" + fileName);

                    try (
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))
                    ) {
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
            case DOWNLOAD_FILES:
                System.out.println("Command 'DOWNLOAD FILES' has been got");
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    System.out.println("ClientHandler " + actionStage);
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
                    System.out.println("ClientHandler " + actionStage);

                    File file = new File("server/folder/" + clientFolder + "/" + fileName);

                    try (
                            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))
                    ) {
                        int bufLen;
                        byte[] arr = new byte[8192];
                        while ((bufLen = in.read(arr)) > 0) {
                            accumulator.writeBytes(arr, 0, bufLen);
                            ctx.writeAndFlush(accumulator);
                            accumulator.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            /* delete the file on the server*/
            case DELETE_FILES:
                System.out.println("Command 'DELETE FILES' has been got");
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (buf.readableBytes() < 4) {
                        return;
                    }
                    nameLength = buf.readInt();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    System.out.println("ClientHandler " + actionStage);
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
                    System.out.println("ClientHandler " + actionStage);
                    boolean isDeleted = new DeleteFile().deleteFile(String.valueOf(clientFolder), fileName);
                    if (isDeleted) {
                        byte[] loginError = {3};
                        ByteBuf respond = Unpooled.copiedBuffer(loginError);
                        ctx.writeAndFlush(respond);
                    }
                    actionStage = ActionStage.GETTING_COMMAND;
                }
            case RESET:
                System.out.println("Command 'RESET' has been got");
                if (actionStage.equals(ActionStage.SENDING_FILE_NAME_LIST)) {
                    System.out.println("ClientHandler " + actionStage);
                    ArrayList<UserFile> fileList = new UserFiles(String.valueOf(clientFolder)).getUseFiles();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (UserFile o :
                            fileList) {
                        stringBuilder.append(" " + o.getName() + " " + o.getSize());
                    }
                    stringBuilder.delete(0, 1);
                    byte[] arr = stringBuilder.toString().getBytes();
                    ByteBuf bufList = Unpooled.copiedBuffer(arr);

                    int lengthList = arr.length;
                    ByteBuf buf1 = Unpooled.copyInt(lengthList);
                    ctx.writeAndFlush(buf1);

                    ctx.writeAndFlush(bufList);

                    actionStage = ActionStage.GETTING_COMMAND;
                }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
