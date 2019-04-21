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

            //* create file
            FileOutputStream outFile = new FileOutputStream(UESER_FOLDER + "location.txt");
            //* take stream for receiving data from client
            inputStream = socket.getInputStream();
            //* sending data to client
            outputStream = socket.getOutputStream();

            //temp
            byte[] arrBytes = new byte[8154];
            inputStream.read(arrBytes);

            outFile.write(arrBytes);

            System.out.println("Server get " + arrBytes);

            //* write data to file
            outFile.write(inputStream.available());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
