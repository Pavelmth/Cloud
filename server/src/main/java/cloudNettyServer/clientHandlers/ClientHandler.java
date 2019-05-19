package cloudNettyServer.clientHandlers;

import cloudNettyServer.enums.CommandType;
import cloudNettyServer.enums.ActionStage;
import cloudNettyServer.fileWork.DeleteFile;
import cloudNettyServer.fileWork.UserFile;
import cloudNettyServer.fileWork.UserFiles;
import io.netty.buffer.*;
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
    private byte nameLength;
    private String fileName = null;
    private int clientFolder;
    File file;

    private final int BUF_CAPACITY = 512;

    private long counter;
    private long remain;

    //for checking time
    long start;

    private ByteBuf accumulator;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024 * 1, 5 * 1024 * 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf input = ((ByteBuf) msg);
        if (actionStage.equals(ActionStage.GETTING_CLIENT_FOLDER)) {
            System.out.println("ClientHandler " + actionStage);
            if (input.readableBytes() < 4) {
                return;
            }
            clientFolder = input.readInt();
            actionStage = ActionStage.GETTING_COMMAND;
        }

        if (actionStage.equals(ActionStage.GETTING_COMMAND)) {
            System.out.println("ClientHandler " + actionStage);
            byte firstByte;
            if (!input.isReadable()) {
                return;
            }
            firstByte = input.readByte();
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

                start = System.currentTimeMillis();

                if (actionStage.equals(ActionStage.GETTING_FILE_LENGTH)) {
                    if (input.readableBytes() < 8) {
                        return;
                    }
                    fileLength = input.readLong();

                    counter = fileLength;

                    actionStage = ActionStage.GETTING_FILE_NAME_LENGTH;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    if (!input.isReadable()) {
                        return;
                    }
                    nameLength = input.readByte();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    //waiting for all the letters of the file name
                    if (input.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    input.readBytes(array, 0, nameLength);
                    fileName = new String(array);

                    file = new File("server/folder/" + clientFolder + "/" + fileName);

                    actionStage = ActionStage.GETTING_FILE_CONTENT;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_CONTENT)) {
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true))) {
                        while (counter != 0) {
                            if (!input.isReadable()) {
                                return;
                            }
                            out.write(input.readByte());
                            counter--;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    byte[] loginError = {3};
                    ByteBuf respond = Unpooled.copiedBuffer(loginError);
                    ctx.writeAndFlush(respond);

                    System.out.println("Time " + (System.currentTimeMillis() - start));

                    actionStage = ActionStage.GETTING_COMMAND;
                }
                break;
            /* download file from the server*/
            case DOWNLOAD_FILES:
                System.out.println("Command 'DOWNLOAD FILES' has been got");
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME_LENGTH)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (!input.isReadable()) {
                        return;
                    }
                    nameLength = input.readByte();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                    System.out.println("file name length is " + nameLength);
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (input.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    input.readBytes(array, 0, nameLength);
                    fileName = new String(array);

                    file = new File("server/folder/" + clientFolder + "/" + fileName);

                    fileLength = file.length();

                    if (fileLength > BUF_CAPACITY) {
                        counter = fileLength / BUF_CAPACITY;
                        remain = fileLength % BUF_CAPACITY;
                    } else {
                        counter = 0;
                    }

                    actionStage = ActionStage.SENDING_FILE_CONTENT;
                    System.out.println(fileName);
                }
                if (actionStage.equals(ActionStage.SENDING_FILE_CONTENT)) {
                    System.out.println("ClientHandler " + actionStage);

                    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                        ByteBuf tempBuf;
                        ByteBuf finTempBuf;

                        if (counter == 0) {
                            byte[] arr = new byte[(int) fileLength];
                            in.read(arr);
                            tempBuf = Unpooled.copiedBuffer(arr);
                            ctx.writeAndFlush(tempBuf);

                            byte[] backCommand = {3};
                            ByteBuf respond = Unpooled.copiedBuffer(backCommand);
                            ctx.writeAndFlush(respond);
                        } else {
                            byte[] arr = new byte[BUF_CAPACITY];
                            while (counter != 0) {
                                in.read(arr);
                                tempBuf = Unpooled.copiedBuffer(arr);
                                ctx.writeAndFlush(tempBuf);
                                counter--;

                                if (counter == 0) {
                                    tempBuf.clear();
                                }
                            }

                            byte[] finArr = new byte[(int) remain];

                            in.read(finArr, 0, (int) remain);
                            finTempBuf = Unpooled.copiedBuffer(finArr);
                            ctx.writeAndFlush(finTempBuf);
                            finTempBuf.clear();

                            byte[] backCommand = {3};
                            ByteBuf respond = Unpooled.copiedBuffer(backCommand);
                            ctx.writeAndFlush(respond);
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
                    if (!input.isReadable()) {
                        return;
                    }
                    nameLength = input.readByte();
                    actionStage = ActionStage.GETTING_FILE_NAME;
                }
                if (actionStage.equals(ActionStage.GETTING_FILE_NAME)) {
                    System.out.println("ClientHandler " + actionStage);
                    if (input.readableBytes() < nameLength) {
                        return;
                    }
                    byte[] array = new byte[nameLength];
                    input.readBytes(array, 0, nameLength);
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
