/*
package com.flamexander.netty.servers.unlimited_reader;

import com.flamexander.netty.servers.discard.DiscardServer;
import com.flamexander.netty.servers.discard.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class UnlimitedServer implements Runnable {
    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new UnlimitedHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(8189).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Thread(new UnlimitedServer()).start();
        Thread.sleep(1000);
        Socket socket = new Socket("localhost", 8189);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        byte[] outData = new byte[32];
        Arrays.fill(outData, (byte) 65);
        while (true) {
            out.write(outData);
            Thread.sleep(50);
        }
    }
}

package com.flamexander.netty.servers.unlimited_reader;

        import io.netty.buffer.ByteBuf;
        import io.netty.buffer.ByteBufAllocator;
        import io.netty.channel.ChannelHandlerContext;
        import io.netty.channel.ChannelInboundHandlerAdapter;

        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.RandomAccessFile;
        import java.nio.channels.FileChannel;

public class UnlimitedHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf accumulator;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
        accumulator = allocator.directBuffer(1024 * 1024 * 1, 5 * 1024 * 1024);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf input = (ByteBuf) msg;
        accumulator.writeBytes(input);
        input.release();
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("1.txt", true))) {
            while (accumulator.readableBytes() > 0) {
                out.write(accumulator.readByte());
            }
            accumulator.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
*/