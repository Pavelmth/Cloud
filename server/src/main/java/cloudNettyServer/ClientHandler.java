package cloudNettyServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    String fileName = null;
    private int state = -1;
    private int reqLen = -1;
    private int nameLen = 0;
    private DataType dataType = DataType.EMPTY;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);

        if (state == -1) {
            byte firstByte = buf.readByte();
            dataType = DataType.getDataTypeFromByte(firstByte);
            state = 0;
        }
        if (state == 0) {
            switch (dataType) {
                case SEND_FILES: /*send file*/
                    System.out.println("send file");
                    if (state == 0) {
                        reqLen = buf.readInt();
                        state = 1;
                        System.out.println("file size is " + reqLen);
                    }
                    //
                    if (state == 1) {
                        nameLen = buf.readInt();
                        state = 2;
                        System.out.println("file name length is " + nameLen);
                    }
                    //
                    if (state == 2) {
                        buf.readBytes(nameLen);
                        fileName = new String(buf.array());
                        state = 3;
                        System.out.println(fileName);
                    }
                    if (state == 3) {
                        //
                        //* create file
                        FileOutputStream outFile = new FileOutputStream("server/folder/" + fileName);
                        byte[] arr = new byte[8192];
                        int bufLen;
                        while (( bufLen = buf.readBytes(arr).capacity()) > 0){
                            outFile.write(arr, 0, bufLen);
                        }
                        outFile.close();
                    }
                    break;
                case DOWNLOAD_FILES: /*download file*/
                    System.out.println("download file");
                    if (state == 0) {
                        if (buf.readableBytes() < 4) {
                            return;
                        }
                        nameLen = buf.readInt();
                        state = 2;
                        System.out.println("name length " + nameLen);
                    }
                    if (state == 2) {
                        if (buf.readableBytes() < nameLen) {
                            return;
                        }
                        byte[] data = new byte[nameLen];
                        buf.readBytes(data);
                        String str = new String(data);
                        System.out.println(str);
                    }
                    //
                    break;
                default: /* add late */
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //create ENUM for defining what we've got?
    private enum DataType {
        EMPTY((byte) - 1), SEND_FILES((byte)15), DOWNLOAD_FILES((byte)16);
        byte firstMessageByte;
        DataType(byte firstMessageByte) {
            this.firstMessageByte = firstMessageByte;
        }

        static DataType getDataTypeFromByte(byte b) {
            if (b == SEND_FILES.firstMessageByte) {
                return SEND_FILES;
            }
            if (b == DOWNLOAD_FILES.firstMessageByte) {
                return DOWNLOAD_FILES;
            }
            return EMPTY;
        }
    }
}
