package cloudServer;

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

            //* take stream for receiving data from client by BufferedInputStream
            DataInputStream buffIn = new DataInputStream(socket.getInputStream());

            Byte aByte = buffIn.readByte();
            System.out.println(aByte);

            Long aLong = buffIn.readLong();
            System.out.println(aLong);

            Integer anInteger = buffIn.readInt();
            System.out.println(anInteger);

            String str = buffIn.readUTF();
            System.out.println(str);

            //* create file
            FileOutputStream outFile = new FileOutputStream(UESER_FOLDER + str);

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
