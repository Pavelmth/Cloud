package cloudServer;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    MyServer server;
    Socket socket = null;

    InputStream inputStream;
    OutputStream outputStream;

    String login;

    //temp
    final String UESER_FOLDER = "server/folder/";

    public ClientHandler(MyServer server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            long start = System.currentTimeMillis();

            //* create file
            FileOutputStream outFile = new FileOutputStream(UESER_FOLDER + "location.txt");

            //* take stream for receiving data from client by BufferedInputStream
            BufferedInputStream buffIn = new BufferedInputStream(socket.getInputStream());

            byte[] arr = new byte[8192];

            int bufLen;
            while (( bufLen = buffIn.read(arr)) > 0){
                outFile.write(arr, 0, bufLen);
            }

            System.out.println("Time " + (System.currentTimeMillis() - start));

            buffIn.close();
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
